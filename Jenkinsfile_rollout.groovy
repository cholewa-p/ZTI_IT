pipeline {
    agent {
        label "zti-slave2"
    }
    parameters {
        string(name: 'VERSION', defaultValue: 'latest', description: 'Version of the application')
        string(name: 'DEPLOYMENT', defaultValue: 'tetris-web-app')
        string(name: 'APP', defaultValue: 'tetris')
    }
    stages {
        stage('Deploy'){
            steps{
                script{
                    sh"""
                    kubectl set image deployment/${DEPLOYMENT} ${DEPLOYMENT}=cholewap/${APP}:${VERSION}
                    """
            }
            }
            post {
                success {
                    echo 'Production deployment successful!'
                }
                failure {
                    echo 'Production deployment unsuccessful'
                }
            }
        
    }
    }
}