# 🏥 Hospital Management System — Web Edition

A role-based hospital operations platform with four user types — **Admin,
Receptionist, Doctor, and Patient** — each with a tailored dashboard for
appointments, prescriptions, and staff/patient management.

This is a web rebuild of a JavaFX + MySQL desktop project, re-engineered with
**Streamlit + SQLite** so it runs anywhere and deploys for free with a single
shareable link.

## ✨ Live demo

> Add your deployed link here, e.g. `https://your-app.streamlit.app`

Sign in with any demo account (pick the matching **Role** in the dropdown):

| Role         | Name           | Password       |
|--------------|----------------|----------------|
| Admin        | `admin`        | `admin123`     |
| Receptionist | `Olivia Reed`  | `reception123` |
| Doctor       | `Dr. Sara Ahmed` | `doctor123`  |
| Patient      | `John Carter`  | `patient123`   |

## 🧩 Features

- **Authentication & registration** with role selection; passwords hashed with SHA-256.
- **Admin** — hospital-wide metrics, staff distribution chart, user management (view/delete).
- **Receptionist** — schedule & delete patient meetings, review patient appointment requests.
- **Doctor** — view personal appointments, write prescriptions, browse all prescriptions.
- **Patient** — book appointments, browse doctors, view personal prescriptions.

## 🛠️ Tech stack

Python · Streamlit · SQLite · pandas

## 🚀 Run locally

```bash
pip install -r requirements.txt
streamlit run app.py
```

The SQLite database (`hospital.db`) is created and seeded with demo data on first run.

## ☁️ Deploy (free public link)

1. Push this repo to GitHub.
2. Go to [share.streamlit.io](https://share.streamlit.io) and sign in with GitHub.
3. **New app** → pick the repo → set **main file path** to `streamlit_app/app.py`.
4. Deploy. You'll get a public `*.streamlit.app` URL to share on your resume.

## 🔁 Improvements over the original desktop app

- Plain-text passwords → **SHA-256 hashing**.
- Local-only MySQL desktop app → **deployable web app** with a public link.
- SQL built via string concatenation → **parameterized queries** (SQL-injection safe).
- Per-role JavaFX windows → unified, responsive web dashboards.
