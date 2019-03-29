
def call(body) {

       /* def config = [:]
  		 def mvnHome
        body.resolveStrategy = Closure.DELEGATE_FIRST
        body.delegate = config
        body()*/
        steps=new steps()


        node {
            try {
                stage ('initialize') {
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

                stage ('archive artifacts') {
                    steps.archiveArtifact()
                }

            } catch (err) {
                currentBuild.result = 'FAILED'
                throw err
            }finally{
                stage ('deployment into jboss') {
                    steps.deploy()
                }

            }
        }
    }
