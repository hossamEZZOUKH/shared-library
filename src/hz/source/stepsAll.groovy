#!groovy
package hz.source


def initialize(script) {
    echo 'Initializing PipelineSteps.'
    constants.mvnHome = tool 'MAVEN'
}

def cleanWorkspace() {
    sh "echo 'Cleaning workspace'"
    deleteDir()
}
def preparation(script){
    script.sh "echo 'preparation of ${constants.projectName} ... '"
    script.checkout scm

}

def build(script){
    script.sh "echo 'building ${constants.projectName} ...'"
    script.sh "'${constants.mvnHome}/bin/mvn' -B -DskipTests clean package"
}
def test(script){
    try{
        script.sh "echo 'shell scripts to run static tests...'"
        script.sh "'${constants.mvnHome}/bin/mvn' -fn test"
    }finally{
        script.sh "echo 'archive test results ...'"
        archiveTestResults()
    }

}
def archiveTestResults(script) {
    //with junit plugin
    //junit "target/surefire-reports/*.xml"
    script.step([$class: 'JUnitResultArchiver', testResults: '**/target/**/TEST*.xml', allowEmptyResults: true])
    // with testNG plugin
    //step([$class: 'hudson.plugins.testng.Publisher', reportFilenamePattern: 'target/surefire-reports/*.xml'])

}

def archiveArtifact(script){
    script.sh "echo 'generate artifacts under /target/** ...'"
    script.archiveArtifacts  'target/*.jar'

}
def deploy(script){
    script.sh "echo 'deploy to a running jboss container ...'"
    script.sh"'${constants.mvnHome}/bin/mvn' package wildfly:deploy -Dhostname=${constants.host_server_name} -Dport=${constants.port} -Dusername=${constants.userName} -Dpassword=${constants.password} -Dfilename=${artifactId}-${project.version}.jar"
}

