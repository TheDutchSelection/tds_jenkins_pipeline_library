package tds.jenkins

def buildDockerImage(registryAddress, name, tag) {
  docker.build(registryAddress + '/' + name + ':' + tag)
}

def cleanupDanglingDockerImages() {
  sh 'docker rmi $(/usr/bin/docker images -q -f "dangling=true") || true'
}

def cleanupDockerImage(registryAddress, name, tag) {
  sh "docker rmi ${registryAddress + '/' + name + ':' + tag}"
}

def cleanupOldDockerTestContainers() {
  sh 'docker rm -v $(/usr/bin/docker ps -q -f "status=exited" -f "label=test-container") || true'
}

def dockerRegistryName(registryAddress) {
  return 'http://' + registryAddress + '/'
}

def pullGeneralDockerImages() {
  pullDockerImage(tdsJenkinsGlobals.elasticsearchImageName, tdsJenkinsGlobals.elasticsearchImageTag)
  pullDockerImage(tdsJenkinsGlobals.postgresqlImageName, tdsJenkinsGlobals.postgresqlImageTag)
  pullDockerImage(tdsJenkinsGlobals.redisImageName, tdsJenkinsGlobals.redisImageTag)
}

def pullDockerImage(imageName, imageTag) {
  docker.image(imageName + ':' + imageTag).pull()
}

def pushDockerImage(registryAddress, name, tag) {
  docker.image(registryAddress + '/' + name + ':' + tag).push(tag)
}

def runPostgresql() {
  postgresqlImage = docker.image(tdsJenkinsGlobals.postgresqlImageName + ':' + tdsJenkinsGlobals.postgresqlImageTag)

  return postgresqlImage.run('-l "test" -e "DATA_DIRECTORY=/home/postgresql/data" -e "SUPERUSER_USERNAME=' + tdsJenkinsGlobals.postgresqlTestUsername + '" -e "SUPERUSER_PASSWORD=' + tdsJenkinsGlobals.postgresqlTestPassword + '" -p :5432 -p :5432/udp')
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
