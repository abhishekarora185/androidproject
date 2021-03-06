#serves JSON data on demand and makes authentication calls

from django.shortcuts import render
from django.http import HttpResponse
from pymongo import Connection
from droid.authentication import FacebookOAUTH2Authentication
import datetime
from django.utils import simplejson
from django.views.decorators.csrf import csrf_exempt
import json
from bson import ObjectId

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
			curr_user = {"name":auth_success['name'],"facebook_id":auth_success['username'],"group_ids":[]}
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
		print "Saving new post..."
		new = {"content":request.POST['content'],"author":curr_user,"type":request.POST['type'],"group":curr_group,"comments":[]}
		print new
		posts.save(new)
		print "Post saved."
		
	elif request.method == 'GET' and not curr_user == '':		
		response = []
		for post in posts.find():
			if "author" in post.keys() and "group" in post.keys():
				comment_list = []
				#ObjectID's are not JSON serializable. So converting them to string before calling dumps to prevent error.
				for comment in post['comments']:
					comment_list.append({"content":comment['content'],"author":{"id":str(comment['author']['_id']),"name":comment['author']['name'],"facebook_id":comment['author']['facebook_id']},"id":str(comment['_id'])})
				response.append({"content":post['content'],"author":post['author']['name'],"group_id":post['group']['group_id'],"type":post['type'],"comments":comment_list,"post_id":str(post["_id"])})
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
			#Debugging print statements
			#print "Response : \n",response
			#print "Dumps: \n",simplejson.dumps(response)
			#print "HTTP Response : \n", HttpResponse(simplejson.dumps(response),mimetype = "application/json") 
			return HttpResponse(simplejson.dumps(response), mimetype="application/json")	
	return "Faulty run"

def group_view(request):

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
			curr_user = {"name":auth_success['name'],"facebook_id":auth_success['username'],"group_ids":[]}
			print "New user"
			print users
			users.save(curr_user)
	#Authentication and lookup with user collection done. 
	if request.method == 'GET' and not curr_user == "":
		response = []		
		for group in groups.find():
			response.append({"group_id":group['group_id'],"id":str(group['_id']),"members":group['members'],'admins':group['admins'],'requests':group['requests']})
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

@csrf_exempt
def comment_add(request):

	print "Authenticating"
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
			curr_user = {"name":auth_success['name'],"facebook_id":auth_success['username'],"group_ids":[]}
			print "New user"
			print users
			users.save(curr_user)
	#Authentication and lookup with user collection done. 
	#Now to find the post using the post_id and adding the comment.
	if request.method == "POST" and not curr_user == "":
		for post in posts.find():
			print request.POST['post_id']
			if str(post['_id']) == request.POST['post_id']:
				print "Post found"
				new_comment = {"_id":post["_id"],"author":curr_user,"content":request.POST['content']}
				new_comment_list = []
				for comment in post['comments']:
					new_comment_list.append(comment)
				new_comment_list.append(new_comment)
				query = {"_id":post['_id']}
				new_post = {"author":post['author'],"type":post['type'],"content":post['content'],"group":post['group'],"comments":new_comment_list}
				posts.update(query,new_post) #updating comments list.
				print new_post
				print "Comment saved"

def user_view(request):
	
	print "Authenticating"
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
			curr_user = {"name":auth_success['name'],"facebook_id":auth_success['username'],"group_ids":[]}
			print "New user"
			print users
			users.save(curr_user)

	if request.method == "GET" and not curr_user == "":
		response = []
		for user in users.find():
			response.append({"name":user['name'],"facebook_id":user['facebook_id']})
		print "Done searching."
		
		result = filter(response,request)
		response = []
		for entry in result:
			response.append(entry)
		
		if response == []:
			print "No posts found"
			return "No response"
		else:
			"Filtered"
			return HttpResponse(simplejson.dumps(response),mimetype = "application/json")
	return "Faulty run"

@csrf_exempt
def request_view(request):
	
	print "Authenticating"
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
			curr_user = {"name":auth_success['name'],"facebook_id":auth_success['username'],"group_ids":[]}
			print "New user"
			print users
			users.save(curr_user)
		if request.method == 'POST' and not curr_user == "":
			new_req = {"id":str(curr_user['_id']),"name":curr_user['name'],"facebook_id":curr_user['facebook_id']}
			print "User requesting :",curr_user
			print "Request", new_req
			for group in groups.find():
				if group['group_id'] == request.POST['group_id']:
					found = 0
					new_req_list = []
					for req in group['requests']:
						new_req_list.append(req)
						if req['facebook_id']== new_req['facebook_id']:
							found = 1
					new_req_list.append(new_req)
					if found == 0:
						groups.update({"group_id":group['group_id']},{"group_id":group['group_id'],"name":group['name'],"members":group['members'],'admins':group['admins'],'requests' : new_req_list})
						print "Request added"
					else:
						print "User request already added"

		elif request.method == "GET" and not curr_user == "":
			response = []
			for group in groups.find():
				response.append({"group_id":group['group_id'],"id":str(group["_id"]),"requests":group["requests"]})
			print "Done searching"

			result = filter(response,request)
			response = []
			for entry in result:
				response.append(entry)

			if response == []:
				return "No response"
			else:
				print "Filtered:"
				print response
				return HttpResponse(simplejson.dumps(response),mimetype = "application/json")
	return "Faulty run"

@csrf_exempt
def add_member(request):
	print "Authenticating"
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
			curr_user = {"name":auth_success['name'],"facebook_id":auth_success['username'],"group_ids":[]}
			print "New user"
			print users
			users.save(curr_user)

	if request.method == 'POST' and not curr_user == "":
		new_member = {"id":str(curr_user['_id']),"name":curr_user['name'],"facebook_id":curr_user['facebook_id']}
		print "Member being added :",curr_user
		print "Member :", new_member
		for group in groups.find():
			if group['group_id'] == request.POST['group_id']:
				found = 0
				new_member_list = []
				for member in group['members']:
					new_member_list.append(member)
					if member['facebook_id']== new_member['facebook_id']:
						found = 1
				new_member_list.append(new_member)
				if found == 0:
					groups.update({"group_id":group['group_id']},{"group_id":group['group_id'],"name":group['name'],"members":new_member_list,'admins':group['admins'],'requests' : group['requests']})
					print "Member added to group"
					#Adding group_id to the group_ids list of the user.
					new_group_ids = []
					for added_group_id in curr_user['group_ids']:
						new_group_ids.append(added_group_id)
					new_group_ids.append(group['group_id'])
					print group['group_id'],new_group_ids
					users.update({"_id":curr_user["_id"]},{"name":curr_user['name'],"facebook_id":curr_user['facebook_id'],"group_ids":new_group_ids})
					print "User group id updated"
				else:
					print "Member already added"