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
                     echo 'test finished'
                  }
				 
                }
                stage ('Deploy') {
                    sh "echo 'deploying to server ${config.serverDomain}...'"
                    def testImage = docker.build("jboss-image") 

                      testImage.inside {
                          sh 'echo "EZZOUKH"'
                      }
                }

            } catch (err) {
                currentBuild.result = 'FAILED'
                throw err
            }
        }
    }
