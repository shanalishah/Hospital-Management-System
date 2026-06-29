"""
Hospital Management System — Streamlit frontend (full-stack).

This frontend holds NO database logic. It authenticates against the FastAPI
backend and performs every read/write over HTTP via the APIClient. Role-based
access control is enforced server-side; the UI simply adapts to the user's role.

Run the backend first (cd backend && uvicorn app.main:app --reload), then:
    streamlit run streamlit_app/app.py
"""

from datetime import datetime

import pandas as pd
import streamlit as st

from api import APIClient, APIError

st.set_page_config(
    page_title="Hospital Management System",
    page_icon="🏥",
    layout="wide",
    initial_sidebar_state="expanded",
)

ROLES = ["Patient", "Admin", "Receptionist", "Doctor"]
STATUSES = ["Requested", "Confirmed", "Cancelled", "Completed"]

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
        margin-bottom: 1.6rem; box-shadow: 0 10px 30px rgba(15,110,140,0.25);
    }
    .hero h1 { color:#fff; margin:0; font-size:2rem; font-weight:700; }
    .hero p  { color:#e3f4f2; margin:.4rem 0 0; font-size:1.02rem; }
    .metric-card {
        background:#fff; border-radius:14px; padding:1.1rem 1.3rem;
        border:1px solid #e6edf4; box-shadow:0 4px 14px rgba(20,40,70,.05); height:100%;
    }
    .metric-card .label { color:#5b6b7d; font-size:.82rem; text-transform:uppercase; letter-spacing:.06em; }
    .metric-card .value { color:#0f2a43; font-size:2rem; font-weight:700; line-height:1.1; }
    .metric-card .icon  { font-size:1.4rem; }
    .login-card {
        background:#fff; border-radius:18px; padding:2rem 2.2rem;
        border:1px solid #e6edf4; box-shadow:0 12px 34px rgba(20,40,70,.08);
    }
    .pill { display:inline-block; background:#e6f6f4; color:#0f6e8c;
            padding:.18rem .7rem; border-radius:999px; font-size:.78rem; font-weight:600; }
    .stButton > button { background:#0f6e8c; color:#fff; border:none; border-radius:10px;
                         padding:.55rem 1rem; font-weight:600; }
    .stButton > button:hover { background:#14a098; color:#fff; }
    #MainMenu, footer { visibility:hidden; }
</style>
"""
st.markdown(CSS, unsafe_allow_html=True)


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------
def client() -> APIClient:
    return APIClient(token=st.session_state.auth["token"])


def hero(title, subtitle):
    st.markdown(f'<div class="hero"><h1>{title}</h1><p>{subtitle}</p></div>', unsafe_allow_html=True)


def metric_card(col, label, value, icon):
    col.markdown(
        f'<div class="metric-card"><div class="icon">{icon}</div>'
        f'<div class="value">{value}</div><div class="label">{label}</div></div>',
        unsafe_allow_html=True,
    )


def fmt_dt(iso):
    try:
        return datetime.fromisoformat(iso).strftime("%Y-%m-%d %H:%M")
    except (TypeError, ValueError):
        return iso


def name_map(users):
    return {u["id"]: u["name"] for u in users}


def handle(api_call):
    """Run an API call, surfacing errors as Streamlit messages. Returns (ok, result)."""
    try:
        return True, api_call()
    except APIError as e:
        if e.status_code == 401:
            st.session_state.pop("auth", None)
            st.warning("Session expired — please sign in again.")
            st.rerun()
        st.error(e.detail)
        return False, None


# ---------------------------------------------------------------------------
# Login
# ---------------------------------------------------------------------------
def login_screen():
    st.markdown(
        '<div class="hero"><h1>🏥 Hospital Management System</h1>'
        "<p>Full-stack hospital platform — a Streamlit frontend over a FastAPI backend "
        "with JWT auth and role-based access control.</p></div>",
        unsafe_allow_html=True,
    )
    left, right = st.columns([1.1, 1])

    with left:
        st.markdown('<div class="login-card">', unsafe_allow_html=True)
        tab_login, tab_register = st.tabs(["🔐  Sign in", "📝  Register"])

        with tab_login:
            username = st.text_input("Username", key="login_user", placeholder="e.g. admin")
            password = st.text_input("Password", type="password", key="login_pw")
            if st.button("Sign in", width="stretch"):
                c = APIClient()
                try:
                    with st.spinner("Signing in… (first request may take ~30s while the free server wakes up)"):
                        data = c.login(username.strip(), password)
                    st.session_state.auth = {
                        "token": c.token, "name": data["name"], "role": data["role"],
                        "username": username.strip(),
                    }
                    st.rerun()
                except APIError as e:
                    st.error(e.detail if e.status_code else
                             "Cannot reach the backend API. Start it with: "
                             "cd backend && uvicorn app.main:app --reload")

        with tab_register:
            r_name = st.text_input("Full name", key="reg_name")
            r_user = st.text_input("Username", key="reg_user")
            r_email = st.text_input("Email", key="reg_email")
            r_pw = st.text_input("Password", type="password", key="reg_pw")
            r_role = st.selectbox("Register as", ROLES, key="reg_role")
            if st.button("Create account", width="stretch"):
                if not (r_name and r_user and r_pw):
                    st.warning("Name, username and password are required.")
                else:
                    try:
                        APIClient().register(r_name.strip(), r_user.strip(), r_email.strip(), r_pw, r_role)
                        st.success("Account created — switch to Sign in.")
                    except APIError as e:
                        st.error(e.detail)
        st.markdown("</div>", unsafe_allow_html=True)

    with right:
        st.markdown("#### 👋 Try the live demo")
        st.caption("Sign in with any account. Auth is via JWT; passwords are bcrypt-hashed server-side.")
        st.table(pd.DataFrame(
            [["Admin", "admin", "admin123"], ["Receptionist", "olivia", "reception123"],
             ["Doctor", "sara", "doctor123"], ["Patient", "john", "patient123"]],
            columns=["Role", "Username", "Password"],
        ))
        st.info("This UI talks to the FastAPI backend — see `/docs` on the API for the Swagger spec.")


# ---------------------------------------------------------------------------
# Dashboards
# ---------------------------------------------------------------------------
def admin_dashboard():
    hero("Admin Console", "Hospital-wide overview and staff management.")
    c = client()
    ok, users = handle(c.users)
    if not ok:
        return
    _, appts = handle(c.appointments)
    _, pres = handle(c.prescriptions)
    appts, pres = appts or [], pres or []

    by_role = {r: sum(1 for u in users if u["role"] == r) for r in ROLES}
    c1, c2, c3, c4, c5 = st.columns(5)
    metric_card(c1, "Admins", by_role["Admin"], "🛡️")
    metric_card(c2, "Receptionists", by_role["Receptionist"], "💁")
    metric_card(c3, "Doctors", by_role["Doctor"], "🩺")
    metric_card(c4, "Patients", by_role["Patient"], "🧑‍⚕️")
    metric_card(c5, "Appointments", len(appts), "📅")

    st.markdown("### 👥 Manage users")
    st.dataframe(
        pd.DataFrame(users)[["id", "name", "username", "email", "role"]]
          .rename(columns={"id": "ID", "name": "Name", "username": "Username",
                           "email": "Email", "role": "Role"}),
        width="stretch", hide_index=True,
    )

    col_del, col_chart = st.columns([1, 1.4])
    with col_del:
        st.markdown("#### Remove a user")
        opts = {f"#{u['id']} — {u['name']} ({u['role']})": u["id"] for u in users}
        target = st.selectbox("Select user", list(opts.keys()))
        if st.button("Delete user"):
            ok, _ = handle(lambda: c.delete_user(opts[target]))
            if ok:
                st.success("User deleted.")
                st.rerun()
    with col_chart:
        st.markdown("#### Staff distribution")
        chart = pd.DataFrame({"role": list(by_role), "count": list(by_role.values())}).set_index("role")
        st.bar_chart(chart)


def receptionist_dashboard():
    hero("Reception Desk", "Manage appointment requests across all doctors.")
    c = client()
    ok, appts = handle(c.appointments)
    if not ok:
        return
    _, doctors = handle(c.doctors)
    _, patients = handle(c.patients)
    dmap, pmap = name_map(doctors or []), name_map(patients or [])

    pending = sum(1 for a in appts if a["status"] == "Requested")
    c1, c2, c3 = st.columns(3)
    metric_card(c1, "Total appointments", len(appts), "📅")
    metric_card(c2, "Pending requests", pending, "📨")
    metric_card(c3, "Doctors on staff", len(doctors or []), "🩺")

    st.markdown("### 🗓️ Appointments")
    if appts:
        rows = [{"ID": a["id"], "Patient": pmap.get(a["patient_id"], a["patient_id"]),
                 "Doctor": dmap.get(a["doctor_id"], a["doctor_id"]),
                 "When": fmt_dt(a["scheduled_at"]), "Reason": a["reason"],
                 "Status": a["status"]} for a in appts]
        st.dataframe(pd.DataFrame(rows), width="stretch", hide_index=True)

        st.markdown("#### Update appointment status")
        col1, col2, col3 = st.columns([2, 1, 1])
        opts = {f"#{a['id']} — {pmap.get(a['patient_id'])} → {dmap.get(a['doctor_id'])}": a["id"] for a in appts}
        target = col1.selectbox("Appointment", list(opts.keys()))
        new_status = col2.selectbox("New status", STATUSES)
        if col3.button("Apply", width="stretch"):
            ok, _ = handle(lambda: c.set_appointment_status(opts[target], new_status))
            if ok:
                st.success("Status updated.")
                st.rerun()
    else:
        st.info("No appointments yet.")


def doctor_dashboard():
    auth = st.session_state.auth
    hero(f"Welcome, {auth['name']}", "Your appointments and prescriptions.")
    c = client()
    _, appts = handle(c.appointments)
    _, pres = handle(c.prescriptions)
    ok, patients = handle(c.patients)
    appts, pres, patients = appts or [], pres or [], patients or []
    pmap = name_map(patients)

    c1, c2, c3 = st.columns(3)
    metric_card(c1, "My appointments", len(appts), "📅")
    metric_card(c2, "Prescriptions issued", len(pres), "💊")
    metric_card(c3, "Patients in system", len(patients), "🧑‍⚕️")

    tab_appt, tab_new, tab_pre = st.tabs(["📅 My appointments", "✍️ New prescription", "💊 My prescriptions"])

    with tab_appt:
        if appts:
            rows = [{"ID": a["id"], "Patient": pmap.get(a["patient_id"], a["patient_id"]),
                     "When": fmt_dt(a["scheduled_at"]), "Reason": a["reason"],
                     "Status": a["status"]} for a in appts]
            st.dataframe(pd.DataFrame(rows), width="stretch", hide_index=True)
        else:
            st.info("No appointments booked with you yet.")

    with tab_new:
        if not patients:
            st.info("No patients registered yet.")
        else:
            popts = {f"{p['name']} (#{p['id']})": p["id"] for p in patients}
            with st.form("new_pre", clear_on_submit=True):
                patient = st.selectbox("Patient", list(popts.keys()))
                a, b = st.columns(2)
                diagnosis = a.text_input("Diagnosis")
                bill = b.number_input("Bill amount", min_value=0.0, step=10.0)
                symptoms = st.text_area("Symptoms")
                medication = st.text_area("Prescribed medication & dosage")
                if st.form_submit_button("Save prescription"):
                    if not diagnosis:
                        st.warning("Diagnosis is required.")
                    else:
                        ok, _ = handle(lambda: c.add_prescription(
                            popts[patient], diagnosis, symptoms, medication, bill))
                        if ok:
                            st.success("Prescription saved.")
                            st.rerun()

    with tab_pre:
        if pres:
            rows = [{"ID": p["id"], "Patient": pmap.get(p["patient_id"], p["patient_id"]),
                     "Diagnosis": p["diagnosis"], "Medication": p["medication"],
                     "Bill": p["bill_amount"]} for p in pres]
            st.dataframe(pd.DataFrame(rows), width="stretch", hide_index=True)
        else:
            st.info("You haven't issued any prescriptions yet.")


def patient_dashboard():
    auth = st.session_state.auth
    hero(f"Hello, {auth['name']}", "Book appointments and view your prescriptions.")
    c = client()
    ok, doctors = handle(c.doctors)
    if not ok:
        return
    dmap = name_map(doctors)

    tab_book, tab_appt, tab_pre = st.tabs(["📅 Book appointment", "🗓️ My appointments", "💊 My prescriptions"])

    with tab_book:
        dopts = {d["name"]: d["id"] for d in doctors}
        with st.form("book", clear_on_submit=True):
            doctor = st.selectbox("Doctor", list(dopts.keys()))
            a, b = st.columns(2)
            d = a.date_input("Preferred date")
            t = b.time_input("Preferred time")
            reason = st.text_input("Reason for visit")
            if st.form_submit_button("Request appointment"):
                scheduled = datetime.combine(d, t).isoformat()
                ok, _ = handle(lambda: c.book_appointment(dopts[doctor], scheduled, reason))
                if ok:
                    st.success("Appointment requested! Reception will confirm shortly.")
                    st.rerun()

    with tab_appt:
        _, appts = handle(c.appointments)
        appts = appts or []
        if appts:
            rows = [{"ID": a["id"], "Doctor": dmap.get(a["doctor_id"], a["doctor_id"]),
                     "When": fmt_dt(a["scheduled_at"]), "Reason": a["reason"],
                     "Status": a["status"]} for a in appts]
            st.dataframe(pd.DataFrame(rows), width="stretch", hide_index=True)
        else:
            st.info("You have no appointments yet.")

    with tab_pre:
        _, pres = handle(c.prescriptions)
        pres = pres or []
        if pres:
            for p in pres:
                with st.expander(f"💊 {p['diagnosis']} — bill ${p['bill_amount']:.0f}"):
                    st.write(f"**Symptoms:** {p['symptoms'] or '—'}")
                    st.write(f"**Medication:** {p['medication'] or '—'}")
        else:
            st.info("You have no prescriptions on file yet.")


# ---------------------------------------------------------------------------
# Router
# ---------------------------------------------------------------------------
def main():
    if "auth" not in st.session_state:
        login_screen()
        return

    auth = st.session_state.auth
    with st.sidebar:
        st.markdown("## 🏥 HMS")
        st.markdown(f"**{auth['name']}**")
        st.markdown(f'<span class="pill">{auth["role"]}</span>', unsafe_allow_html=True)
        st.markdown("---")
        if st.button("🚪 Sign out", width="stretch"):
            st.session_state.pop("auth", None)
            st.rerun()
        st.markdown("---")
        st.caption("Full-stack: Streamlit frontend → FastAPI backend (JWT + RBAC).")

    role = auth["role"]
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
