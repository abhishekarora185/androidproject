#associates views with URLs for the application server, and bundles resources

from django.conf.urls import patterns, include, url
from tastypie import api
from droid import resources
from droid.views import *


v1_api = api.Api(api_name='v1')
v1_api.register(resources.UserResource())
v1_api.register(resources.AdminResource())
v1_api.register(resources.GroupResource())
v1_api.register(resources.CommentResource())
v1_api.register(resources.PostResource())
v1_api.register(resources.RequestResource())


urlpatterns = patterns('',
	url(r'^posts/?',posts_view),
)
urlpatterns += patterns('',
    url(r'^api/', include(v1_api.urls)),
)