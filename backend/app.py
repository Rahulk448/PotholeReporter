from flask import Flask, jsonify, request
from flask_cors import CORS
import os
import psycopg2
from psycopg2.extras import RealDictCursor
from datetime import datetime

app = Flask(__name__)
CORS(app)

DATABASE_URL = os.getenv("DATABASE_URL")


def get_db_connection():
    return psycopg2.connect(DATABASE_URL, sslmode="require")


# -----------------------
# DATABASE INIT
# -----------------------
def init_db():
    conn = get_db_connection()
    cur = conn.cursor()

    cur.execute("""
        CREATE TABLE IF NOT EXISTS users (
            id SERIAL PRIMARY KEY,
            name TEXT,
            email TEXT UNIQUE,
            password TEXT,
            role TEXT DEFAULT 'user'
        )
    """)

    cur.execute("""
        CREATE TABLE IF NOT EXISTS issues (
            id SERIAL PRIMARY KEY,
            title TEXT,
            description TEXT,
            latitude DOUBLE PRECISION,
            longitude DOUBLE PRECISION,
            issue_type TEXT,
            assigned_department TEXT,
            image_url TEXT,
            user_email TEXT,
            status TEXT DEFAULT 'pending',
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
    """)

    conn.commit()
    cur.close()
    conn.close()


@app.route("/")
def home():
    return jsonify({
        "status": "Backend running",
        "message": "Road Guardian API active"
    })


# -----------------------
# USER REGISTRATION
# -----------------------
@app.route("/register", methods=["POST"])
def register():
    data = request.get_json()

    name = data.get("name")
    email = data.get("email")
    password = data.get("password")

    if not name or not email or not password:
        return jsonify({"status": "error", "message": "Missing fields"}), 400

    conn = get_db_connection()
    cur = conn.cursor()

    try:
        cur.execute(
            "INSERT INTO users (name,email,password,role) VALUES (%s,%s,%s,%s)",
            (name, email, password, "user")
        )
        conn.commit()

        message = "User registered successfully"

    except:
        message = "User already exists"

    cur.close()
    conn.close()

    return jsonify({"status": "success", "message": message})


# -----------------------
# LOGIN
# -----------------------
@app.route("/login", methods=["POST"])
def login():

    data = request.get_json()

    email = data.get("email")
    password = data.get("password")

    conn = get_db_connection()
    cur = conn.cursor(cursor_factory=RealDictCursor)

    cur.execute(
        "SELECT * FROM users WHERE email=%s AND password=%s",
        (email, password)
    )

    user = cur.fetchone()

    cur.close()
    conn.close()

    if user:
        return jsonify({
            "status": "success",
            "role": user["role"],
            "email": user["email"],
            "message": "Login successful"
        })

    return jsonify({
        "status": "error",
        "message": "Invalid credentials"
    }), 401


# -----------------------
# FORGOT PASSWORD
# -----------------------
@app.route("/forgot-password", methods=["POST"])
def forgot_password():

    data = request.get_json()

    email = data.get("email")
    new_password = data.get("new_password")

    if not email or not new_password:
        return jsonify({"status": "error", "message": "Missing fields"}), 400

    conn = get_db_connection()
    cur = conn.cursor()

    cur.execute(
        "UPDATE users SET password=%s WHERE email=%s",
        (new_password, email)
    )

    conn.commit()

    cur.close()
    conn.close()

    return jsonify({
        "status": "success",
        "message": "Password updated successfully"
    })


# -----------------------
# REPORT ISSUE
# -----------------------
@app.route("/report-issue", methods=["POST"])
def report_issue():

    data = request.get_json()

    title = data.get("title")
    description = data.get("description")
    latitude = data.get("latitude")
    longitude = data.get("longitude")
    issue_type = data.get("issue_type")
    image_url = data.get("image_url")
    user_email = data.get("user_email")

    if not title or not description:
        return jsonify({"status": "error", "message": "Missing fields"}), 400

    # Department assignment
    if issue_type == "traffic_block":
        department = "traffic_police"

    elif issue_type == "pothole":
        department = "pwd"

    elif issue_type == "vehicle_breakdown":
        department = "service_provider"

    else:
        department = "general"

    conn = get_db_connection()
    cur = conn.cursor()

    cur.execute("""
        INSERT INTO issues
        (title,description,latitude,longitude,issue_type,assigned_department,image_url,user_email,status,created_at)
        VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)
    """, (
        title,
        description,
        latitude,
        longitude,
        issue_type,
        department,
        image_url,
        user_email,
        "pending",
        datetime.now()
    ))

    conn.commit()

    cur.close()
    conn.close()

    return jsonify({
        "status": "success",
        "message": "Issue reported successfully"
    })


# -----------------------
# GET ALL ISSUES
# -----------------------
@app.route("/issues", methods=["GET"])
def get_issues():

    department = request.args.get("department")

    conn = get_db_connection()
    cur = conn.cursor(cursor_factory=RealDictCursor)

    if department:
        cur.execute(
            "SELECT * FROM issues WHERE assigned_department=%s",
            (department,)
        )
    else:
        cur.execute("SELECT * FROM issues")

    issues = cur.fetchall()

    cur.close()
    conn.close()

    return jsonify(issues)


# -----------------------
# GET USER ISSUES (VIEW STATUS)
# -----------------------
@app.route("/user-issues/<path:email>", methods=["GET"])
def get_user_issues(email):

    conn = get_db_connection()
    cur = conn.cursor(cursor_factory=RealDictCursor)

    cur.execute(
        "SELECT * FROM issues WHERE user_email=%s",
        (email,)
    )

    issues = cur.fetchall()

    cur.close()
    conn.close()

    return jsonify(issues)


# -----------------------
# UPDATE ISSUE STATUS
# -----------------------
@app.route("/update-status/<int:issue_id>", methods=["PUT"])
def update_status(issue_id):

    data = request.get_json()
    new_status = data.get("status")

    if not new_status:
        return jsonify({"status": "error", "message": "Status required"}), 400

    conn = get_db_connection()
    cur = conn.cursor()

    cur.execute(
        "UPDATE issues SET status=%s WHERE id=%s",
        (new_status, issue_id)
    )

    conn.commit()

    cur.close()
    conn.close()

    return jsonify({
        "status": "success",
        "message": "Issue status updated"
    })


if __name__ == "__main__":
    init_db()
    app.run(debug=True)