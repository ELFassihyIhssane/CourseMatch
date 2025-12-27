import grpc
from app.config import COURSE_GRPC_HOST, COURSE_GRPC_PORT

from app.protos_gen import course_pb2, course_pb2_grpc

def list_courses() -> list[course_pb2.Course]:
    target = f"{COURSE_GRPC_HOST}:{COURSE_GRPC_PORT}"
    with grpc.insecure_channel(target) as channel:
        stub = course_pb2_grpc.CourseServiceStub(channel)
        resp = stub.ListCourses(course_pb2.ListCoursesRequest(), timeout=5)
        return list(resp.courses)
