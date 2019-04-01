//#!/usr/bin/env groovy

import hz.source.stepsAll

def call(body) {

       def config = [:]
  		 def mvnHome
        body.resolveStrategy = Closure.DELEGATE_FIRST
        body.delegate = config
        body()

        def steps = new stepsAll()

        node {

                stage ('initialize') {
                     //def steps = new stepsAll()
                    steps.initialize()
                    steps.cleanWorkspace();
                    steps.preparation();


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
