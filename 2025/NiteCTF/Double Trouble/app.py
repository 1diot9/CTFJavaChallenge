from flask import Flask, request, Response, jsonify
import os
import time
import logging
import uuid
import threading
import json
from niteproxy.backend.services import metrics_service, auth_service  
from niteproxy.backend.core import request_processor, security_layer

app = Flask(__name__)


logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)


FLAG = os.environ.get('FLAG', 'nite{REDACTED}')


player_sessions = {}
session_lock = threading.Lock()


def cleanup_old_sessions():
    while True:
        time.sleep(60)
        with session_lock:
            current_time = time.time()
            for user_id in list(player_sessions.keys()):
                sessions = player_sessions[user_id]
                for token in list(sessions.keys()):
                    if current_time - sessions[token]['timestamp'] > 3600:
                        del sessions[token]
                if not sessions:
                    del player_sessions[user_id]


cleanup_thread = threading.Thread(target=cleanup_old_sessions, daemon=True)
cleanup_thread.start()


def get_user_id():
    user_id = request.cookies.get('user_id')
    if not user_id:
        user_id = str(uuid.uuid4())
    return user_id


def create_session_token(user_id):
    
    token = str(uuid.uuid4())
    with session_lock:
        if user_id not in player_sessions:
            player_sessions[user_id] = {}
        player_sessions[user_id][token] = {
            'poisoned': False,
            'timestamp': time.time(),
            'user_id': user_id
        }
    
    return token

def is_admin_ip(ip):
    if not ip:
        return False
    ip = str(ip).strip()
    if ',' in ip:
        ip = ip.split(',')[0].strip()
    return (ip.startswith('127.') or ip.startswith('10.') or ip.startswith('192.168.') or ip.startswith('172.16.'))


@app.route('/')
def index():
    user_id = get_user_id()
    proxy_count = metrics_service.count_proxy_headers(request.headers)
    
    html = f'''<!DOCTYPE html>
<html>
<head>
    <title>NiteProxy Enterprise Gateway</title>
    <style>
        body {{ background: #0a0a0a; color: #ccc; font-family: monospace; padding: 2rem; }}
        .container {{ max-width: 800px; margin: 0 auto; border: 1px solid #333; padding: 1rem; }}
        h1 {{ color: #fff; border-bottom: 1px solid #333; padding-bottom: 0.5rem; margin-bottom: 1rem; font-size: 1.2rem; }}
        .stat {{ margin-bottom: 0.5rem; }}
        .label {{ color: #666; width: 140px; display: inline-block; }}
        .value {{ color: #0f0; }}
        .footer {{ margin-top: 2rem; font-size: 0.8rem; color: #444; border-top: 1px solid #333; padding-top: 1rem; }}
    </style>
</head>
<body>
    <div class="container">
        <h1>NiteProxy Enterprise v2.1.3</h1>
        <div class="stat"><span class="label">Service Status:</span> <span class="value">ACTIVE</span></div>
        <div class="stat"><span class="label">Backend Sync:</span> <span class="value">CONNECTED</span></div>
        <div class="stat"><span class="label">Session ID:</span> <span class="value">{user_id}</span></div>
        <div class="stat"><span class="label">Proxy Layers:</span> <span class="value">{proxy_count}</span></div>
        <div class="footer">
            NiteProxy Enterprise Gateway | Powered by niteproxy.backend.services
        </div>
    </div>
</body>
</html>'''

    response = Response(html, mimetype='text/html')
    response.set_cookie('user_id', user_id, max_age=86400, httponly=True, samesite='Lax')
    return response

@app.route('/health')
def health():
    return jsonify({"status": "ok", "timestamp": int(time.time())}), 200


@app.route('/con', methods=['GET', 'POST'])
def reserved_names():
    user_id = get_user_id()
    token = create_session_token(user_id)   

    content_length = request.headers.get('Content-Length')
    if content_length:
        try:
            if int(content_length) > 0:
                with session_lock:
                   
                    player_sessions[user_id][token]['poisoned'] = True
        except Exception:
            pass

    response = Response("Reserved", status=200, headers={'Connection': 'keep-alive'})

    cookie_kwargs = {
        "max_age": 86400,
        "httponly": True,
        "samesite": "Lax",
    }
   
    if request.is_secure or os.environ.get("FORCE_SECURE_COOKIES", "0") == "1":
        cookie_kwargs["secure"] = True

    response.set_cookie('user_id', user_id, **cookie_kwargs)
    
    
    if content_length:
        try:
            if int(content_length) > 0:
                response.set_cookie('session_token', token, **cookie_kwargs)
        except Exception:
            pass
    
    return response



@app.route('/api/v1/debug')
def api_debug():
    return metrics_service.gather_runtime_stats(request)

@app.route('/api/v1/session/check')
def api_session_check():
    return auth_service.evaluate_connection_integrity(request)

@app.route('/api/v1/data', methods=['GET', 'POST'])
def api_data():
    return request_processor.run_data_transformations(request)

@app.route('/admin')
def admin_flag():
    return security_layer.apply_policy_controls(request)

@app.errorhandler(404)
def not_found(e):
    return jsonify({"error": "Not found"}), 404


@app.errorhandler(500)
def server_error(e):
    return jsonify({"error": "Internal server error"}), 500


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8000, debug=False)
