import os
import requests
from typing import List, Dict, Any

# Base URL du course-service (Kubernetes ou local)
COURSE_BASE_URL = os.getenv(
    "COURSE_BASE_URL",
    "http://course-service:8081"  # K8s service name
)

def list_courses() -> List[Dict[str, Any]]:
    """
    Appelle le course-service (Spring Boot) via HTTP
    et retourne la liste des cours (JSON).
    """
    url = f"{COURSE_BASE_URL}/api/courses"
    try:
        resp = requests.get(url, timeout=5)
        resp.raise_for_status()
        return resp.json()  # liste de dicts
    except requests.RequestException as e:
        raise RuntimeError(f"Failed to fetch courses from {url}: {e}")
