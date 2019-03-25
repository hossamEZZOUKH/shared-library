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

                    sh "'${mvnHome}/bin/mvn' -B -DskipTests clean package "
                }
                stage ('Tests') {

                        sh "echo 'shell scripts to run static tests...'"
                  sh "'${mvnHome}/bin/mvn' test"
                }
                stage ('Deploy') {
                    sh "echo 'deploying to server ${config.serverDomain}...'"
                }

            } catch (err) {
                currentBuild.result = 'FAILED'
                throw err
            }finally {
              stage('results'){
                  junit 'target/surefire-reports/*.xml'
				  archive 'target/*.jar'
                }
            }
        }
    }
