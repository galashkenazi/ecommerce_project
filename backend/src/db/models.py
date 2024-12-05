from datetime import datetime
from uuid import uuid4
from sqlalchemy.dialects.postgresql import UUID
from .connection import db

class User(db.Model):
    __tablename__ = 'user'
    
    id = db.Column(UUID(as_uuid=True), primary_key=True, default=uuid4)
    username = db.Column(db.String, unique=True, nullable=False)
    hashed_password = db.Column(db.String, nullable=False)
    email_address = db.Column(db.String, unique=True, nullable=False)
    is_business_owner = db.Column(db.Boolean, default=False)
    
    business_details = db.relationship('BusinessDetails', backref='user', uselist=False)
    enrollments = db.relationship('Enrollment', backref='user')

class BusinessDetails(db.Model):
    __tablename__ = 'business_detail'
    
    id = db.Column(UUID(as_uuid=True), primary_key=True, default=uuid4)
    user_id = db.Column(UUID(as_uuid=True), db.ForeignKey('user.id'), nullable=False)
    business_name = db.Column(db.String, nullable=False)
    description = db.Column(db.Text)
    email_address = db.Column(db.String, nullable=False)
    address = db.Column(db.String)
    phone_number = db.Column(db.String)
    
    rewards = db.relationship('Reward', backref='business')
    enrollments = db.relationship('Enrollment', backref='business')

class Enrollment(db.Model):
    __tablename__ = 'enrollment'
    
    id = db.Column(UUID(as_uuid=True), primary_key=True, default=uuid4)
    user_id = db.Column(UUID(as_uuid=True), db.ForeignKey('user.id'), nullable=False)
    business_id = db.Column(UUID(as_uuid=True), db.ForeignKey('business_detail.id'), nullable=False)
    points = db.Column(db.Numeric(10, 2), default=0)

class Reward(db.Model):
    __tablename__ = 'reward'
    
    id = db.Column(UUID(as_uuid=True), primary_key=True, default=uuid4)
    business_id = db.Column(UUID(as_uuid=True), db.ForeignKey('business_detail.id'), nullable=False)
    name = db.Column(db.String, nullable=False)
    description = db.Column(db.Text)
    required_points = db.Column(db.Numeric(10, 2), nullable=False)
    usage_count = db.Column(db.Integer, default=0)
    valid_from_timestamp = db.Column(db.DateTime, nullable=False, default=datetime.utcnow)
    valid_until_timestamp = db.Column(db.DateTime)

class BlacklistedToken(db.Model):
    __tablename__ = 'blacklisted_tokens'
    
    id = db.Column(db.Integer, primary_key=True)
    token = db.Column(db.String, unique=True, nullable=False)
    blacklisted_on = db.Column(db.DateTime, nullable=False, default=datetime.utcnow)
