"""
Database layer for the Hospital Management System (Streamlit edition).

The original NetBeans/JavaFX project used MySQL. This version uses SQLite so the
app is fully self-contained and can be deployed for free (e.g. Streamlit
Community Cloud) with a single shareable link and no external database server.

Schema mirrors the original tables:
    reg          -> user accounts / authentication
    patient      -> meetings scheduled by the receptionist
    appointment  -> appointment requests booked by patients
    prescription -> prescriptions written by doctors
"""

import hashlib
import os
import sqlite3
from contextlib import contextmanager

DB_PATH = os.path.join(os.path.dirname(__file__), "hospital.db")

# Doctors offered throughout the app (matches the original combo boxes).
DOCTORS = ["Dr. Sara Ahmed", "Dr. Martin Cole", "Dr. Roza Khan", "Dr. Josef Novak"]
ROLES = ["Patient", "Admin", "Receptionist", "Doctor"]


def hash_password(password: str) -> str:
    """Hash a password with SHA-256.

    The original Java app stored passwords in plain text. Hashing here is a
    deliberate security improvement worth calling out in interviews.
    """
    return hashlib.sha256(password.encode("utf-8")).hexdigest()


@contextmanager
def get_conn():
    conn = sqlite3.connect(DB_PATH, check_same_thread=False)
    conn.row_factory = sqlite3.Row
    try:
        yield conn
        conn.commit()
    finally:
        conn.close()


def init_db():
    """Create tables and seed demo data once."""
    with get_conn() as conn:
        c = conn.cursor()
        c.executescript(
            """
            CREATE TABLE IF NOT EXISTS reg (
                user_id   INTEGER PRIMARY KEY AUTOINCREMENT,
                name      TEXT NOT NULL,
                userName  TEXT,
                email     TEXT,
                passw     TEXT NOT NULL,
                userType  TEXT NOT NULL
            );

            CREATE TABLE IF NOT EXISTS patient (
                user_id      INTEGER PRIMARY KEY AUTOINCREMENT,
                name         TEXT NOT NULL,
                phone        TEXT,
                doctor       TEXT,
                mettingDate  TEXT,
                mettingTime  TEXT
            );

            CREATE TABLE IF NOT EXISTS appointment (
                user_id      INTEGER PRIMARY KEY AUTOINCREMENT,
                name         TEXT NOT NULL,
                Pphone       TEXT,
                Pdoctor      TEXT,
                mettingDate  TEXT
            );

            CREATE TABLE IF NOT EXISTS prescription (
                ID       INTEGER PRIMARY KEY AUTOINCREMENT,
                bill     TEXT,
                name     TEXT NOT NULL,
                disease  TEXT,
                syptoms  TEXT,
                drug     TEXT
            );
            """
        )

        # Seed only if the accounts table is empty.
        c.execute("SELECT COUNT(*) FROM reg")
        if c.fetchone()[0] == 0:
            _seed(c)


def _seed(c):
    users = [
        # name,          userName,    email,                    password,       role
        ("admin",        "admin",     "admin@hospital.io",      "admin123",     "Admin"),
        ("Olivia Reed",  "olivia",    "olivia@hospital.io",     "reception123", "Receptionist"),
        ("Dr. Sara Ahmed", "sara",    "sara@hospital.io",       "doctor123",    "Doctor"),
        ("Dr. Martin Cole", "martin", "martin@hospital.io",     "doctor123",    "Doctor"),
        ("John Carter",  "john",      "john@example.com",       "patient123",   "Patient"),
        ("Maria Lopez",  "maria",     "maria@example.com",      "patient123",   "Patient"),
    ]
    c.executemany(
        "INSERT INTO reg (name, userName, email, passw, userType) VALUES (?,?,?,?,?)",
        [(n, u, e, hash_password(p), t) for (n, u, e, p, t) in users],
    )

    patients = [
        ("John Carter", "0788-114-220", "Dr. Sara Ahmed",  "2026-07-02", "09:30"),
        ("Maria Lopez", "0788-553-119", "Dr. Martin Cole", "2026-07-02", "11:00"),
        ("Liam Walsh",  "0788-441-882", "Dr. Roza Khan",   "2026-07-03", "14:15"),
        ("Emma Stone",  "0788-907-334", "Dr. Josef Novak", "2026-07-04", "10:00"),
    ]
    c.executemany(
        "INSERT INTO patient (name, phone, doctor, mettingDate, mettingTime) VALUES (?,?,?,?,?)",
        patients,
    )

    appointments = [
        ("John Carter", "0788-114-220", "Dr. Sara Ahmed",  "2026-07-05"),
        ("Maria Lopez", "0788-553-119", "Dr. Sara Ahmed",  "2026-07-06"),
        ("Noah Patel",  "0788-220-665", "Dr. Martin Cole", "2026-07-07"),
    ]
    c.executemany(
        "INSERT INTO appointment (name, Pphone, Pdoctor, mettingDate) VALUES (?,?,?,?)",
        appointments,
    )

    prescriptions = [
        ("$45",  "John Carter", "Seasonal flu",   "Fever, sore throat",        "Paracetamol 500mg, twice daily for 5 days"),
        ("$80",  "Maria Lopez", "Hypertension",   "Headache, dizziness",       "Amlodipine 5mg, once daily; reduce sodium intake"),
        ("$120", "Liam Walsh",  "Type 2 diabetes","Fatigue, increased thirst", "Metformin 500mg, twice daily with meals"),
    ]
    c.executemany(
        "INSERT INTO prescription (bill, name, disease, syptoms, drug) VALUES (?,?,?,?,?)",
        prescriptions,
    )


# ---------------------------------------------------------------------------
# Auth
# ---------------------------------------------------------------------------
def authenticate(name: str, password: str, user_type: str):
    with get_conn() as conn:
        row = conn.execute(
            "SELECT * FROM reg WHERE name = ? AND passw = ? AND userType = ?",
            (name, hash_password(password), user_type),
        ).fetchone()
        return dict(row) if row else None


def register_user(name, username, email, password, user_type):
    with get_conn() as conn:
        conn.execute(
            "INSERT INTO reg (name, userName, email, passw, userType) VALUES (?,?,?,?,?)",
            (name, username, email, hash_password(password), user_type),
        )


def name_exists(name: str) -> bool:
    with get_conn() as conn:
        return conn.execute("SELECT 1 FROM reg WHERE name = ?", (name,)).fetchone() is not None


# ---------------------------------------------------------------------------
# Generic query helpers
# ---------------------------------------------------------------------------
def query(sql, params=()):
    with get_conn() as conn:
        return [dict(r) for r in conn.execute(sql, params).fetchall()]


def execute(sql, params=()):
    with get_conn() as conn:
        cur = conn.execute(sql, params)
        return cur.lastrowid


def count(table, where="", params=()):
    sql = f"SELECT COUNT(*) AS n FROM {table}"
    if where:
        sql += f" WHERE {where}"
    with get_conn() as conn:
        return conn.execute(sql, params).fetchone()["n"]
