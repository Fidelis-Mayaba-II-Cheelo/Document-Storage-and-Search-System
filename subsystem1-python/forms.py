from flask_wtf import FlaskForm
from wtforms import StringField,SubmitField, TextAreaField
from wtforms.validators import DataRequired
from wtforms.fields.choices import SelectField


class AddDocumentForm(FlaskForm):
    author = StringField('Author', validators=[DataRequired()])
    title = StringField('Title', validators=[DataRequired()])
    submit = SubmitField('Add Document')
    
class AddContentForm(FlaskForm):
    document = SelectField('Document', choices=[], validators=[DataRequired()])
    content = TextAreaField('Content', validators=[DataRequired()])
    submit = SubmitField('Add Content')
    

class UpdateDocumentForm(FlaskForm):
    author = StringField('Author', validators=[DataRequired()])
    title = StringField('Title', validators=[DataRequired()])
    submit = SubmitField('Update Document')
    
    
class UpdateContentForm(FlaskForm):
    document = SelectField('Document', choices=[], validators=[DataRequired()])
    content = TextAreaField('Content', validators=[DataRequired()])
    submit = SubmitField('Update Content')