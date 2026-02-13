from flask import Flask, jsonify, request
from flask_cors import CORS
import sqlite3

app = Flask(__name__)
CORS(app)

DATABASE = "pothole.db"

def get_db_connection():
    conn = sqlite3.connect(DATABASE)
    conn.row_factory = sqlite3.Row
    return conn

def init_db():
    conn = get_db_connection()
    cursor = conn.cursor()

    cursor.execute("""
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT,
            email TEXT UNIQUE,
            password TEXT,
            role TEXT
        )
    """)

    cursor.execute("""
        CREATE TABLE IF NOT EXISTS issues (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            title TEXT,
            description TEXT,
            latitude REAL,
            longitude REAL,
            status TEXT
        )
    """)

    conn.commit()
    conn.close()

@app.route("/")
def home():
    return jsonify({
        "status": "Backend running",
        "message": "SQLite backend ready"
    })

@app.route("/create-test-user")
def create_test_user():
    conn = get_db_connection()
    cursor = conn.cursor()

    try:
        cursor.execute(
            "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)",
            ("Admin", "admin@gmail.com", "admin123", "admin")
        )
        conn.commit()
        message = "Test user created"
    except:
        message = "User already exists"

    conn.close()
    return jsonify({"message": message})

@app.route("/login", methods=["POST"])
def login():
    data = request.get_json()
    email = data.get("email")
    password = data.get("password")

    conn = get_db_connection()
    cursor = conn.cursor()

    cursor.execute("SELECT * FROM users WHERE email=? AND password=?", (email, password))
    user = cursor.fetchone()
    conn.close()

    if user:
        return jsonify({
            "status": "success",
            "role": user["role"],
            "message": "Login successful"
        })

    return jsonify({
        "status": "error",
        "message": "Invalid credentials"
    }), 401

@app.route("/report-issue", methods=["POST"])
def report_issue():
    data = request.get_json()

    title = data.get("title")
    description = data.get("description")
    latitude = data.get("latitude")
    longitude = data.get("longitude")

    if not title or not description or latitude is None or longitude is None:
        return jsonify({
            "status": "error",
            "message": "Missing required fields"
        }), 400

    conn = get_db_connection()
    cursor = conn.cursor()

    cursor.execute("""
        INSERT INTO issues (title, description, latitude, longitude, status)
        VALUES (?, ?, ?, ?, ?)
    """, (title, description, latitude, longitude, "pending"))

    conn.commit()
    conn.close()

    return jsonify({
        "status": "success",
        "message": "Issue reported successfully"
    })

@app.route("/issues", methods=["GET"])
def get_issues():
    conn = get_db_connection()
    cursor = conn.cursor()

    cursor.execute("SELECT * FROM issues")
    rows = cursor.fetchall()
    conn.close()

    issues = []
    for row in rows:
        issues.append({
            "id": row["id"],
            "title": row["title"],
            "description": row["description"],
            "latitude": row["latitude"],
            "longitude": row["longitude"],
            "status": row["status"]
        })

    return jsonify(issues)

@app.route("/update-status/<int:issue_id>", methods=["PUT"])
def update_status(issue_id):
    data = request.get_json()
    new_status = data.get("status")

    if not new_status:
        return jsonify({
            "status": "error",
            "message": "Status is required"
        }), 400

    conn = get_db_connection()
    cursor = conn.cursor()

    cursor.execute("UPDATE issues SET status=? WHERE id=?", (new_status, issue_id))
    conn.commit()
    conn.close()

    return jsonify({
        "status": "success",
        "message": "Issue status updated"
    })

if __name__ == "__main__":
    init_db()
    app.run(debug=True)
