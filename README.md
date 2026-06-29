# 🏥 Hospital Management System

A full-stack, role-based hospital operations platform — **Admin, Receptionist,
Doctor, and Patient** each get a tailored dashboard for appointments,
prescriptions, and staff/patient management.

Originally a JavaFX + MySQL desktop project, **re-engineered into a deployed
full-stack web application** with a REST API, JWT authentication, server-enforced
role-based access control, automated tests, and CI/CD.

## 🔗 Live demo

| | URL |
|---|---|
| **Web app** | **https://hospital-mgmt-system.streamlit.app** |
| **API (Swagger docs)** | https://hms-api-8raz.onrender.com/docs |

Sign in with any demo account:

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| Receptionist | `olivia` | `reception123` |
| Doctor | `sara` | `doctor123` |
| Patient | `john` | `patient123` |

> ⏱️ The API runs on a free tier that sleeps when idle — the **first** sign-in may
> take ~30s to wake it, then it's fast.

## 🏗️ Architecture

```
┌──────────────────┐     HTTPS + JWT      ┌──────────────────┐          ┌──────────────┐
│  Streamlit (UI)  │ ───────────────────▶ │ FastAPI (REST)   │ ───────▶ │  PostgreSQL  │
│  role dashboards │ ◀─────────────────── │ services + RBAC  │          │   (Neon)     │
└──────────────────┘     JSON             └──────────────────┘          └──────────────┘
   Streamlit Cloud                              Render                      Neon
```

Layered backend: **routers → services → models**, with auth dependencies
enforcing role-based access on every endpoint. See
[`backend/README.md`](backend/README.md) and
[`streamlit_app/README.md`](streamlit_app/README.md) for details.

## 🧰 Tech stack

**Backend:** Python · FastAPI · SQLAlchemy 2.0 · Pydantic · PostgreSQL · JWT · bcrypt
**Frontend:** Streamlit · pandas
**Infra / quality:** Docker · Docker Compose · GitHub Actions (CI) · pytest · Render · Neon

## ✨ Features

- **Authentication & RBAC** — JWT login; authorization enforced server-side per role and per row (patients can read only their own data).
- **Admin** — hospital-wide metrics, staff distribution, user management.
- **Receptionist** — review and update appointment requests across all doctors.
- **Doctor** — view personal appointments, write prescriptions.
- **Patient** — book appointments, browse doctors, view prescriptions.
- **Domain logic** — appointment double-booking prevention, audit logging.

## 🔐 Re-engineered from the original desktop app

| Original JavaFX app | This version |
|---|---|
| Plain-text passwords | bcrypt hashing |
| SQL string concatenation (injectable) | parameterized ORM queries |
| Hardcoded DB credentials in source | environment-based config |
| Authorization implied by which window opened | server-enforced RBAC on every endpoint |
| Any user could read all prescriptions | per-row access scoping |
| No tests, no deployment | pytest suite, CI, Dockerized, deployed |

## 🚀 Run locally

```bash
# Backend
cd backend
pip install -r requirements.txt
uvicorn app.main:app --reload          # http://localhost:8000/docs

# Frontend (in another terminal)
cd streamlit_app
pip install -r requirements.txt
streamlit run app.py                    # http://localhost:8501
```

Or run the API + PostgreSQL together: `docker compose up --build`.

## ☁️ Deployment

See [`DEPLOY.md`](DEPLOY.md) for the full Neon + Render + Streamlit Cloud walkthrough.

---

> 📁 The original JavaFX desktop source is preserved under `src/` for reference.
