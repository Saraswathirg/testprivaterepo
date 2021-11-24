pipeline{
    agent any
    parameters{
        string(name:'BRANCH',defaultValue:'master',description:'add the branch')
    }
    stages{
        stage("clone the code"){
            steps{
                println "the code cloned"
                checkout([
                    $class:'GitSCM',
                    branches:[[name:'${BRANCH}']],
                    userRemoteConfigs:[[url:'https://github.com/Saraswathirg/boxfuse-sample-java-war-hello.git']]
                ])
            }
        }
        stage("build the code"){
            steps{
                println "the code built"
                sh "ls -lart ./*"
                sh "mvn clean package"
            }
        }
        stage("store to s3"){
            steps{
                println "artefact stored to s3"
                sh "aws s3 cp target/hello-${BUILD_NUMBER}.war s3://alltime/${BRANCH}/${BUILD_NUMBER}"
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
