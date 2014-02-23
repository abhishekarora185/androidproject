#associating database models with certain properties

from tastypie import authorization as tastypie_authorization, fields as tastypie_fields

from tastypie_mongoengine import fields, paginator, resources

from droid import documents
from droid.authentication import FacebookOAUTH2Authentication

class AdminResource(resources.MongoEngineResource):
	class Meta:
		object_class = documents.Admin
		queryset = documents.Admin.objects.all()
		allowed_methods = ('get', 'post', 'put', 'patch', 'delete')
		authorization = tastypie_authorization.Authorization()

class UserResource(resources.MongoEngineResource):
    class Meta:
		object_class = documents.User
		queryset = documents.User.objects.all()
		allowed_methods = ('get', 'post', 'put', 'patch', 'delete')
		authorization = tastypie_authorization.Authorization()
		authentication = FacebookOAUTH2Authentication()
		ordering = ('name',)
		polymorphic = {
			'user': 'self',
			'admin': AdminResource,
		}

class CommentResource(resources.MongoEngineResource):
    class Meta:
		object_class = documents.Comment
		ordering = ('datetime',)
		authentication = FacebookOAUTH2Authentication()

class PostResource(resources.MongoEngineResource):
    comments = fields.EmbeddedListField(of='HelloDjandroid.droid.resources.CommentResource', attribute='comments', full=True, null=True)

    class Meta:
		object_class = documents.Post
		authentication = FacebookOAUTH2Authentication()
		ordering = ('datetime',)

class RequestResource(resources.MongoEngineResource):
	class Meta:
		object_class = documents.Request
		authentication = FacebookOAUTH2Authentication()
		ordering = ('datetime',)
		
class GroupResource(resources.MongoEngineResource):
	class Meta:
			object_class = documents.Group
			authentication = FacebookOAUTH2Authentication()