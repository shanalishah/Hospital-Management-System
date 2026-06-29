"""Application configuration.

Settings are loaded from environment variables (or a local .env file) so that
secrets like the database URL and JWT signing key are never hardcoded in source.
This is a direct fix for the original project, which committed `root` / empty
MySQL credentials straight into the Java controllers.
"""

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", extra="ignore")

    # Default to a local SQLite file so the app runs with zero setup.
    # docker-compose overrides this with a PostgreSQL URL.
    database_url: str = "sqlite:///./hospital.db"

    # CHANGE THIS in production via the JWT_SECRET env var.
    jwt_secret: str = "dev-secret-change-me"
    jwt_algorithm: str = "HS256"
    access_token_expire_minutes: int = 60

    seed_demo_data: bool = True


settings = Settings()
