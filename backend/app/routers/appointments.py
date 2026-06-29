"""Appointment endpoints with role-scoped visibility.

Patients see only their own appointments; doctors see only theirs; admins and
receptionists see all. This per-row authorization is enforced server-side.
"""

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from app.database import get_db
from app.deps import get_current_user, require_role
from app.models import Appointment, Role, User
from app.schemas import AppointmentCreate, AppointmentOut, AppointmentStatusUpdate
from app.services import book_appointment, write_audit

router = APIRouter(prefix="/appointments", tags=["appointments"])


@router.post("", response_model=AppointmentOut, status_code=status.HTTP_201_CREATED)
def create_appointment(
    data: AppointmentCreate,
    db: Session = Depends(get_db),
    patient: User = Depends(require_role(Role.PATIENT)),
):
    return book_appointment(db, patient, data.doctor_id, data.scheduled_at, data.reason)


@router.get("", response_model=list[AppointmentOut])
def list_appointments(
    db: Session = Depends(get_db),
    current: User = Depends(get_current_user),
):
    q = db.query(Appointment)
    if current.role == Role.PATIENT:
        q = q.filter(Appointment.patient_id == current.id)
    elif current.role == Role.DOCTOR:
        q = q.filter(Appointment.doctor_id == current.id)
    # Admin / Receptionist see everything.
    return q.order_by(Appointment.scheduled_at).all()


@router.patch("/{appt_id}/status", response_model=AppointmentOut)
def update_status(
    appt_id: int,
    update: AppointmentStatusUpdate,
    db: Session = Depends(get_db),
    current: User = Depends(require_role(Role.RECEPTIONIST, Role.DOCTOR, Role.ADMIN)),
):
    appt = db.query(Appointment).filter(Appointment.id == appt_id).first()
    if not appt:
        raise HTTPException(status.HTTP_404_NOT_FOUND, "Appointment not found")
    # A doctor may only change appointments assigned to them.
    if current.role == Role.DOCTOR and appt.doctor_id != current.id:
        raise HTTPException(status.HTTP_403_FORBIDDEN, "Not your appointment")
    appt.status = update.status
    write_audit(db, current.id, "update_status", "appointment", appt.id, update.status.value)
    db.commit()
    db.refresh(appt)
    return appt
