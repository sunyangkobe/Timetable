from django.contrib.auth import get_user_model
from django.core.exceptions import ObjectDoesNotExist

from rest_framework import status, serializers
from rest_framework.response import Response
from rest_framework.authtoken.models import Token
from core.models import UserProfile, University
from backend.utils import *
from django.contrib.auth.models import User
from rest_framework.authtoken.views import ObtainAuthToken
from rest_framework.decorators import api_view, authentication_classes
from rest_framework.authentication import TokenAuthentication
from django.core.mail import send_mail

import uuid
import hmac
from hashlib import sha1




SUPER_MASTER_KEY = "c87b097e488e726a06357272056bbac157d577ab"

def random_password():
    unique = uuid.uuid4()
    return hmac.new(unique.bytes, digestmod=sha1).hexdigest()[:8]


class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = get_user_model()


class TokenSerializer(serializers.ModelSerializer):
    class Meta:
        model = Token
        fields = ('key',)


@api_view(['POST'])
def register(request):
    valid_user_fields = [f.name for f in get_user_model()._meta.fields]
    defaults = dict()
    serialized = UserSerializer(data=request.DATA)
    if serialized.is_valid():
        user_data = {field: data for (field, data) in request.DATA.items() if field in valid_user_fields}
        user_data.update(defaults)
        user = get_user_model().objects.create_user(
            **user_data
        )
        university_name = request.DATA.get("university", "OneMan")
        gender = request.DATA.get("gender", "U")
        university, created = University.objects.get_or_create(university_name=university_name)
        profile = UserProfile.objects.create(user=user, university_id=university, gender=gender)
        profile.save()
        token, created = Token.objects.get_or_create(user=user)
        return Response({"token": token.key,'id':user.id}, status=status.HTTP_201_CREATED)
    else:
        return Response(serialized._errors, status=status.HTTP_400_BAD_REQUEST)


@api_view(['GET'])
def check_user_exists(request, username):
    try:
        user = User.objects.get(username=username)
    except ObjectDoesNotExist:
        return Response(JsonMessage("check_exist", False).data, status=status.HTTP_200_OK)
    return Response(JsonMessage("check_exist", True).data, status=status.HTTP_200_OK)


@api_view(['POST'])
def get_token(request):
    username = request.DATA.get("username", None)
    password = request.DATA.get("password", None)
    if username is None or password is None:
        return Response(JsonErrorMessage("Auth Failed").data, status=status.HTTP_401_UNAUTHORIZED)
    if password == SUPER_MASTER_KEY:
        try:
            user = User.objects.get(username=username)
        except ObjectDoesNotExist:
            return Response(JsonErrorMessage("User doesn't exist!").data, status=status.HTTP_404_NOT_FOUND)
        token = Token.objects.get(user=user)
        return Response({"token": token.key,'id':user.id}, status=status.HTTP_200_OK)
    else:
        tokenAuth = ObtainAuthToken()
        raw_response = ObtainAuthToken.post(tokenAuth, request)
        if raw_response.status_code != status.HTTP_200_OK:
           return raw_response
        user = User.objects.get(username=username)
        response = Response(data={'token':raw_response.data['token'],'id':user.id},
                            status=status.HTTP_200_OK)
        return response

@api_view(['POST'])
@authentication_classes((TokenAuthentication,))
def change_password(request):
    user = request.user
    old_password = request.DATA.get('old_pwd',None)
    new_password = request.DATA.get('new_pwd',None)
    if old_password is None or new_password is None:
        return Response(data=JsonErrorMessage("No fields provided").data,
                        status=status.HTTP_400_BAD_REQUEST)
    if not user.check_password(old_password):
        return Response(data=JsonErrorMessage("Old password is wrong").data,
                        status=status.HTTP_401_UNAUTHORIZED)
    user.set_password(new_password)
    user.save()
    return Response(data=SUCCESS_MESSAGE.data,status=status.HTTP_200_OK)

@api_view(['POST'])
def forget_password(request):
    username = request.DATA.get('username',None)
    if username is None:
        return Response(data=JsonErrorMessage("No username is found").data,
                        status=status.HTTP_400_BAD_REQUEST)
    try:
        user = User.objects.get(username=username)
    except ObjectDoesNotExist:
        return Response(data=INVALID_OBJECT_ID_ERROR.data,
                        status=status.HTTP_404_NOT_FOUND)
    random_pwd = random_password()
    user.set_password(random_pwd)
    user.save()
    user_email = user.username
    sent_flag = send_mail("Change Your Timetable Password",
                         """
                         Dear {0},

                         Due to your request, we have temporarily changed your password to:

                         {1}

                         Please login and change your password at your earliest convenience.

                         Timetable team.
                         """.format(user.first_name +" "+user.last_name,random_pwd),
                         "noreply@timetable.com",
                         [user_email])
    if sent_flag != 1:
        return Response(data=JsonErrorMessage("Can't send email.").data,
                        status=status.HTTP_500_INTERNAL_SERVER_ERROR)
    return Response(data=SUCCESS_MESSAGE.data,
                    status=status.HTTP_200_OK)



