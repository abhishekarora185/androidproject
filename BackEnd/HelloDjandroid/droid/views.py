#serves JSON data on demand and makes authentication calls

from django.shortcuts import render
from django.http import HttpResponse
from pymongo import Connection
from droid.authentication import FacebookOAUTH2Authentication
import datetime
from django.utils import simplejson
from django.views.decorators.csrf import csrf_exempt
import json

#pymongo API to establish a connection to a mongo database server running on port no. 27017
connection = Connection()

#name of the database
db = connection['gooddb']

#views decorated with 'csrf_exempt' will be allowed to perform POST requests without passing CSRF tokens
@csrf_exempt
def facebook_auth(request):
#starts facebook authentication and returns user details
	facebook_handler = FacebookOAUTH2Authentication()
	user = facebook_handler.is_authenticated(request)
	
	if user == facebook_handler._unauthorized():
		print "Facebook token authentication unsuccessful."
		return False
	else:
		print "Returning user..."
		return user
		
def filter(collection,criteria):
#filters the items in a list according to certain criteria
	print "Filtering..."
	for item in collection:
		print item
		passed = True
		for key in criteria.GET:
			print key
			if not key == 'access_token' and not item[key] == criteria.GET[key]:
				print "Hello"
				passed = False
				break
		if passed == True:
			yield item

@csrf_exempt
def posts_view(request):
#serves entries from the 'Post' table, and accepts entries for the same	
	auth_success = facebook_auth(request)
	posts = db['Post']
	users = db['User']
	groups = db['Group']
	
	curr_user = ''
	
	if not auth_success == False:
		found = 0
		for user in users.find():
			if auth_success['username'] == user['facebook_id']:
				found = 1
				curr_user = user
				break
		if found == 1:
			print "User already exists, " + str(curr_user['name'])
		else:
			print "Adding new user."
			curr_user = {"name":auth_success['name'],"facebook_id":auth_success['username']}
			print "New user"
			print users
			users.save(curr_user)
	
	if request.method == 'POST' and not curr_user == '':
		print "Looking up group"
		curr_group = ''
		found = 0 
		for group in groups.find():
			if group['group_id'] == request.POST['group_id']:
				curr_group = group
				found = 1
				break;
		#added to sort out errors while testing. Should remove once group ids are given by the app and not inputted by user. From here.
		if found == 1:
			print "Group found"
		else:
			print "Adding new group"
			curr_group = {"group_id":request.POST['group_id']}
			print "New group"
			print curr_group
			groups.save(curr_group)
		#Till here
		print "Saving new post..."
		new = {"content":request.POST['content'],"author":curr_user,"type":request.POST['type'],"group":curr_group,"comments":[]}
		print new
		posts.save(new)
		print "Post saved."
		
	elif request.method == 'GET' and not curr_user == '':		
		response = []
		for post in posts.find():
			if "author" in post.keys() and "group" in post.keys():
				response.append({"content":post['content'],"author":post['author']['name'],"group_id":post['group']['group_id'],"type":post['type'],"comments":post['comments'],"post_id":str(post["_id"])})
		print "Done searching."
		
		result = filter(response,request)
		response = []
		for entry in result:
			response.append(entry)
		
		if response == []:
			print "No posts found."
			return "No response"
		else:
			"Filtered:"
			print response
			return HttpResponse(simplejson.dumps(response), mimetype="application/json")	
	return "Faulty run"

def group_view(request):
#Returns all posts associated with the group
	auth_success = facebook_auth(request)
	posts = db['Post']
	users = db['User']
	groups = db['Group']
	
	curr_user = ''
	
	if not auth_success == False:
		found = 0
		for user in users.find():
			if auth_success['username'] == user['facebook_id']:
				found = 1
				curr_user = user
				break
		if found == 1:
			print "User already exists, " + str(curr_user['name'])
		else:
			print "Adding new user."
			curr_user = {"name":auth_success['name'],"facebook_id":auth_success['username']}
			print "New user"
			print users
			users.save(curr_user)
	#Authentication and lookup with user collection done. 
	#Now to return posts.  
	response = []
	if request.method == 'GET' and not curr_user == "":
		print "Looking up group..."
		curr_group = ""
		response = []		
		#Find group.
		for group in groups.find():
			if group['group_id'] == request.GET['group_id']:
				curr_group = group
				print "Group Found"
		#Iterate through and append all posts with relevant group ID.
		if not curr_group == "":
			for post in posts.find():
				if post['group'] == curr_group:
					response.append({"content":post['content'],"author":post['author']['name'],"comments":post['comments'],"group_id":curr_group['group_id'],"type":post['type']})
		print "Done searching"
		print "Response",response
		result = response
		#result = filter(response,request) 
		#TODO :Filter fails. item['group_id'] does not exit. only ['group'] does. FIX! 
		response = []
		for entry in result:
			response.append(entry)
	
		if response == []:
			print "No posts found."
			return "No response"
		else:
			"Filtered:"
			print response
			return HttpResponse(simplejson.dumps(response), mimetype="application/json")	
	return "Faulty run"

#Not tested. 
def comment_add(request):

	auth_success = facebook_auth(request)
	posts = db['Post']
	users = db['User']
	groups = db['Group']
	
	curr_user = ''
	
	if not auth_success == False:
		found = 0
		for user in users.find():
			if auth_success['username'] == user['facebook_id']:
				found = 1
				curr_user = user
				break
		if found == 1:
			print "User already exists, " + str(curr_user['name'])
		else:
			print "Adding new user."
			curr_user = {"name":auth_success['name'],"facebook_id":auth_success['username']}
			print "New user"
			print users
			users.save(curr_user)
	#Authentication and lookup with user collection done. 
	#Now to find the post using the post_id and adding the comment.
	for post in posts:
		if post['_id'] == ObjectId(request.GET['post_id']):
			new_comment = {"author":curr_user,"content":request.POST['content']}
			new_comment_list = post['comments'].append(new)
			query = {"_id":post['_id']}
			posts.update(query,{"comments" : new_comment_list}) #updating comments list.