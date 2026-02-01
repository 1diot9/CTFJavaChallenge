const express = require("express")
const app = express();
const path = require("path")
const route = express.Router()
const bot = require("./bot")
const rateLimit = require("express-rate-limit")

app.use(express.urlencoded({ extended: false }))
app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));
app.set('trust proxy', () => true)

const limit = rateLimit({
    ...bot,
    handler: ((req, res, _next) => {
        const timeRemaining = Math.ceil((req.rateLimit.resetTime - Date.now()) / 1000)
        res.status(429).json({
            error: `Too many requests, please try again later after ${timeRemaining} seconds.`,
        });
    })
})


route.post("/", limit, async (req, res) => {
    const { tool, arguments } = req.body;
    if (!tool) {
        return res.status(400).send({ error: "Tool name is missing." });
    }
    if (!arguments) {
        return res.status(400).send({ error: "Arguments are missing." });
    }
    
    try {
        JSON.parse(arguments);
    } catch (e) {
        return res.status(400).send({ error: "Invalid JSON format in arguments." });
    }
    
    if (await bot.bot(tool, arguments)) {
        return res.send({ success: "Admin successfully called the tool." });
    } else {
        return res.status(500).send({ error: "Admin failed to call the tool." });
    }
});

route.get("/", (_, res) => {
    const { name } = bot
    res.render("index", { name });
});

app.use("/", route)

app.listen(4003, () => {
    console.log("Server running at http://localhost:4003");
});
