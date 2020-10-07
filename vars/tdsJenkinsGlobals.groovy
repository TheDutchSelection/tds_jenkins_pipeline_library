#!groovy

def getDataContainerImageName() {
  return 'thedutchselection/data'
}

def getDataContainerImageTag() {
  return 'latest'
}

def getElasticsearchImageName() {
  return 'thedutchselection/elasticsearch'
}

def getElasticsearchImageTag() {
  return '6.8.12'
}

def getPostgresqlImageName() {
  return 'thedutchselection/postgresql'
}

def getPostgresqlImageTag() {
  return '12.4'
}

def getPostgresqlTestPassword() {
  return 'test123test'
}

def getPostgresqlTestUsername() {
  return 'test'
}

def getRedisImageName() {
  return 'thedutchselection/redis'
}

def getRedisImageTag() {
  return '4.0.2'
}

def getStandardRspecOpts() {
  return '--backtrace --fail-fast'
}