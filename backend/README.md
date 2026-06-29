# 🏥 Hospital Management System — Backend API

A production-shaped REST API for hospital operations, built with **FastAPI**,
**SQLAlchemy 2.0**, and **JWT authentication with server-enforced role-based
access control**. This is a ground-up re-engineering of a JavaFX + MySQL desktop
project into a layered, tested, containerized web service.

## 🏗️ Architecture

```
            ┌─────────────┐
 client ──▶ │   routers   │  HTTP, validation, auth dependencies
            └──────┬──────┘
                   │
            ┌──────▼──────┐
            │  services   │  business rules (conflict detection, audit)
            └──────┬──────┘
                   │
            ┌──────▼──────┐
            │   models    │  SQLAlchemy ORM (users, appointments, …)
            └──────┬──────┘
                   │
            ┌──────▼──────┐
            │  database   │  PostgreSQL (prod) / SQLite (local)
            └─────────────┘
```

- **`routers/`** — thin HTTP handlers; validation via Pydantic schemas; auth via dependencies.
- **`services.py`** — domain logic kept out of handlers so it's unit-testable.
- **`deps.py`** — `get_current_user` and `require_role(...)` implement RBAC centrally.
- **`models.py`** — ORM models with relationships, constraints, timestamps, audit log.
- **`schemas.py`** — request/response DTOs, separate from ORM models.

## 🔐 Security improvements over the original

| Original desktop app | This API |
|---|---|
| Plain-text passwords | **bcrypt** hashing |
| SQL via string concatenation (injectable) | **parameterized ORM queries** |
| Hardcoded DB credentials in source | **environment-based config** |
| Authorization implied by which window opened | **server-enforced RBAC on every endpoint** |
| Any user could read all prescriptions | **per-row access scoping** (patients see only their own) |
| No audit trail | **immutable audit log** of mutations |

## 🚀 Run locally

```bash
cd backend
pip install -r requirements.txt
uvicorn app.main:app --reload
```

Open **http://localhost:8000/docs** for interactive Swagger UI. The database is
auto-created and seeded with demo data on first run.

### Or run the full stack (API + PostgreSQL) with Docker

```bash
docker compose up --build      # from the repo root
```

## 🧪 Tests

```bash
cd backend
pytest -q
```

The suite covers authentication, RBAC (403 on unauthorized access), per-row data
scoping, and the appointment double-booking rule (409 Conflict). CI runs it on
every push via GitHub Actions.

## 👤 Demo accounts

| Role         | Username | Password       |
|--------------|----------|----------------|
| Admin        | `admin`  | `admin123`     |
| Receptionist | `olivia` | `reception123` |
| Doctor       | `sara`   | `doctor123`    |
| Patient      | `john`   | `patient123`   |

## 📡 Key endpoints

| Method | Path | Auth |
|---|---|---|
| `POST` | `/auth/register` | public |
| `POST` | `/auth/login` | public |
| `GET`  | `/users/me` | any authenticated |
| `GET`  | `/users` | Admin |
| `DELETE` | `/users/{id}` | Admin |
| `GET`  | `/users/doctors` | any authenticated |
| `POST` | `/appointments` | Patient |
| `GET`  | `/appointments` | role-scoped |
| `PATCH`| `/appointments/{id}/status` | Receptionist / Doctor / Admin |
| `POST` | `/prescriptions` | Doctor |
| `GET`  | `/prescriptions` | role-scoped |

## 🗺️ Roadmap

- [ ] Alembic migrations (schema currently auto-created for demo convenience)
- [ ] React frontend consuming this API
- [ ] Refresh tokens + token revocation
- [ ] Deploy (Render/Railway/Fly) with managed PostgreSQL
