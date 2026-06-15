from pydantic import BaseModel


class ChatRequest(BaseModel):
    query: str
    history: list = []


class PlannerRequest(BaseModel):
    user_query: str
    current_date: str
    tests: str = ""
    assignments: str = ""
    timetable: str = ""
    holidays: str = ""
    user_response: str = ""
    career_goal: str
    preferred_activities: str
    available_hours: str
    history: list = []


class GeneralRequest(BaseModel):
    user_query: str
    current_date: str
    history: list = []



class UploadRequest(BaseModel):
    file_url: str