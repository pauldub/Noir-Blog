from fabric.api import task, local, env, cd, run
from fabric.contrib.files import exists
from fabric.contrib.console import confirm

@task
def init():
    run('git init')
    run('git remote add origin ' + env.remote_string)
    run('git pull -u origin ' + env.branch)

@task
def commit():
    local('git add -p && git commit')

@task
def push():
    local('git push ' + env.remote + ' ' + env.branch)


@task(alias='update')
def pull():
    with cd(env.deploy_path):
        if not exists('.git') and confirm('The current directory is not a git repo, run git init?'):
            init()
        run('git checkout ' + env.branch)
        run('git pull origin ' + env.branch)

@task(default=True)
def full_deploy():
    commit()
    push()
    pull()
