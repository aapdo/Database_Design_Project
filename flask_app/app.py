from flask import Flask, request, jsonify
from scipy.spatial.distance import cosine
import numpy as np
import faiss
import random

app = Flask(__name__)

# FAISS 인덱스와 데이터 로드
class VectorDB:
    def __init__(self, nouns_path, embeddings_path):
        # 단어 목록과 임베딩 로드
        self.nouns = np.load(nouns_path)
        self.embeddings = np.load(embeddings_path).astype(np.float32)

        # FAISS 인덱스 생성 (코사인 유사도를 위한 인덱스)
        self.index = faiss.IndexFlatIP(self.embeddings.shape[1])
        faiss.normalize_L2(self.embeddings)  # 코사인 유사도를 위해 정규화
        self.index.add(self.embeddings)

        # 단어와 벡터 인덱스 매핑
        self.word_to_index = {word: idx for idx, word in enumerate(self.nouns)}

    def get_vector(self, word):
        """단어에 해당하는 벡터를 가져옵니다."""
        if word not in self.word_to_index:
            return None
        idx = self.word_to_index[word]
        return self.embeddings[idx]

    def calculate_similarity(self, word1, word2):
        """두 단어의 코사인 유사도를 계산합니다."""
        vec1 = self.get_vector(word1)
        vec2 = self.get_vector(word2)
        if vec1 is None or vec2 is None:
            return None
        return 1 - cosine(vec1, vec2)

    def get_random_word(self):
        """저장된 단어 중 하나를 랜덤으로 반환합니다."""
        return random.choice(self.nouns)

# VectorDB 객체 (전역 변수로 선언)
vector_db = None

# /getSimilarity 엔드포인트
@app.route('/getSimilarity', methods=['GET'])
def get_similarity():
    global vector_db

    # 쿼리 매개변수 가져오기
    correct_word = request.args.get('correctWord')
    guessed_word = request.args.get('guessedWord')

    # 매개변수 유효성 검사
    if correct_word not in vector_db.word_to_index:
        return jsonify({"error": "Correct word not found in embeddings"}), 400

    if guessed_word not in vector_db.word_to_index:
        return jsonify({"error": "Guessed word not found in embeddings"}), 501

    # 유사도 계산
    similarity = vector_db.calculate_similarity(correct_word, guessed_word)
    if similarity is None:
        return jsonify({"error": "Error calculating similarity"}), 500

    # 결과 반환
    return jsonify({"similarity": similarity})

# /randomWord 엔드포인트
@app.route('/randomWord', methods=['GET'])
def random_word():
    global vector_db

    # 랜덤 단어 반환
    random_word = vector_db.get_random_word()
    return jsonify({"randomWord": random_word})

if __name__ == '__main__':
    # 전역 변수 초기화
    NOUNS_PATH = "/app/data/nouns.npy"
    EMBEDDINGS_PATH = "/app/data/embeddings.npy"

    vector_db = VectorDB(NOUNS_PATH, EMBEDDINGS_PATH)
    print("VectorDB 초기화 완료")

    # Flask 실행
    app.run(host='0.0.0.0', port=5000)
