package tds.jenkins

class Utilities {

  Utilities() { }

  def mvn(args) {
    steps.sh "${steps.tool 'Maven'}/bin/mvn -o ${args}"
  }
}