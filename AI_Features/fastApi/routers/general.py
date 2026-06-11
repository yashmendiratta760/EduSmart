from fastapi import APIRouter, Depends, HTTPException

from utils.auth import get_current_user
from utils.history import convert_history
from models.requests import GeneralRequest
from chains.general_chain import general_chain

router = APIRouter()


@router.post("/chat-general")
def chat_general(
    request: GeneralRequest,
    current_user: dict = Depends(get_current_user)
):
    try:
        response = general_chain.invoke({
            "current_date": request.current_date,
            "user_query": request.user_query,
            "history": convert_history(request.history)
        })

        return {"response": response}

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))