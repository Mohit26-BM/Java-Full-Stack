pipeline {
    agent any

    tools {
        maven 'maven3'
        jdk 'JDK17'
    }

    environment {
        PATH = "C:\\Users\\mbmoh\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin;${env.PATH}"
        KUBECONFIG = 'C:\\Users\\mbmoh\\.kube\\config'
    }

    stages {
        stage('checkout') {
            steps {
                echo 'checkout the code from github'
                checkout scm
                echo '===== checkout finished ====='
            }
        }
        stage('build') {
            steps {
                echo 'build and test all five backend modules'
                dir('backend') {
                    bat 'mvn -B clean package'
                }
                echo '===== backend build finished, jars created ====='
            }
        }
        stage('docker-images') {
            steps {
                echo 'build the six docker images (5 services + frontend)'
                bat 'docker-compose build'
                bat 'docker images --filter "reference=bank-*"'
                echo '===== docker images built ====='
            }
        }
        stage('deploy') {
            steps {
                echo 'deploy to kubernetes'
                bat 'kubectl config use-context docker-desktop'
                bat 'kubectl apply -f k8s/'
                echo '===== manifests applied ====='
                // Eureka first: the other services register with it
                bat 'kubectl rollout restart deployment/eureka-server'
                bat 'kubectl rollout status deployment/eureka-server --timeout=180s'
                script {
                    def others = ['auth-service', 'customer-service', 'account-service', 'api-gateway', 'frontend']
                    others.each { bat "kubectl rollout restart deployment/${it}" }
                    others.each { bat "kubectl rollout status deployment/${it} --timeout=180s" }
                }
                echo '===== all deployments rolled out ====='
            }
        }
        stage('verify') {
            steps {
                bat 'kubectl get pods'
                bat 'kubectl get services'
                echo '===== pipeline finished: run the port-forward commands to open the app ====='
            }
        }
    }
}
