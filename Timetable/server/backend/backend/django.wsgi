import os
import sys

path = '/root/timetable/timetable/Timetable/server/backend'
if path not in sys.path:
    sys.path.append(path)


os.environ['DJANGO_SETTINGS_MODULE'] = 'backend.settings'

import django.core.handlers.wsgi
application = django.core.handlers.wsgi.WSGIHandler()

