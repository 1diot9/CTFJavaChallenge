import jwt
from os import environ

SECRET = environ.get("SECRET", "default_secret_key")

def generateCookie(user_id: int):
    cookie = jwt.encode({"user_id": user_id}, SECRET, algorithm="HS256")
    return cookie

def decodeCookie(cookie: str):
    try:
        decoded_cookie = jwt.decode(cookie, SECRET, algorithms=["HS256"])
        return decoded_cookie
    except jwt.DecodeError:
        return {"user_id": 0}

