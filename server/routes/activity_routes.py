from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from db import get_db
from models.UserModel import User
from schemas.activity_schemas import ActivityLogPayload
from utils.auth import get_current_user

router = APIRouter(prefix="/activity", tags=["activity"])


@router.post("/log")
def log_activity(body: ActivityLogPayload, current_user: User = Depends(get_current_user), db: Session = Depends(get_db), response_model=ActivityLogPayload):
  pass
