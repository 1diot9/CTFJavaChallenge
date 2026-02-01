const expres=require('express')
const JSON5 = require('json5');
const bodyParser = require('body-parser')
const pugjs=require('pug')
const session = require('express-session')
const rand = require('string-random')
var cookieParser = require('cookie-parser');
const SECRET = rand(32, '0123456789abcdef')

const port=80
const app=expres()

app.use(bodyParser.urlencoded({ extended: false }))
app.use(bodyParser.json())
app.use(session({
    secret: SECRET,
    resave: false,
    saveUninitialized:  true,
    cookie: { maxAge: 3600 * 1000 }
}));
app.use(cookieParser());
function waf(obj, arr){
    let verify = true;

    Object.keys(obj).forEach((key) => {
        if (arr.indexOf(key) > -1) {
            verify = false;
        }
    });
    return verify;
}
app.get('/',(req,res)=>{
    res.send('hey bro!')
})

app.post('/login',(req,res)=>{
    let userinfo=JSON.stringify(req.body)
    const user = JSON5.parse(userinfo)
    if (waf(user, ['admin'])) {
        req.session.user  = user
        if(req. session.user.admin==true){
            req.session.user='admin'
            res.send('hello,admin')
        }
        else{
            res.send('hello,guest')
        }
    }
    else {
        res.send('login error!')
    }
})

app.post('/render',(req,res)=>{
    if (req.session.user === 'admin'){
    
        var word = req.body.word
        
        const blacklist = ['require', 'exec']
        let isBlocked = false
        
        if (word) {
            for (let keyword of blacklist) {
                if (word.toLowerCase().includes(keyword.toLowerCase())) {
                    isBlocked = true
                    break
                }
            }
        }
        
        if (isBlocked) {
            res.send('Blocked:  dangerous keywords detected!')
        } else {
            var hello='welcome '+ word
            res.send (pugjs.render(hello))
        }
    }
    else{
        res.send('you are not admin')
    }
})

app.listen(port, () => {
    console.log(`Example app listening on port ${port}`)
})