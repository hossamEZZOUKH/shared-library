
def call(body) {
    def config = [:]
    def mvnHome
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()


    node {
        // Clean workspace before doing anything
        deleteDir()

        try {
            stage ('Clone') {
                checkout scm
                mvnHome = tool 'MAVEN'
            }
            stage('preparation'){
                /* sh "'${mvnHome}/bin/mvn' archetype:generate -B " +
                 '-DarchetypeGroupId=org.apache.maven.archetypes ' +
                 '-DarchetypeArtifactId=maven-archetype-quickstart ' +
                 "-DgroupId=com.company -DartifactId=${config.projectName}"*/
                sh "echo 'preparation of ${config.projectName} ... '"

            }
            stage ('Build') {
                sh "echo 'building ${config.projectName} ...'"

                sh "'${mvnHome}/bin/mvn' -B -DskipTests clean package"
            }
            stage ('Tests') {
                try{
                    sh "echo 'shell scripts to run static tests...'"
                    sh "'${mvnHome}/bin/mvn' -fn test"

                }finally{
                    junit "target/surefire-reports/*.xml"
                    //step([$class: 'hudson.plugins.testng.Publisher', reportFilenamePattern: 'target/surefire-reports/*.xml'])
                    archiveArtifacts  'target/*.jar'
                    echo    cc'test finished'
                }

            }
            stage ('Deploy') {
                sh "echo 'deploying to server ${config.serverDomain}...'"
                /*def testImage = docker.build("jboss-image")

                testImage.inside {
                    sh 'echo "EZZOUKH"'
                    sh "java -jar target/*.jar"

                    input message:"press 'proceed' to continue"
                }*/
                 //sh"'${mvnHome}/bin/mvn' package wildfly:deploy -Dhostname=10.1.19.26 -Dport=9990 -Dusername=hossam -Dpassword=hossam -Dfilename=project_mvn_shared-1.0-SNAPSHOT.jar"
                 sh"'${mvnHome}/bin/mvn' package wildfly:deploy"
                 input message:"press 'proceed' to continue"
                 sh "'${mvnHome}/bin/mvn' wildfly:undeploy"

            }

        } catch (err) {
            currentBuild.result = 'FAILED'
            throw err
        }
    }
}
