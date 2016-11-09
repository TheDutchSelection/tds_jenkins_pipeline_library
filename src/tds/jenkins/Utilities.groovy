package tds.jenkins

def cleanupOldDockerImages() {
  sh 'docker rmi $(/usr/bin/docker images -q -f "dangling=true") || true'
}

def cleanupOldDockerTestContainers() {
  sh 'docker rm -v $(/usr/bin/docker ps -q -f "status=exited" -f "label=test-container") || true'
}

def pullGeneralDockerImages() {
  pullDockerImage(tdsJenkinsGlobals.elasticsearchImageName, tdsJenkinsGlobals.elasticsearchImageTag)
  pullDockerImage(tdsJenkinsGlobals.postgresqlImageName, tdsJenkinsGlobals.postgresqlImageTag)
  pullDockerImage(tdsJenkinsGlobals.redisImageName, tdsJenkinsGlobals.redisImageTag)
}

def pullDockerImage(imageName, imageTag) {
  docker.image(imageName + ':' + imageTag).pull()
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
