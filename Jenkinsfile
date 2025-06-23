pipeline {
    agent any

    tools {
        maven 'maven'          // Trùng tên với tool đã cấu hình trong Jenkins
        jdk 'jdk-21'
        allure 'allure'
    }

    environment {
        ALLURE_RESULTS = 'allure-results'
        ALLURE_REPORT = 'allure-report'
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/June593/Sele3.git', branch: 'TestJenkin'
            }
        }

        stage('Clean & Test') {
            steps {
                sh 'mvn clean test'
            }
        }

        stage('JUnit Summary') {
            steps {
                // Publish test result để Jenkins hiểu được pass/fail
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
                // Trích xuất số lượng test case từ kết quả JUnit
                def testResult = junit 'target/surefire-reports/*.xml'
                def totalTests = testResult.totalCount
                def passedTests = testResult.passCount
                def failedTests = testResult.failCount

                emailext(
                    subject: "🔔 ${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - ${currentBuild.currentResult}",
                    to: 'june5.gaming2@gmail.com',
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
