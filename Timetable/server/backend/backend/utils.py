__author__ = 'mimighostipad'

import json

class JsonMessage(object):

    def __init__(self,key,msg):
        self.msg = msg
        self.data = {key:msg}

class JsonErrorMessage(JsonMessage):

    def __init__(self,error):
        super(JsonErrorMessage,self).__init__("error",error)

SUCCESS_MESSAGE = JsonMessage("status","success")
NO_ID_FOUND_ERROR = JsonErrorMessage("No id provided")
INVALID_OBJECT_ID_ERROR = JsonErrorMessage("Invalid object id. Object not found")

if __name__ == "__main__":
    print JsonErrorMessage("this is wrong").data

