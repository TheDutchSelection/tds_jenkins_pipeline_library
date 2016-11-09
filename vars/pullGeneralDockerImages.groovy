#!groovy

def call() {
  onzinAap()
  docker.images('thedutchselection/postgresql:9.4.8').pull
}