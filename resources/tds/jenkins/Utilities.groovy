#!groovy

package tds.jenkins

class Utilities {

  static def pullGeneralDockerImages() {
    docker.images('thedutchselection/postgresql:9.4.8').pull
  }

}
