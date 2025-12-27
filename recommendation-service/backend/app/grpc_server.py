import asyncio
import grpc
from concurrent.futures import ThreadPoolExecutor

from app.config import RECO_GRPC_PORT
from app.db import AsyncSessionLocal
from app.recommender import recommend, submit_feedback

from app.protos_gen import recommendation_pb2, recommendation_pb2_grpc

class RecommendationServicer(recommendation_pb2_grpc.RecommendationServiceServicer):
    def GetRecommendations(self, request, context):
        user_id = request.user_id
        top_k = request.top_k if request.top_k > 0 else None

        async def _run():
            async with AsyncSessionLocal() as db:
                recos = await recommend(db, user_id=user_id, top_k=top_k)
                return recos

        recos = asyncio.run(_run())
        return recommendation_pb2.GetRecommendationsResponse(
            user_id=user_id,
            recommendations=[
                recommendation_pb2.Recommendation(course_id=cid, score=score)
                for cid, score in recos
            ],
        )

    def SubmitFeedback(self, request, context):
        async def _run():
            async with AsyncSessionLocal() as db:
                await submit_feedback(db, request.user_id, request.course_id, request.value)

        try:
            asyncio.run(_run())
            return recommendation_pb2.SubmitFeedbackResponse(status="ok")
        except Exception as e:
            context.set_code(grpc.StatusCode.INTERNAL)
            context.set_details(str(e))
            return recommendation_pb2.SubmitFeedbackResponse(status="error")

def serve():
    server = grpc.server(ThreadPoolExecutor(max_workers=10))
    recommendation_pb2_grpc.add_RecommendationServiceServicer_to_server(RecommendationServicer(), server)
    server.add_insecure_port(f"0.0.0.0:{RECO_GRPC_PORT}")
    server.start()
    server.wait_for_termination()
