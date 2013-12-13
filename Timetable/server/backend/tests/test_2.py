user1 = dict()

user1["username"] = "testxx"
user1["password"] = "mimitest"

user2 = dict()

user2["username"] = "testx"
user2["password"] = "mimitest"

import requests
import json

headers = {'content-type':'application/json'}

def get_token(user):
    return json.loads(requests.post('http://127.0.0.1:8000/api-token-auth/',data=json.dumps(user),headers=headers).text)["token"]

token1 = get_token(user1)
token2 = get_token(user2)

print token1,token2


headers1 = {'content-type':'application/json'}
headers2 = {'content-type':'application/json'}

headers1["Authorization"] = str("Token " + token1)
headers2["Authorization"] = str("Token " + token2)

message = dict()
message['id'] = 10

print requests.get("http://127.0.0.1:8000/user/friends/",headers=headers1).text

print requests.post("http://127.0.0.1:8000/user/invite/",data=json.dumps(message), headers=headers1).text

print requests.post("http://127.0.0.1:8000/user/decline/",data=json.dumps({'id':11}),headers=headers2).text

print requests.get("http://127.0.0.1:8000/user/notifications/",headers=headers2).text

#print requests.post("http://127.0.0.1:8000/user/add/friend/",data=json.dumps({"id":11}),headers=headers2).text

print requests.get("http://127.0.0.1:8000/user/is-friend/",params={"id":12},headers=headers1).text

print requests.get("http://127.0.0.1:8000/user/profile/",params={'id':12},headers=headers1).text

profile = dict()
profile['first_name'] = "tt"
profile['last_name'] = 'ff'
profile['university'] = 'Beihang University'
profile['avatar'] = "dfdfdfdf"

print requests.post("http://127.0.0.1:8000/user/edit/profile/",json.dumps(profile),headers=headers1).text
