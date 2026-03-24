from datetime import date, timedelta

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from db import get_db
from models.UserModel import UserModel
from models.ActivityModel import ActivityModel
from schemas.activity_schemas import ActivityLogPayload
from utils.auth import get_current_user

router = APIRouter(prefix="/activity", tags=["activity"])


@router.post("/log", status_code=201)
def create_activity(body: ActivityLogPayload, current_user: UserModel = Depends(get_current_user), db: Session = Depends(get_db)):
  
  new_activity = ActivityModel(
    user_id=current_user.id, 
    activity_type=body.activity_type,
    start=body.start,
    end=body.end,
    notes=body.notes,
    steps_taken=body.steps_taken,
    max_hr=body.max_hr
    )
  db.add(new_activity)
  db.commit()
  db.refresh(new_activity)
  
  return new_activity


@router.get("/date/{day}")
def get_activities_by_date(day: date, current_user: UserModel = Depends(get_current_user), db: Session = Depends(get_db)):
  return (
    db.query(ActivityModel)
    .filter(
      ActivityModel.user_id == current_user.id,
      ActivityModel.start >= f"{day} 00:00:00",
      ActivityModel.start <= f"{day} 23:59:59"
    )
    .order_by(ActivityModel.start)
    .all()
  )


@router.get("/last/{n}")
def get_activities_last_n_days(n: int, current_user: UserModel = Depends(get_current_user), db: Session = Depends(get_db)):
  since = date.today() - timedelta(days=n - 1)
  return (
    db.query(ActivityModel)
    .filter(
      ActivityModel.user_id == current_user.id,
      ActivityModel.start >= f"{since} 00:00:00"
    )
    .order_by(ActivityModel.start)
    .all()
  )
