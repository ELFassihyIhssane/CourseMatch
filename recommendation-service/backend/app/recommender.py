from __future__ import annotations

from typing import Iterable

from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select, desc, func, text

from app.embeddings import embed_text
from app.models import CourseEmbedding, RecommendationLog, Feedback
from app.clients.user_client import get_user
from app.clients.course_client import list_courses
from app.config import TOP_K_DEFAULT


EMBED_DIM = 384


def _normalize_vector(vec: Iterable) -> list[float]:
    if vec is None:
        raise ValueError("Embedding is None")

    if isinstance(vec, list) and vec and isinstance(vec[0], list):
        vec = vec[0]

    vec = list(map(float, vec))

    if len(vec) != EMBED_DIM:
        raise ValueError(f"Expected {EMBED_DIM}, got {len(vec)}")

    return vec


def _course_text(c) -> str:
    return f"{c.title}. Category: {c.category}. Level: {c.level}. {c.description or ''}"


def _build_user_profile(user) -> str:
    interests = list(getattr(user, "interests", []) or [])
    levels_map = dict(getattr(user, "levels_by_interest", {}) or {})

    parts: list[str] = []
    for it in interests:
        lvl = levels_map.get(it)
        parts.append(f"{it} Level: {lvl}" if lvl else it)

    global_level = getattr(user, "level", None)
    if global_level:
        parts.append(f"Global Level: {global_level}")

    return " | ".join(parts) if parts else "Global Level: unknown"


async def _ensure_course_embeddings(db: AsyncSession, courses):
    existing = set(
        (await db.execute(select(CourseEmbedding.course_id))).scalars().all()
    )

    created = 0
    for c in courses:
        if c.id in existing:
            continue

        vec = _normalize_vector(embed_text(_course_text(c)))

        db.add(
            CourseEmbedding(
                course_id=c.id,
                category=c.category,
                level=c.level,
                embedding=vec,  # on laisse SQLAlchemy insert
            )
        )
        created += 1

    if created:
        await db.commit()


def _vec_to_pgvector_literal(vec: list[float]) -> str:
    """
    Convertit list[float] vers le format attendu par pgvector côté SQL:
    '[0.1,0.2,...]'
    """
    # IMPORTANT: format compact, pas de scientific notation extrême
    return "[" + ",".join(f"{x:.10f}" for x in vec) + "]"


async def recommend(db: AsyncSession, user_id: int, top_k: int | None = None):
    top_k = top_k or TOP_K_DEFAULT

    user = get_user(user_id)
    courses = list_courses()

    await _ensure_course_embeddings(db, courses)

    user_vec = _normalize_vector(embed_text(_build_user_profile(user)))
    user_vec_sql = _vec_to_pgvector_literal(user_vec)

    # ✅ SQL brut: on cast explicitement en ::vector, zéro magie SQLAlchemy/pgvector
    stmt = text("""
        SELECT course_id,
               (1.0 - (embedding <=> (:user_vec)::vector)) AS sim
        FROM course_embeddings
        ORDER BY sim DESC
        LIMIT :limit
    """)

    rows = (await db.execute(stmt, {"user_vec": user_vec_sql, "limit": int(top_k * 3)})).all()

    fb_stmt = (
        select(Feedback.course_id, func.sum(Feedback.value))
        .where(Feedback.user_id == user_id)
        .group_by(Feedback.course_id)
    )
    feedback = dict((await db.execute(fb_stmt)).all())

    results: list[tuple[int, float]] = []
    for cid, sim in rows:
        bonus = 0.05 * float(feedback.get(cid, 0) or 0)
        score = max(0.0, min(1.0, float(sim or 0.0) + bonus))
        results.append((int(cid), round(score, 4)))

    results.sort(key=lambda x: x[1], reverse=True)
    final = results[:top_k]

    for cid, score in final:
        db.add(RecommendationLog(user_id=user_id, course_id=cid, score=score))

    await db.commit()
    return final


async def submit_feedback(db: AsyncSession, user_id: int, course_id: int, value: int):
    if value not in (-1, 1):
        raise ValueError("value must be +1 or -1")

    db.add(Feedback(user_id=user_id, course_id=course_id, value=value))
    await db.commit()
