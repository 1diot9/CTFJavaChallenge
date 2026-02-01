const express = require('express');
const fs = require('fs');
const path = require('path');
const cookieParser = require('cookie-parser');
const yaml = require('js-yaml');

const TMP_DIR = path.join(__dirname, 'tmp');
const SYSTEM_CONFIG_PATH = path.join(TMP_DIR, 'config.yml'); // system YAML file

const { DOMParser, XMLSerializer } = require('@xmldom/xmldom');
const xpath = require('xpath');

const app = express();
const port = process.env.port || 8000;

try {
  if (!fs.existsSync(TMP_DIR)) {
    fs.mkdirSync(TMP_DIR, { recursive: true });
  }
} catch (e) {
  console.error('Could not ensure /tmp exists:', e);
}

const MAX_FILE_AGE_MS =  3*60 * 1000; // 3 min

function cleanupTmp() {
  try {
    const now = Date.now();
    const files = fs.readdirSync(TMP_DIR);

    for (const f of files) {
      if (f.startsWith('.')) continue;

      // keep the system config forever
      if (f === 'config.yml') continue;

      const full = path.join(TMP_DIR, f);
      const stat = fs.statSync(full);
      const age = now - stat.mtimeMs;

      if (age > MAX_FILE_AGE_MS) {
        console.log('[CLEANUP] Removing old tmp file:', f);
        fs.unlinkSync(full);
      }
    }
  } catch (e) {
    console.error('[CLEANUP] Error while cleaning tmp:', e);
  }
}

// run every 2 minutes
setInterval(cleanupTmp,2*60 * 1000);

app.use(express.urlencoded({ extended: true }));
app.use(express.json());
app.use(cookieParser());


app.use(express.static(path.join(__dirname, 'public')));


const dbPath = path.join(__dirname, 'data.xml');

let xmlDoc;
try {
  const xmlContent = fs.readFileSync(dbPath, 'utf8');
  xmlDoc = new DOMParser().parseFromString(xmlContent);
  console.log('[SYSTEM] XML database loaded.');
} catch (err) {
  console.error('Failed to load data.xml:', err);
  process.exit(1);
}


function saveXml() {
  const serializer = new XMLSerializer();
  const xmlString = serializer.serializeToString(xmlDoc);
  fs.writeFileSync(dbPath, xmlString, 'utf8');
}

const sessions = {};

function requireLogin(req, res, next) {
  const sid = req.cookies.sid;
  if (!sid || !sessions[sid]) {
    return res.redirect('/login.html');
  }
  req.user = sessions[sid];
  next();
}

function requireAdmin(req, res, next) {
  const sid = req.cookies.sid;
  if (!sid || !sessions[sid]) {
    return res.redirect('/login.html');
  }

  const username = sessions[sid];


  const query = `//user[username/text()='${username}' and role/text()='admin']`;
  const userNode = xpath.select(query, xmlDoc)[0];

  if (!userNode) {
    return res.status(403).send('ACCESS DENIED: Admin only');
  }

  req.user = username;
  next();
}

app.get('/', (req, res) => {
  res.redirect('/login.html');
});

app.post('/register', (req, res) => {
  const { username, password } = req.body;

  if (!username || !password) {
    return res.status(400).send('Missing username or password');
  }

  const checkQuery = `//user[username/text()='${username}']`;
  const exists = xpath.select(checkQuery, xmlDoc)[0];

  if (exists) {
    return res.status(400).send('User already exists');
  }

  const user = xmlDoc.createElement('user');

  const un = xmlDoc.createElement('username');
  const pw = xmlDoc.createElement('password');
  const rl = xmlDoc.createElement('role');
  const id = xmlDoc.createElement('id');

  un.appendChild(xmlDoc.createTextNode(username));
  pw.appendChild(xmlDoc.createTextNode(password));
  rl.appendChild(xmlDoc.createTextNode('employee'));
  id.appendChild(xmlDoc.createTextNode(Date.now().toString()));

  user.appendChild(un);
  user.appendChild(pw);
  user.appendChild(rl);
  user.appendChild(id);

  const usersNode = xpath.select('//users', xmlDoc)[0];
  usersNode.appendChild(user);

  saveXml();

  res.send("Registered! <a href='/login.html'>Login</a>");
});

app.post('/login', async (req, res) => {
  const { username, password } = req.body;

  if (!username || !password) {
    return res.status(400).send('Missing username or password');
  }

  const query = `//user[username/text()='${username}']`;
  const userNode = xpath.select(query, xmlDoc)[0];

  if (userNode) {
    await new Promise(resolve => setTimeout(resolve, 2000));
  }

  if (!userNode) {
    return res.status(401).send('Invalid username or password');
  }

  const storedPassword = xpath.select1('string(password)', userNode);

  if (storedPassword !== password) {
    return res.status(401).send('Invalid username or password');
  }


  const sid = Math.random().toString(36).slice(2);
  sessions[sid] = xpath.select1('string(username)', userNode);

 res.cookie('sid', sid, {
  httpOnly: true,     
  sameSite: 'Lax',      
});


  res.redirect('/index');
});


app.get('/index', requireLogin, (req, res) => {
  res.sendFile(path.join(__dirname, 'protected', 'index.html'));
});


app.get('/store', requireAdmin, (req, res) => {
  res.sendFile(path.join(__dirname, 'protected', 'store.html'));
});


app.get('/logout', (req, res) => {
  const sid = req.cookies.sid;
  if (sid) {
    delete sessions[sid];
  }
  res.clearCookie('sid');
  res.redirect('/login.html');
});


app.get('/me', (req, res) => {
  const sid = req.cookies.sid;
  if (sid && sessions[sid]) {
    return res.json({ loggedIn: true, user: sessions[sid] });
  }
  res.json({ loggedIn: false });
});

app.post('/admin/create', requireAdmin, (req, res) => {
    console.log('HIT /admin/create', req.body);
  const { filename, fileContent } = req.body;

  if (!filename || !fileContent) {
    return res.status(400).send('Missing filename or YAML content');
  }

    
    const datePrefix = new Date().toISOString().split('T')[0];
   
  const safeBase = path.basename(filename);
  const finalName = `${datePrefix}_${safeBase}`;

  if (finalName === 'config.yml') {
    return res.status(400).send('That filename is not allowed');
  }

    const targetPath = path.join(TMP_DIR, finalName);

  try {
    fs.writeFileSync(targetPath, fileContent, 'utf8');

    let parsed;
    try {
     parsed = yaml.load(fileContent);
     const applied = '' + parsed; 
      return res.json({
        success: true,
        filename: finalName,
        result: applied,  
      });
    } catch (e) {
      return res.status(400).json({
        success: false,
        filename: finalName,
        error: 'Invalid YAML',
        details: e.message,
      });
    }

  } catch (err) {
    console.error('Error writing file:', err);
    return res.status(500).json({ success: false, error: 'Failed to save file' });
  }
});


app.get('/admin/files', requireAdmin, (req, res) => {
  try {
    const files = fs.readdirSync(TMP_DIR)
      .filter(f => !f.startsWith('.'));

    return res.json({ files });
  } catch (err) {
    console.error('Error listing /tmp files:', err);
    return res.status(500).send('Failed to list files');
  }
});

app.listen(port, () => {
  console.log(`Server running on port ${port}`);
});
