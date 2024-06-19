def releasePrefix = "1."

pipeline {
    agent {
        label "zti-slave1"
    }
    environment{
        EMAIL_TO = 'pawel.cholewa@o2.pl'
        DOCKER_IMAGE = "cholewap/tetris"
        DOCKER_TAG = "$releasePrefix${currentBuild.number}"
        VERSION = "latest"
        ARTIFACT_NAME = "tetris_artifacts_${VERSION}"    
    }

    stages {
        // stage('Checkout') {
        //     steps {
        //         checkout([$class: 'GitSCM', 
        //             branches: [[name: '*/master']], 
        //             userRemoteConfigs: [[url: 'https://github.com/cholewa-p/react-tetris']]])
        //     }
        // }
        stage('Build') {
            steps {
                echo "$DOCKER_TAG"
                //docker inspect <name> | jq '.[] | .RepoTags[0]' | cut -d ":" -f 2
                withCredentials([usernamePassword(credentialsId: 'dockerhub_credentials', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                    withCredentials([usernamePassword(credentialsId: 'github_credentials', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
                        sh """
                            docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
                            docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} --build-arg GH_USER=${GIT_USERNAME} --build-arg GH_TOKEN=${GIT_PASSWORD} --no-cache=true --pull .
                        """
                    }
                }
            }
            post {
                success {
                    echo 'Build succesful'
                }
                failure {
                    echo 'Build failed!'
                }
            }
        }
        stage('Push to artifactory'){
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub_credentials', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                    sh"""
                    docker login -u $USERNAME -p $PASSWORD
                    docker push ${DOCKER_IMAGE}:${DOCKER_TAG}
                    """
                }
            }
        }
        stage('Deploy'){
            steps{
                script{
                    withCredentials([usernamePassword(credentialsId: 'dockerhub_credentials', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                    sh"""
                    #!/bin/bash
                    docker login -u $USERNAME -p $PASSWORD
                    """
                // try {
                    sh """                    
                    docker ps -aq | xargs docker stop | xargs docker rm
                    docker container prune --force
                    docker image prune --force --all
                    """
                // }
                // catch (Exception e) {
                //     echo "Error: ${e}"
                // }
                sh """
                docker run -d -p 8080:8080 --name tetris_app ${DOCKER_IMAGE}:${DOCKER_TAG}
                docker ps
                """
                }
            }
            post {
                success {
                    echo 'Deployment successful!'
                }
                failure {
                    echo 'Deployment unsuccessful'
                }
            }
        }
    }
    }
}