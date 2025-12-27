import os

# =========================
# HTTP base URLs (microservices)
# =========================

USER_BASE_URL = os.getenv(
    "USER_BASE_URL",
    "http://user-service:8005"   # K8s Service name + port
)

COURSE_BASE_URL = os.getenv(
    "COURSE_BASE_URL",
    "http://course-service:8081"  # K8s Service name + port
)

# =========================
# Database (PostgreSQL + asyncpg)
# =========================

DATABASE_URL = os.getenv(
    "DATABASE_URL",
    "postgresql+asyncpg://postgres:ihssane%402003I@localhost:5433/reco_db"
)

# =========================
# Recommendation settings
# =========================

TOP_K_DEFAULT = int(os.getenv("TOP_K_DEFAULT", "5"))

# =========================
# Embedding model
# =========================

EMBEDDING_MODEL = os.getenv(
    "EMBEDDING_MODEL",
    "sentence-transformers/all-MiniLM-L6-v2"
)
