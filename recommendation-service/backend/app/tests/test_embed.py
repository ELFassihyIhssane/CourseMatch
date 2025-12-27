from app.embeddings import embed_text

vec = embed_text("I like data science and machine learning")
print(len(vec), vec[:5])