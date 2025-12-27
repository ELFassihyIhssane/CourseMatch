import os

# gRPC endpoints (K8s DNS names)
USER_GRPC_HOST = os.getenv("USER_GRPC_HOST", "user-service")
USER_GRPC_PORT = int(os.getenv("USER_GRPC_PORT", "50051"))

COURSE_GRPC_HOST = os.getenv("COURSE_GRPC_HOST", "course-service")
COURSE_GRPC_PORT = int(os.getenv("COURSE_GRPC_PORT", "50052"))

RECO_GRPC_PORT = int(os.getenv("RECO_GRPC_PORT", "50054"))

# Postgres
DATABASE_URL = os.getenv(
    "DATABASE_URL",
    "postgresql+asyncpg://postgres:ihssane%402003I@localhost:5433/reco_db"
)

TOP_K_DEFAULT = int(os.getenv("TOP_K_DEFAULT", "5"))

# Embedding model
EMBEDDING_MODEL = os.getenv("EMBEDDING_MODEL", "sentence-transformers/all-MiniLM-L6-v2")
