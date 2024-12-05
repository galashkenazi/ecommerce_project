from decimal import Decimal
from pydantic import BaseModel, UUID4
from typing import Optional, List
from datetime import datetime


class CreateUserRequest(BaseModel):
    username: str
    password: str
    email_address: str
    is_business_owner: bool = False


class LoginUserRequest(BaseModel):
    username: str
    password: str


class BusinessDetailsModel(BaseModel):
    id: UUID4
    business_name: str
    description: Optional[str] = None
    email_address: str
    address: Optional[str] = None
    phone_number: Optional[str] = None


class UpsertBusinessRequest(BaseModel):
    business_name: str
    description: Optional[str] = None
    email_address: str
    address: Optional[str] = None
    phone_number: Optional[str] = None


class CreateRewardRequest(BaseModel):
    name: str
    description: Optional[str] = None
    required_points: Decimal
    valid_from_timestamp: Optional[datetime] = None
    valid_until_timestamp: Optional[datetime] = None


class UpdateRewardRequest(BaseModel):
    name: Optional[str] = None
    description: Optional[str] = None
    required_points: Optional[float] = None
    valid_from_timestamp: Optional[datetime] = None
    valid_until_timestamp: Optional[datetime] = None


class RewardModel(CreateRewardRequest):
    id: UUID4
    name: str
    business_id: UUID4
    usage_count: int
    description: Optional[str] = None
    required_points: Decimal
    valid_from_timestamp: Optional[datetime] = None
    valid_until_timestamp: Optional[datetime] = None


class EnrollmentResponse(BaseModel):
    id: UUID4
    business: BusinessDetailsModel
    points: Decimal


class AddPointsRequest(BaseModel):
    user_id: UUID4
    points: Decimal


class RedeemRewardRequest(BaseModel):
    user_id: UUID4
    reward_id: UUID4


class TokenResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"


class UserModel(BaseModel):
    id: UUID4
    username: str
    email_address: str
    is_business_owner: bool


class EnrollmentModel(BaseModel):
    id: UUID4
    user_id: UUID4
    business_id: UUID4
    points: Decimal


class EnrollmentDetailsResponse(EnrollmentModel):
    pass


class GetBusinessEnrollmentResponse(BaseModel):
    user: UserModel
    enrollment: EnrollmentModel


class GetUserEnrollmentsResponse(BaseModel):
    business: BusinessDetailsModel
    enrollment: EnrollmentModel


class EnrollBusinessRequest(BaseModel):
    business_id: UUID4


class EnrollBusinessResponse(BaseModel):
    message: str


class AddPointsResponse(BaseModel):
    user_id: UUID4
    new_points_balance: Decimal


class RedeemRewardResponse(BaseModel):
    success: bool
    new_points_balance: Decimal


class ListBusinessesResponse(BaseModel):
    details: BusinessDetailsModel
    rewards: List[RewardModel]
