from decimal import Decimal
from src.main import create_app
import pytest
import uuid
from src.api_schemas import (
    CreateUserRequest,
    LoginUserRequest,

    CreateRewardRequest,
    AddPointsRequest,
    RedeemRewardRequest,
    UpsertBusinessRequest
)
from client import AppBackendRequester, TestConfig
from src.db.connection import db
from src.db.models import User, BusinessDetails, Enrollment, Reward, BlacklistedToken


@pytest.fixture(autouse=True)
def app():
    app = create_app()
    with app.app_context():
        yield app

@pytest.fixture(autouse=True)
def cleanup_database(app):
    yield
    # Truncate all tables after each test
    db.session.query(Enrollment).delete()
    db.session.query(Reward).delete()
    db.session.query(BusinessDetails).delete()
    db.session.query(BlacklistedToken).delete()
    db.session.query(User).delete()
    db.session.commit()

@pytest.fixture
def client():
    return AppBackendRequester(TestConfig.BASE_URL)

def test_register_user_and_sign_in(client):
    # Register
    username = f"test_user_{uuid.uuid4()}"
    user_data = CreateUserRequest(
        username=username,
        password="password123",
        email_address=f"{username}@test.com",
        is_business_owner=False
    )
    response = client.register(user_data)
    assert response.status_code == 200
    
    # Logout
    response = client.logout()
    assert response.status_code == 200
    
    # Login
    login_data = LoginUserRequest(
        username=username,
        password="password123"
    )
    response = client.login(login_data)
    assert response.status_code == 200
    assert "access_token" in response.json()
    
    # Get current user details
    response = client.get_current_user_details()
    assert response.status_code == 200
    assert response.json()["username"] == username
    assert response.json()["email_address"] == f"{username}@test.com"
    assert response.json()["is_business_owner"] is False
    assert "id" in response.json()
    

def test_upsert_business(client):
    # Register business user
    username = f"business_user_{uuid.uuid4()}"
    user_data = CreateUserRequest(
        username=username,
        password="password123",
        email_address=f"{username}@test.com",
        is_business_owner=True
    )
    response = client.register(user_data)
    assert response.status_code == 200
    
    # Verify no business details exists using /me endpoint
    response = client.get_current_business_details()
    assert response.status_code == 404
    
    # Create business details
    business_details = UpsertBusinessRequest(
        business_name="Test Business",
        description="Test Description",
        email_address="business@test.com",
        address="123 Test St",
        phone_number="123-456-7890"
    )
    response = client.upsert_business(business_details)
    assert response.status_code == 200
    
    # Verify business details using /me endpoint
    response = client.get_current_business_details()
    assert response.status_code == 200
    business_data = response.json()
    assert business_data["details"]["business_name"] == "Test Business"
    assert business_data["details"]["description"] == "Test Description"
    
    # List businesses and verify
    response = client.list_businesses()
    assert response.status_code == 200
    businesses = response.json()
    found_business = next((b for b in businesses if b["details"]["business_name"] == "Test Business"), None)
    assert found_business is not None
    assert found_business["details"]["description"] == "Test Description"
    
    # Update business details
    business_details.description = "Updated Description"
    response = client.upsert_business(business_details)
    assert response.status_code == 200
    
    # Verify update
    response = client.list_businesses()
    assert response.status_code == 200
    businesses = response.json()
    found_business = next((b for b in businesses if b["details"]["business_name"] == "Test Business"), None)
    assert found_business is not None
    assert found_business["details"]["description"] == "Updated Description"

def test_user_enrollment(client):
    # Create business user and business
    business_username = f"business_user_{uuid.uuid4()}"
    business_user = CreateUserRequest(
        username=business_username,
        password="password123",
        email_address=f"{business_username}@test.com",
        is_business_owner=True
    )
    response = client.register(business_user)
    assert response.status_code == 200
    
    business_details = UpsertBusinessRequest(
        business_name="Test Business",
        email_address="business@test.com"
    )
    response = client.upsert_business(business_details)
    assert response.status_code == 200
    
    # Get business ID
    response = client.list_businesses()
    assert response.status_code == 200
    business_id = next(b["details"]["id"] for b in response.json() if b["details"]["business_name"] == "Test Business")
    
    # Create regular user
    client.token = None  # Clear business user token
    username = f"test_user_{uuid.uuid4()}"
    user_data = CreateUserRequest(
        username=username,
        password="password123",
        email_address=f"{username}@test.com",
        is_business_owner=False
    )
    response = client.register(user_data)
    assert response.status_code == 200
    
    # Enroll in business
    response = client.enroll_to_business(business_id)
    assert response.status_code == 200
    
    # Verify enrollment
    response = client.get_user_enrollments()
    assert response.status_code == 200
    enrollments = response.json()
    assert len(enrollments) == 1
    assert enrollments[0]["business"]["business_name"] == "Test Business"
    
    # Cancel enrollment
    response = client.cancel_enrollment(business_id)
    assert response.status_code == 204
    
    # Verify enrollment cancelled
    response = client.get_user_enrollments()
    assert response.status_code == 200
    enrollments = response.json()
    assert len(enrollments) == 0

def test_redeem_reward(client):
    # Create business user and business
    business_username = f"business_user_{uuid.uuid4()}"
    business_user = CreateUserRequest(
        username=business_username,
        password="password123",
        email_address=f"{business_username}@test.com",
        is_business_owner=True
    )
    response = client.register(business_user)
    assert response.status_code == 200
    
    business_details = UpsertBusinessRequest(
        business_name="Test Business",
        email_address="business@test.com"
    )
    response = client.upsert_business(business_details)
    assert response.status_code == 200
    
    # Create reward
    reward_details = CreateRewardRequest(
        name="Test Reward",
        description="Test Description",
        required_points=Decimal(100.0),
    )
    response = client.create_reward(reward_details)
    assert response.status_code == 200
    
    # Get business and reward IDs
    response = client.list_businesses()
    assert response.status_code == 200
    business = next(b for b in response.json() if b["details"]["business_name"] == "Test Business")
    business_id = business["details"]["id"]
    reward_id = business["rewards"][0]["id"]
    
    # Create and enroll customer
    client.token = None
    customer_username = f"customer_{uuid.uuid4()}"
    customer_data = CreateUserRequest(
        username=customer_username,
        password="password123",
        email_address=f"{customer_username}@test.com",
        is_business_owner=False
    )
    response = client.register(customer_data)
    assert response.status_code == 200
    
    response = client.enroll_to_business(business_id)
    assert response.status_code == 200
    
    response = client.get_current_user_details()
    assert response.status_code == 200
    user_id = response.json()["id"]
    
    # Try to redeem reward without enough points
    redeem_data = RedeemRewardRequest(
        user_id=user_id,
        reward_id=reward_id
    )
    client.token = None
    response = client.login(LoginUserRequest(username=business_username, password="password123"))
    assert response.status_code == 200
    
    response = client.redeem_reward(redeem_data)
    assert response.status_code == 400  # Should fail due to insufficient points
    
    # Add points
    points_data = AddPointsRequest(
        user_id=user_id,
        points=Decimal(150.0)
    )
    response = client.add_points(points_data)
    assert response.status_code == 200
    
    # Try redeem again
    response = client.redeem_reward(redeem_data)
    assert response.status_code == 200
    response_data = response.json()
    assert response_data["success"] is True
    assert Decimal(response_data["new_points_balance"]) == Decimal(50.0)  # 150 - 100


