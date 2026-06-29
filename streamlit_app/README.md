# 🏥 Hospital Management System — Frontend (Streamlit)

A role-aware web UI for the Hospital Management System. It holds **no database
logic** — it authenticates against the [FastAPI backend](../backend) and performs
every read/write over HTTP, adapting the interface to the signed-in user's role
(Admin, Receptionist, Doctor, Patient).

**Live app:** https://hospital-mgmt-system.streamlit.app

```
 Streamlit UI  ──HTTP/JWT──▶  FastAPI backend  ──▶  PostgreSQL / SQLite
 (this folder)                (../backend)
```

## ✨ What it shows

- **JWT login** against the API; the token is kept in session and sent on every request.
- **Role-based dashboards** — the same app renders a different experience per role.
- **Live data** — metrics, tables, appointment booking, status updates, and prescriptions all flow through the API.
- Graceful handling of auth expiry and unreachable-backend states.

## 🚀 Run locally

Start the backend first (see [`../backend/README.md`](../backend/README.md)), then:

```bash
cd streamlit_app
pip install -r requirements.txt
streamlit run app.py
```

By default the UI talks to `http://localhost:8000`. Override with the
`API_BASE_URL` environment variable, or—on Streamlit Community Cloud—set an
`API_BASE_URL` entry in the app's **Secrets**:

```toml
API_BASE_URL = "https://your-backend-host.example.com"
```

## 👤 Demo accounts

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| Receptionist | `olivia` | `reception123` |
| Doctor | `sara` | `doctor123` |
| Patient | `john` | `patient123` |

## ☁️ Deployment

This is the **frontend half** of a full-stack app, so deployment is two pieces:

1. **Backend** → a host that runs a web service + database (Render / Railway / Fly.io) — see the backend README.
2. **Frontend** → Streamlit Community Cloud, with `API_BASE_URL` in Secrets pointing at the deployed backend, and main file path `streamlit_app/app.py`.
