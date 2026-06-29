"""FastAPI application entry point."""

from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.config import settings
from app.database import Base, SessionLocal, engine
from app.routers import appointments, auth, prescriptions, users
from app.seed import seed


@asynccontextmanager
async def lifespan(app: FastAPI):
    # For a portfolio/demo app we create tables on startup. In the "real" setup
    # Alembic migrations own the schema (see backend/alembic).
    Base.metadata.create_all(bind=engine)
    if settings.seed_demo_data:
        with SessionLocal() as db:
            seed(db)
    yield


app = FastAPI(
    title="Hospital Management System API",
    description="Role-based hospital operations API — auth, appointments, "
                "prescriptions, and user management with server-enforced RBAC.",
    version="1.0.0",
    lifespan=lifespan,
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # tighten to the frontend origin in production
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(auth.router)
app.include_router(users.router)
app.include_router(appointments.router)
app.include_router(prescriptions.router)


@app.get("/health", tags=["meta"])
def health():
    return {"status": "ok"}
