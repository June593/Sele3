# Automated testing web application by using Java, Selenide

This is an example of using Selenide test framework.

## Pre-requites

- Install Java 21 or above
- Using maven latest
- Using Selenide 7.9.x
- Using TestNG latest

## Installation

- CD to the project folder
- Open CMD/terminal then type

```cmd
mvn clean install
```

## Execute

- You can run test suite by this command line.

### Run by Maven Command Line
Open your Terminal and run these commands for checking Java version.

```
javac -version
```


Open the cmd and go to your project contains POM file `\HieuNguyen_8298_Sele2\automated-test>\`
. Add this command to run all of testcases
```mvn clean test```

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
 <configuration>
    <suiteXmlFiles>
        <suiteXmlFile>${suiteFile}</suiteXmlFile>
    </suiteXmlFiles>
 </configuration>
```
In command line, add this command

```mvn clean test -DtestngFile=${suiteFile}.xml```

                                           Browser start with maximize or not |

### Parallel testing

- Tests, classes, methods can be run in parallel by adding parallel attribute on xml suite
- For example:
  ```
  <suite name="My suite" parallel="classes" thread-count="2">
  ```

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

## License

[MIT](https://choosealicense.com/licenses/mit/)