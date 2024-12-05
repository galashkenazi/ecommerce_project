from typing import Optional
from src.api_schemas import (
    CreateUserRequest, 
    LoginUserRequest,
    CreateRewardRequest,
    UpdateRewardRequest,
    AddPointsRequest,
    RedeemRewardRequest,
    UpsertBusinessRequest
)
import requests
import uuid

class TestConfig:
    BASE_URL = "http://app_backend:5000"

class AppBackendRequester:
    def __init__(self, base_url: str):
        self.base_url = base_url
        self.token: Optional[str] = None
        
    def _headers(self) -> dict:
        if self.token:
            return {"Authorization": f"Bearer {self.token}"}
        return {}
        
    def register(self, user_data: CreateUserRequest) -> requests.Response:
        response = requests.post(
            f"{self.base_url}/auth/register",
            json=user_data.model_dump(mode="json")
        )
        if response.ok:
            self.token = response.json()["access_token"]
        return response
        
    def login(self, login_data: LoginUserRequest) -> requests.Response:
        response = requests.post(
            f"{self.base_url}/auth/login",
            json=login_data.model_dump(mode="json")
        )
        if response.ok:
            self.token = response.json()["access_token"]
        return response
        
    def logout(self) -> requests.Response:
        response = requests.post(
            f"{self.base_url}/auth/logout",
            headers=self._headers()
        )
        if response.ok:
            self.token = None
        return response

    def get_current_user_details(self) -> requests.Response:
        response = requests.get(
            f"{self.base_url}/auth/me",
            headers=self._headers()
        )
        return response
        
    def list_businesses(self) -> requests.Response:
        return requests.get(
            f"{self.base_url}/businesses",
            headers=self._headers()
        )
        
    def upsert_business(self, upsert_business_request: UpsertBusinessRequest) -> requests.Response:
        return requests.put(
            f"{self.base_url}/businesses",
            headers=self._headers(),
            json=upsert_business_request.model_dump(mode="json")
        )
        
    def create_reward(self, reward_details: CreateRewardRequest) -> requests.Response:
        return requests.post(
            f"{self.base_url}/businesses/rewards",
            headers=self._headers(),
            json=reward_details.model_dump(mode="json")
        )
        
    def delete_reward(self, reward_id: uuid.UUID) -> requests.Response:
        return requests.delete(
            f"{self.base_url}/businesses/rewards/{reward_id}",
            headers=self._headers()
        )
        
    def update_reward(self, reward_id: uuid.UUID, reward_details: UpdateRewardRequest) -> requests.Response:
        return requests.put(
            f"{self.base_url}/businesses/rewards/{reward_id}",
            headers=self._headers(),
            json=reward_details.model_dump(mode="json")
        )
        
    def get_user_enrollments(self) -> requests.Response:
        return requests.get(
            f"{self.base_url}/enrollments/me",
            headers=self._headers()
        )
        
    def enroll_to_business(self, business_id: uuid.UUID) -> requests.Response:
        return requests.post(
            f"{self.base_url}/enrollments/businesses/{business_id}",
            headers=self._headers()
        )
        
    def cancel_enrollment(self, business_id: uuid.UUID) -> requests.Response:
        return requests.delete(
            f"{self.base_url}/enrollments/businesses/{business_id}",
            headers=self._headers()
        )

    def add_points(self, points_data: AddPointsRequest) -> requests.Response:
        return requests.post(
            f"{self.base_url}/enrollments/add_points",
            headers=self._headers(),
            json=points_data.model_dump(mode="json")
        )

    def redeem_reward(self, redeem_data: RedeemRewardRequest) -> requests.Response:
        return requests.post(
            f"{self.base_url}/enrollments/redeem_reward",
            headers=self._headers(),
            json=redeem_data.model_dump(mode="json"),
        )
