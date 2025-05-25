from subsystem1 import db 
from datetime import datetime


class Documents(db.Model):
    doc_id = db.Column(db.Integer, primary_key=True)
    author = db.Column(db.String(255), nullable=False)
    title = db.Column(db.String(255), nullable=False)
    file_name = db.Column(db.String(255))
    upload_date = db.Column(db.DateTime, default=datetime.utcnow, nullable=False)
    
    contents = db.relationship('Contents', backref='document', lazy=True)
    
    def __repr__(self):
        return f"Documents('{self.doc_id}', '{self.author}', '{self.title}', '{self.upload_date}')"
    
class Contents(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    content = db.Column(db.String(255), nullable=False)
    doc_id = db.Column(db.Integer, db.ForeignKey('documents.doc_id'), nullable=False)
    
    def __repr__(self):
        return f"Contents('{self.id}', '{self.content}', '{self.doc_id}')"