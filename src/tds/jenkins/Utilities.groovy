package tds.jenkins

def buildDockerImage(registryAddress, name, tag) {
  def result = docker.build(registryAddress + '/' + name + ':' + tag)

  return result
}

def cleanupDanglingDockerImages() {
  sh 'docker rmi $(/usr/bin/docker images -q -f "dangling=true") || true'
}

def cleanupDockerImage(registryAddress, name, tag) {
  sh "docker rmi ${registryAddress + '/' + name + ':' + tag} || true"
}

def cleanupDockerContainers(label) {
  sh 'docker rm -f -v $(/usr/bin/docker ps -a -q -f "label=' + label + '") || true'
}

def dockerRegistryName(registryAddress) {
  return 'http://' + registryAddress + '/'
}

def deployApp(redisAppName, redisEnv) {
  def command = "bash /usr/local/bin/deploy_app.sh"
  def env = ['REDIS_APP="beladvies_nl"', 'REDIS_APP_ENV="wrkprd"']

  println command.execute(env).text
}

def dockerContainerIp(container) {
  def result = sh(
    script: "docker inspect --format '{{ .NetworkSettings.Gateway }}' " + container.id,
    returnStdout: true
  ).trim()

  return result
}

def dockerContainerPort(container, port) {
  def result = sh(
    script: "docker inspect --format '{{(index (index .NetworkSettings.Ports \"" + port + "\") 0).HostPort }}' " + container.id,
    returnStdout: true
  ).trim()
  
  return result
}

def pullGeneralDockerImages() {
  pullDockerImage(tdsJenkinsGlobals.dataContainerImageName, tdsJenkinsGlobals.dataContainerImageTag)
  pullDockerImage(tdsJenkinsGlobals.elasticsearchImageName, tdsJenkinsGlobals.elasticsearchImageTag)
  pullDockerImage(tdsJenkinsGlobals.postgresqlImageName, tdsJenkinsGlobals.postgresqlImageTag)
  pullDockerImage(tdsJenkinsGlobals.redisImageName, tdsJenkinsGlobals.redisImageTag)
}

def pullDockerImage(imageName, imageTag) {
  docker.image(imageName + ':' + imageTag).pull()
}

def runDataContainer(userId, groupId, dataDirectory, label) {
  def dataContainerImage = docker.image(tdsJenkinsGlobals.dataContainerImageName + ':' + tdsJenkinsGlobals.dataContainerImageTag)
  def dataContainerContainer = dataContainerImage.run(
    '-l "' + label + '" ' +
    '-e "DATA_DIRECTORY=' + dataDirectory + '" ' +
    '-e "USER_ID=' + userId + '" ' +
    '-e "GROUP_ID=' + groupId + '" ' +
    '-v "' + dataDirectory + '"'
  )

  return dataContainerContainer
}

def runPostgresql(label) {
  def dataDirectory = '/home/postgresql/data'
  def dataContainer = runDataContainer('5432', '5432', dataDirectory, label)

  def postgresqlImage = docker.image(tdsJenkinsGlobals.postgresqlImageName + ':' + tdsJenkinsGlobals.postgresqlImageTag)
  def postgresqlContainer = postgresqlImage.run(
    '-l "' + label + '" ' +
    '-e "DATA_DIRECTORY=' + dataDirectory + '" ' +
    '-e "SUPERUSER_USERNAME=' + tdsJenkinsGlobals.postgresqlTestUsername + '" ' +
    '-e "SUPERUSER_PASSWORD=' + tdsJenkinsGlobals.postgresqlTestPassword + '" ' +
    '--volumes-from ' + dataContainer.id + ' ' +
    '-p :5432 -p :5432/udp'
  )
  sleep(5) // give the database some time

  return postgresqlContainer
}

def setPipelineProperties() {
  properties(
    [
      pipelineTriggers([[$class: 'GitHubPushTrigger']]),
      [$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', daysToKeepStr: '30', numToKeepStr: '3']],
      [$class: 'DisableConcurrentBuildsJobProperty']
    ]
  )
}
