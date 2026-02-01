from flask import Flask, request, make_response, render_template_string
import os, base64, sys, threading, time, jsonify, nh3
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address


app = Flask(__name__)

PORT = 6005

flag = open('flag.txt').read().strip()
# flag charset is string.ascii_lowercase + string.digits

ALLOWED_TAGS = {
    'a', 'b', 'blockquote', 'br', 'code', 'div', 'em', 
    'h1', 'h2', 'h3', 'i', 'iframe', 'img', 'li', 'link', 
    'ol', 'p', 'pre', 'span', 'strong', 'ul'
}
ALLOWED_ATTRIBUTES = {
    'a': {'href', 'target'},
    'link': {'rel', 'href', 'type', 'as'}, 
    '*': {

        'style','src', 'width', 'height', 'alt', 'title',
        'lang', 'dir', 'loading', 'role', 'aria-label'
    }
}

APP_LIMIT_TIME = 60  
APP_LIMIT_COUNT = 5  


limiter = Limiter(
    get_remote_address,
    app=app,
    storage_uri="memory://" 
)

@app.errorhandler(429)
def ratelimit_handler(e):
    return jsonify({
        "error": f"Too many requests, please try again later. Limit is {APP_LIMIT_COUNT} requests per {APP_LIMIT_TIME} seconds."
    }), 429

template = """<!DOCTYPE html>
<html>
<head>

</head>
<body>
    <div class="head"></div>
    {% if flag %}
        <div class="flag"><h1>{{ flag }}</h1></div>
    {% endif %}
    {% if note %}
        <div class="note">{{ note | safe}}</div>
    {% endif %}
    <script nonce="{{ nonce }}">
        Array.from(document.getElementsByClassName('flag')).forEach(function(element) {
            let text = element.innerText;
            element.innerHTML = '';
            for (let i = 0; i < text.length; i++) {
                let charElem = document.createElement('span');
                charElem.innerText = text[i];
                element.appendChild(charElem);
            }
        });
    </script>
</body>
</html>
"""



@app.route('/', methods=['GET'])
def index():

    _flag = request.cookies.get('flag', None)
    nonce = base64.b64encode(os.urandom(32)).decode('utf-8')
    _note = request.args.get('note', 'Try putting your note with /?note=..')
    clean_note = nh3.clean(
        _note, 
        tags=ALLOWED_TAGS, 
        attributes=ALLOWED_ATTRIBUTES,
    )
    
    rendered = render_template_string(
        template,
        nonce=nonce,
        flag=_flag,
        note=clean_note,
    )
    
    response = make_response(rendered)

    response.headers['Content-Security-Policy'] = (
        f"default-src 'none'; script-src 'nonce-{nonce}'; style-src 'none'; "
        "base-uri 'none'; frame-ancestors 'self'; frame-src 'self'; object-src 'none'; "
    )
    response.headers['Referrer-Policy'] = 'no-referrer'
    response.headers['X-Frame-Options'] = 'SAMEORIGIN'
    response.headers['X-Content-Type-Options'] = 'nosniff'
    
    return response

def admin_visit(raw_data_b64: str):
    try:
    
        try:
            data = base64.b64decode(raw_data_b64.encode('utf-8')).decode('utf-8')
        except Exception as e:
            print(f"[BOT] base64 decode error: {e}", file=sys.stderr)
            return

        url = f"http://127.0.0.1:6005/?note={data}"
        print(f"[BOT] Visiting {url}", file=sys.stderr)

        options = Options()
        options.add_argument("--headless")
        options.add_argument("--no-sandbox")
        options.add_argument("--disable-dev-shm-usage")
        options.add_argument("--disable-gpu")

        driver = webdriver.Chrome(options=options)

        try:
            
            driver.get("http://127.0.0.1:6005/")
            driver.add_cookie({
                'name': 'flag',
                'value': flag.replace("{", "").replace("}", ""), 
                'path': '/',
                'httpOnly': True,
                'sameSite': 'Strict'
            })

            print(f"[BOT] Now visiting target URL {url}", file=sys.stderr)

           
            driver.set_page_load_timeout(5)
            try:
                driver.get(url)
            except Exception as e:
                print(f"[BOT] error during driver.get: {e}", file=sys.stderr)
            time.sleep(5)
        finally:
            driver.quit()
            print(f"[BOT] Done visiting URL {url}", file=sys.stderr)

    except Exception as e:
        print(f"[BOT] Unexpected bot error: {e}", file=sys.stderr)


@app.route('/bot', methods=['GET'])
@limiter.limit(f"{APP_LIMIT_COUNT} per {APP_LIMIT_TIME} second")
def bot():
    raw_data = request.args.get('note')
    if not raw_data:
        return make_response("Missing ?note parameter\n", 400)

    t = threading.Thread(target=admin_visit, args=(raw_data,))
    t.daemon = True
    t.start()

    return make_response("Admin will visit this URL soon.\n", 202)


if __name__ == '__main__':
    app.run(port=PORT, debug=False, host='0.0.0.0')


