const express = require('express');
const puppeteer = require('puppeteer');

const app = express();
app.use(express.urlencoded({ extended: false }));

const BLOG_HOST = process.env['BLOG_HOST'] ?? 'blog-app';
const LOGIN_URL = `http://${BLOG_HOST}/login`;
const ADMIN_EMAIL= process.env['ADMIN_EMAIL'] ?? 'admin@rois.team';
const ADMIN_PASSWD= process.env['ADMIN_PASSWD'] ?? 'admin123';
const FLAG = process.env['FLAG'] ?? 'RCTF{test_flag}';
const PORT = 3000;

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

app.get('/', (req, res) => {
    res.send(`
    <h2>Article Audit Assistant</h2>
    <form method="POST" action="/audit">
      <input type="number" name="id" placeholder="Input Your Article ID" style="width: 500px;" />
      <button type="submit">Submit</button>
    </form>
  `);
});

app.post('/audit', async (req, res) => {
    const { id } = req.body;
    const article_id = parseInt(id);

    if (Number.isNaN(article_id)){
        return res.status(400).send('Invalid Article Id');
    }


    const article_url = `http://${BLOG_HOST}/article/${article_id}`;

    try {
        console.log(`[+] Visiting: ${article_url}`);
        const browser = await puppeteer.launch({
            headless: 'new',
            args: [
                '--no-sandbox',
                '--disable-setuid-sandbox',
            ]
        });

        await browser.setCookie({ name: 'flag', value: FLAG, domain: BLOG_HOST });

        const page = await browser.newPage();


        await page.goto(LOGIN_URL, { timeout: 3000, waitUntil: 'domcontentloaded' })

        await page.type('#email', ADMIN_EMAIL, { delay: 50 });
        await page.type('#password', ADMIN_PASSWD, { delay: 50 });

        await Promise.all([
            page.click('button[type="submit"]'),
            page.waitForNavigation({ timeout: 3000, waitUntil: 'domcontentloaded' }),
        ]);


        await page.goto(article_url, { timeout: 3000, waitUntil: 'domcontentloaded' })

        const auditBtn = await page.$('#audit');
        if (auditBtn) {
            await Promise.all([
                auditBtn.click(),
                page.waitForNavigation({ waitUntil: 'domcontentloaded' }),
            ]);
        }

        let rejectBtn = null;
        try {
            rejectBtn = await page.waitForSelector('.btn-reject', { visible: true, timeout: 5000 });
        } catch (err) {}
        if (rejectBtn) {
            await rejectBtn.click();
        }

        await sleep(5000);

        await browser.close();
        res.send('Audit completed');

    } catch (err) {
        console.error(`[!] Error visiting URL:`, err);
        res.status(500).send('Bot error visiting URL');
    }
});


app.listen(PORT, () => {
    console.log(`Audit Assistant running at port ${PORT}`);
});

