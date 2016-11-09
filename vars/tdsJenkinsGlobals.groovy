#!groovy

def getElasticsearchImageName() {
  return 'thedutchselection/elasticsearch'
}

def getElasticsearchImageTage() {
  return '2.3.3'
}

def getPostgresqlImageName() {
  return 'thedutchselection/postgresql'
}

def getPostgresqlImageTage() {
  return '9.4.8'
}

def getRedisImageName() {
  return 'thedutchselection/redis'
}

def getRedisImageTage() {
  return '3.2.5'
}
