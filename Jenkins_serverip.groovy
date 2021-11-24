pipeline{
    agent any
    stages{
        stage("clone the code"){
            steps{
                println "the code cloned"
            }
        }
        stage("build the code"){
            steps{
                println "the code built"
            }
        }
        stage("store to s3"){
            steps{
                println "artefact stored to s3"
            }
        }
        stage("download to present location"){
            steps{
                println "the artefact downloaded"
            }
        }
        stage("copy the artefact"){
            steps{
                println "the artefact copied"
            }
        }
    }
}
