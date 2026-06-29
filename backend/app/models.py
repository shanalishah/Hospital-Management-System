"""SQLAlchemy ORM models.

Mirrors the original schema (reg / patient / appointment / prescription) but
with real relationships, constraints, timestamps, and an audit trail.
"""

import enum
from datetime import datetime, timezone

from sqlalchemy import (
    DateTime,
    Enum,
    ForeignKey,
    Integer,
    String,
    Text,
    UniqueConstraint,
)
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.database import Base


def utcnow() -> datetime:
    return datetime.now(timezone.utc)


class Role(str, enum.Enum):
    ADMIN = "Admin"
    RECEPTIONIST = "Receptionist"
    DOCTOR = "Doctor"
    PATIENT = "Patient"


class AppointmentStatus(str, enum.Enum):
    REQUESTED = "Requested"
    CONFIRMED = "Confirmed"
    CANCELLED = "Cancelled"
    COMPLETED = "Completed"


class User(Base):
    __tablename__ = "users"

    id: Mapped[int] = mapped_column(primary_key=True)
    name: Mapped[str] = mapped_column(String(120), nullable=False)
    username: Mapped[str] = mapped_column(String(60), unique=True, index=True, nullable=False)
    email: Mapped[str | None] = mapped_column(String(120), unique=True)
    password_hash: Mapped[str] = mapped_column(String(255), nullable=False)
    role: Mapped[Role] = mapped_column(Enum(Role), nullable=False)
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=utcnow)

    # A doctor has many appointments; a patient has many appointments/prescriptions.
    doctor_appointments: Mapped[list["Appointment"]] = relationship(
        back_populates="doctor", foreign_keys="Appointment.doctor_id"
    )
    patient_appointments: Mapped[list["Appointment"]] = relationship(
        back_populates="patient", foreign_keys="Appointment.patient_id"
    )
    prescriptions: Mapped[list["Prescription"]] = relationship(
        back_populates="patient", foreign_keys="Prescription.patient_id"
    )


class Appointment(Base):
    __tablename__ = "appointments"
    # Business rule: a doctor cannot hold two appointments in the same slot.
    __table_args__ = (
        UniqueConstraint("doctor_id", "scheduled_at", name="uq_doctor_slot"),
    )

    id: Mapped[int] = mapped_column(primary_key=True)
    patient_id: Mapped[int] = mapped_column(ForeignKey("users.id", ondelete="CASCADE"), index=True)
    doctor_id: Mapped[int] = mapped_column(ForeignKey("users.id", ondelete="CASCADE"), index=True)
    scheduled_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), nullable=False)
    reason: Mapped[str | None] = mapped_column(String(255))
    status: Mapped[AppointmentStatus] = mapped_column(
        Enum(AppointmentStatus), default=AppointmentStatus.REQUESTED
    )
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=utcnow)

    patient: Mapped["User"] = relationship(back_populates="patient_appointments", foreign_keys=[patient_id])
    doctor: Mapped["User"] = relationship(back_populates="doctor_appointments", foreign_keys=[doctor_id])


class Prescription(Base):
    __tablename__ = "prescriptions"

    id: Mapped[int] = mapped_column(primary_key=True)
    patient_id: Mapped[int] = mapped_column(ForeignKey("users.id", ondelete="CASCADE"), index=True)
    doctor_id: Mapped[int] = mapped_column(ForeignKey("users.id", ondelete="CASCADE"), index=True)
    diagnosis: Mapped[str] = mapped_column(String(255), nullable=False)
    symptoms: Mapped[str | None] = mapped_column(Text)
    medication: Mapped[str | None] = mapped_column(Text)
    bill_amount: Mapped[float] = mapped_column(default=0.0)
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=utcnow)

    patient: Mapped["User"] = relationship(back_populates="prescriptions", foreign_keys=[patient_id])
    doctor: Mapped["User"] = relationship(foreign_keys=[doctor_id])


class AuditLog(Base):
    """Immutable record of who did what — a common compliance requirement in
    healthcare systems and a strong talking point in interviews."""

    __tablename__ = "audit_logs"

    id: Mapped[int] = mapped_column(primary_key=True)
    actor_id: Mapped[int | None] = mapped_column(ForeignKey("users.id", ondelete="SET NULL"))
    action: Mapped[str] = mapped_column(String(80), nullable=False)
    entity: Mapped[str] = mapped_column(String(80), nullable=False)
    entity_id: Mapped[int | None] = mapped_column(Integer)
    detail: Mapped[str | None] = mapped_column(Text)
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=utcnow)
