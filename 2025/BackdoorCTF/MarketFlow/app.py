from flask import Flask, request, jsonify, session, send_file, render_template
from werkzeug.security import generate_password_hash, check_password_hash
import os
import secrets
from datetime import datetime
from functools import wraps

from models.database import init_db, get_db
from services.object_manager import ObjectManager
from services.extension_loader import ExtensionLoader
from services.cache_service import CacheService
from services.scheduler import Scheduler
from services.renderer import TemplateRenderer
from config import Config

app = Flask(__name__)
app.config.from_object(Config)
app.secret_key = secrets.token_hex(32)

app.template_folder = 'templates'

init_db()
object_manager = ObjectManager()
extension_loader = ExtensionLoader()
cache_service = CacheService()
scheduler = Scheduler()
renderer = TemplateRenderer()

def login_required(f):
    @wraps(f)
    def decorated_function(*args, **kwargs):
        if 'user_id' not in session:
            return jsonify({"error": "Authentication required"}), 401
        return f(*args, **kwargs)
    return decorated_function

# --- Frontend Routes ---

@app.route('/')
def index():
    return render_template('login.html')

@app.route('/register.html')
def register_page():
    return render_template('register.html')

@app.route('/login.html')
def login_page():
    return render_template('login.html')

@app.route('/dashboard.html')
@login_required
def dashboard_page():
    return render_template('dashboard.html', username=session.get('username'))

# --- API Routes ---

@app.route('/api/auth/register', methods=['POST'])
def register():
    data = request.json
    username = data.get('username')
    password = data.get('password')
    email = data.get('email')
    
    if not all([username, password, email]):
        return jsonify({"error": "Missing required fields"}), 400
    
    db = get_db()
    existing = db.execute("SELECT id FROM users WHERE username = ?", [username]).fetchone()
    
    if existing:
        return jsonify({"error": "Username already exists"}), 400
    
    user_id = secrets.token_hex(16)
    password_hash = generate_password_hash(password)
    
    db.execute(
        "INSERT INTO users (id, username, password_hash, email, role) VALUES (?, ?, ?, ?, ?)",
        [user_id, username, password_hash, email, 'user']
    )
    db.commit()
    
    return jsonify({"user_id": user_id, "username": username}), 201

@app.route('/api/auth/login', methods=['POST'])
def login():
    data = request.json
    username = data.get('username')
    password = data.get('password')
    
    db = get_db()
    user = db.execute("SELECT * FROM users WHERE username = ?", [username]).fetchone()
    
    if not user or not check_password_hash(user['password_hash'], password):
        return jsonify({"error": "Invalid credentials"}), 401
    
    session['user_id'] = user['id']
    session['username'] = user['username']
    session['role'] = user['role']
    
    return jsonify({
        "user_id": user['id'],
        "username": user['username'],
        "role": user['role']
    })

@app.route('/api/auth/logout', methods=['POST'])
def logout():
    session.clear()
    return jsonify({"status": "logged out"})

@app.route('/api/campaigns', methods=['GET'])
@login_required
def get_campaigns():
    db = get_db()
    user_id = session['user_id']
    
    campaigns = db.execute(
        "SELECT * FROM campaigns WHERE user_id = ? ORDER BY created_at DESC",
        [user_id]
    ).fetchall()
    
    return jsonify([dict(c) for c in campaigns])

@app.route('/api/campaigns', methods=['POST'])
@login_required
def create_campaign():
    data = request.json
    user_id = session['user_id']
    
    campaign_obj = object_manager.deserialize(data)
    
    if not hasattr(campaign_obj, 'name'):
        return jsonify({"error": "Invalid campaign data"}), 400
    
    db = get_db()
    campaign_id = secrets.token_hex(16)
    
    db.execute(
        "INSERT INTO campaigns (id, user_id, name, status, config) VALUES (?, ?, ?, ?, ?)",
        [campaign_id, user_id, campaign_obj.name, 'draft', str(data)]
    )
    db.commit()
    
    return jsonify({"campaign_id": campaign_id, "name": campaign_obj.name}), 201

@app.route('/api/campaigns/<campaign_id>', methods=['PUT'])
@login_required
def update_campaign(campaign_id):
    data = request.json
    user_id = session['user_id']
    
    db = get_db()
    campaign = db.execute(
        "SELECT * FROM campaigns WHERE id = ? AND user_id = ?",
        [campaign_id, user_id]
    ).fetchone()
    
    if not campaign:
        return jsonify({"error": "Campaign not found"}), 404
    
    campaign_obj = object_manager.deserialize(data)
    
    db.execute(
        "UPDATE campaigns SET name = ?, status = ?, config = ?, updated_at = ? WHERE id = ?",
        [getattr(campaign_obj, 'name', campaign['name']), 
         getattr(campaign_obj, 'status', campaign['status']),
         str(data), datetime.now().isoformat(), campaign_id]
    )
    db.commit()
    
    return jsonify({"status": "updated"})

@app.route('/api/analytics/track', methods=['POST'])
def track_analytics():
    data = request.json
    
    event_obj = object_manager.deserialize(data)
    
    db = get_db()
    event_id = secrets.token_hex(16)
    
    db.execute(
        "INSERT INTO analytics_events (id, event_type, event_data, timestamp) VALUES (?, ?, ?, ?)",
        [event_id, getattr(event_obj, 'event_type', 'unknown'), str(data), datetime.now().isoformat()]
    )
    db.commit()
    
    return jsonify({"event_id": event_id})

@app.route('/api/analytics/reports', methods=['POST'])
@login_required
def generate_report():
    data = request.json
    user_id = session['user_id']
    
    report_config = object_manager.deserialize(data)
    
    report_id = secrets.token_hex(16)
    report_token = secrets.token_urlsafe(32)
    
    scheduler.schedule_task({
        'task_type': 'generate_report',
        'report_id': report_id,
        'report_token': report_token,
        'user_id': user_id,
        'config': data
    })
    
    return jsonify({
        "report_id": report_id,
        "status": "scheduled",
        "report_url": f"/reports/{report_token}.html"
    })


@app.route('/reports/<token>')
def serve_report(token):
    reports_dir = '/var/tmp/sessionmaze/reports'
    report_path = os.path.join(reports_dir, token)
    
    if os.path.exists(report_path):
        return send_file(report_path, mimetype='text/html')
    
    return jsonify({"error": "Report not found"}), 404


@app.route('/api/webhooks/receive', methods=['POST'])
def receive_webhook():
    data = request.json
    
    webhook_payload = object_manager.deserialize(data)
    
    db = get_db()
    webhook_id = secrets.token_hex(16)
    
    db.execute(
        "INSERT INTO webhooks (id, payload, received_at) VALUES (?, ?, ?)",
        [webhook_id, str(data), datetime.now().isoformat()]
    )
    db.commit()
    
    return jsonify({"webhook_id": webhook_id})

@app.route('/internal/cron/process', methods=['POST'])
def process_scheduled_tasks():
    if request.remote_addr not in ['127.0.0.1', 'localhost']:
        return jsonify({"error": "Internal only"}), 403
    
    try:
        scheduler.process_pending()
    except Exception as e:
        return jsonify({"error": str(e)}), 403
    
    return jsonify({"status": "processed"})


@app.route('/api/webhooks/forward', methods=['POST'])
@login_required
def forward_webhook():
    from services.webhook_service import WebhookForwarder
    
    data = request.json
    forwarder_obj = object_manager.deserialize(data)
    
    if not isinstance(forwarder_obj, WebhookForwarder):
        return jsonify({"error": "Invalid forwarder configuration"}), 400
    
    payload = data.get('payload', {})
    result = forwarder_obj.forward(payload)
    
    return jsonify(result)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=False)