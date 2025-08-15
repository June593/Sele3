pipeline {
    agent any

    parameters {
        choice(
            name: 'TEST_SUITE',
            choices: [
                '.\\src\\test\\resources\\suites\\allTestSuite.xml',
                '.\\src\\test\\resources\\suites\\vjTestSuite.xml',
                '.\\src\\test\\resources\\suites\\leapFrogContentTestSuite.xml',
                '.\\src\\test\\resources\\suites\\agodaTestSuite.xml'
            ],
            description: 'Select the test suite'
        )

        choice(
            name: 'BROWSER',
            choices: ['chrome', 'safari'],
            description: 'Select the browser'
        )
        choice(
            name: 'LANGUAGE',
            choices: ['en', 'vi'],
            description: 'Select the language')
        choice(
            name: 'RETRY_TYPE',
            choices: ['immediately'],
            description: 'Select the retry type')
        choice(
            name: 'RETRY_MAX',
            choices: ['0', '1', '2'],
            description: 'Select the retry max')
    }

    environment {
        TOTAL_TESTS = 'N/A'
        PASSED_TESTS = 'N/A'
        FAILED_TESTS = 'N/A'
        SKIPPED_TESTS = 'N/A'
        TEST_DETAILS_HTML = ''
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/June593/Sele3.git', branch: 'TestJenkins'
            }
        }

        stage('Generate Test Details HTML') {
            steps {
                script {
                    def suiteFile = readFile(env.TESTNG_SUITE)
                    def xml = new XmlSlurper().parseText(suiteFile)

                    def htmlContent = new StringBuilder("<ul>")
                    xml.test.each { testNode ->
                        htmlContent.append("<li><b>${testNode.@name}</b><ul>")
                        testNode.classes.class.each { classNode ->
                            htmlContent.append("<li>${classNode.@name}</li>")
                        }
                        htmlContent.append("</ul></li>")
                    }
                    htmlContent.append("</ul>")

                    env.TEST_DETAILS_HTML = htmlContent.toString()
                    echo "Generated TEST_DETAILS_HTML:\n${env.TEST_DETAILS_HTML}"
                }
            }
        }

        stage('Build and Test with Maven') {
            steps {
                script {
                    def testSuite = isUnix()
                        ? params.TEST_SUITE.replace("\\", "/")
                        : params.TEST_SUITE

                    if (isUnix()) {
                        sh '''
                            rm -rf allure-results
                            rm -rf target/surefire-reports
                        '''
                        sh """
                            mvn clean test \
                            -DsuiteXmlFile=${testSuite} \
                            -Dbrowser=${params.BROWSER} \
                            -Dlanguage=${params.LANGUAGE} \
                            -DretryType=${params.RETRY_TYPE} \
                            -DmaxRetry=${params.RETRY_MAX}
                        """
                    } else {
                        bat '''
                            if exist allure-results rmdir /s /q allure-results
                            if exist target\\surefire-reports rmdir /s /q target\\surefire-reports
                        '''
                        bat """
                            mvn clean test ^
                            -DsuiteXmlFile=${testSuite} ^
                            -Dbrowser=${params.BROWSER} ^
                            -Dlanguage=${params.LANGUAGE} ^
                            -DretryType=${params.RETRY_TYPE} ^
                            -DmaxRetry=${params.RETRY_MAX}
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                // Generate Allure report
                if (fileExists('allure-results')) {
                    try {
                        def allureCommand = 'allure generate allure-results --clean --single-file -o allure-report'
                        if (isUnix()) {
                            sh allureCommand
                        } else {
                            bat allureCommand
                        }
                    } catch (Exception e) {
                        echo "Allure report generation failed: ${e.message}"
                    }
                } else {
                    echo "⚠️ No Allure results found."
                }

                // Path to TestNG results
                def reportPath = isUnix()
                    ? 'target/surefire-reports/testng-results.xml'
                    : 'target\\surefire-reports\\testng-results.xml'

                if (fileExists(reportPath)) {
                    def content = readFile(reportPath)

                    TOTAL_TESTS   = content.find(/total="(\d+)"/)?.replaceAll(/total="|"/, "")   ?: 'N/A'
                    PASSED_TESTS  = content.find(/passed="(\d+)"/)?.replaceAll(/passed="|"/, "") ?: 'N/A'
                    FAILED_TESTS  = content.find(/failed="(\d+)"/)?.replaceAll(/failed="|"/, "") ?: 'N/A'
                    SKIPPED_TESTS = content.find(/skipped="(\d+)"/)?.replaceAll(/skipped="|"/, "") ?: 'N/A'

                    echo "Parsed values → total=${TOTAL_TESTS}, passed=${PASSED_TESTS}, failed=${FAILED_TESTS}, skipped=${SKIPPED_TESTS}"

                    // Parse per <test name> details
                    def testRows = ""
                    def matcher = content =~ /<test name="([^"]+)"[\s\S]*?<\/test>/
                    matcher.each { m ->
                        def testName = m[1]
                        def section = m[0]
                        def passed = (section =~ /status="PASS"/).count
                        def failed = (section =~ /status="FAIL"/).count
                        def skipped = (section =~ /status="SKIP"/).count
                        testRows += """
                            <tr>
                                <td>${testName}</td>
                                <td style="color:green;"><b>${passed}</b></td>
                                <td style="color:red;"><b>${failed}</b></td>
                                <td style="color:orange;"><b>${skipped}</b></td>
                            </tr>
                        """
                    }
                             } else {
                    echo "⚠️ testng-results.xml not found."
                }

                // Send email with report summary
   emailext(
    subject: "[Hieu Nguyen - Automation CI Report] ${env.JOB_NAME} | Build #${env.BUILD_NUMBER}",
    body: """<!DOCTYPE html>
<html>
<head>
    <style>
        body { font-family: Arial, sans-serif; background-color: #fafafa; padding: 20px; }
        h2 { color: #333; }
        table { border-collapse: collapse; width: 100%; background: #fff; margin-top: 10px; }
        th, td { padding: 8px; text-align: center; border-bottom: 1px solid #ddd; }
        th { background-color: #f4f6f8; }
        .pass { color: #28a745; font-weight: bold; }
        .fail { color: #dc3545; font-weight: bold; }
        .skip { color: #ff9800; font-weight: bold; }
    </style>
</head>
<body>
    <h2>📊 Automated Test Execution Summary</h2>
    <p><strong>Pipeline:</strong> ${env.JOB_NAME}</p>
    <p><strong>Build Number:</strong> ${env.BUILD_NUMBER}</p>
    <p><strong>Execution Time:</strong> ${new Date()}</p>

    <table>
        <tr>
            <th>Total Tests</th>
            <th>Passed</th>
            <th>Failed</th>
            <th>Skipped</th>
        </tr>
        <tr>
            <td>${TOTAL_TESTS}</td>
            <td class="pass">${PASSED_TESTS}</td>
            <td class="fail">${FAILED_TESTS}</td>
            <td class="skip">${SKIPPED_TESTS}</td>
        </tr>
    </table>

    <p>📂 For a detailed view of each step and screenshot evidence, please check the attached HTML report generated by Allure.</p>
</body>
</html>""",
    mimeType: 'text/html',
    attachLog: false,
    attachmentsPattern: 'allure-report/index.html',
    to: 'june5.gaming2@gmail.com'
)
            }
        }
    }
}
