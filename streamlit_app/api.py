"""Thin HTTP client for the Hospital Management System FastAPI backend.

The Streamlit frontend holds no database logic of its own anymore — it
authenticates against the API and reads/writes everything over HTTP. This is the
clean separation interviewers look for: a frontend that talks to a real backend.

The API base URL is configurable so the same frontend works locally and when
deployed against a hosted backend:
    1. st.secrets["API_BASE_URL"]   (Streamlit Cloud)
    2. env var API_BASE_URL
    3. http://localhost:8000         (local default)
"""

import os

import requests
import streamlit as st

DEFAULT_BASE = "http://localhost:8000"


def _base_url() -> str:
    try:
        if "API_BASE_URL" in st.secrets:
            return str(st.secrets["API_BASE_URL"]).rstrip("/")
    except Exception:
        pass
    return os.environ.get("API_BASE_URL", DEFAULT_BASE).rstrip("/")


class APIError(Exception):
    def __init__(self, status_code: int, detail: str):
        self.status_code = status_code
        self.detail = detail
        super().__init__(f"{status_code}: {detail}")


class APIClient:
    def __init__(self, token: str | None = None):
        self.base = _base_url()
        self.token = token

    # -- low level --
    def _headers(self):
        return {"Authorization": f"Bearer {self.token}"} if self.token else {}

    def _request(self, method, path, **kwargs):
        url = f"{self.base}{path}"
        try:
            resp = requests.request(method, url, headers=self._headers(), timeout=15, **kwargs)
        except requests.RequestException as exc:
            raise APIError(0, f"Cannot reach API at {self.base}. Is the backend running? ({exc})")
        if resp.status_code >= 400:
            detail = resp.json().get("detail") if resp.headers.get("content-type", "").startswith("application/json") else resp.text
            raise APIError(resp.status_code, detail or resp.reason)
        if resp.status_code == 204 or not resp.content:
            return None
        return resp.json()

    # -- auth --
    def login(self, username, password):
        # OAuth2 password flow expects form-encoded data.
        data = self._request("POST", "/auth/login",
                             data={"username": username, "password": password})
        self.token = data["access_token"]
        return data

    def register(self, name, username, email, password, role):
        return self._request("POST", "/auth/register", json={
            "name": name, "username": username, "email": email or None,
            "password": password, "role": role,
        })

    # -- users --
    def me(self):
        return self._request("GET", "/users/me")

    def users(self, role=None):
        params = {"role": role} if role else None
        return self._request("GET", "/users", params=params)

    def doctors(self):
        return self._request("GET", "/users/doctors")

    def patients(self):
        return self._request("GET", "/users/patients")

    def delete_user(self, user_id):
        return self._request("DELETE", f"/users/{user_id}")

    # -- appointments --
    def appointments(self):
        return self._request("GET", "/appointments")

    def book_appointment(self, doctor_id, scheduled_at_iso, reason):
        return self._request("POST", "/appointments", json={
            "doctor_id": doctor_id, "scheduled_at": scheduled_at_iso, "reason": reason,
        })

    def set_appointment_status(self, appt_id, status):
        return self._request("PATCH", f"/appointments/{appt_id}/status", json={"status": status})

    # -- prescriptions --
    def prescriptions(self):
        return self._request("GET", "/prescriptions")

    def add_prescription(self, patient_id, diagnosis, symptoms, medication, bill_amount):
        return self._request("POST", "/prescriptions", json={
            "patient_id": patient_id, "diagnosis": diagnosis, "symptoms": symptoms,
            "medication": medication, "bill_amount": bill_amount,
        })
