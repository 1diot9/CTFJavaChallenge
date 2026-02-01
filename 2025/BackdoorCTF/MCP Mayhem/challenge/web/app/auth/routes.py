import sys
from flask import Blueprint, render_template, request, redirect, url_for, make_response, jsonify 
from app.utils import query_db, generateCookie, generate_csrf_token, validate_csrf_token
from sqlite3 import IntegrityError
from os import environ
from app.utils import decodeCookie
import requests

auth_bp = Blueprint('auth', __name__)

MCP_PROXY_URL = environ.get("MCP_PROXY_URL", "http://localhost:8000")

@auth_bp.route('/', methods=["GET"])
def landing():
    return render_template('auth/landing.html')

@auth_bp.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']
        user = query_db('SELECT * FROM users WHERE username = ? AND password = ?', (username, password), one=True)
        if user:
            res = make_response(redirect(url_for("client.private")))
            cookie = generateCookie(user[0])
            res.set_cookie("session", cookie, domain="challenge.com")
            return res
        else:
            return render_template('auth/login.html', error='Invalid username or password')
    return render_template('auth/login.html')

@auth_bp.route('/register', methods=['GET', 'POST'])
def register():
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']
        about = request.form['about']
        confirm_password = request.form['confirm_password']

        if password != confirm_password:
            return render_template('auth/register.html', error='Passwords do not match')

        try:
            query_db('INSERT INTO users (username, password, description) VALUES (?, ?, ?)', (username, password, about))
            return redirect(url_for('auth.login'))
        except IntegrityError:
            return render_template('auth/register.html', error='Username already exists')
    return render_template('auth/register.html')

@auth_bp.route("/profile", methods=["GET"])
def profile():
    user_id = decodeCookie(request.cookies.get("session")).get('user_id')
    if user_id == 0:
        return redirect(url_for('auth.login'))
    current_user = query_db('SELECT * FROM users WHERE id = ?', (user_id,), one=True)
    details = {'username': current_user[1], 'description': current_user[3]}
    return render_template("auth/account_detail.html", current_user=details)

@auth_bp.route('/logout', methods=['GET'])
def logout():
    res = make_response(redirect(url_for("auth.login")))
    res.set_cookie("session", "", domain="challenge.com")
    return res

@auth_bp.route('/admin/csrf-token', methods=['GET'])
def get_csrf_token():
    """Generate and return a CSRF token for the current user"""
    user_id = decodeCookie(request.cookies.get("session")).get('user_id')
    if user_id != 1:
        return jsonify({
            "error": "Unauthorized",
            "message": "Only admin can request CSRF tokens"
        }), 403
    
    csrf_token = generate_csrf_token(user_id)
    return jsonify({"csrf_token": csrf_token}), 200

@auth_bp.route('/admin/mcp/add-server', methods=['POST'])
def admin_add_mcp_server():
    user_id = decodeCookie(request.cookies.get("session")).get('user_id')
    print("[Admin] Received admin add-server request from user_id:", user_id, file=sys.stderr)
    if user_id != 1:
        return jsonify({
            "error": "Unauthorized",
            "message": "Invalid admin token"
        }), 403

    # Validate CSRF token
    csrf_token = request.json.get('csrf_token')
    if not validate_csrf_token(csrf_token, user_id):
        return jsonify({
            "error": "Forbidden",
            "message": "Invalid or missing CSRF token"
        }), 403

    mcp_name = request.json.get("name")
    mcp_url = request.json.get("url")

    try:
        response = requests.post(f"{MCP_PROXY_URL}/admin/add-server", json={"name": mcp_name, "url": mcp_url}, cookies={"token": request.cookies.get("session")})
        if response.status_code == 201:
            return jsonify({"message": "Server added successfully"}), 200
        else:
            return jsonify({
                "error": "Failed to add server",
                "message": response.json().get("message", "Unknown error")
            }), response.status_code
    except Exception as e:
        return jsonify({
            "error": "Internal Server Error",
            "message": str(e)
        }), 500