from fabric.api import task, local, env

@task
def commit():
    local('git add -p && git commit')

@task
def push():
    local('git push ' + env.remote + ' ' + env.branch)

@task
def pull():
    with cd(env.path):
        run('git checkout ' + env.branch)
        run('git pull origin ' + env.branch)

@task(default=True)
def full_deploy():
    commit()
    push()
    pull()
