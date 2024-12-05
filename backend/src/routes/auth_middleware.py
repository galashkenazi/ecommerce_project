from functools import wraps
from flask import request, jsonify, g
from src.db.models import User, BlacklistedToken
from jose import jwt, JWTError
from src.config import Config

def get_current_user():
    auth_header = request.headers.get('Authorization')
    if not auth_header or not auth_header.startswith('Bearer '):
        return None
    
    token = auth_header.split(' ')[1]
    try:
        payload = jwt.decode(token, Config.JWT_SECRET_KEY, algorithms=['HS256'])
        user = User.query.get(payload['user_id'])
        return user
    except JWTError:
        return None

def require_auth(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        user = get_current_user()
        if not user:
            return jsonify({'error': 'Authentication required'}), 401
        g.user = user
        return f(*args, **kwargs)
    return decorated

def require_business_owner(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        user = get_current_user()
        if not user:
            return jsonify({'error': 'Authentication required'}), 401
        if not user.is_business_owner:
            return jsonify({'error': 'Business owner access required'}), 403
        g.user = user
        return f(*args, **kwargs)
    return decorated

def token_required(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        auth_header = request.headers.get('Authorization')
        if not auth_header or not auth_header.startswith('Bearer '):
            return jsonify({'error': 'Missing or invalid token'}), 401
        
        token = auth_header.split(' ')[1]
        
        # Check if token is blacklisted
        if BlacklistedToken.query.filter_by(token=token).first():
            return jsonify({'error': 'Token has been invalidated'}), 401
            
        try:
            payload = jwt.decode(token, Config.JWT_SECRET_KEY, algorithms=['HS256'])
            request.user_id = payload['user_id']
        except JWTError:
            return jsonify({'error': 'Invalid token'}), 401
            
        return f(*args, **kwargs)
    return decorated
