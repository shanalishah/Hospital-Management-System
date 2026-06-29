"""Database engine and session management (SQLAlchemy 2.0)."""

from sqlalchemy import create_engine
from sqlalchemy.orm import DeclarativeBase, sessionmaker

from app.config import settings


def normalize_db_url(url: str) -> str:
    """Some hosts (Render, Heroku) hand out URLs starting with the legacy
    `postgres://` scheme, which SQLAlchemy 2.0 no longer accepts. Rewrite it to
    the `postgresql://` form so the same code runs locally and in the cloud."""
    if url.startswith("postgres://"):
        return url.replace("postgres://", "postgresql://", 1)
    return url


DATABASE_URL = normalize_db_url(settings.database_url)

# SQLite needs check_same_thread=False for FastAPI's threaded request handling.
connect_args = {"check_same_thread": False} if DATABASE_URL.startswith("sqlite") else {}

engine = create_engine(DATABASE_URL, connect_args=connect_args, pool_pre_ping=True)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)


class Base(DeclarativeBase):
    pass


def get_db():
    """FastAPI dependency that yields a scoped session and always closes it."""
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
