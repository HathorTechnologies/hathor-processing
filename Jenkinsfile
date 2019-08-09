#!/usr/bin/env groovy

pipeline {
    agent none
    stages {
        stage('init') {
            agent { label 'ecs'}
            steps {
                script {
                    gitCommitHash = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
                    shortCommitHash = gitCommitHash.take(7)
                    env.VERSION = shortCommitHash
                }
            }
        }
        stage('test') {
              agent { label 'ecs'}
              steps {
                  slackSend (color: '#FFFF00', message: "STARTED: Job '${env.STAGE_NAME} ${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
                  sh 'python3 tests/__main__.py'
              }
          }
        stage('PyPi') {
              agent { label 'ecs'}
              steps {
                  slackSend (color: '#FFFF00', message: "STARTED: Job '${env.STAGE_NAME} ${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
              }
          }
        stage('Docker') {
              agent { label 'ecs'}
              steps {
                  slackSend (color: '#FFFF00', message: "STARTED: Job '${env.STAGE_NAME} ${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
                  sh("eval \$(aws ecr get-login --no-include-email --region $REGION)")
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