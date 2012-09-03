from fabric.api import *
import deploy

env.branch = 'master'
env.remote = 'origin'

@task
def production():
    env.hosts = ['endou.fr']
    env.user  = 'paul'
    env.key_filename = '/home/paul/.ssh/id_rsa'
    env.remote_string = 'https://github.com/pauldub/Noir-Blog.git'
    env.deploy_path   = '~/projects/noir/blog'

