import threading
from fastapi import FastAPI

from app.migrations import init_db
from app.grpc_server import serve

app = FastAPI(title="Recommendation Service (gRPC + AI + Postgres)")

@app.on_event("startup")
async def startup():
    await init_db()
    t = threading.Thread(target=serve, daemon=True)
    t.start()

@app.get("/health")
def health():
    return {"status": "ok", "grpc": "running"}
