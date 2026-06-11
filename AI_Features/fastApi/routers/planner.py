from fastapi import APIRouter, Depends, HTTPException

from utils.auth import get_current_user
from utils.history import convert_history
from models.requests import PlannerRequest
from chains.planner_chain import planner_chain

router = APIRouter()


@router.post("/planner")
def planner(
    request: PlannerRequest,
    current_user: dict = Depends(get_current_user)
):
    try:
        response = planner_chain.invoke({
            "current_date": request.current_date,
            "tests": request.tests,
            "assignments": request.assignments,
            "timetable": request.timetable,
            "holidays": request.holidays,
            "user_response": request.user_response,
            "career_goal": request.career_goal,
            "preferred_activities": request.preferred_activities,
            "available_hours": request.available_hours,
            "user_query": request.user_query,
            "history": convert_history(request.history)
        })

        return {"response": response}

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))