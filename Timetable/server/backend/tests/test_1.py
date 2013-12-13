import requests
import json

auth = dict()
auth["username"] = "testx"
auth["password"] = "mimitest"

headers = {'content-type':'application/json'}

token = json.loads(requests.post('http://127.0.0.1:8000/api-token-auth/',data=json.dumps(auth),headers=headers).text)["token"]

print token

headers["Authorization"] = str("Token " + token)

print headers

print requests.get("http://127.0.0.1:8000/course/info/1/",headers=headers).text

add_course = dict()
for i in range(100):
    add_course["lecture"] = 6000+i
    #requests.post("http://127.0.0.1:8000/enroll/lectures/",data=json.dumps(add_course),headers=headers)

print "get",requests.get("http://23.92.17.12/user/lectures/",headers=headers,params={"timestamp":"2013:11:15:15:28:04"}).text

task = dict()
task["start_time"] = "14:30:00"
task["end_time"] = "15:30:00"
task["name"] = "Exam 2"
task["course_id"] = "3"
task["date"] = "2013-11-27"

lecture = dict()
lecture['course_id'] = "12"
lecture['start_time'] = "10:30:11"
lecture['end_time'] = "10:35:12"


#print requests.post("http://127.0.0.1:8000/user/add/task/",data = json.dumps(task),headers=headers).text

task_params = {'timestamp':'2013:11:24:22:00:00','course_id':3,'task_today':False}

print task_params

#print requests.get("http://127.0.0.1:8000/user/tasks/",params=task_params,headers=headers).text

update = {'id':25,'location':"test pp"}

#print requests.post("http://127.0.0.1:8000/user/edit/task/",data=json.dumps(update),headers=headers).text

comment = dict()
comment["content"] = "I agree"
comment["course_id"] = 1
#comment["parent"] = 34

print requests.post("http://127.0.0.1:8000/user/add/comment/",data=json.dumps(comment),headers=headers).text

#print requests.get("http://127.0.0.1:8000/course/comments/1/",params={'comment_num':2},headers=headers).text

#print requests.get("http://127.0.0.1:8000/user/notifications/",headers=headers).text

#print requests.get("http://127.0.0.1:8000/user/messages/",headers=headers).text

like = dict()
like['post_id'] = 7

#print requests.post("http://127.0.0.1:8000/user/like/",data=json.dumps(like),headers=headers).text


#print requests.get("http://127.0.0.1:8000/user/week/",headers=headers,params={"datetime":"2013:11:15:12:00:00"}).text

#print requests.get("http://127.0.0.1:8000/user/courses/",headers=headers).text

dates = dict()
dates['start_date'] = "2013:08:13"
dates['end_date'] = "2013:12:17"


#print requests.post("http://127.0.0.1:8000/user/add/semester/",data=json.dumps(dates),headers=headers).text

course = dict()
course['id'] = 1000


#print requests.post("http://127.0.0.1:8000/enroll/courses/",data=json.dumps(course),headers=headers).text

dc = dict()
dc['id'] = 1000


#print requests.post("http://127.0.0.1:8000/user/delete/course/",data=json.dumps(dc),headers=headers).text


#print requests.get("http://127.0.0.1:8000/user/dates/",headers=headers).text

print requests.get("http://127.0.0.1:8000/post/",params={'id':34},headers=headers).text

print requests.get("http://127.0.0.1:8000/user/profile/",headers=headers,params={'id':10}).text

print requests.post("http://127.0.0.1:8000/user/messages/clear/",headers=headers).text

print requests.post("http://127.0.0.1:8000/user/status/",headers=headers,data=json.dumps({"status":"This is just test"})).text
