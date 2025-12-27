from app.embeddings import embed_text
import numpy as np

v1 = embed_text("I love data science and machine learning")
v2 = embed_text("I enjoy AI and data analysis")

sim = np.dot(v1, v2)
print("Similarity:", sim)
