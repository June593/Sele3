pipeline {
    agent any

    environment {
        MAVEN_HOME = tool(name: 'Maven 3.9.10')
        JAVA_HOME = tool(name: 'JDK 21')
        PATH = "${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${env.PATH}"
        ALLURE_RESULTS = 'allure-results'
        ALLURE_REPORT = 'allure-report'
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/June593/Sele3.git', branch: 'config-selenide-framework'
            }
        }

        stage('Clean & Test') {
            steps {
                sh 'mvn clean test'
            }
        }

        stage('Generate Allure Report') {
            steps {
                sh "allure generate ${ALLURE_RESULTS} -o ${ALLURE_REPORT} --clean"
            }
        }

        stage('Archive Report') {
            steps {
                archiveArtifacts artifacts: "${ALLURE_REPORT}/**", allowEmptyArchive: true
            }
        }
    }

    post {
        always {
            emailext(
                subject: "🔔 ${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - ${currentBuild.currentResult}",
                to: 'june5.gaming2@gmail.com',
                body: """
                <p><b>Project:</b> ${env.JOB_NAME}</p>
                <p><b>Status:</b> ${currentBuild.currentResult}</p>
                <p><b>Build Number:</b> #${env.BUILD_NUMBER}</p>
                <p><b>Details:</b> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                """,
                mimeType: 'text/html'
            )
        }
    }
}
