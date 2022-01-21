//declarative pipeline
pipeline{
    agent any
    parameters{
        string(name:'BRANCH',defaultValue:'master',description:'enter the branch')
        string(name:'BUILDNUM',defaultValue:'',description:'enter the buildnum')
        string(name:'SERVERIP',defaultValue:'',description:'enter the serverip')

    }
    stages{
        stage("check the updates"){
            steps{
                println "the code is updated"
                sh "ls -l"
                checkout([
                    $class: 'GitSCM',
                    branches: [[name:'${BRANCH}']],
                    userRemoteConfigs:[[url:'https://github.com/Saraswathirg/testprivaterepo.git']]
                ])
            }
        }
        stage("build the code"){
            steps{
                println "the code is triggered"
                sh"""
                ls -lart ./*
                mvn clean package
                """
            }
        }
        stage("the artifact stored to s3"){
            steps{
                println "the artifact stored to s3"
                sh "aws s3 cp target/hello-${BUILDNUM}.war s3://alltime/${BRANCH}/${BUILDNUM}/"
            }

        }
        stage("copy from s3 to present location"){
            steps{
                println "the artifact stored"
                sh " aws s3 cp s3://alltime/${BRANCH}/${BUILDNUM}/hello-${BUILDNUM}.war ."
            }
        }
        stage("deployment to multiservers"){
            steps{
                println "the deployment"
                sh '''
                ls -l
                IFS=',' read -ra ADDR<<<"${SERVERIP}"
                for ip in \"${ADDR[@]}\":
                do 
                echo $ip
                echo "here we can use scp command"
                scp -o strictHostKeyChecking=no -i /tmp/awsaws.pem hello-${BUILDNUM}.war ec2-user@$ip:/var/lib/tomcat/webapps
                ssh -o strictHostKeyChecking=no -i /tmp/awsaws.pem ec2-user@$ip "hostname"
                done
                '''
            }
        }
        
        }
    }
