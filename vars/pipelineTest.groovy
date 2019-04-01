#!/usr/bin/env groovy
import static hz.source.steps.*

def call(body) {

       /* def config = [:]
  		 def mvnHome
        body.resolveStrategy = Closure.DELEGATE_FIRST
        body.delegate = config
        body()*/
       // hz.source.steps=new hz.source.steps()


        node {

                stage ('initialize') {
                    initialize();
                    cleanWorkspace();
                    preparation();

                }
                stage('Build'){

                    build()

                }
                stage ('Test') {
                    test()
                }

                stage ('archive artifacts') {
                    archiveArtifact()
                }


                stage ('deployment into jboss') {
                    deploy()
                }


        }
    }
