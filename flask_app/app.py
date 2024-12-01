from flask import Flask, request, jsonify
from scipy.spatial.distance import cosine
import numpy as np

app = Flask(__name__)

# 단어 임베딩 데이터 로드 (예: {"word": [0.1, 0.2, ...]})
embeddings = {
    "apple": np.array([0.1, 0.2, 0.3]),
    "orange": np.array([0.3, 0.1, 0.4]),
    "banana": np.array([0.2, 0.4, 0.1])
}

# 유사도 계산 함수
def calculate_similarity(vec1, vec2):
    return 1 - cosine(vec1, vec2)  # 코사인 유사도 계산

# /getSimilarity 엔드포인트
@app.route('/getSimilarity', methods=['GET'])
def get_similarity():
    # 쿼리 매개변수 가져오기
    correct_word = request.args.get('correctWord')
    guessed_word = request.args.get('guessedWord')

    # 매개변수 유효성 검사
    if correct_word not in embeddings or guessed_word not in embeddings:
        return jsonify({"error": "Word not found in embeddings"}), 400

    # 임베딩 벡터 가져오기
    correct_vector = embeddings[correct_word]
    guessed_vector = embeddings[guessed_word]

    # 유사도 계산
    similarity = calculate_similarity(correct_vector, guessed_vector)

    # 결과 반환
    return jsonify({"similarity": similarity})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
