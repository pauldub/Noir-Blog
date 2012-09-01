from fabric.api import *
import deploy

@task
def production():
    env.hosts = ['endou.fr']
    env.user  = 'paul'
    env.key_filename = '/home/paul/.ssh/id_rsa'
    env.branch = 'master'
    env.remote = 'origin'
    env.path   = '/home/paul/projets/noir/blog'

