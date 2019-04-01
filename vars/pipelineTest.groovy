#!/usr/bin/env groovy

def call(body) {

       /* def config = [:]
  		 def mvnHome
        body.resolveStrategy = Closure.DELEGATE_FIRST
        body.delegate = config
        body()*/
       // steps=new steps()


        node {

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


                stage ('deployment into jboss') {
                    steps.deploy()
                }


        }
    }
