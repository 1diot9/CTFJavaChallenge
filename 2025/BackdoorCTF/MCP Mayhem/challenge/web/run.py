from os import urandom, environ
from app import create_app
import sqlite3

import sqlite3

DATABASE = 'users.db'

def init_db():
    with sqlite3.connect(DATABASE) as conn:
        cursor = conn.cursor()
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                description TEXT
            )
        ''')
        cursor.execute(f'''INSERT OR IGNORE INTO users (username, password, description) VALUES ('admin', '{urandom(16).hex()}', 'I am admin')''')
        conn.commit()

app = create_app()

if __name__ == '__main__':
    init_db()
    app.run(host="0.0.0.0", port=environ.get("CHALL_PORT", 6001), debug=True)