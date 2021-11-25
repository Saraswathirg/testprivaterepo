pipeline{
    agent any
    parameters{
        string(name:'BRANCH',defaultValue:'master',description:'add the branch')
        string(name:'BUILD_NUMBER',defaultValue:'',description:'add the bUILD NUM')
        string(name:'SERVER_IP',defaultValue:'',description:'add the serverip')
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
                sh "aws s3 cp target/hello-${BUILD_NUMBER}.war s3://alltime/${BRANCH}/${BUILD_NUMBER}/"
            }
        }
        stage("download to present location"){
            steps{
                println "the artefact downloaded"
                sh """
                aws s3 ls
                aws s3 cp s3://alltime/${BRANCH}/${BUILD_NUMBER}/hello-${BUILD_NUMBER}.war ."""
            }
        }
        stage("copy the artefact"){
            steps{
                println "the artefact copied"
                    sh """
                    inputArray=${SERVER_IP}
                    echo ${inputArray}
                    IFS=',' read -r -a outputArray <<< '${inputArray}'
                    for ip in ${outputArray[]}
                    do
                    echo $ip
                   scp -o StrictHostKeyChecking=no -i /tmp/awsaws.pem hello-${BUILD_NUMBER}.war ec2-user@$ip:/var/lib/tomcat/webapps/
                done
                """
                
        }
        }
    }
}
