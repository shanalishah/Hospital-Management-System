"""Pytest fixtures: a fresh, seeded SQLite database per test session and a
TestClient wired to it."""

import os
import tempfile

import pytest

# Point the app at a throwaway database BEFORE importing it.
_tmp = os.path.join(tempfile.gettempdir(), "hms_test.db")
if os.path.exists(_tmp):
    os.remove(_tmp)
os.environ["DATABASE_URL"] = f"sqlite:///{_tmp}"

from fastapi.testclient import TestClient  # noqa: E402

from app.main import app  # noqa: E402


@pytest.fixture(scope="session")
def client():
    # Entering the context manager runs the lifespan: create tables + seed demo data.
    with TestClient(app) as c:
        yield c


def _token(client, username, password):
    resp = client.post("/auth/login", data={"username": username, "password": password})
    assert resp.status_code == 200, resp.text
    return resp.json()["access_token"]


@pytest.fixture
def auth(client):
    """Returns a helper that builds an Authorization header for a given user."""
    def _make(username, password):
        return {"Authorization": f"Bearer {_token(client, username, password)}"}
    return _make
