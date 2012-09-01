from fabric.api import *
import deploy

@task
def production():
    env.hosts = ['paul@endou.fr']
    env.branch = 'master'
    env.remote = 'origin'
    env.path   = '/home/paul/projets/noir/blog'

