pipeline {
    agent any

    tools {
        maven 'maven'
        jdk 'jdk-21'
        allure 'allure'
    }

    environment {
        ALLURE_RESULTS = 'allure-results'
        ALLURE_REPORT = 'allure-report'
    }

    triggers {
            cron('0 8 * * *')
        }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/June593/Sele3.git', branch: 'main'
            }
        }

        stage('Clean & Test') {
            steps {
                sh 'mvn clean test'
            }
        }

        stage('JUnit Summary') {
            steps {
                junit 'target/surefire-reports/*.xml'
            }
        }

        stage('Allure Report') {
            steps {
                allure includeProperties: false, jdk: '', results: [[path: "${ALLURE_RESULTS}"]]
            }
        }

        stage('Generate + Zip Allure Report') {
            steps {
                sh "allure generate ${ALLURE_RESULTS} -o ${ALLURE_REPORT} --clean --single-file"
            }
        }
    }

    post {
        always {
            script {

                def testResult = junit 'target/surefire-reports/*.xml'
                def totalTests = testResult.totalCount
                def passedTests = testResult.passCount
                def failedTests = testResult.failCount

                emailext(
                    subject: "🔔 ${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - ${currentBuild.currentResult}",
                    to: 'june5.gaming2@gmail.com', 'thuong.dang@agest.vn'
                    attachmentsPattern: "${ALLURE_REPORT}/index.html",
                    body: """
                        <p><b>Project:</b> ${env.JOB_NAME}</p>
                        <p><b>Status:</b> ${currentBuild.currentResult}</p>
                        <p><b>Build Number:</b> #${env.BUILD_NUMBER}</p>
                        <p><b>Details:</b> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                        <p><b>🧪 Test Summary:</b></p>
                        <ul>
                          <li>Total Test Cases: <b>${totalTests}</b></li>
                          <li>Passed: <span style="color:green"><b>${passedTests}</b></span></li>
                          <li>Failed: <span style="color:red"><b>${failedTests}</b></span></li>
                        </ul>

                    """,
                    mimeType: 'text/html'
                )
            }
        }
    }
}
