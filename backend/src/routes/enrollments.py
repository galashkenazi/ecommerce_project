from decimal import Decimal
from flask import Blueprint, request, jsonify, g
from src.db.models import Enrollment, BusinessDetails, Reward
from src.api_schemas import (
    AddPointsRequest,
    RedeemRewardRequest,
    BusinessDetailsModel,
    GetUserEnrollmentsResponse,
    EnrollmentModel,
    EnrollBusinessResponse,
    AddPointsResponse,
    RedeemRewardResponse
)
from src.db.connection import db
from .auth_middleware import require_auth, require_business_owner

bp = Blueprint('enrollments', __name__, url_prefix='/enrollments')

@bp.route('/me', methods=['GET'])
@require_auth
def get_user_enrollments():
    print(f'Fetching enrollments for user {g.user.id}')
    enrollments = Enrollment.query.filter_by(user_id=g.user.id).all()
    result = []
    
    for enrollment in enrollments:
        response = GetUserEnrollmentsResponse(
            business=BusinessDetailsModel(
                id=enrollment.business.id,
                business_name=enrollment.business.business_name,
                description=enrollment.business.description,
                email_address=enrollment.business.email_address,
                address=enrollment.business.address,
                phone_number=enrollment.business.phone_number
            ),
            enrollment=EnrollmentModel(
                id=enrollment.id,
                user_id=enrollment.user_id,
                business_id=enrollment.business_id,
                points=enrollment.points
            ),
        )
        result.append(response.model_dump())
    
    print(f'Found {len(enrollments)} enrollments')
    return jsonify(result)

@bp.route('/businesses/<uuid:business_id>', methods=['POST'])
@require_auth
def enroll_to_business(business_id):
    print(f'Enrolling user {g.user.id} to business {business_id}')
    business = BusinessDetails.query.get_or_404(business_id)
    
    existing_enrollment = Enrollment.query.filter_by(
        user_id=g.user.id,
        business_id=business_id
    ).first()
    
    if existing_enrollment:
        print(f'User {g.user.id} already enrolled in business {business_id}')
        return jsonify({'error': 'Already enrolled'}), 400
    
    enrollment = Enrollment(
        user_id=g.user.id,
        business_id=business_id,
        points=Decimal(0.0)
    )
    
    db.session.add(enrollment)
    db.session.commit()
    
    response = EnrollBusinessResponse(message='Successfully enrolled')
    return jsonify(response.model_dump()), 200

@bp.route('/businesses/<uuid:business_id>', methods=['DELETE'])
@require_auth
def cancel_enrollment(business_id):
    print(f'Canceling enrollment for user {g.user.id} from business {business_id}')
    enrollment = Enrollment.query.filter_by(
        user_id=g.user.id,
        business_id=business_id
    ).first_or_404()
    
    db.session.delete(enrollment)
    db.session.commit()
    
    return '', 204

@bp.route('/add_points', methods=['POST'])
@require_business_owner
def add_points():
    request_data = AddPointsRequest.model_validate(request.get_json())
    print(f'Adding {request_data.points} points for user {request_data.user_id}')
    business = BusinessDetails.query.filter_by(user_id=g.user.id).first()
    
    if not business:
        return jsonify({'error': 'Business details not found'}), 404
    
    enrollment = Enrollment.query.filter_by(
        user_id=request_data.user_id,
        business_id=business.id
    ).first()
    
    if not enrollment:
        return jsonify({'error': 'Customer not enrolled'}), 404
    
    enrollment.points += Decimal(request_data.points)
    db.session.commit()
    
    response = AddPointsResponse(
        user_id=enrollment.user_id,
        new_points_balance=float(enrollment.points)
    )
    print(f'New points balance: {enrollment.points}')
    return jsonify(response.model_dump())

@bp.route('/redeem_reward', methods=['POST'])
@require_business_owner
def redeem_reward():
    request_data = RedeemRewardRequest.model_validate(request.get_json())
    print(f'Redeeming reward {request_data.reward_id} for user {request_data.user_id}')
    business = BusinessDetails.query.filter_by(user_id=g.user.id).first()
    
    if not business:
        return jsonify({'error': 'Business details not found'}), 404
    
    reward = Reward.query.filter_by(
        id=request_data.reward_id,
        business_id=business.id
    ).first()
    
    if not reward:
        return jsonify({'error': 'Reward not found'}), 404
    
    enrollment = Enrollment.query.filter_by(
        user_id=request_data.user_id,
        business_id=business.id
    ).first()
    
    if not enrollment:
        return jsonify({'error': 'Customer not enrolled'}), 404
    
    if enrollment.points < reward.required_points:
        print(f'Insufficient points: required={reward.required_points}, current={enrollment.points}')
        return jsonify({
            'error': 'Insufficient points',
            'required': float(reward.required_points),
            'current': float(enrollment.points)
        }), 400
    
    enrollment.points -= reward.required_points
    reward.usage_count += 1
    db.session.commit()
    
    response = RedeemRewardResponse(
        success=True,
        new_points_balance=float(enrollment.points)
    )
    print(f'New points balance after redemption: {enrollment.points}')
    return jsonify(response.model_dump())
