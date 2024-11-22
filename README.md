# Linkedinium
Powerful tool to automate LinkedIn tasks

## Do you want to connect with people from a specific company?

```java
    class App {
        public static void main(String[] args) {
            linkedInDriver = new LinkedInDriver(); // Create a new instance of LinkedInDriver
            linkedInDriver.setCredentials(dataTeller.getEmail(), dataTeller.getPassword()); // Set your LinkedIn credentials in a data.yml file
            linkedInDriver.logIn();
            linkedInDriver.sendConnections("Google", 500); // Send 500 connection requests to people from Google
        }
    }
```