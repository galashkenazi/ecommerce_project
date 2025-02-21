from src.routes.auth_middleware import require_auth
from src.config import Config
from flask import Blueprint, g, request, jsonify
from src.db.models import Enrollment, User, BlacklistedToken, BusinessDetails
from src.api_schemas import CreateUserRequest, LoginUserRequest, TokenResponse, UserModel
from src.db.connection import db
from passlib.hash import pbkdf2_sha256
from jose import jwt
import datetime
from src.services.business_recommendation import BusinessRecommendationService

bp = Blueprint('auth', __name__, url_prefix='/auth')

business_recommendation_service = BusinessRecommendationService()

@bp.route('/register', methods=['POST'])
def register():
    print(f"Registering user")
    request_data = CreateUserRequest.model_validate(request.get_json())
    
    if User.query.filter_by(username=request_data.username).first():
        return jsonify({'error': 'Username already exists'}), 400
        
    user = User(
        username=request_data.username,
        hashed_password=pbkdf2_sha256.hash(request_data.password),
        email_address=request_data.email_address,
        is_business_owner=request_data.is_business_owner
    )
    
    db.session.add(user)
    db.session.commit()
    
    token = _create_access_token(str(user.id))
    return TokenResponse(access_token=token).model_dump()

@bp.route('/login', methods=['POST'])
def login():
    print(f"Logging in user")
    request_data = LoginUserRequest.model_validate(request.get_json())
    user = User.query.filter_by(username=request_data.username).first()
    
    if not user or not pbkdf2_sha256.verify(request_data.password, user.hashed_password):
        return jsonify({'error': 'Invalid credentials'}), 401
        
    token = _create_access_token(str(user.id))
    return TokenResponse(access_token=token).model_dump()       

@bp.route('/logout', methods=['POST'])
def logout():
    print(f"Logging out user")
    auth_header = request.headers.get('Authorization')
    if not auth_header or not auth_header.startswith('Bearer '):
        return jsonify({'error': 'Missing or invalid token'}), 401
    
    token = auth_header.split(' ')[1]
    
    blacklisted_token = BlacklistedToken(token=token)
    db.session.add(blacklisted_token)
    try:
        db.session.commit()
        return jsonify({'message': 'Successfully logged out'}), 200
    except Exception as e:
        db.session.rollback()
        return jsonify({'error': 'Error logging out'}), 500

@bp.route('/me', methods=['GET'])
@require_auth
def get_current_user_details():
    print(f"Getting current user details")
    user = User.query.filter_by(id=g.user.id).first()
    
    user_enrollments = Enrollment.query.filter_by(user_id=g.user.id).all()
    user_businesses = [enrollment.business for enrollment in user_enrollments]
    all_businesses = BusinessDetails.query.all()

    response = UserModel(
        id=user.id,
        username=user.username,
        email_address=user.email_address,
        is_business_owner=user.is_business_owner,
    )

    if user_businesses and all_businesses:
        response.recommendations = business_recommendation_service.get_recommendations(
            user_businesses, 
            all_businesses
        )

    return response.model_dump()

def _create_access_token(user_id: str) -> str:
    expire = datetime.datetime.utcnow() + datetime.timedelta(hours=1)
    return jwt.encode(
        {'user_id': user_id, 'exp': expire},
        Config.JWT_SECRET_KEY,
        algorithm='HS256'
    )
