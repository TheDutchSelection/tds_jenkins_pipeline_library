package tds.jenkins

class Utilities {

  Utilities() { this }

  def mvn(args) {
    steps.sh "${steps.tool 'Maven'}/bin/mvn -o ${args}"
  }
}