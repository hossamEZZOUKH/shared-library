#!groovy
package hz.source


def initialize() {
    echo 'Initializing PipelineSteps.'
     def mvnHome = tool 'MAVEN'
}

def cleanWorkspace() {
    echo 'Cleaning workspace'
    deleteDir()
}
def preparation(){
    //script.sh "echo 'preparation of ${constants.projectName} ... '"
    checkout scm

}

def build(){
    //script.sh "echo 'building ${constants.projectName} ...'"

    def mvnHome = tool 'MAVEN'
    sh "'${mvnHome}/bin/mvn' -B -DskipTests clean package"
}
def test(){
    try{
        //script.sh "echo 'shell scripts to run static tests...'"
        def mvnHome = tool 'MAVEN'
        sh "'${mvnHome}/bin/mvn' -fn test"
    }finally{
        sh "echo 'archive test results ...'"
        archiveTestResults()
    }

}
def archiveTestResults() {
    //with junit plugin
    //junit "target/surefire-reports/*.xml"
    step([$class: 'JUnitResultArchiver', testResults: '**/target/**/TEST*.xml', allowEmptyResults: true])
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
    String host_server_name="10.1.19.3";
    String port="9990";
    String userName="hossam";
    String password="hossam";
    script.sh"'${mvnHome}/bin/mvn' package wildfly:deploy -Dhostname=${host_server_name} -Dport=${port} -Dusername=${userName} -Dpassword=${password} -Dfilename=*.jar"
}

return this