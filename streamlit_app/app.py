"""
Hospital Management System — Streamlit edition.

A web rebuild of a JavaFX + MySQL desktop project. Four roles (Admin,
Receptionist, Doctor, Patient) each get a tailored dashboard. Backed by SQLite
so it deploys for free with a single shareable link.

Run locally:   streamlit run streamlit_app/app.py
"""

import pandas as pd
import streamlit as st

import db

st.set_page_config(
    page_title="Hospital Management System",
    page_icon="🏥",
    layout="wide",
    initial_sidebar_state="expanded",
)

db.init_db()

# ---------------------------------------------------------------------------
# Styling
# ---------------------------------------------------------------------------
CSS = """
<style>
    .stApp { background: #f5f8fb; }
    section[data-testid="stSidebar"] { background: #0f2a43; }
    section[data-testid="stSidebar"] * { color: #e8eef5 !important; }

    .hero {
        background: linear-gradient(120deg, #0f6e8c 0%, #14a098 100%);
        padding: 2.2rem 2.4rem; border-radius: 18px; color: #fff;
        margin-bottom: 1.6rem;
        box-shadow: 0 10px 30px rgba(15,110,140,0.25);
    }
    .hero h1 { color:#fff; margin:0; font-size:2rem; font-weight:700; }
    .hero p  { color:#e3f4f2; margin:.4rem 0 0; font-size:1.02rem; }

    .metric-card {
        background:#fff; border-radius:14px; padding:1.1rem 1.3rem;
        border:1px solid #e6edf4; box-shadow:0 4px 14px rgba(20,40,70,.05);
        height:100%;
    }
    .metric-card .label { color:#5b6b7d; font-size:.82rem; text-transform:uppercase; letter-spacing:.06em; }
    .metric-card .value { color:#0f2a43; font-size:2rem; font-weight:700; line-height:1.1; }
    .metric-card .icon  { font-size:1.4rem; }

    .login-card {
        background:#fff; border-radius:18px; padding:2rem 2.2rem;
        border:1px solid #e6edf4; box-shadow:0 12px 34px rgba(20,40,70,.08);
    }
    .pill {
        display:inline-block; background:#e6f6f4; color:#0f6e8c;
        padding:.18rem .7rem; border-radius:999px; font-size:.78rem; font-weight:600;
    }
    .stButton > button {
        background:#0f6e8c; color:#fff; border:none; border-radius:10px;
        padding:.55rem 1rem; font-weight:600;
    }
    .stButton > button:hover { background:#14a098; color:#fff; }
    #MainMenu, footer { visibility:hidden; }
</style>
"""
st.markdown(CSS, unsafe_allow_html=True)


# ---------------------------------------------------------------------------
# Small UI helpers
# ---------------------------------------------------------------------------
def hero(title, subtitle):
    st.markdown(
        f'<div class="hero"><h1>{title}</h1><p>{subtitle}</p></div>',
        unsafe_allow_html=True,
    )


def metric_card(col, label, value, icon):
    col.markdown(
        f"""<div class="metric-card">
                <div class="icon">{icon}</div>
                <div class="value">{value}</div>
                <div class="label">{label}</div>
            </div>""",
        unsafe_allow_html=True,
    )


def df(rows, rename=None):
    frame = pd.DataFrame(rows)
    if rename and not frame.empty:
        frame = frame.rename(columns=rename)
    return frame


def show_table(rows, rename=None):
    st.dataframe(df(rows, rename), width="stretch", hide_index=True)


# ---------------------------------------------------------------------------
# Authentication screen
# ---------------------------------------------------------------------------
def login_screen():
    st.markdown(
        '<div class="hero"><h1>🏥 Hospital Management System</h1>'
        "<p>Role-based hospital operations platform — appointments, prescriptions, "
        "staff and patient management. Web rebuild of a JavaFX + MySQL project.</p></div>",
        unsafe_allow_html=True,
    )

    left, right = st.columns([1.1, 1])

    with left:
        st.markdown('<div class="login-card">', unsafe_allow_html=True)
        tab_login, tab_register = st.tabs(["🔐  Sign in", "📝  Register"])

        with tab_login:
            name = st.text_input("Full name", key="login_name", placeholder="e.g. admin")
            password = st.text_input("Password", type="password", key="login_pw")
            role = st.selectbox("Role", db.ROLES, key="login_role")
            if st.button("Sign in", width="stretch"):
                user = db.authenticate(name.strip(), password, role)
                if user:
                    st.session_state.user = user
                    st.rerun()
                else:
                    st.error("Invalid credentials for the selected role.")

        with tab_register:
            r_name = st.text_input("Full name", key="reg_name")
            r_user = st.text_input("Username", key="reg_user")
            r_email = st.text_input("Email", key="reg_email")
            r_pw = st.text_input("Password", type="password", key="reg_pw")
            r_role = st.selectbox("Register as", db.ROLES, key="reg_role")
            if st.button("Create account", width="stretch"):
                if not (r_name and r_pw):
                    st.warning("Name and password are required.")
                elif db.name_exists(r_name.strip()):
                    st.warning("That name is already registered.")
                else:
                    db.register_user(r_name.strip(), r_user.strip(), r_email.strip(), r_pw, r_role)
                    st.success("Account created — switch to the Sign in tab to log in.")
        st.markdown("</div>", unsafe_allow_html=True)

    with right:
        st.markdown("#### 👋 Try the live demo")
        st.caption("Use any account below to explore that role. Passwords are hashed (SHA-256).")
        st.table(
            pd.DataFrame(
                [
                    ["Admin", "admin", "admin123"],
                    ["Receptionist", "Olivia Reed", "reception123"],
                    ["Doctor", "Dr. Sara Ahmed", "doctor123"],
                    ["Patient", "John Carter", "patient123"],
                ],
                columns=["Role", "Name", "Password"],
            )
        )
        st.info("Tip: pick the matching **Role** in the dropdown before signing in.")


# ---------------------------------------------------------------------------
# Role dashboards
# ---------------------------------------------------------------------------
def admin_dashboard():
    hero("Admin Console", "Hospital-wide overview and staff management.")

    c1, c2, c3, c4, c5 = st.columns(5)
    metric_card(c1, "Admins", db.count("reg", "userType='Admin'"), "🛡️")
    metric_card(c2, "Receptionists", db.count("reg", "userType='Receptionist'"), "💁")
    metric_card(c3, "Doctors", db.count("reg", "userType='Doctor'"), "🩺")
    metric_card(c4, "Appointments", db.count("appointment"), "📅")
    metric_card(c5, "Patients", db.count("patient"), "🧑‍⚕️")

    st.markdown("### 👥 Manage users")
    users = db.query("SELECT user_id, name, userName, email, userType FROM reg ORDER BY user_id")
    st.dataframe(
        df(users, {"user_id": "ID", "name": "Name", "userName": "Username",
                   "email": "Email", "userType": "Role"}),
        width="stretch", hide_index=True,
    )

    col_del, col_chart = st.columns([1, 1.4])
    with col_del:
        st.markdown("#### Remove a user")
        options = {f"#{u['user_id']} — {u['name']} ({u['userType']})": u["user_id"] for u in users}
        if options:
            target = st.selectbox("Select user", list(options.keys()))
            if st.button("Delete user"):
                db.execute("DELETE FROM reg WHERE user_id=?", (options[target],))
                st.success("User deleted.")
                st.rerun()
    with col_chart:
        st.markdown("#### Staff distribution")
        counts = db.query("SELECT userType AS role, COUNT(*) AS n FROM reg GROUP BY userType")
        chart = df(counts).set_index("role") if counts else pd.DataFrame()
        if not chart.empty:
            st.bar_chart(chart)


def receptionist_dashboard():
    hero("Reception Desk", "Schedule patient meetings and review appointment requests.")

    c1, c2, c3 = st.columns(3)
    metric_card(c1, "Scheduled meetings", db.count("patient"), "📋")
    metric_card(c2, "Appointment requests", db.count("appointment"), "📨")
    metric_card(c3, "Doctors on staff", db.count("reg", "userType='Doctor'"), "🩺")

    tab_sched, tab_req = st.tabs(["🗓️ Scheduled meetings", "📨 Appointment requests"])

    with tab_sched:
        st.markdown("#### Add / update a meeting")
        with st.form("add_meeting", clear_on_submit=True):
            a, b, c = st.columns(3)
            name = a.text_input("Patient name")
            phone = b.text_input("Phone")
            doctor = c.selectbox("Doctor", db.DOCTORS)
            d, e = st.columns(2)
            mdate = d.date_input("Meeting date")
            mtime = e.time_input("Meeting time")
            if st.form_submit_button("Schedule meeting"):
                if not name:
                    st.warning("Patient name is required.")
                else:
                    db.execute(
                        "INSERT INTO patient (name, phone, doctor, mettingDate, mettingTime) VALUES (?,?,?,?,?)",
                        (name, phone, doctor, str(mdate), mtime.strftime("%H:%M")),
                    )
                    st.success("Meeting scheduled.")
                    st.rerun()

        rows = db.query("SELECT * FROM patient ORDER BY user_id")
        st.dataframe(
            df(rows, {"user_id": "ID", "name": "Patient", "phone": "Phone",
                      "doctor": "Doctor", "mettingDate": "Date", "mettingTime": "Time"}),
            width="stretch", hide_index=True,
        )
        if rows:
            opts = {f"#{r['user_id']} — {r['name']}": r["user_id"] for r in rows}
            col1, col2 = st.columns([2, 1])
            target = col1.selectbox("Select meeting to delete", list(opts.keys()))
            if col2.button("Delete meeting"):
                db.execute("DELETE FROM patient WHERE user_id=?", (opts[target],))
                st.success("Meeting deleted.")
                st.rerun()

    with tab_req:
        reqs = db.query("SELECT * FROM appointment ORDER BY user_id")
        st.dataframe(
            df(reqs, {"user_id": "ID", "name": "Patient", "Pphone": "Phone",
                      "Pdoctor": "Doctor", "mettingDate": "Requested date"}),
            width="stretch", hide_index=True,
        )


def doctor_dashboard():
    user = st.session_state.user
    hero(f"Welcome, {user['name']}", "Review appointments and issue prescriptions.")

    c1, c2, c3 = st.columns(3)
    metric_card(c1, "My appointments",
                db.count("appointment", "Pdoctor=?", (user["name"],)), "📅")
    metric_card(c2, "Prescriptions issued", db.count("prescription"), "💊")
    metric_card(c3, "Patients in system", db.count("patient"), "🧑‍⚕️")

    tab_appt, tab_new, tab_pre = st.tabs(
        ["📅 My appointments", "✍️ New prescription", "💊 All prescriptions"]
    )

    with tab_appt:
        appts = db.query("SELECT * FROM appointment WHERE Pdoctor=? ORDER BY mettingDate", (user["name"],))
        if appts:
            st.dataframe(
                df(appts, {"user_id": "ID", "name": "Patient", "Pphone": "Phone",
                           "Pdoctor": "Doctor", "mettingDate": "Date"}),
                width="stretch", hide_index=True,
            )
        else:
            st.info("No appointments are currently booked with you.")

    with tab_new:
        with st.form("new_pre", clear_on_submit=True):
            a, b = st.columns(2)
            pname = a.text_input("Patient name")
            bill = b.text_input("Bill", placeholder="$50")
            disease = st.text_input("Diagnosis / disease")
            symptoms = st.text_area("Symptoms")
            drug = st.text_area("Prescribed drugs & dosage")
            if st.form_submit_button("Save prescription"):
                if not pname:
                    st.warning("Patient name is required.")
                else:
                    db.execute(
                        "INSERT INTO prescription (bill, name, disease, syptoms, drug) VALUES (?,?,?,?,?)",
                        (bill, pname, disease, symptoms, drug),
                    )
                    st.success("Prescription saved.")
                    st.rerun()

    with tab_pre:
        pres = db.query("SELECT * FROM prescription ORDER BY ID DESC")
        st.dataframe(
            df(pres, {"ID": "ID", "bill": "Bill", "name": "Patient",
                      "disease": "Diagnosis", "syptoms": "Symptoms", "drug": "Prescription"}),
            width="stretch", hide_index=True,
        )


def patient_dashboard():
    user = st.session_state.user
    hero(f"Hello, {user['name']}", "Book appointments, meet our doctors, and view your prescriptions.")

    tab_book, tab_docs, tab_pre = st.tabs(
        ["📅 Book appointment", "🩺 Our doctors", "💊 My prescriptions"]
    )

    with tab_book:
        st.markdown("#### Request an appointment")
        with st.form("book", clear_on_submit=True):
            a, b = st.columns(2)
            name = a.text_input("Your name", value=user["name"])
            phone = b.text_input("Phone")
            c, d = st.columns(2)
            doctor = c.selectbox("Doctor", db.DOCTORS)
            mdate = d.date_input("Preferred date")
            if st.form_submit_button("Request appointment"):
                if not name:
                    st.warning("Name is required.")
                else:
                    db.execute(
                        "INSERT INTO appointment (name, Pphone, Pdoctor, mettingDate) VALUES (?,?,?,?)",
                        (name, phone, doctor, str(mdate)),
                    )
                    st.success("Appointment requested! Reception will confirm shortly.")

        mine = db.query("SELECT * FROM appointment WHERE name=? ORDER BY mettingDate", (user["name"],))
        if mine:
            st.markdown("##### Your requests")
            st.dataframe(
                df(mine, {"user_id": "ID", "name": "Patient", "Pphone": "Phone",
                          "Pdoctor": "Doctor", "mettingDate": "Date"}),
                width="stretch", hide_index=True,
            )

    with tab_docs:
        st.markdown("#### Meet our specialists")
        cols = st.columns(2)
        blurbs = [
            "General Medicine · 12 yrs experience",
            "Cardiology · 9 yrs experience",
            "Pediatrics · 7 yrs experience",
            "Orthopedics · 15 yrs experience",
        ]
        for i, doc in enumerate(db.DOCTORS):
            with cols[i % 2]:
                st.markdown(
                    f"""<div class="metric-card" style="margin-bottom:1rem;">
                            <div class="icon">🩺</div>
                            <div class="value" style="font-size:1.2rem;">{doc}</div>
                            <div class="label">{blurbs[i]}</div>
                        </div>""",
                    unsafe_allow_html=True,
                )

    with tab_pre:
        mine = db.query("SELECT * FROM prescription WHERE name=? ORDER BY ID DESC", (user["name"],))
        if mine:
            for p in mine:
                with st.expander(f"💊 {p['disease']} — bill {p['bill']}"):
                    st.write(f"**Symptoms:** {p['syptoms']}")
                    st.write(f"**Prescription:** {p['drug']}")
        else:
            st.info("You have no prescriptions on file yet.")


# ---------------------------------------------------------------------------
# Router
# ---------------------------------------------------------------------------
def main():
    if "user" not in st.session_state:
        login_screen()
        return

    user = st.session_state.user
    with st.sidebar:
        st.markdown("## 🏥 HMS")
        st.markdown(f"**{user['name']}**")
        st.markdown(f'<span class="pill">{user["userType"]}</span>', unsafe_allow_html=True)
        st.markdown("---")
        if st.button("🚪 Sign out", width="stretch"):
            del st.session_state.user
            st.rerun()
        st.markdown("---")
        st.caption("Web rebuild of a JavaFX + MySQL desktop project.\nBuilt with Streamlit + SQLite.")

    role = user["userType"]
    if role == "Admin":
        admin_dashboard()
    elif role == "Receptionist":
        receptionist_dashboard()
    elif role == "Doctor":
        doctor_dashboard()
    else:
        patient_dashboard()


if __name__ == "__main__":
    main()
