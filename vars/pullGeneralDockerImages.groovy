#!groovy

def call() {
  docker.images('thedutchselection/postgresql:9.4.8').pull
}