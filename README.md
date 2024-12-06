# Loyalty Rewards System

A full-stack loyalty rewards platform enabling businesses to create and manage reward programs while allowing customers to earn and redeem points across different businesses.

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register new user |
| POST | `/auth/login` | Login user |
| POST | `/auth/logout` | Logout user |
| GET  | `/auth/me` | Get current user details |

### Businesses
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/businesses` | List all businesses |
| PUT | `/businesses` | Update business details |

### Rewards
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/rewards` | Create new reward |
| DELETE | `/rewards/{reward_id}` | Delete reward |
| PUT | `/rewards/{reward_id}` | Update reward |
| GET | `/rewards/{reward_id}` | Get reward details |

### Enrollments
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/enrollments/me` | Get user enrollments |
| POST | `/enrollments/businesses/{id}` | Enroll in business for a user |
| DELETE | `/enrollments/businesses/{id}` | Cancel enrollment for a user |
| POST | `/enrollments/add_points` | Add points |
| POST | `/enrollments/redeem_reward` | Redeem reward |

## Backend

### Tech Stack
- **Python 3.11**: Core programming language
- **Flask**: Web framework
- **PostgreSQL**: Database
- **SQLAlchemy**: ORM for database operations
- **Alembic**: Database migrations
- **Docker**: Containerization
- **Pydantic**: Data validation and settings management

### Prerequisites
- Docker and Docker Compose
- Python 3.11
- pip (Python package installer)

### Local Development Setup

#### Setting Up Virtual Environment
```bash
# Navigate to backend directory
cd backend

# Create virtual environment
virtualenv --python=python3.11 --prompt ecommerce .venv

# Activate virtual environment
# On Windows
.venv\Scripts\activate
# On macOS/Linux
source .venv/bin/activate

# Install dependencies
pip install -r requirements.txt
```

#### Running with Docker
```bash
# Navigate to backend directory
cd backend

# Build and start all services in background
docker-compose up --build -d app_backend

# Stop services
docker-compose down
```

#### Database Migrations
After the app_backend container is running, you can manage database migrations:

```bash
# Navigate to backend directory
cd backend

# Initialize migrations (first time only)
docker-compose exec app_backend flask db init

# Create a new migration
docker-compose exec app_backend flask db migrate -m "Description of changes"

# Apply migrations
docker-compose exec app_backend flask db upgrade

# Rollback migrations
docker-compose exec app_backend flask db downgrade
```

#### Running System Tests
This project uses `pytest` for system testing. System tests externally validate the backend system by sending requests to the running container, simulating mobile app interactions and testing API endpoints for critical application flows.

```bash
# Navigate to backend directory
cd backend

# Start the system tests container
docker-compose up --build -d system_tests

# Run a specific test
docker-compose run system_tests python -m pytest -v -k <test_name>
```
