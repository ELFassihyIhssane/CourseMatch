from __future__ import annotations

from fastapi import FastAPI, Depends, HTTPException, Query
from pydantic import BaseModel, Field
from sqlalchemy.ext.asyncio import AsyncSession

from app.db import get_db
from app.migrations import init_db
from app.recommender import recommend as recommend_fn, submit_feedback as submit_feedback_fn

app = FastAPI(title="Recommendation Service (HTTP + AI + Postgres)")


@app.on_event("startup")
async def startup():
    # initialise pgvector + tables
    await init_db()


@app.get("/health")
def health():
    return {"status": "ok", "mode": "http"}


@app.get("/api/recommendations/{user_id}")
async def get_recommendations(
    user_id: int,
    top_k: int = Query(default=5, ge=1, le=50),
    db: AsyncSession = Depends(get_db),
):
    """
    Retourne une liste: [[course_id, score], ...]
    """
    try:
        results = await recommend_fn(db, user_id=user_id, top_k=top_k)
        return {
            "user_id": user_id,
            "top_k": top_k,
            "results": [{"course_id": cid, "score": score} for cid, score in results],
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


class FeedbackPayload(BaseModel):
    user_id: int = Field(..., ge=1)
    course_id: int = Field(..., ge=1)
    value: int = Field(..., description="Must be +1 or -1")


@app.post("/api/recommendations/feedback")
async def post_feedback(
    payload: FeedbackPayload,
    db: AsyncSession = Depends(get_db),
):
    try:
        await submit_feedback_fn(
            db,
            user_id=payload.user_id,
            course_id=payload.course_id,
            value=payload.value,
        )
        return {"status": "ok"}
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
