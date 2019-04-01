#!groovy
package hz.source


def initialize() {
    echo 'Initializing PipelineSteps.'
     def mvnHome = tool 'MAVEN'
}

def cleanWorkspace(script) {
    echo 'Cleaning workspace'
    script.deleteDir()
}
def preparation(script){
    //script.sh "echo 'preparation of ${constants.projectName} ... '"
    script.checkout scm

}

def build(script){
    //script.sh "echo 'building ${constants.projectName} ...'"

    def mvnHome = tool 'MAVEN'
    script.sh "'${mvnHome}/bin/mvn' -B -DskipTests clean package"
}
def test(script){
    try{
        //script.sh "echo 'shell scripts to run static tests...'"
        def mvnHome = tool 'MAVEN'
        script.sh "'${mvnHome}/bin/mvn' -fn test"
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
   // script.sh "echo 'generate artifacts under /target/** ...'"
    script.archiveArtifacts  'target/*.jar'

}
def deploy(script){
   // script.sh "echo 'deploy to a running jboss container ...'"
    def mvnHome = tool 'MAVEN'
    String host_server_name="localhost";
    String port="9990";
    String userName="hossam";
    String password="hossam";
    script.sh"'${mvnHome}/bin/mvn' package wildfly:deploy -Dhostname=${host_server_name} -Dport=${port} -Dusername=${userName} -Dpassword=${password} -Dfilename=${artifactId}-${project.version}.jar"
}

