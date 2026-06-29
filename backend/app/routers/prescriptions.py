"""Prescription endpoints.

Doctors create prescriptions; patients can read only their own. This is the
classic broken-access-control case the original app got wrong (any logged-in
user could read every prescription) — fixed here with server-side row scoping.
"""

from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.database import get_db
from app.deps import get_current_user, require_role
from app.models import Prescription, Role, User
from app.schemas import PrescriptionCreate, PrescriptionOut
from app.services import create_prescription

router = APIRouter(prefix="/prescriptions", tags=["prescriptions"])


@router.post("", response_model=PrescriptionOut, status_code=201)
def add_prescription(
    data: PrescriptionCreate,
    db: Session = Depends(get_db),
    doctor: User = Depends(require_role(Role.DOCTOR)),
):
    return create_prescription(db, doctor, data)


@router.get("", response_model=list[PrescriptionOut])
def list_prescriptions(
    db: Session = Depends(get_db),
    current: User = Depends(get_current_user),
):
    q = db.query(Prescription)
    if current.role == Role.PATIENT:
        q = q.filter(Prescription.patient_id == current.id)
    elif current.role == Role.DOCTOR:
        q = q.filter(Prescription.doctor_id == current.id)
    # Admin / Receptionist see all.
    return q.order_by(Prescription.created_at.desc()).all()
