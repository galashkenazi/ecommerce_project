from src.routes.auth_middleware import require_auth
from src.config import Config
from flask import Blueprint, g, request, jsonify
from src.db.models import User, BlacklistedToken
from src.api_schemas import CreateUserRequest, LoginUserRequest, TokenResponse, UserModel
from src.db.connection import db
from passlib.hash import pbkdf2_sha256
from jose import jwt
import datetime

bp = Blueprint('auth', __name__, url_prefix='/auth')

@bp.route('/register', methods=['POST'])
def register():
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
    request_data = LoginUserRequest.model_validate(request.get_json())
    user = User.query.filter_by(username=request_data.username).first()
    
    if not user or not pbkdf2_sha256.verify(request_data.password, user.hashed_password):
        return jsonify({'error': 'Invalid credentials'}), 401
        
    token = _create_access_token(str(user.id))
    return TokenResponse(access_token=token).model_dump()       

@bp.route('/logout', methods=['POST'])
def logout():
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
    user = User.query.filter_by(id=g.user.id).first()
    return UserModel(
        id=user.id,
        username=user.username,
        email_address=user.email_address,
        is_business_owner=user.is_business_owner,
    ).model_dump()

def _create_access_token(user_id: str) -> str:
    expire = datetime.datetime.utcnow() + datetime.timedelta(hours=1)
    return jwt.encode(
        {'user_id': user_id, 'exp': expire},
        Config.JWT_SECRET_KEY,
        algorithm='HS256'
    )
