package tds.jenkins

class Utilities {

  Utilities() {this.steps = steps}

  def mvn(args) {
    steps.sh "${steps.tool 'Maven'}/bin/mvn -o ${args}"
  }
}