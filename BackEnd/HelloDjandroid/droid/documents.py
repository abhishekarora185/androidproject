#database models

import bson
import mongoengine
import datetime


class InheritableDocument(mongoengine.Document):
	meta = {
        'abstract': True,
        'allow_inheritance': True,
    }


class InheritableEmbeddedDocument(mongoengine.EmbeddedDocument):
	meta = {
        'abstract': True,
        'allow_inheritance': True,
    }


class User(InheritableDocument):
    name = mongoengine.StringField(max_length=20, required=True, unique=False)
    facebook_id = mongoengine.StringField(max_length=20, required=True, unique=True)
    group_ids = mongoengine.ListField(mongoengine.StringField(max_length=10, required=False))
	
class Request(InheritableDocument):
	user = mongoengine.ReferenceField(User, required=True)
	
class Group(InheritableDocument):
	name = mongoengine.StringField(max_length=40, required=True, unique=True)
	group_id = mongoengine.StringField(max_length=20, required=True, unique=True)
	members = mongoengine.ListField(mongoengine.ReferenceField(User, required=False))
	admins = mongoengine.ListField(mongoengine.ReferenceField(User, required=False))
	requests = mongoengine.ListField(mongoengine.ReferenceField(Request, required=False))

class Comment(InheritableEmbeddedDocument):
	content = mongoengine.StringField(max_length=200, required=True)
	author = mongoengine.ReferenceField(User, required=True)
	datetime = mongoengine.DateTimeField(default=datetime.datetime.now())

class Post(InheritableDocument):
	author = mongoengine.ReferenceField(User, required=True)
	content = mongoengine.StringField(max_length=300, required=True)
	comments = mongoengine.ListField(mongoengine.EmbeddedDocumentField(Comment))
	group = mongoengine.ReferenceField(Group, required=True)
	datetime = mongoengine.DateTimeField(default=datetime.datetime.now())
	type = mongoengine.StringField(max_length=7, required=True)
	
class Admin(User):
	group = mongoengine.ReferenceField(Group, required=True)