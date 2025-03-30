# HW3
# CSE360 Homework 3 – Automated Testing

## Overview

This project contains my submission for Homework 3, which demonstrates automated testing on our team’s TP2 codebase. The goal of this assignment was to write five automated test cases, generate internal documentation using Javadoc, and demonstrate test execution via screencast.


## Automated Tests

All tests are implemented in the file:

Each test was created as a separate method to ensure modularity and clear console output.

### Test Cases Implemented:

1. Ask a valid random question – Ensures a standard question is successfully saved.
2. Ask an empty question – Tests rejection of invalid (blank) input.
3. Clarify a question – Adds a clarification or reply to a previously asked question.
4. Mark answer as correct – Skipped due to a missing `isSelected` column in the database schema.
5. View all asked questions – Retrieves and displays all questions asked by the test user.



## Screencast Link

The screencast demonstrates test implementation, execution, and Javadoc documentation.

Link: 



## How to Run

1. Open the project in Eclipse.
2. Navigate to `PhaseTwoTesting.java`
3. Right-click the file and select Run As > Java Application
4. Observe the test outputs in the Console

Each test run generates a unique test user to avoid duplicate username conflicts in the database.


## Notes

- The "mark answer as correct" test case was skipped due to schema mismatch (missing column).
- All other test cases were implemented successfully and are covered in the screencast and PDF submission.


## Author

Name: Nandini Bahirgonde 
ASU Email: nbahirgo@asu.edu
Course: CSE360 – Spring 2025 Lynn Robert Carter 



