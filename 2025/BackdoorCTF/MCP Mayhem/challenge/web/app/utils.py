import sqlite3
import jwt
from os import environ
import secrets
import hashlib
import time

DATABASE = 'users.db'

def query_db(query, args=(), one=False):
    with sqlite3.connect(DATABASE) as conn:
        cursor = conn.cursor()
        cursor.execute(query, args)
        result = cursor.fetchall()
        conn.commit()
    return (result[0] if result else None) if one else result

SECRET = environ.get("SECRET", "default_secret_key")
CSRF_SECRET = environ.get("CSRF_SECRET", "csrf_secret_key")

def generateCookie(user_id: int):
    cookie = jwt.encode({"user_id": user_id}, SECRET, algorithm="HS256")
    return cookie

def decodeCookie(cookie: str):
    try:
        decoded_cookie = jwt.decode(cookie, SECRET, algorithms=["HS256"])
        return decoded_cookie
    except jwt.DecodeError:
        return {"user_id": 0}

def generate_csrf_token(user_id: int):
    """Generate a CSRF token for the given user"""
    timestamp = str(int(time.time()))
    token_data = f"{user_id}:{timestamp}:{secrets.token_urlsafe(32)}"
    token = jwt.encode({"data": token_data, "exp": int(time.time()) + 3600}, CSRF_SECRET, algorithm="HS256")
    return token

def validate_csrf_token(token: str, user_id: int):
    """Validate a CSRF token for the given user"""
    if not token:
        return False
    try:
        decoded = jwt.decode(token, CSRF_SECRET, algorithms=["HS256"])
        token_data = decoded.get("data", "")
        token_user_id = int(token_data.split(":")[0])
        return token_user_id == user_id
    except (jwt.DecodeError, jwt.ExpiredSignatureError, ValueError, IndexError):
        return False

