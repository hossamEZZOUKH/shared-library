
def call(body) {

       /* def config = [:]
  		 def mvnHome
        body.resolveStrategy = Closure.DELEGATE_FIRST
        body.delegate = config
        body()*/


        node {
            try {
                stage ('initialize') {
                    steps = new steps()
                    steps.initialize()
                    steps.cleanWorkspace()
                    steps.preparation()

                }
                stage('Build'){

                    steps.build()

                }
                stage ('Test') {
                    steps.test()
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
                          sh "java -jar target/*.jar"
                        
                           input message:"press 'proceed' to continue"
                      }
                }

            } catch (err) {
                currentBuild.result = 'FAILED'
                throw err
            }
        }
    }
