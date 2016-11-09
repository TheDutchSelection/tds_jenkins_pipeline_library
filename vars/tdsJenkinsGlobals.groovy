#!groovy

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
  return '9.4.8'
}

def getRedisImageName() {
  return 'thedutchselection/redis'
}

def getRedisImageTag() {
  return '3.2.5'
}

def getRedisImageNamePlusTag() {
  return getRedisImageName() + ':' + getRedisImageTag()
}
