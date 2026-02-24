from flask import Flask, jsonify, request
from flask_cors import CORS
import os
import psycopg2
from psycopg2.extras import RealDictCursor

app = Flask(__name__)
CORS(app)

# Render provides DATABASE_URL automatically
DATABASE_URL = os.getenv("DATABASE_URL")


def get_db_connection():
    return psycopg2.connect(DATABASE_URL, sslmode="require")


# Initialize database tables
def init_db():
    conn = get_db_connection()
    cur = conn.cursor()

    cur.execute("""
        CREATE TABLE IF NOT EXISTS users (
            id SERIAL PRIMARY KEY,
            name TEXT,
            email TEXT UNIQUE,
            password TEXT,
            role TEXT
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
            status TEXT
        )
    """)

    conn.commit()
    cur.close()
    conn.close()


@app.route("/")
def home():
    return jsonify({
        "status": "Backend running",
        "message": "PostgreSQL backend ready"
    })


@app.route("/create-test-user")
def create_test_user():
    conn = get_db_connection()
    cur = conn.cursor()

    try:
        cur.execute(
            "INSERT INTO users (name, email, password, role) VALUES (%s, %s, %s, %s)",
            ("Admin", "admin@gmail.com", "admin123", "admin")
        )
        conn.commit()
        message = "Test user created"
    except:
        message = "User already exists"

    cur.close()
    conn.close()

    return jsonify({"message": message})


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
    issue_type = data.get("issue_type")

    if not title or not description or latitude is None or longitude is None or not issue_type:
        return jsonify({
            "status": "error",
            "message": "Missing required fields"
        }), 400

    # Auto assign department
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
        INSERT INTO issues (title, description, latitude, longitude, issue_type, assigned_department, status)
        VALUES (%s, %s, %s, %s, %s, %s, %s)
    """, (title, description, latitude, longitude, issue_type, department, "pending"))

    conn.commit()
    cur.close()
    conn.close()

    return jsonify({
        "status": "success",
        "message": "Issue reported successfully"
    })


@app.route("/issues", methods=["GET"])
def get_issues():
    department = request.args.get("department")

    conn = get_db_connection()
    cur = conn.cursor(cursor_factory=RealDictCursor)

    if department:
        cur.execute("SELECT * FROM issues WHERE assigned_department=%s", (department,))
    else:
        cur.execute("SELECT * FROM issues")

    issues = cur.fetchall()

    cur.close()
    conn.close()

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