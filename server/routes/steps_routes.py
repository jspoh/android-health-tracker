from datetime import date, timedelta

from fastapi import APIRouter, Depends, Query
from sqlalchemy.orm import Session
from sqlalchemy.dialects.sqlite import insert

from db import get_db
from models.UserModel import UserModel
from models.DailyStepsModel import DailyStepsModel
from schemas.steps_schemas import StepsSyncPayload, DailyStepsResponse
from utils.auth import get_current_user

router = APIRouter(prefix="/steps", tags=["steps"])


@router.post("/sync", response_model=DailyStepsResponse, status_code=201)
def sync_steps(body: StepsSyncPayload, current_user: UserModel = Depends(get_current_user), db: Session = Depends(get_db)):
    stmt = (
        insert(DailyStepsModel)
        .values(user_id=current_user.id, date=body.date, steps=body.steps)
        .on_conflict_do_update(
            index_elements=["user_id", "date"],
            set_={"steps": body.steps}
        )
    )
    db.execute(stmt)
    db.commit()

    record = db.query(DailyStepsModel).filter_by(user_id=current_user.id, date=body.date).first()
    return record


@router.get("/", response_model=list[DailyStepsResponse])
def get_steps(current_user: UserModel = Depends(get_current_user), db: Session = Depends(get_db)):
    return db.query(DailyStepsModel).filter_by(user_id=current_user.id).order_by(DailyStepsModel.date).all()


@router.get("/target")
def get_step_target(current_user: UserModel = Depends(get_current_user)):
    return {"step_target": current_user.step_target}


@router.get("/date/{day}", response_model=DailyStepsResponse)
def get_steps_by_date(day: date, current_user: UserModel = Depends(get_current_user), db: Session = Depends(get_db)):
    record = db.query(DailyStepsModel).filter_by(user_id=current_user.id, date=day).first()
    if not record:
        return DailyStepsResponse(date=day, steps=0)
    return record


@router.get("/last/{n}", response_model=list[DailyStepsResponse])
def get_steps_last_n_days(n: int, current_user: UserModel = Depends(get_current_user), db: Session = Depends(get_db)):
    since = date.today() - timedelta(days=n - 1)
    return (
        db.query(DailyStepsModel)
        .filter(DailyStepsModel.user_id == current_user.id, DailyStepsModel.date >= since)
        .order_by(DailyStepsModel.date)
        .all()
    )


@router.get("/range", response_model=list[DailyStepsResponse])
def get_steps_range(start: date = Query(...), end: date = Query(...), current_user: UserModel = Depends(get_current_user), db: Session = Depends(get_db)):
    return (
        db.query(DailyStepsModel)
        .filter(DailyStepsModel.user_id == current_user.id, DailyStepsModel.date >= start, DailyStepsModel.date <= end)
        .order_by(DailyStepsModel.date)
        .all()
    )


@router.delete("/date/{day}", status_code=204)
def delete_steps_by_date(day: date, current_user: UserModel = Depends(get_current_user), db: Session = Depends(get_db)):
    db.query(DailyStepsModel).filter_by(user_id=current_user.id, date=day).delete()
    db.commit()
