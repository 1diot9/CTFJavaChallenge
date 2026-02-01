from os import environ

class Config:
    SERVER_NAME = f'challenge.com:{environ.get("CHALL_PORT", 6001)}'
    DATABASE = 'users.db'