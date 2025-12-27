import os
import json
import requests
from typing import Dict, Any


USER_BASE_URL = os.getenv(
    "USER_BASE_URL",
    "http://user-service:8080"  # en K8s: service name + port HTTP du user-service
)


def _safe_json_loads(s: str | None, default):
    if not s:
        return default
    try:
        return json.loads(s)
    except Exception:
        return default


def get_user(user_id: int) -> Dict[str, Any]:
    """
    Appelle le user-service (Spring Boot) via HTTP.
    Retourne un dict prêt pour le recommender:
      {
        "id": ...,
        "fullName": ...,
        "email": ...,
        "role": ...,
        "interests": [...],
        "levels_by_interest": {...}
      }
    """
    url = f"{USER_BASE_URL}/api/users/{user_id}"
    try:
        resp = requests.get(url, timeout=5)
        resp.raise_for_status()
        u = resp.json()

        # Ton ami stocke les listes/maps en string JSON dans interestsJson / levelsByInterestJson
        interests = _safe_json_loads(u.get("interestsJson"), [])
        levels_map = _safe_json_loads(u.get("levelsByInterestJson"), {})

        return {
            "id": u.get("id"),
            "fullName": u.get("fullName"),
            "email": u.get("email"),
            "role": u.get("role"),
            "interests": interests,
            "levels_by_interest": levels_map,
        }
    except requests.RequestException as e:
        raise RuntimeError(f"Failed to fetch user from {url}: {e}")
