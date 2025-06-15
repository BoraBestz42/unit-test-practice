--ลองฝึกทำตามข้อสอบนี้ครับ--

# Exam Overview





You are working for a software development company and have been assigned a task to improve a legacy service. This service is responsible for retrieving bank information based on a bank account number. The test code has already been written, so your goal is to refactor, redesign, and/or rewrite the service code to support the existing tests.


 -  Test Class: **InquiryServiceTest.java**
 -  Service Class: **InquiryService.java**


## Instructions


- Refactor the **InquiryService.java** class to improve code quality.
- Ensure that the code follows good coding practices.
- Write unit tests for all classes you modify or create.


## How to Check Unit Test

To make sure all test cases pass.

```


    mvn clean test


```



## Rules

1. You are allowed to modify the existing source code, add new classes, or redesign the solution.
2. You should apply clean code techniques and appropriate design patterns.
3. All tests must pass.
4. Any new classes must have adequate test coverage.

## Submission Code
You have two options for submitting your code:

1. Compress your code into a zip file and attach it to the interview invitation email.
2. Fork the project into your own source code repository and send the repository link in response to the interview invitation email.


## Data Dictionary for Bank Response Code


| Code        | Description           |  
| ------------- |-------------|  
| approved      | approved | 
| invalid_data      | 100:1091:Data type is invalid.      |  
| invalid_data | General error.     |
| transaction_error |      |
| transaction_error | Transaction error.     |
| transaction_error | 100:1091:Transaction is error with code 1091.    |
| transaction_error | 1092:Transaction is error with code 1092.    |
| transaction_error | 98:Transaction is error with code 98.    |
| unknown |    |
| unknown | 5001:Unknown error code 5001   |
| unknown | 5002:   |
| unknown | General Invalid Data code 501   |
| not_support |     Not Support |



## Require Skills
- Java Programming
- Spring Boot
- JUnit
- Unit Testing
- Refactoring Code
