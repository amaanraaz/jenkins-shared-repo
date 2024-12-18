def call() {
    pipeline {
        agent any
        stages {
            stage('Clone Repository') {
                steps {
                    echo "Cloning repository"
                    checkout([$class: 'GitSCM', 
                              branches: [[name: 'main']], 
                              userRemoteConfigs: [[url: 'https://github.com/amaanraaz/ansible-nginx.git']]])
                }
            }
            stage('User Approval') {
                steps {
                    script {
                        try {
                            input message: 'Do you approve to execute the playbook?'
                        } catch (Exception e) {
                            echo "User denied approval. Aborting pipeline."
                            currentBuild.result = 'ABORTED'
                            return // Exit the pipeline gracefully
                        }
                    }
                }
            }
            stage('Run Ansible Playbook') {
                steps {
                    echo "Running Ansible playbook: install_nginx.yml"
                    sh """
                    ansible-playbook nginx.yml -i inventoryFile
                    """
                }
            }
        }
        post {
            aborted {
                echo "Pipeline was aborted."
            }
            failure {
                echo "Pipeline failed."
            }
        }
    }
}
