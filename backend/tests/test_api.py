"""End-to-end API tests covering auth, RBAC, and the appointment conflict rule.

These tests double as executable documentation of the security properties the
original desktop app lacked.
"""

from datetime import datetime, timedelta, timezone


def test_health(client):
    assert client.get("/health").json() == {"status": "ok"}


def test_login_each_role(client):
    for user, pw in [("admin", "admin123"), ("olivia", "reception123"),
                     ("sara", "doctor123"), ("john", "patient123")]:
        r = client.post("/auth/login", data={"username": user, "password": pw})
        assert r.status_code == 200, r.text
        assert r.json()["access_token"]


def test_login_wrong_password(client):
    r = client.post("/auth/login", data={"username": "admin", "password": "nope"})
    assert r.status_code == 401


def test_rbac_patient_cannot_list_all_users(client, auth):
    r = client.get("/users", headers=auth("john", "patient123"))
    assert r.status_code == 403


def test_rbac_admin_can_list_users(client, auth):
    r = client.get("/users", headers=auth("admin", "admin123"))
    assert r.status_code == 200
    assert len(r.json()) >= 6


def test_rbac_patient_cannot_create_prescription(client, auth):
    r = client.post("/prescriptions",
                    headers=auth("john", "patient123"),
                    json={"patient_id": 5, "diagnosis": "x"})
    assert r.status_code == 403


def test_patient_sees_only_own_prescriptions(client, auth):
    # The original app let any logged-in user read every prescription.
    r = client.get("/prescriptions", headers=auth("john", "patient123"))
    assert r.status_code == 200
    data = r.json()
    assert data, "John should have a seeded prescription"
    assert all(p["patient_id"] == 5 for p in data)  # John is user id 5


def test_appointment_conflict_detection(client, auth):
    headers = auth("john", "patient123")
    doctors = client.get("/users/doctors", headers=headers).json()
    doctor_id = doctors[0]["id"]
    slot = (datetime.now(timezone.utc) + timedelta(days=10)).replace(microsecond=0).isoformat()

    first = client.post("/appointments", headers=headers,
                        json={"doctor_id": doctor_id, "scheduled_at": slot, "reason": "checkup"})
    assert first.status_code == 201, first.text

    # Same doctor, same slot -> 409 Conflict.
    second = client.post("/appointments", headers=headers,
                         json={"doctor_id": doctor_id, "scheduled_at": slot, "reason": "again"})
    assert second.status_code == 409


def test_unauthenticated_is_rejected(client):
    assert client.get("/users/me").status_code == 401
