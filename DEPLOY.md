# 🚀 Deployment Guide

This is a full-stack app, so deployment has three free pieces:

| Layer | Service | Why |
|---|---|---|
| Database | **Neon** (serverless PostgreSQL) | Free, *persistent*, modern |
| Backend API | **Render** (Docker web service) | Free, deploys from `render.yaml` |
| Frontend | **Streamlit Community Cloud** | Free, permanent link |

```
Streamlit Cloud (UI) ──HTTPS──▶ Render (FastAPI) ──▶ Neon (PostgreSQL)
```

---

## 1. Database — Neon

1. Sign up at **https://neon.tech** (sign in with GitHub).
2. Create a project (any name, e.g. `hospital`).
3. Copy the **connection string** — it looks like:
   `postgresql://user:pass@ep-xxx.region.aws.neon.tech/dbname?sslmode=require`
4. Keep it handy for step 2.

## 2. Backend API — Render

1. Sign up at **https://render.com** (sign in with GitHub).
2. **New ▸ Blueprint**, select the `Hospital-Management-System` repo. Render reads `render.yaml`.
3. When prompted for env vars, set **`DATABASE_URL`** to your Neon string from step 1.
   (`JWT_SECRET` is auto-generated; `SEED_DEMO_DATA=true` loads demo accounts.)
4. **Apply / Deploy.** First build takes a few minutes.
5. When live, your API is at `https://hms-api-xxxx.onrender.com`.
   Verify: open `…/health` (should return `{"status":"ok"}`) and `…/docs` (Swagger UI).

> ⓘ Render's free tier sleeps after ~15 min idle; the first request then takes
> ~50s to wake. Fine for a portfolio demo — just hit the link once before a call.

## 3. Frontend — Streamlit Community Cloud

1. Go to **https://share.streamlit.io** ▸ **Create app** ▸ from GitHub.
2. Repository `shanalishah/Hospital-Management-System`, branch `main`,
   **main file path** `streamlit_app/app.py`.
3. **Advanced settings ▸ Secrets**, add:
   ```toml
   API_BASE_URL = "https://hms-api-xxxx.onrender.com"
   ```
   (your Render URL from step 2, no trailing slash)
4. **Deploy.** You'll get a permanent `…streamlit.app` link — **this is the one for your résumé.**

## 4. Demo accounts

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| Receptionist | `olivia` | `reception123` |
| Doctor | `sara` | `doctor123` |
| Patient | `john` | `patient123` |

## Updating the deployed app

Both Render and Streamlit Cloud auto-deploy on every push to `main`. Just
`git push` and they rebuild.
