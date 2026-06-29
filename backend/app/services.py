"""Business-logic layer.

Keeping domain rules here (rather than inside route handlers) keeps the API thin
and the rules testable in isolation.
"""

from fastapi import HTTPException, status
from sqlalchemy.orm import Session

from app.models import (
    Appointment,
    AppointmentStatus,
    AuditLog,
    Prescription,
    Role,
    User,
)


def write_audit(db: Session, actor_id: int | None, action: str, entity: str,
                entity_id: int | None = None, detail: str | None = None) -> None:
    db.add(AuditLog(actor_id=actor_id, action=action, entity=entity,
                    entity_id=entity_id, detail=detail))


def book_appointment(db: Session, patient: User, doctor_id: int,
                     scheduled_at, reason: str | None) -> Appointment:
    doctor = db.query(User).filter(User.id == doctor_id, User.role == Role.DOCTOR).first()
    if not doctor:
        raise HTTPException(status.HTTP_404_NOT_FOUND, "Doctor not found")

    # Conflict detection: a doctor can't be double-booked in the same slot.
    clash = (
        db.query(Appointment)
        .filter(
            Appointment.doctor_id == doctor_id,
            Appointment.scheduled_at == scheduled_at,
            Appointment.status != AppointmentStatus.CANCELLED,
        )
        .first()
    )
    if clash:
        raise HTTPException(
            status.HTTP_409_CONFLICT,
            "That time slot is already booked with this doctor.",
        )

    appt = Appointment(
        patient_id=patient.id,
        doctor_id=doctor_id,
        scheduled_at=scheduled_at,
        reason=reason,
        status=AppointmentStatus.REQUESTED,
    )
    db.add(appt)
    db.flush()  # populate appt.id before audit
    write_audit(db, patient.id, "create", "appointment", appt.id,
                f"with doctor {doctor_id} at {scheduled_at}")
    db.commit()
    db.refresh(appt)
    return appt


def delete_user_cascade(db: Session, admin: User, user_id: int) -> None:
    """Delete a user along with the rows that reference them.

    PostgreSQL enforces foreign keys, so we must clear dependents first:
    remove the user's appointments and prescriptions, and detach (null out)
    their audit-log entries so the history itself is preserved.
    """
    user = db.query(User).filter(User.id == user_id).first()
    if not user:
        raise HTTPException(status.HTTP_404_NOT_FOUND, "User not found")
    if user.id == admin.id:
        raise HTTPException(status.HTTP_400_BAD_REQUEST, "You cannot delete your own account")

    db.query(Appointment).filter(
        (Appointment.patient_id == user_id) | (Appointment.doctor_id == user_id)
    ).delete(synchronize_session=False)
    db.query(Prescription).filter(
        (Prescription.patient_id == user_id) | (Prescription.doctor_id == user_id)
    ).delete(synchronize_session=False)
    db.query(AuditLog).filter(AuditLog.actor_id == user_id).update(
        {AuditLog.actor_id: None}, synchronize_session=False
    )

    db.delete(user)
    write_audit(db, admin.id, "delete", "user", user_id)
    db.commit()


def create_prescription(db: Session, doctor: User, data) -> Prescription:
    patient = db.query(User).filter(User.id == data.patient_id, User.role == Role.PATIENT).first()
    if not patient:
        raise HTTPException(status.HTTP_404_NOT_FOUND, "Patient not found")
    pre = Prescription(
        patient_id=data.patient_id,
        doctor_id=doctor.id,
        diagnosis=data.diagnosis,
        symptoms=data.symptoms,
        medication=data.medication,
        bill_amount=data.bill_amount,
    )
    db.add(pre)
    db.flush()
    write_audit(db, doctor.id, "create", "prescription", pre.id, f"for patient {data.patient_id}")
    db.commit()
    db.refresh(pre)
    return pre
