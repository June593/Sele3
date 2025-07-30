# 🚀 Level-3 Selenium Automation Framework

A robust UI automation framework built using **Selenide & Selenium**, with support for **parallel execution**, **dynamic environment switching**, **Selenium Grid integration**, and **detailed reporting** via Allure.

---

## ✅ Project Highlights

### Implemented Features:
- [x] Core framework built on Selenide
- [ ] **Report**
  - [x] Allure Report
  - [ ] Report Portal
- [ ] **Retry failed testcases**
  - [x] Retry immediately after the testcase failed
  - [ ] Retry failed testcases after all testcase done
- [x] Parallel execution
- [x] Parallel/distributed testing
- [x] Cross browsers testing: Chrome, Safari
- [x] Remote execution via Selenium Grid
- [ ] Implement testcase
  - [x] Agoda - TC1
  - [x] Agoda - TC2
  - [x] Agoda - TC3
  - [x] Vietjet - TC1
  - [ ] Vietjet - TC2
- [x] Jenkins CI pipeline with scheduled runs and email reports
- 
### User Cases
- [x] Content testing
- [x] Multiple languages testing
- [x] Group tests by purposes: regression, smoke/sanity test
- [x] Source control practice: branch
- [x] Switch test environment: dev, stg (dev: agoda.com, stg: vj.com)
- [x] Wrap custom controls
- [ ] Data driven testing: test data is in Excel file
- [ ] Working with Shadow DOM
- [ ] Compare with another FW e.g. Playwright

---

## 📁 Project Structure

```bash
.
├── pom.xml
├── Jenkinsfile
├── README.md
├── src
│   ├── main/java
│   │   ├── data  
│   │   ├── common          # Shared constants
│   │   ├── testng          # TestListener
│   │   ├── page            # Page Object Models
│   │   └── utils           # Utility methods
│   └── test/java/sele3     # Testcases
│   └── test/resources
│       ├── languages       #  languages yaml file
│       ├── profiles        #  properties files
│       ├── suites          # TestNG XML files
│       └── selenide.properties #selenide config
```
## Pre-requites

- Install Java 21 or above
- Using maven latest
- Using Selenide 7.9.x
- Using TestNG latest

# 🏎️ Running Tests

### 1️⃣ Install Dependencies

- Get the project file then open your terminal from the project root folder and execute.

```
mvn clean install -DskipTests
```

### 2️⃣ Run Tests

```
mvn clean test \
  -Dselenide.browser=chrome \
  -Dselenide.headless=true \
  -Dselenide.timeout=20000 \
  -Dselenide.pageLoadStrategy=normal \
  -Dselenide.remote=http://localhost:4444 \ # only use this if you are running tests on Selenium Grid
  -Dselenide.baseUrl=https://www.agoda.com \
  -Dgroups=agoda \
  -Dparallel=methods \
  -DthreadCount=5 \
  -DmaxRetry=3 \
  -DretryStrategy=post-suite
  ```

| Parameter                     | Description                                                                  |
|-------------------------------|------------------------------------------------------------------------------|
| `-Dselenide.browser`          | Specifies the browser to use (`chrome`, `firefox`, `edge`, `safari`).        |
| `-Dselenide.headless`         | Enables headless mode (`true` or `false`) for browser execution.             |
| `-Dselenide.timeout`          | Sets the default timeout (in milliseconds) for element waits.                |
| `-Dselenide.pageLoadStrategy` | Controls how the browser waits for page loading (`normal`, `eager`, `none`). |
| `-Dselenide.remote`           | URL of the remote Selenium Grid server (only needed for remote execution).   |
| `-Dselenide.baseUrl`          | Base URL of the application under test.                                      |
| `-Dsurefire.suiteXmlFiles`    | Path to the TestNG XML suite file to execute.                                |
| `-Dgroups`                    | Specifies which test group(s) to run (e.g., `smoke`, `regression`).          |
| `-Dparallel`                  | Specifies parallel execution mode (`classes`, `methods`, or `tests`).        |
| `-DthreadCount`               | Number of threads to use when running tests in parallel.                     |
| `-DmaxRetry`                  | Maximum number of retry attempts for failed tests.                           |
| `-DretryStrategy`             | Retry strategy to apply (`immediate` or `post-suite`).                       |




If you want to run the case in Specify .xml file, give a reference at your .xml file path

In the Maven Apache Plugin to the POM.xml

```
<build>
<plugins>
    [...]
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>3.0.0-M7</version>
        <configuration>
          <suiteXmlFiles>
            <suiteXmlFile>testng.xml</suiteXmlFile>
          </suiteXmlFiles>
        </configuration>
      </plugin>
    [...]
</plugins>
</build>
```

Provide the Location of testNG.xml file
Example: `src/test/resources/suites/defaultSuite.xml`
```
 <profiles>
        <profile>
            <id>testSuite</id>
            <properties>
                <suiteFile>src/test/resources/suites/testSuite.xml</suiteFile>
            </properties>
        </profile>

        <profile>
            <id>testContent</id>
            <properties>
                <suiteFile>src/test/resources/suites/testContentSuite.xml</suiteFile>
            </properties>
        </profile>
    </profiles>
```
In command line, add this command

```mvn clean test -P testSuite```
```mvn clean test -P testContent```
```mvn clean test -DsuiteFile=src/test/resources/suites/testSuite.xml```



### Distributed testing

- First, install Selenium Grid according to this [link](https://www.selenium.dev/documentation/grid/getting_started/)
- Then config URL of hub into the configuration of browser as above.
- For examlple:
  ```
   "remote": "http://localhost:4444/wd/hub"
  ```

## Report

- Allure report is using in this framework

  ### Installation
  - You can refer to this link to install allure: [link](https://docs.qameta.io/allure/#_installing_a_commandline)
  ### Report generation
  - After running the test, the report is generated on the /allure-result directory
  - Use this command from project folder to generate report

```cmd
allure serve
```

### 3️⃣ View Allure Report

```sh    
  allure serve
```
