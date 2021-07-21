# Mobile Authentication Sample with PC
This is the sample to show how to implement authentication for mobile application with Airome's PayConfirm solution.

## Scenarios description
Before authentication process mobile application must be personalized.

So, this sample realizes 2 scenarios:
1. Mobile app personalization
2. Authentication of personalized mobile app

## How to launch
The sample consists of 2 parts:
1. Web-app that emulates your mobile application back-end
2. Mobile application

### Web App
Pre-requisites:
1. Java JDK 8 or later
2. Java IDE for Spring Boot framework (e.q. IntelliJ IDEA) 

Installation process:
1. open content of `web-app` directory to your Java IDE
2. the Java IDE will install any dependencies required for this project
3. fill values in `application.properties` regarding comments

Run the application:
1. click run on `PayconfirmDemoApplication.java` in `com.payconfirm.demo` package to build and run this project
2. the other option to run this project is to build this project into JAR file first. You can use `mvn clean package` (Command line or Maven GUI). Find the target JAR in `target` folder. In this sample code, the name is `payconfirmDemo-0.0.1-SNAPSHOT.jar`
3. to run the JAR file, copy the `payconfirmDemo-0.0.1-SNAPSHOT.jar` and `application.properties` files in same directory. Use `java -jar payconfirmDemo-0.0.1-SNAPSHOT.jar` command in that directory to run the application

### Mobile App
Pre-requisites:
1. Android Studio
2. Username and Password to PC repository (request from Airome / SafeTech)

Compilation process:
1. open the project with Android Studio
2. open `build.gradle` and replace in following block credentials with your username and password
```gradle
maven {
    url "https://repo.payconfirm.org/android/maven"
}
```
3. open `app/src/main/java/tech/paycon/mobile_auth_sample/Constants.java` and fill values regarding comments
4. build the app

## How to use
1. in your browser go to `<your server address>` (e.g. http://localhost:8080)
2. press `Create QR-code` or `Create Alias` button
3. if you have configured web-app correctly, you will see
    - User ID and QR-code from PC or
    - Alias and Activation Code
4. launch mobile app
5. choose `Personalization` option on the main screen (`with QR-code` or `with Alias`)
6. Follow instructions

After this step your mobile application is personalized

7. press `Authenticate` button on the main screen
8. you will see authentication process and result in the app's log

## Process description
1. Personalization is made by standard PC scenarios
    - with QR-code only (see [docs here](https://repo.payconfirm.org/server/doc/v5/arch_and_principles/#mobile-app-personalization-and-keys-generation))
    - with Exported JSON, passed via sample web-app - Automatically (see [docs here](https://repo.payconfirm.org/server/doc/v5/arch_and_principles/#mobile-app-personalization-and-keys-generation))

2. Authentication process
    - mobile-app -> backend/auth/start_authentication.php - send user id to be authenticated
    - backend/auth/start_authentication.php -> PC Server - create transaction (see [docs here](https://repo.payconfirm.org/server/doc/v5/rest-api/#create-transaction))
    - mobile-app -> PC Server - confirm (digitally sign) transaction (see [docs here](https://repo.payconfirm.org/android/doc/5.x/getting_started/#transaction-confirmation-and-declination))
    - PC Server -> backend/pc_callback_receiver.php - callback with event 'transaction confirmed' or error (see [docs here](https://repo.payconfirm.org/server/doc/v5/rest-api/#transactions-endpoint))
    - mobile-app -> backend/auth/finish_authentication.php - "what about my authentication?"
    - backend/auth/finish_authentication.php -> mobile-app - if PC transaction has been confirmed, then authorize (grant permissions)

---

## Notes
>The sample code on the backend side (web-app) is rewritten code based on [https://github.com/airome-tech/pc-mobile-auth-sample/tree/master/web-app](https://github.com/airome-tech/pc-mobile-auth-sample/tree/master/web-app). This code is written in Java programming language (Spring Boot framework), where the original code is written in PHP script

>The sample code on the mobile application side (android-app) is slighty modified from [https://github.com/airome-tech/pc-mobile-auth-sample/tree/master/android-app](https://github.com/airome-tech/pc-mobile-auth-sample/tree/master/android-app). Adjustments and changes were made to make the android-app able to communicate with the web-app

>This sample code also adds a feature to record logs of the PC Server Events Post parameters. Information from the Events Post can be used for business needs such as business analytics or for security purposes such as a Fraud Detection System (FDS). In this sample code will be saved in a file called `fds.log`


### Swagger documentation
For web-app documentation:
`http://localhost:8080/swagger-ui.html#`

### H2 Database Console
Accessing the H2 database:
`http://localhost:8080/h2-console`