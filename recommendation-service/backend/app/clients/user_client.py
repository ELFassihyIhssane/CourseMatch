import grpc
from app.config import USER_GRPC_HOST, USER_GRPC_PORT

# generated modules (after protoc)
from app.protos_gen import user_pb2, user_pb2_grpc

def get_user(user_id: int) -> user_pb2.User:
    target = f"{USER_GRPC_HOST}:{USER_GRPC_PORT}"
    with grpc.insecure_channel(target) as channel:
        stub = user_pb2_grpc.UserServiceStub(channel)
        return stub.GetUser(user_pb2.GetUserRequest(user_id=user_id), timeout=5)
