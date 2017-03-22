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
  return '2.3.3'
}

def getPostgresqlImageName() {
  return 'thedutchselection/postgresql'
}

def getPostgresqlImageTag() {
  return '9.6.2'
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
  return '3.2.5'
}

def getStandardRspecOpts() {
  return '--backtrace --fail-fast'
}