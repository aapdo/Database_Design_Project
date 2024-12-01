# Python 베이스 이미지
FROM python:3.9-slim

# 작업 디렉토리 설정
WORKDIR /app

# requirements.txt 복사 및 의존성 설치
COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

# Flask 애플리케이션 복사
COPY . .

# Flask 애플리케이션 실행
CMD ["python", "index.py"]