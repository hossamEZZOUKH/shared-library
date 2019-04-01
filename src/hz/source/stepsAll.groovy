package hz.source
//import com.cloudbees.groovy.cps.NonCPS

 class stepsAll {

     stepsAll() {
     }


     def initialize() {
         stage("rrrrrrrrrrrrrrrrrrrr"){
             sh "echo 'Initializing PipelineSteps.'"
             constants.mvnHome = tool 'MAVEN'
             
         }
     }

     def cleanWorkspace() {
         sh "echo 'Cleaning workspace'"
         deleteDir()
     }
     def preparation(){
         sh "echo 'preparation of ${constants.projectName} ... '"
         checkout scm

     }

     def build(){
         sh "echo 'building ${constants.projectName} ...'"
         sh "'${constants.mvnHome}/bin/mvn' -B -DskipTests clean package"
     }
     def test(){
         try{
             sh "echo 'shell scripts to run static tests...'"
             sh "'${constants.mvnHome}/bin/mvn' -fn test"
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

     def archiveArtifact(){
         sh "echo 'generate artifacts under /target/** ...'"
         archiveArtifacts  'target/*.jar'

     }
     def deploy(){
         sh "echo 'deploy to a running jboss container ...'"
         sh"'${constants.mvnHome}/bin/mvn' package wildfly:deploy -Dhostname=${constants.host_server_name} -Dport=${constants.port} -Dusername=${constants.userName} -Dpassword=${constants.password} -Dfilename=${artifactId}-${project.version}.jar"
     }

 }

