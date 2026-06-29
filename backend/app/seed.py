"""Idempotent demo-data seeding so the API and Swagger UI are explorable
immediately after startup."""

from datetime import datetime, timedelta, timezone

from sqlalchemy.orm import Session

from app.models import Appointment, AppointmentStatus, Prescription, Role, User
from app.security import hash_password


def seed(db: Session) -> None:
    if db.query(User).count() > 0:
        return

    users = [
        User(name="System Admin", username="admin", email="admin@hospital.io",
             password_hash=hash_password("admin123"), role=Role.ADMIN),
        User(name="Olivia Reed", username="olivia", email="olivia@hospital.io",
             password_hash=hash_password("reception123"), role=Role.RECEPTIONIST),
        User(name="Dr. Sara Ahmed", username="sara", email="sara@hospital.io",
             password_hash=hash_password("doctor123"), role=Role.DOCTOR),
        User(name="Dr. Martin Cole", username="martin", email="martin@hospital.io",
             password_hash=hash_password("doctor123"), role=Role.DOCTOR),
        User(name="John Carter", username="john", email="john@example.com",
             password_hash=hash_password("patient123"), role=Role.PATIENT),
        User(name="Maria Lopez", username="maria", email="maria@example.com",
             password_hash=hash_password("patient123"), role=Role.PATIENT),
    ]
    db.add_all(users)
    db.flush()

    by_user = {u.username: u for u in users}
    base = datetime.now(timezone.utc).replace(hour=9, minute=0, second=0, microsecond=0)

    db.add_all([
        Appointment(patient_id=by_user["john"].id, doctor_id=by_user["sara"].id,
                    scheduled_at=base + timedelta(days=1), reason="Annual checkup",
                    status=AppointmentStatus.CONFIRMED),
        Appointment(patient_id=by_user["maria"].id, doctor_id=by_user["sara"].id,
                    scheduled_at=base + timedelta(days=1, hours=1), reason="Follow-up",
                    status=AppointmentStatus.REQUESTED),
        Appointment(patient_id=by_user["maria"].id, doctor_id=by_user["martin"].id,
                    scheduled_at=base + timedelta(days=2), reason="Cardiology consult",
                    status=AppointmentStatus.REQUESTED),
    ])
    db.add_all([
        Prescription(patient_id=by_user["john"].id, doctor_id=by_user["sara"].id,
                     diagnosis="Seasonal flu", symptoms="Fever, sore throat",
                     medication="Paracetamol 500mg, 2x/day for 5 days", bill_amount=45),
        Prescription(patient_id=by_user["maria"].id, doctor_id=by_user["martin"].id,
                     diagnosis="Hypertension", symptoms="Headache, dizziness",
                     medication="Amlodipine 5mg, 1x/day", bill_amount=80),
    ])
    db.commit()
