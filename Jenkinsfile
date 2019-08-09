#!/usr/bin/env groovy

pipeline {
    agent none
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
        stage('PyTest') {
              agent { label 'py'}
              steps {
                  slackSend (color: '#FFFF00', message: "STARTED: Job '${env.STAGE_NAME} ${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
                  sh 'cd tests && python3 -m unittest discover -s main'
              }
          }
        stage('PyPi') {
              agent { label 'py'}
              steps {
                  slackSend (color: '#FFFF00', message: "STARTED: Job '${env.STAGE_NAME} ${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
                  sh 'python3 setup.py sdist bdist_wheel'
                  sh 'twine -u $PYUSER -p $PYPASS'
                //    upload dist/*'
              }
          }
        stage('Docker') {
              agent { label 'py'}
              steps {
                  slackSend (color: '#FFFF00', message: "STARTED: Job '${env.STAGE_NAME} ${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
                  sh "docker login"
                //   sh "docker build --tag hathortechnologies/processing:1.2.0.dev --tag hathortechnologies/processing:dev ."
                //   sh "docker push hathortechnologies/processing:1.2.0.dev"
                //   sh "docker push hathortechnologies/processing:dev"
              }
          }
    }
                post {
                  success {
                    slackSend (color: '#00FF00', message: "SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")  }
                  failure {
                    slackSend (color: '#FF0000', message: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")  }
                }
}