#!groovy

def call() {
  echo 'aapa'
  docker.images('thedutchselection/postgresql:9.4.8').pull
}