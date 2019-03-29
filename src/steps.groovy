class steps{
    steps() {
    }

    void initialize() {
        echo 'Initializing PipelineSteps.'
        constants.mvnHome = tool 'MAVEN'
    }

    void cleanWorkspace() {
        sh "echo 'Cleaning workspace'"
        deleteDir()
    }
    void preparation(){
        sh "echo 'preparation of ${constants.projectName} ... '"
        checkout scm

    }

    void build(){
        sh "echo 'building ${constants.projectName} ...'"
        sh "'${constants.mvnHome}/bin/mvn' -B -DskipTests clean package"
    }
    void test(){
        try{
            sh "echo 'shell scripts to run static tests...'"
            sh "'${constants.mvnHome}/bin/mvn' -fn test"
        }finally{
            sh "echo 'archive test results ...'"
            archiveTestResults()
        }

    }
    void archiveTestResults() {
        //with junit plugin
        //junit "target/surefire-reports/*.xml"
        step([$class: 'JUnitResultArchiver', testResults: '**/target/**/TEST*.xml', allowEmptyResults: true])
        // with testNG plugin
        //step([$class: 'hudson.plugins.testng.Publisher', reportFilenamePattern: 'target/surefire-reports/*.xml'])

    }

    void archiveArtifact(){
        sh "echo 'generate artifacts under /target/** ...'"
        archiveArtifacts  'target/*.jar'

    }
    void deploy(){
        sh "echo 'deploy to a running jboss container ...'"
        sh"'${constants.mvnHome}/bin/mvn' package wildfly:deploy -Dhostname=${constants.host_server_name} -Dport=${constants.port} -Dusername=${constants.userName} -Dpassword=${constants.password} -Dfilename=${artifactId}-${project.version}.jar"
    }

}