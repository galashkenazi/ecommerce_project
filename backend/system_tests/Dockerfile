FROM python:3.11-slim

WORKDIR /app

# Copy requirements first to leverage Docker cache
COPY system_tests/requirements.txt .
RUN pip install -r requirements.txt

# Copy backend source code first

RUN ls -la

COPY src/ /app/src/

# Copy test files
COPY system_tests/ /app/

# Set Python path to include the app directory
ENV PYTHONPATH=/app

# Command to run tests
CMD ["python", "-m", "pytest", "-v"]