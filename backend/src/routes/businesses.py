from flask import Blueprint, request, jsonify, g, current_app
from src.db.models import BusinessDetails, Reward, User, Enrollment
from src.api_schemas import (
    BusinessDetailsModel,
    CreateRewardRequest,
    RewardModel,
    UserModel,
    EnrollmentDetailsResponse,
    GetBusinessEnrollmentResponse,
    BusinessWithRewards,
    UpsertBusinessRequest,
    UpdateRewardRequest,
)
from src.db.connection import db
from .auth_middleware import require_business_owner

bp = Blueprint('businesses', __name__, url_prefix='/businesses')

@bp.route('', methods=['GET'])
def list_businesses():
    print(f"Fetching all businesses")
    current_app.logger.warning(f"Fetching all businesses")
    businesses = BusinessDetails.query.all()
    print(f"Found {len(businesses)} businesses")
    result = []
    for business in businesses:
        response = BusinessWithRewards(
            details=BusinessDetailsModel(
                id=business.id,
                business_name=business.business_name,
                description=business.description,
                email_address=business.email_address,
                address=business.address,
                phone_number=business.phone_number
            ),
            rewards=[RewardModel(
                id=reward.id,
                name=reward.name,
                business_id=reward.business_id,
                usage_count=reward.usage_count,
                description=reward.description,
                required_points=reward.required_points,
            ) for reward in business.rewards]
        )
        result.append(response.model_dump())
    
    return jsonify(result)

@bp.route('/me', methods=['GET'])
@require_business_owner
def get_current_business_details():
    print(f"Fetching current business details for user_id: {g.user.id}")
    business = BusinessDetails.query.filter_by(user_id=g.user.id).first()
    
    if not business:
        return jsonify({"error": "No business details found for this user"}), 404
    
    response = BusinessWithRewards(
            details=BusinessDetailsModel(
                id=business.id,
                business_name=business.business_name,
                description=business.description,
                email_address=business.email_address,
                address=business.address,
                phone_number=business.phone_number
            ),
            rewards=[RewardModel(
                id=reward.id,
                name=reward.name,
                business_id=reward.business_id,
                usage_count=reward.usage_count,
                description=reward.description,
                required_points=reward.required_points,
            ) for reward in business.rewards]
        )
    
    return jsonify(response.model_dump())

@bp.route('', methods=['PUT'])
@require_business_owner
def upsert_business():
    print(f"Upserting business for user_id: {g.user.id}")
    request_data = UpsertBusinessRequest.model_validate(request.get_json())
    business = BusinessDetails.query.filter_by(user_id=g.user.id).first()
    
    if not business:
        business = BusinessDetails(user_id=g.user.id)
        db.session.add(business)
    
    business.business_name = request_data.business_name
    business.description = request_data.description
    business.email_address = request_data.email_address
    business.address = request_data.address
    business.phone_number = request_data.phone_number
    
    db.session.commit()
    print(f"Business {'created' if not business else 'updated'} successfully")
    return '', 200

@bp.route('/enrollments', methods=['GET'])
@require_business_owner
def get_business_enrollments():
    print(f"Fetching enrollments for business owned by user_id: {g.user.id}")
    # Get the business owned by the authenticated user
    business = BusinessDetails.query.filter_by(user_id=g.user.id).first()
    if not business:
        return jsonify({"error": "Business not found"}), 404

    # Get all enrollments for this business
    enrollments = Enrollment.query.filter_by(business_id=business.id).all()
    
    print(f"Found {len(enrollments)} enrollments")
    
    result = []
    for enrollment in enrollments:
        user = User.query.get(enrollment.user_id)
        response = GetBusinessEnrollmentResponse(
            user=UserModel.model_validate(user),
            enrollment=EnrollmentDetailsResponse.model_validate(enrollment)
        )
        result.append(response.model_dump())
    
    return jsonify(result)

@bp.route('/rewards', methods=['POST'])
@require_business_owner
def create_reward():
    print(f"Creating new reward for business owned by user_id: {g.user.id}")
    # Get the business owned by the authenticated user
    business = BusinessDetails.query.filter_by(user_id=g.user.id).first()
    if not business:
        return jsonify({"error": "Business not found"}), 404

    # Validate and create the reward
    request_data = CreateRewardRequest.model_validate(request.get_json())
    
    reward = Reward(
        business_id=business.id,
        name=request_data.name,
        description=request_data.description,
        required_points=request_data.required_points,
        valid_from_timestamp=request_data.valid_from_timestamp,
        valid_until_timestamp=request_data.valid_until_timestamp
    )
    
    db.session.add(reward)
    db.session.commit()
    print(f"Created reward: {reward.id} - {reward.name}")
    return '', 200

@bp.route('/rewards/<int:reward_id>', methods=['DELETE'])
@require_business_owner
def delete_reward(reward_id):
    print(f"Attempting to delete reward {reward_id}")
    # Get the business owned by the authenticated user
    business = BusinessDetails.query.filter_by(user_id=g.user.id).first()
    if not business:
        return jsonify({"error": "Business not found"}), 404

    # Find the reward and verify it belongs to this business
    reward = Reward.query.get(reward_id)
    if not reward:
        return jsonify({"error": "Reward not found"}), 404
        
    if reward.business_id != business.id:
        return jsonify({"error": "Unauthorized"}), 403
    
    db.session.delete(reward)
    db.session.commit()
    print(f"Successfully deleted reward {reward_id}")
    return '', 200

@bp.route('/rewards/<int:reward_id>', methods=['PUT'])
@require_business_owner
def update_reward(reward_id):
    print(f"Updating reward {reward_id} for user_id: {g.user.id}")
    business = BusinessDetails.query.filter_by(user_id=g.user.id).first()
    if not business:
        return jsonify({"error": "Business not found"}), 404

    reward = Reward.query.get(reward_id)
    if not reward:
        return jsonify({"error": "Reward not found"}), 404
        
    if reward.business_id != business.id:
        return jsonify({"error": "Unauthorized"}), 403

    request_data = UpdateRewardRequest.model_validate(request.get_json())
    
    # Only update fields that were provided in the request
    if request_data.name is not None:
        reward.name = request_data.name
    if request_data.description is not None:
        reward.description = request_data.description
    if request_data.required_points is not None:
        reward.required_points = request_data.required_points
    if request_data.valid_from_timestamp is not None:
        reward.valid_from_timestamp = request_data.valid_from_timestamp
    if request_data.valid_until_timestamp is not None:
        reward.valid_until_timestamp = request_data.valid_until_timestamp
    
    db.session.commit()
    print(f"Successfully updated reward {reward_id}")
    return '', 200
