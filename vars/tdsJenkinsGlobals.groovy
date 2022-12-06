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
  return '7.17.7'
}

def getElasticsearchTestPassword() {
  return 'simplepass'
}

def getElasticsearchTestUsername() {
  return 'local'
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
  return '6.2.6'
}

def getStandardRspecOpts() {
  return '--backtrace --fail-fast'
}