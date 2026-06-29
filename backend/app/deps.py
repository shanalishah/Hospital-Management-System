"""Reusable FastAPI dependencies: current-user resolution and role guards.

`require_role(...)` is the heart of server-enforced RBAC — authorization is
checked on the server for every protected endpoint, not in the UI. This is the
key security property the original desktop app lacked.
"""

from fastapi import Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer
from sqlalchemy.orm import Session

from app.database import get_db
from app.models import Role, User
from app.security import decode_token

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="auth/login")

_credentials_error = HTTPException(
    status_code=status.HTTP_401_UNAUTHORIZED,
    detail="Could not validate credentials",
    headers={"WWW-Authenticate": "Bearer"},
)


def get_current_user(
    token: str = Depends(oauth2_scheme), db: Session = Depends(get_db)
) -> User:
    payload = decode_token(token)
    if not payload or "sub" not in payload:
        raise _credentials_error
    user = db.query(User).filter(User.username == payload["sub"]).first()
    if user is None:
        raise _credentials_error
    return user


def require_role(*allowed: Role):
    """Return a dependency that allows only the given roles."""

    def checker(user: User = Depends(get_current_user)) -> User:
        if user.role not in allowed:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail=f"Requires one of roles: {[r.value for r in allowed]}",
            )
        return user

    return checker
