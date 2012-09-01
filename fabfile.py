from fabric.api import *
import deploy

@task
def staging():
    env.hosts = ['paul@endou.fr']
    env.branch = 'develop'
    env.remote = 'staging'
    pass

@task
def production():
    env.hosts = ['tabbakk@pauldhubert.com']
    env.branch = 'master'
    env.remote = 'production'
    pass
