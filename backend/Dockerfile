FROM python:3.11-slim

WORKDIR /app
ENV FLASK_APP=src.main

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
RUN pip install alembic

COPY . .

CMD ["python", "-u", "-m", "src.main"]
