package tds.jenkins

def pullGeneralDockerImages() {
  docker.image(tdsJenkinsGlobals.redisImageName + tdsJenkinsGlobals.redisImageTag).pull()
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
