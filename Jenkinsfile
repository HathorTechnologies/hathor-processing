#!/usr/bin/env groovy

pipeline {
    agent none
    environment {
        registryCredential = 'DockerHub'
        repoUrl = 'hathortechnologies/processing'
        dockerfilePath = './docker/Dockerfile'
    }
    stages {
        stage('init') {
            agent { label 'py'}
            steps {
                script {
                    def scmVars = checkout scm
                    env.MY_GIT_PREVIOUS_SUCCESSFUL_COMMIT = scmVars.GIT_PREVIOUS_SUCCESSFUL_COMMIT
                }
            }
        }
        // stage('PyTest') {
        //       agent { label 'py'}
        //       steps {
        //           slackSend (color: '#FFFF00', message: "STARTED: Job '${env.STAGE_NAME} ${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
        //           sh 'cd tests && python3 -m unittest discover -s main'
        //           slackSend (color: '#00FF00', message: "SUCCESS: Job '${env.STAGE_NAME} ${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")  
        //     }
        // }
        // stage('PyPi') {
        //       agent { label 'py'}
        //       steps {
        //           slackSend (color: '#FFFF00', message: "STARTED: Job '${env.STAGE_NAME} ${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
        //           sh 'python3 setup.py sdist bdist_wheel'
        //           sh 'twine upload dist/*'
        //           slackSend (color: '#00FF00', message: "SUCCESS: Job '${env.STAGE_NAME} ${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")  
        //     }
        // }
        stage('Docker') {
              agent { label 'py'}
              steps {
                  slackSend (color: '#FFFF00', message: "STARTED: Job '${env.STAGE_NAME} ${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
                  sh "nohup dockerd --host=unix:///var/run/docker.sock --host=tcp://127.0.0.1:2375 --storage-driver=overlay2&"
                  script {
                    dockerImage = docker.build(repoUrl + ":1.2.3.dev", "-f ${dockerfilePath} .")  
                    docker.withRegistry( '', registryCredential ) {
                        dockerImage.push()
                    }
                    slackSend (color: '#00FF00', message: "SUCCESS: Job '${env.STAGE_NAME} ${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")  
                }
            }
        }
    }
                post {
                  success {
                    slackSend (color: '#00FF00', message: "SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")  
                }
                  failure {
                    slackSend (color: '#FF0000', message: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})") 
                }
        }
}