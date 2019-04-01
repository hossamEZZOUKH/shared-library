#!/usr/bin/env groovy
package hz.source

public class steps{

    public  static void initialize() {
        echo 'Initializing PipelineSteps.'
        constants.mvnHome = tool 'MAVEN'
    }

    public static void  cleanWorkspace() {
        sh "echo 'Cleaning workspace'"
        deleteDir()
    }
    public static void  preparation(){
        sh "echo 'preparation of ${constants.projectName} ... '"
        checkout scm

    }

    public static void  build(){
        sh "echo 'building ${constants.projectName} ...'"
        sh "'${constants.mvnHome}/bin/mvn' -B -DskipTests clean package"
    }
    public static void  test(){
        try{
            sh "echo 'shell scripts to run static tests...'"
            sh "'${constants.mvnHome}/bin/mvn' -fn test"
        }finally{
            sh "echo 'archive test results ...'"
            archiveTestResults()
        }

    }
    public static void  archiveTestResults() {
        //with junit plugin
        //junit "target/surefire-reports/*.xml"
        step([$class: 'JUnitResultArchiver', testResults: '**/target/**/TEST*.xml', allowEmptyResults: true])
        // with testNG plugin
        //step([$class: 'hudson.plugins.testng.Publisher', reportFilenamePattern: 'target/surefire-reports/*.xml'])

    }

    public static void  archiveArtifact(){
        sh "echo 'generate artifacts under /target/** ...'"
        archiveArtifacts  'target/*.jar'

    }
    public static void  deploy(){
        sh "echo 'deploy to a running jboss container ...'"
        sh"'${constants.mvnHome}/bin/mvn' package wildfly:deploy -Dhostname=${constants.host_server_name} -Dport=${constants.port} -Dusername=${constants.userName} -Dpassword=${constants.password} -Dfilename=${artifactId}-${project.version}.jar"
    }

}

