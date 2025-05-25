from flask import render_template, redirect, request, url_for, flash
from forms import AddContentForm, AddDocumentForm, UpdateDocumentForm, UpdateContentForm
from models import Documents, Contents
from subsystem1 import db, app, api
from flask_restful import Resource, Api, reqparse, fields, marshal_with, abort
from datetime import datetime
import os
from werkzeug.utils import secure_filename
from flask import send_from_directory

UPLOAD_FOLDER = os.path.join(os.getcwd(), 'uploads')
os.makedirs(UPLOAD_FOLDER, exist_ok=True)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
app.config['MAX_CONTENT_LENGTH'] = 16 * 1024 * 1024  # 16MB max file size


@app.route('/')
@app.route('/home', methods=['GET', 'POST'])
def home():
    return render_template("home.html")

@app.route('/files/<filename>')
def uploaded_file(filename):
    return send_from_directory(app.config['UPLOAD_FOLDER'], filename)

@app.route('/addDocument', methods=['GET', 'POST'])
def addDocument():
    form = AddDocumentForm()
    if form.validate_on_submit():
        file = request.files['file']
        if file and file.filename.endswith('.pdf'):
            filename = secure_filename(file.filename)
            file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))

            document = Documents(
                author=form.author.data,
                title=form.title.data,
                file_name=filename
            )
            db.session.add(document)
            db.session.commit()
            flash('Your Document was added successfully!', 'success')
            return redirect(url_for('addContent'))
        else:
            flash('Please upload a valid PDF file.', 'danger')
    return render_template("addDocument.html", form=form, title='Add Documents')

@app.route('/addContent', methods=['GET', 'POST'])
def addContent():
    load_documents = Documents.query.all()
    form = AddContentForm()
    form.document.choices = [(d.doc_id, d.title) for d in load_documents]
    if form.validate_on_submit():
        content = Contents(content=form.content.data, doc_id=form.document.data)
        db.session.add(content)
        db.session.commit()
        flash('The content for the chosen document has been added successfully!', 'success')
        return redirect(url_for('home'))
    return render_template("addContent.html", form=form, title='Add Contents For Existing Documents')

@app.route('/updateDocument/<int:doc_id>', methods=['GET', 'POST'])
def updateDocument(doc_id):
    document = Documents.query.get_or_404(doc_id)
    form = UpdateDocumentForm()
    if form.validate_on_submit():
        document.author = form.author.data
        document.title = form.title.data
        db.session.commit()
        flash("Your document has been updated successfully", "success")
        return redirect(url_for('viewDocument', doc_id=doc_id))
    elif request.method == 'GET':
        form.author.data = document.author
        form.title.data = document.title
    return render_template('addDocument.html', title='Update Document', form=form)

@app.route('/updateContent/<int:doc_id>/<int:id>', methods=['GET', 'POST'])
def updateContent(doc_id,id):
    content = Contents.query.get_or_404(id)
    load_documents = Documents.query.all()
    form = UpdateContentForm()
    form.document.choices = [(d.doc_id, d.title) for d in load_documents]
    if form.validate_on_submit():
        content.content = form.content.data
        content.document = form.document.data
        db.session.commit()
        flash("Your Documents' content has been updated successfully", "success")
        return redirect(url_for('viewContentsPerDocument', doc_id=doc_id))
    elif request.method == 'GET':
        form.content.data = content.content
        form.document.data = content.document
    return render_template('addContent.html', title='Update Contents for Document', form=form)
    
@app.route('/viewDocument/<int:doc_id>', methods=['GET'])
def viewDocument(doc_id):
    document = Documents.query.get_or_404(doc_id)
    return render_template('viewDocument.html', title='View Document', document=document)


@app.route('/viewAllDocuments', methods=['GET'])
def viewAllDocuments():
    page = request.args.get('page', 1, type=int)
    documents = Documents.query.paginate(page=page, per_page=6)
    if documents:
        return render_template('viewAllDocuments.html', title='View All Documents', documents=documents)
    else:
        return render_template('viewAllDocuments.html', title='View All Documents', documents=[])

@app.route('/viewContentsPerDocument/<int:doc_id>', methods=['GET'])
def viewContentsPerDocument(doc_id):
    page = request.args.get('page', 1, type=int)
    contents_per_document = Contents.query.filter_by(doc_id=doc_id).paginate(page=page, per_page=6)
    return render_template('viewContentsPerDocument.html', title='View Contents Per Document', doc_id=doc_id, contents_per_document=contents_per_document)

@app.route('/viewAllContents', methods=['GET'])
def viewAllContents():
    page = request.args.get('page', 1, type=int)
    contents = Contents.query.paginate(page=page, per_page=6)
    if contents:
        return render_template('viewContents.html', title='View All Contents', contents=contents)
    else:
        return render_template('viewContents.html', title='View All Contents', contents=contents)

@app.route('/deleteSingleContentForDocument/<int:doc_id>/<int:id>', methods=['POST'])
def deleteSingleContentForDocument(doc_id, id):
    content = Contents.query.get_or_404((doc_id,id))
    db.session.delete(content)
    db.session.commit()
    flash(f"A single piece of content for {content.document.title} has been deleted successfuly", "success")
    return redirect(url_for('viewAllContents'))

@app.route('/deleteAllContentsForDocument/<int:doc_id>', methods=['POST'])
def deleteAllContentsForDocument(doc_id):
    contents = Contents.query.filter_by(doc_id=doc_id).all()
    for content in contents:
        db.session.delete(content)
        db.session.delete(contents)
        db.session.commit()
        flash(f"All contents for {contents.document.title} have been deleted successfuly", "success")
        return redirect(url_for('viewAllContents'))

@app.route('/deleteSingleDocument/<int:doc_id>', methods=['POST'])
def deleteSingleDocument(doc_id,id):
    document = Documents.query.get_or_404(doc_id)
    contents = Contents.query.filter_by(doc_id=doc_id).all()
    for content in contents:
        db.session.delete(content)
    db.session.delete(document)
    db.session.commit()
    flash(f"Document with title {document.title} together with all its contents has been deleted successfully", "success")
    return redirect(url_for('viewAllDocuments'))

@app.route('/deleteAllDocuments', methods=['POST'])
def deleteAllDocuments():
    documents = Documents.query.all()
    contents = Contents.query.all()
    db.session.delete(contents)
    db.session.delete(documents)
    db.session.commit()
    flash(f"All documents together with all their contents have been deleted successfully", "success")
    return redirect(url_for('viewAllDocuments'))



# reqparse used to define some arguments that are going to come in when we want to send data to our api and create new documents
# Its also used to validate that incoming data to make sure it is what we expect
document_args = reqparse.RequestParser()
document_args.add_argument('title', type=str, required=True, help="Document title should not be blank")
document_args.add_argument('author', type=str, required=True, help="Document author should not be blank")

content_args = reqparse.RequestParser()
content_args.add_argument('content', type=str, required=True, help="Content must not be blank")
content_args.add_argument('doc_id', type=int, required=True, help="Document ID is required for content.")



documentFields = {
    'doc_id': fields.Integer,
    'title': fields.String,
    'author': fields.String,
    'upload_date': fields.DateTime(dt_format='iso8601'),
    'file_url': fields.String
}


contentFields = {
    'id': fields.Integer,
    'doc_id': fields.Integer,
    'content': fields.String
}

def add_file_url(document):
    if document.file_name:
        return url_for('uploaded_file', filename=document.file_name, _external=True)
    return None


#Class for getting all documents 
class DocumentsApi(Resource):
    #Defining our endpoints
    @marshal_with(documentFields) #Marshal_with is a decorator and that helps us send json back in a serilaized format
    def get(self): #Getting documents
        documents = Documents.query.all()
        result = []
        for doc in documents:
            data = {
                "doc_id": doc.doc_id,
                "title": doc.title,
                "author": doc.author,
                "upload_date": doc.upload_date,
                "file_url": add_file_url(doc)
            }
            result.append(data)
        return result
    
    @marshal_with(documentFields)
    def post(self): #Adding documents
        args = document_args.parse_args() #The same document args we are using up above(To parse the arguments to the definitions we set above)
        document = Documents(title=args["title"], author=args["author"])
        db.session.add(document)
        db.session.commit()
        documents = Documents.query.all()
        return documents, 201 #201 is an http status that means create
    

    
class SingleDocumentApi(Resource):
    def get(self, doc_id):
        document = Documents.query.filter_by(doc_id=doc_id).first()
        if not document:
            abort(404, "Document not found")
        return {
            "doc_id": document.doc_id,
            "title": document.title,
            "author": document.author,
            "upload_date": document.upload_date,
            "file_url": url_for('uploaded_file', filename=document.file_name, _external=True) if document.file_name else None
        }

    def patch(self, doc_id):
        args = document_args.parse_args()
        document = Documents.query.filter_by(doc_id=doc_id).first()
        if not document:
            abort(404, "Document not found")
        document.title = args["title"]
        document.author = args["author"]
        db.session.commit()
        return {
            "doc_id": document.doc_id,
            "title": document.title,
            "author": document.author,
            "upload_date": document.upload_date,
            "file_url": url_for('uploaded_file', filename=document.file_name, _external=True) if document.file_name else None
        }

    def delete(self, doc_id):
        document = Documents.query.filter_by(doc_id=doc_id).first()
        if not document:
            abort(404, "Document not found")
        db.session.delete(document)
        db.session.commit()
        return {"message": f"Document with id {doc_id} deleted successfully."}


#Class for getting all contents
class ContentsApi(Resource):
    @marshal_with(contentFields)
    def get(self):
        contents = Contents.query.all()
        return contents

    
    @marshal_with(contentFields)
    def post(self):
        args = content_args.parse_args()
        content = Contents(content=args["content"], doc_id=args["doc_id"])
        db.session.add(content)
        db.session.commit()
        return content, 201

 

#Class for handling a contents for a specific document
class SingleContentsApi(Resource):
    @marshal_with(contentFields)
    def get(self, doc_id, id):
        content = Contents.query.filter_by(doc_id=doc_id, id=id).first()
        if not content:
            abort(404, "Contents for this Document were not found")
        return content
    
    @marshal_with(contentFields)
    def patch(self, doc_id, id):
        args = content_args.parse_args()
        content = Contents.query.filter_by(doc_id=doc_id, id=id).first()
        if not content:
            abort(404, f"Contents were not found for document of id = {doc_id}")
        content.content = args["content"]
        db.session.commit()
        return content
    
    @marshal_with(contentFields)
    def delete(self, doc_id, id):
        content = Contents.query.filter_by(doc_id=doc_id, id=id).first()
        if not content:
            abort(404, f"Contents were not found for document of id = {doc_id}")
        db.session.delete(content)
        db.session.commit()
        contents = Contents.query.all()
        return contents

#Assigning our endpoints to URLs
api.add_resource(DocumentsApi, '/api/documents/')
api.add_resource(SingleDocumentApi, '/api/documents/<int:doc_id>/')
api.add_resource(ContentsApi, '/api/contents/')
api.add_resource(SingleContentsApi, '/api/contents/<int:doc_id>/<int:id>/')