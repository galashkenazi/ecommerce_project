import os
from flask import Flask
from flask_cors import CORS
from src.db.connection import db
from flask_migrate import Migrate, upgrade
from src.config import Config
from src.routes import auth, businesses, enrollments
import logging


def _run_db_migrations_if_exists(app):
    with app.app_context():
    # Get the migrations directory path
        upgrade()


def create_app():
    app = Flask(__name__)
    app.config.from_object(Config)
    app.logger.setLevel(logging.DEBUG)
    logging.basicConfig(level=logging.DEBUG)
    app.debug = True
    
    CORS(app)
    db.init_app(app)
    migrate = Migrate(app, db)
    
    # Register blueprints
    app.register_blueprint(auth.bp)
    app.register_blueprint(businesses.bp)
    app.register_blueprint(enrollments.bp)

    return app


if __name__ == '__main__':
    app = create_app()
    _run_db_migrations_if_exists(app)
    app.run(host='0.0.0.0', port=5013)
