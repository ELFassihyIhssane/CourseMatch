from __future__ import annotations

from sentence_transformers import SentenceTransformer
from app.config import EMBEDDING_MODEL

_model = SentenceTransformer(EMBEDDING_MODEL)

def embed_text(text: str) -> list[float]:
    """
    Retourne un embedding 1D list[float] (len=384).
    """
    vec = _model.encode(text, normalize_embeddings=True)  # shape (384,)
    vec = vec.tolist()

    # safety: si jamais ça revient 2D
    if vec and isinstance(vec[0], list):
        vec = vec[0]

    return vec
