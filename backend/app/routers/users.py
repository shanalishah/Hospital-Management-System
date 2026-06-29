"""User endpoints. Admin-only management plus a self-service /me and a public
doctor directory used by patients when booking."""

from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy.orm import Session

from app.database import get_db
from app.deps import get_current_user, require_role
from app.models import Role, User
from app.schemas import UserOut
from app.services import write_audit

router = APIRouter(prefix="/users", tags=["users"])


@router.get("/me", response_model=UserOut)
def me(current: User = Depends(get_current_user)):
    return current


@router.get("/doctors", response_model=list[UserOut])
def list_doctors(db: Session = Depends(get_db), _: User = Depends(get_current_user)):
    return db.query(User).filter(User.role == Role.DOCTOR).all()


@router.get("", response_model=list[UserOut])
def list_users(
    db: Session = Depends(get_db),
    _: User = Depends(require_role(Role.ADMIN)),
    role: Role | None = None,
    limit: int = Query(50, le=200),
    offset: int = 0,
):
    q = db.query(User)
    if role:
        q = q.filter(User.role == role)
    return q.order_by(User.id).offset(offset).limit(limit).all()


@router.delete("/{user_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_user(
    user_id: int,
    db: Session = Depends(get_db),
    admin: User = Depends(require_role(Role.ADMIN)),
):
    user = db.query(User).filter(User.id == user_id).first()
    if not user:
        raise HTTPException(status.HTTP_404_NOT_FOUND, "User not found")
    if user.id == admin.id:
        raise HTTPException(status.HTTP_400_BAD_REQUEST, "You cannot delete your own account")
    db.delete(user)
    write_audit(db, admin.id, "delete", "user", user_id)
    db.commit()
