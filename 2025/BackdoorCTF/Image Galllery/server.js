const express = require('express');
const path = require('path');
const fs = require('fs');

const app = express();

const BASE_DIR = path.join(__dirname, 'images');

app.use(express.static(path.join(__dirname, 'public'), { index: false }));

app.get('/', (req, res) => {
  const htmlPath = path.join(__dirname, 'public', 'index.html');

  let html;
  try {
    html = fs.readFileSync(htmlPath, 'utf8');
  } catch (e) {
    console.error('Error reading index.html:', e);
    return res.status(500).send('Error loading page');
  }

  let files = [];
  try {
    files = fs.readdirSync(BASE_DIR)
      .filter(f => f.endsWith('.jpg') || f.endsWith('.jpeg') || f.endsWith('.png'));
  } catch (e) {
    console.error('Error reading images folder:', e);
  }


  html = html.replace('/*IMAGE_LIST_HERE*/', JSON.stringify(files));

  res.send(html);
});

app.get('/image', (req, res) => {
  let file = req.query.file || '';

  try {
    file = decodeURIComponent(file);
  } catch (e) {
    return res.status(400).send('Bad encoding');
  }

  file = file.replace(/\\/g, '/');

  file = file.split('../').join('');

  const resolved = path.join(BASE_DIR, file);


  fs.readFile(resolved, (err, data) => {
    if (err) {
      console.error(err);
      return res.status(404).send('Not found.');
    }

    if (resolved.endsWith('.jpg') || resolved.endsWith('.jpeg')) {
      res.setHeader('Content-Type', 'image/jpeg');
    } else if (resolved.endsWith('.png')) {
      res.setHeader('Content-Type', 'image/png');
    } else if (resolved.endsWith('.txt')) {
      res.setHeader('Content-Type', 'text/plain; charset=utf-8');
    }

    res.send(data);
  });
});

const PORT = 8000;
app.listen(PORT, () => {
  console.log(`Running at http://localhost:${PORT}`);
});
