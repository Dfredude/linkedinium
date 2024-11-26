# Linkedinium
Powerful tool to automate LinkedIn tasks

## Getting Started

### Requirements
- Java
- Selenium
- Chrome Browser

### data.yml
The purpose of this file is to add login credentials in it, and other metadata.
The file should be located at `src/main/resources/data.yml`
#### Either a cookie or credentials are required.
> **Required**
```yaml
credentials:
  email: <YOUR_EMAIL>
  password: <YOUR_PASSWORD>

cookie: <YOUR COOKIE>
```
#### Job Searching
You may specify:
- Unwanted companies
- Unwanted titles
- Answers to common job application questions
```yaml
unwanted_companies:
  - "Games"
  - "CPP Investments"
  -  "ACME"

unwanted_title_keywords:
  - Catering
  - .NET Lead
  - Sales
  - Senior Oracle

questions:
  How many years of work experience do you have with T-SQL Stored Procedures? : 2
  How many years of work experience do you have with Mobile Application Programming Languages? : 2
  How many years of work experience do you have with DevOps? : 2
  How many years of work experience do you have with .NET in Azure? : 2
```

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
