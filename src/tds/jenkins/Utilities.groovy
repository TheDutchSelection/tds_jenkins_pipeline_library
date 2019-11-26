package tds.jenkins

def buildDockerImage(registryAddress, name, tag, args = null) {
  if (!args?.trim()) {
    def result = docker.build(registryAddress + '/' + name + ':' + tag)
  } else {
    def result = docker.build(registryAddress + '/' + name + ':' + tag, args)
  }

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

def deployApp(redisApp, redisAppEnv) {
  sh "REDIS_APP_ENV=${redisAppEnv} REDIS_APP=${redisApp} bash /usr/local/bin/deploy_app.sh"
}

def deployApplication(appId, appEnv, dockerImageName, dockerImageTag, probePath) {
  sh "APP_ENV=${appEnv} APP_ID=${appId} DOCKER_IMAGE_NAME=${dockerImageName} DOCKER_IMAGE_TAG=${dockerImageTag} PROBE_PATH=${probePath} bash /usr/local/bin/deploy_application.sh"
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

def runElasticsearch(label) {
  def dataDirectory = '/home/elastic/data'
  def dataContainer = runDataContainer('9200', '9200', dataDirectory, label)

  def elasticsearchImage = docker.image(tdsJenkinsGlobals.elasticsearchImageName + ':' + tdsJenkinsGlobals.elasticsearchImageTag)
  def elasticsearchContainer = elasticsearchImage.run(
    '-l "' + label + '" ' +
    '--cap-add=IPC_LOCK ' +
    '--ulimit memlock=-1:-1 ' +
    '--ulimit nofile=65536:65536 ' +
    '-e "CLUSTER_NAME=test" ' +
    '-e "DATA_DIRECTORY=' + dataDirectory + '" ' +
    '-e "EXPECTED_NUMBER_OF_NODES=1" ' +
    '-e "HOST=0.0.0.0" ' +
    '-e "ES_JAVA_OPTS=-Xms1g -Xmx1g" ' +
    '-e "NODE_DISK_TYPE=ssd" ' +
    '-e "NODE_NAME=test_node" ' +
    '-e "NODE_INGEST=true" ' +
    '-e "NODE_MASTER=true" ' +
    '-e "NODE_DATA=true" ' +
    '-e "MAX_LOCAL_STORAGE_NODES=1" ' +
    '-e "MINIMUM_MASTER_NODES=1" ' +
    '-e "MINIMUM_NUMBER_OF_NODES=1" ' +
    '-e "PATH_DATA=/home/elastic/data/data" ' +
    '-e "PATH_LOGS=/home/elastic/data/logs" ' +
    '-e "PATH_REPO=/home/elastic/data/backups" ' +
    '-e "PUBLISH_HOST=0.0.0.0" ' +
    '-e "TRANSPORT_PORT=9300" ' +
    '-e "HTTP_PORT=9200" ' +
    '--volumes-from ' + dataContainer.id + ' ' +
    '-p :9200 -p :9200/udp'
  )
  sleep(1) // give elasticsearch some time

  return elasticsearchContainer
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

def runRedis(label) {
  def dataDirectory = '/home/redis/data'
  def dataContainer = runDataContainer('6379', '6379', dataDirectory, label)

  def redisImage = docker.image(tdsJenkinsGlobals.redisImageName + ':' + tdsJenkinsGlobals.redisImageTag)
  def redisContainer = redisImage.run(
    '-l "' + label + '" ' +
    '-e "DATA_DIRECTORY=' + dataDirectory + '" ' +
    '--volumes-from ' + dataContainer.id + ' ' +
    '-p :6379 -p :6379/udp'
  )
  sleep(1) // give redis some time

  return redisContainer
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

def standardPipelineSetup(dependentImageName, dependentImageTag) {
  setPipelineProperties()
  pullGeneralDockerImages()
  pullDockerImage(dependentImageName, dependentImageTag)
  cleanupDanglingDockerImages()
}