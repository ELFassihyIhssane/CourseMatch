import asyncio
from types import SimpleNamespace

from app.db import AsyncSessionLocal
import app.recommender as rec


def mock_get_user(user_id: int):
    return SimpleNamespace(
        id=user_id,
        interests=["data-science", "web-dev"],
        level="intermediate",
        levels_by_interest={"data-science": "beginner", "web-dev": "advanced"},
    )


def mock_list_courses():
    return [
        SimpleNamespace(
            id=1,
            title="Intro to Data Science",
            category="data-science",
            level="beginner",
            description="Basics of data analysis, pandas, visualization.",
        ),
        SimpleNamespace(
            id=2,
            title="Advanced React & Frontend",
            category="web-dev",
            level="advanced",
            description="React patterns, performance, state management.",
        ),
        SimpleNamespace(
            id=3,
            title="Machine Learning Fundamentals",
            category="data-science",
            level="intermediate",
            description="Supervised learning, evaluation, scikit-learn.",
        ),
    ]


async def main():
    rec.get_user = mock_get_user
    rec.list_courses = mock_list_courses

    async with AsyncSessionLocal() as db:
        res = await rec.recommend(db, user_id=1, top_k=2)
        print("RECOMMENDATIONS:", res)

        await rec.submit_feedback(db, user_id=1, course_id=res[0][0], value=1)
        res2 = await rec.recommend(db, user_id=1, top_k=2)
        print("AFTER FEEDBACK:", res2)


if __name__ == "__main__":
    asyncio.run(main())
