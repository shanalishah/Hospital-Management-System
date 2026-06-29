"""Pydantic schemas (request/response DTOs).

Keeping API contracts separate from ORM models is a deliberate layering choice:
it prevents leaking internal fields (e.g. password_hash) and gives us validation
at the edge.
"""

from datetime import datetime

from pydantic import BaseModel, ConfigDict, EmailStr, Field

from app.models import AppointmentStatus, Role


# ---- Auth ----
class Token(BaseModel):
    access_token: str
    token_type: str = "bearer"
    role: Role
    name: str


class UserCreate(BaseModel):
    name: str = Field(min_length=2, max_length=120)
    username: str = Field(min_length=3, max_length=60)
    email: EmailStr | None = None
    password: str = Field(min_length=6, max_length=128)
    role: Role = Role.PATIENT


class UserOut(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    id: int
    name: str
    username: str
    email: str | None
    role: Role
    created_at: datetime


# ---- Appointments ----
class AppointmentCreate(BaseModel):
    doctor_id: int
    scheduled_at: datetime
    reason: str | None = Field(default=None, max_length=255)


class AppointmentOut(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    id: int
    patient_id: int
    doctor_id: int
    scheduled_at: datetime
    reason: str | None
    status: AppointmentStatus
    created_at: datetime


class AppointmentStatusUpdate(BaseModel):
    status: AppointmentStatus


# ---- Prescriptions ----
class PrescriptionCreate(BaseModel):
    patient_id: int
    diagnosis: str = Field(min_length=2, max_length=255)
    symptoms: str | None = None
    medication: str | None = None
    bill_amount: float = Field(default=0.0, ge=0)


class PrescriptionOut(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    id: int
    patient_id: int
    doctor_id: int
    diagnosis: str
    symptoms: str | None
    medication: str | None
    bill_amount: float
    created_at: datetime


# ---- Generic ----
class Page(BaseModel):
    total: int
    items: list
