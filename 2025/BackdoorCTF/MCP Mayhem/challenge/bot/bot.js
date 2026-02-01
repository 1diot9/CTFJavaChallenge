const puppeteer = require('puppeteer');

const sleep = ms => new Promise(res => setTimeout(res, ms));

const CONFIG = {
    APPNAME: process.env['APPNAME'] || "Admin",
    APPURL: process.env['APPURL'] || "http://challenge.com:6001",
    APPURLREGEX: process.env['APPURLREGEX'] || "^.*$",
    APPFLAG: process.env['APPFLAG'] || "dev{flag}",
    ADMIN_TOKEN: process.env['ADMIN_TOKEN'] || "secret_admin_token_12345",
    APPLIMITTIME: Number(process.env['APPLIMITTIME'] || "60"),
    APPLIMIT: Number(process.env['APPLIMIT'] || "5"),
}

console.table(CONFIG)

const initBrowser = puppeteer.launch({
    executablePath: "/usr/bin/chromium-browser",
    headless: 'new',
    args: [
        '--disable-dev-shm-usage',
        '--no-sandbox',
        '--disable-setuid-sandbox',
        '--disable-gpu',
        '--no-gpu',
        '--disable-default-apps',
        '--disable-translate',
        '--disable-device-discovery-notifications',
        '--disable-software-rasterizer',
        '--disable-xss-auditor'
    ],
    ipDataDir: '/home/bot/data/',
    ignoreHTTPSErrors: true
});

console.log("Bot started...");

module.exports = {
    name: CONFIG.APPNAME,
    urlRegex: CONFIG.APPURLREGEX,
    rateLimit: {
        windowS: CONFIG.APPLIMITTIME,
        max: CONFIG.APPLIMIT
    },
    bot: async (toolName, argumentsJson) => {
        const browser = await initBrowser;
        const context = await browser.createIncognitoBrowserContext()
        try {
            const page = await context.newPage();

            url = CONFIG.APPURL;
            await page.setCookie({ 
                name: 'session', 
                value: CONFIG.ADMIN_TOKEN, 
                domain: new URL(url).hostname, 
                httpOnly: true
            });

            const clientUrl = url.replace('challenge.com', 'client.challenge.com');
            const callUrl = `${clientUrl}/call`;
            console.log(`bot navigating to ${callUrl}`)
            await page.goto(callUrl, {
                timeout: 5000,
                waitUntil: 'networkidle2'
            });

            await page.waitForSelector('#tool');
            await page.waitForSelector('#arguments');

            console.log(`bot filling tool: ${toolName}`)
            await page.type('#tool', toolName);

            console.log(`bot filling arguments: ${argumentsJson}`)
            await page.evaluate((args) => {
                document.getElementById('arguments').value = args;
            }, argumentsJson);

            console.log(`bot submitting form`)
            await page.click('button[type="submit"]');

            await sleep(3000);

            const hasResult = await page.evaluate(() => {
                const resultDiv = document.getElementById('result');
                const errorDiv = document.getElementById('error');
                if (resultDiv && !resultDiv.classList.contains('hidden')) {
                    return { type: 'result', content: document.getElementById('resultContent')?.textContent };
                }
                if (errorDiv && !errorDiv.classList.contains('hidden')) {
                    return { type: 'error', content: document.getElementById('errorMessage')?.textContent };
                }
                return null;
            });

            if (hasResult) {
                console.log(`Tool call ${hasResult.type}:`, hasResult.content);
            }

            console.log("browser close...")
            await context.close()
            return true;
        } catch (e) {
            console.error(e);
            await context.close();
            return false;
        }
    }
}

