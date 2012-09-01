from fabric.api import *
import deploy

@task
def staging():
    env.hosts = ['paul@endou.fr']
    env.branch = 'develop'
    env.remote = 'staging'
    env.path   = '/home/paul/projets/noir/blog/resources/public/static'

@task
def production():
    env.hosts = ['tabbakk@pauldhubert.com']
    env.branch = 'master'
    env.remote = 'production'
    env.path   = '/home/tabbakk/webapps/main'
