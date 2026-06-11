from fastapi import FastAPI
from routers import upload, rag, planner, general
 
app = FastAPI()
 
app.include_router(upload.router)
app.include_router(rag.router)
app.include_router(planner.router)
app.include_router(general.router)
 
 
@app.get("/check")
def check():
    return {"message": "Hello"}
 