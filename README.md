# E-Commerce Appications Project

A full-stack loyalty rewards platform enabling small businesses to create and manage reward programs while allowing customers to earn and redeem points across different businesses.

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
| GET | `/businesses/me` | Get current user's business details |
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

#### System Tests
The project includes comprehensive system tests that validate critical flows including:
- User authentication and registration
- Business management
- Reward creation and redemption
- Points management
- Business similarity recommendations

## Mobile App

### Tech Stack
- **Kotlin**: Primary programming language
- **Jetpack Compose**: Modern UI toolkit for native Android UI
- **Material3**: Design system for consistent and modern UI components
- **Ktor Client**: HTTP client for API communication
- **Kotlinx Serialization**: JSON serialization/deserialization
- **DataStore**: Data persistence for token storage
- **Android Studio**: Official IDE for Android development

### Key Features
- **State Management**: Centralized state handling through `AppState` using Kotlin Flows and Compose State
- **Navigation**: Type-safe navigation using Jetpack Navigation Compose
- **API Communication**: Ktor client with kotlinx.serialization for type-safe API calls
- **UI Components**: Reusable components following Material3 design guidelines
- **Preview Support**: Design-time previews for UI components
- **Error Handling**: Wrapped responses using Resource sealed class

#### Business Similarity Algorithm
The platform includes a recommendation system that suggests similar businesses based on user enrollment patterns. It uses the Jaccard similarity coefficient to measure the overlap between businesses' customer bases. The algorithm:
- Compares user enrollment sets between businesses
- Returns up to 3 most similar businesses for each business
- Handles edge cases (no enrollments, single business)
- Has comprehensive system tests validating the functionality

### Development Setup
1. Install Android Studio (Latest stable version)
2. Clone the repository
3. Open the project in Android Studio
4. Configure the API endpoint in `ApiService.kt`
5. Run on an emulator or physical device

### UI Patterns
The app follows a consistent pattern for screen development:
- Each screen is split into two components:
  - A stateful screen component that connects to AppState
  - A stateless content component for pure UI rendering
- Preview support for all UI components
- Material3 theming throughout the app
- Common navigation patterns and layouts


### Building and Running
1. Connect an Android device or start an emulator
2. Make sure the backend server is running and accessible
3. Update the `BASE_URL` in `ApiService.kt` to point to your server
4. Click "Run" in Android Studio or use:
```bash
./gradlew installDebug
```
