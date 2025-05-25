from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_restful import Api

app = Flask(__name__)

app.config['SECRET_KEY'] =  '484cf8d93e56f8d6e7b6d9efeba11dc4'
#app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+pymysql://root:Dorothy2002%40localhost:3307/document_db'
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+pymysql://root:Dorothy2002%40@localhost:3307/document_db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

db = SQLAlchemy(app)
api = Api(app)

from routes import *