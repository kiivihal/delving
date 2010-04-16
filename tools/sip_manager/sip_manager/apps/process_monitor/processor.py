"""
 Copyright 2010 EDL FOUNDATION

 Licensed under the EUPL, Version 1.1 or as soon they
 will be approved by the European Commission - subsequent
 versions of the EUPL (the "Licence");
 you may not use this work except in compliance with the
 Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.


 Created by: Jacob Lundqvist (Jacob.Lundqvist@gmail.com)

 Maintains and runs tasks
"""

import os
import sys
import time


from django.conf import settings
from django.db import connection

import sipproc


# Since looking up in settings takes some extra cpu, important settings
# are cached within the module
PROCESS_SLEEP_TIME = settings.PROCESS_SLEEP_TIME
PLUGIN_FILTER = settings.PLUGIN_FILTER
SIP_PROCESS_DBG_LVL = settings.SIP_PROCESS_DBG_LVL
URIVALIDATE_MAX_LOAD = settings.URIVALIDATE_MAX_LOAD


class MainProcessor(object):

    def __init__(self, options):
        self.single_run = options['single-run']
        self.tasks_init = [] # tasks that should be run first
        self.tasks_simple = [] # list of all tasks found
        self.tasks_heavy = [] # resourcs hogs, careful with multitasking them...
        if options['flush-all']:
            self.flush_all()
            sys.exit(0)
        elif options['drop-all']:
            self.drop_all()
            sys.exit(0)
        elif options['clear-pids']:
            self.clear_pids()
            sys.exit(0)
        self.find_tasks()

    def run(self):
        "wrapper to catch Ctrl-C."
        try:
            self.run2()
        except KeyboardInterrupt:
            print 'Terminated from Keyboard!'
            sys.exit(1)
        return

    def run2(self):
        # First run all init tasks once
        print
        print 'Running init plugins'
        for taskClass in self.tasks_init:
            tc = taskClass(debug_lvl=SIP_PROCESS_DBG_LVL)
            print '\t%s' % tc.short_name()
            tc.run()

        print
        print 'Commencing operations'
        while True:
            # First run all simple tasks once
            for task_group in (self.tasks_simple, self.tasks_heavy):
                for taskClass in task_group:
                    if self.system_is_occupied():
                        break
                    if settings.THREADING_PLUGINS and taskClass.IS_THREADABLE:
                        # For the moment try slow starting, just one thread per run
                        # this way load builds up more slowly and should keep
                        # within reasonable limits.
                        taskClass(debug_lvl=SIP_PROCESS_DBG_LVL).run()
                        """
                        while taskClass(debug_lvl=SIP_PROCESS_DBG_LVL).run():
                            # Continue to start new threads as long as they
                            # find something to work on
                            #print '*** started thread for', taskClass.__name__
                            pass
                        """
                    else:
                        taskClass(debug_lvl=SIP_PROCESS_DBG_LVL).run()
            if self.single_run:
                print 'Single run, aborting after one run-through'
                break # only run once
            #print 'sleeping a while'
            time.sleep(PROCESS_SLEEP_TIME)
        return True


    def system_is_occupied(self):
        "dont start new tasks when load is high."
        #load_1, load_5, load_15 = os.getloadavg()
        for load in os.getloadavg():
            if load >= URIVALIDATE_MAX_LOAD:
                return True
        return False


    """
    Scan all apps, find the tasks module and add all classes found there

    """
    def find_tasks(self):
        print 'Scanning for plugins'
        for app in settings.INSTALLED_APPS:
            if not app.find('apps') == 0:
                continue
            try:
                exec('from %s.tasks import task_list' % app )
                print ' %s:' % app,
                for task in task_list:
                    print task.__name__,
                    if task.INIT_PLUGIN:
                        self.tasks_init.append(task)
                        continue
                    if PLUGIN_FILTER and not task.__name__ in PLUGIN_FILTER:
                        # we dont ever want to prevent task inits to run...
                        continue
                    resource_hog = False
                    if task.PLUGIN_TAXES_CPU:
                        resource_hog = True
                    if task.PLUGIN_TAXES_DISK_IO:
                        resource_hog = True
                    if task.PLUGIN_TAXES_NET_IO:
                        resource_hog = True
                    if resource_hog:
                        self.tasks_heavy.append(task)
                    else:
                        self.tasks_simple.append(task)
                print
            except ImportError as inst:
                if inst.args[0].find('No module named ') != 0:
                    raise inst
        print 'done!'


    def flush_all(self):
        cursor = connection.cursor()
        if self.db_is_mysql():
            sql = 'TRUNCATE %s'
        else:
            sql = 'TRUNCATE %s CASCADE'
        for table in ('base_item_mdrecord',
                      'base_item_requestmdrecord',
                      'dummy_ingester_aggregator',
                      'dummy_ingester_dataset',
                      'dummy_ingester_provider',
                      'dummy_ingester_request',
                      'plug_uris_req_uri',
                      'plug_uris_uri',
                      'plug_uris_urisource',
                      'process_monitor_processmonitoring',
                      ):
            cursor.execute(sql % table)
        return True

    def drop_all(self):
        cursor = connection.cursor()
        if self.db_is_mysql():
            sql = 'DROP TABLE %s'
        else:
            sql = 'DROP TABLE %s CASCADE'
        for table in ('base_item_mdrecord',
                      'base_item_requestmdrecord',
                      'dummy_ingester_aggregator',
                      'dummy_ingester_dataset',
                      'dummy_ingester_provider',
                      'dummy_ingester_request',
                      'plug_uris_req_uri',
                      'plug_uris_uri',
                      'plug_uris_urisource',
                      'process_monitor_processmonitoring',
                      ):
            try:
                cursor.execute(sql % table)
            except:
                print 'Failed to remove %s' % table

        cursor.execute('commit')
        return True

    def clear_pids(self):
        cursor = connection.cursor()
        cursor.execute('TRUNCATE process_monitor_processmonitoring')
        for table in ('base_item_mdrecord',
                      'dummy_ingester_request',
                      'plug_uris_uri',
                      'plug_uris_urisource',
                      ):
            cursor.execute('UPDATE %s SET pid=0 WHERE pid>0' % table)

    def db_is_mysql(self):
        # This will be uggly...
        cursor = connection.cursor()
        if hasattr(cursor, 'db'):
            # if DEBUG=True its found here...
            b = 'mysql' in cursor.db.__module__
        else:
            # Otherwise we find it here
            b = 'mysql' in cursor.__module__
        return b


"""
  Request actions

  all items with status REQS_INIT should be parsed and MdRecords created
"""
