def call(body) {

        def config = [:]
        body.resolveStrategy = Closure.DELEGATE_FIRST
        body.delegate = config
        body()


        def mvnHome = tool 'MAVEN'
        node {
            // Clean workspace before doing anything
            deleteDir()

            try {
                stage ('Clone') {
                    checkout scm
                }
                stage('preparation'){
                        sh "'${mvnHome}/bin/mvn' archetype:generate -B " +
                        '-DarchetypeGroupId=org.apache.maven.archetypes ' +
                        '-DarchetypeArtifactId=maven-archetype-quickstart ' +
                        "-DgroupId=com.company -DartifactId=${config.projectName}"

                }
                stage ('Build') {
                    sh "echo 'building ${config.projectName} ...'"

                    sh "'${mvnHome}/bin/mvn' -B -DskipTests clean package "
                }
                stage ('Tests') {

                        sh "echo 'shell scripts to run static tests...'"
                        sh "'${mvnHome/bin/mvn}' test"
                }
                stage ('Deploy') {
                    sh "echo 'deploying to server ${config.serverDomain}...'"
                }

                stage('results'){
                    junit 'target/surefire-reports/*.xml'
                }
            } catch (err) {
                currentBuild.result = 'FAILED'
                throw err
            }
        }
    }