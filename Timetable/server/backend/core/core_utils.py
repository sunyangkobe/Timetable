__author__ = 'mimighostipad'

from django.core.cache import cache


AVATAR_PRE = "avatar_"
DEFAULT_AVATAR = "avatar_default"


def get_user_avatar_key(user):
    return AVATAR_PRE + str(user.id)

def get_user_avatar(user):
    avatar_key = get_user_avatar_key(user)
    if cache.has_key(avatar_key):
        return cache.get(avatar_key)
    return cache.get(DEFAULT_AVATAR)

