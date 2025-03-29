package application;

/*******
 * <p> Title: PasswordEvaluationTestingAutomation Class. </p>
 * 
 * <p> Description: A Java demonstration for semi-automated tests using the 
 * PasswordEvaluator class. This program evaluates a list of passwords 
 * against specific strength rules using a finite state machine (FSM). </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2022 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 2.01 2025-03-29 Updated to include 5 documented test cases for HW3 submission
 */
public class PasswordEvaluationTestingAutomation {
	
	static int numPassed = 0;	// Counter of the number of passed tests
	static int numFailed = 0;	// Counter of the number of failed tests

	/**
	 * This is the main method that initiates test execution. It prints a report header,
	 * runs all the test cases, and prints the test result summary.
	 *
	 * @param args Command-line arguments (unused)
	 */
	public static void main(String[] args) {
		/************** Test cases semi-automation report header **************/
		System.out.println("______________________________________");
		System.out.println("\nTesting Automation");

		/************** Start of the test cases **************/

		// Provided test cases
		performTestCase(1, "Aa!15678", true);
		performTestCase(2, "A!", false);
		performTestCase(3, "Aa!15678", false);
		performTestCase(4, "A!", true);
		performTestCase(5, "", true);

		// HW3 - 5 individual test cases
		performTestCase(6, "Aa1!aaaa", true);     // ✅ All criteria met
		performTestCase(7, "aaaaaaa!", false);    // ❌ Missing uppercase and digit
		performTestCase(8, "AA1!AAAA", false);    // ❌ Missing lowercase
		performTestCase(9, "Aa!1", false);        // ❌ Too short
		performTestCase(10, "Aa1!@@@@", true);    // ✅ Valid, tests multiple special chars

		/************** End of the test cases **************/

		/************** Test cases semi-automation report footer **************/
		System.out.println("____________________________________________________________________________");
		System.out.println();
		System.out.println("Number of tests passed: "+ numPassed);
		System.out.println("Number of tests failed: "+ numFailed);
	}

	/**
	 * Executes a single test case by passing the input password to the evaluator,
	 * comparing the actual result with the expected outcome, and logging results.
	 *
	 * @param testCase Number label for the test case
	 * @param inputText The password to test
	 * @param expectedPass true if password is expected to pass, false if expected to fail
	 */
	private static void performTestCase(int testCase, String inputText, boolean expectedPass) {
		System.out.println("____________________________________________________________________________\n\nTest case: " + testCase);
		System.out.println("Input: \"" + inputText + "\"");
		System.out.println("______________");
		System.out.println("\nFinite state machine execution trace:");

		String resultText = PasswordEvaluator.evaluatePassword(inputText);
		System.out.println();

		// If resultText is not empty, it's an error
		if (!resultText.equals("")) {
			if (expectedPass) {
				System.out.println("***Failure*** The password <" + inputText + "> is invalid." + 
						"\nBut it was supposed to be valid, so this is a failure!\n");
				System.out.println("Error message: " + resultText);
				numFailed++;
			} else {
				System.out.println("***Success*** The password <" + inputText + "> is invalid." + 
						"\nBut it was supposed to be invalid, so this is a pass!\n");
				System.out.println("Error message: " + resultText);
				numPassed++;
			}
		} else {
			if (expectedPass) {
				System.out.println("***Success*** The password <" + inputText + 
						"> is valid, so this is a pass!");
				numPassed++;
			} else {
				System.out.println("***Failure*** The password <" + inputText + 
						"> was judged as valid" + 
						"\nBut it was supposed to be invalid, so this is a failure!");
				numFailed++;
			}
		}
		displayEvaluation();
	}

	/**
	 * Displays the evaluation results for individual password criteria after each test.
	 */
	private static void displayEvaluation() {
		if (PasswordEvaluator.foundUpperCase)
			System.out.println("At least one upper case letter - Satisfied");
		else
			System.out.println("At least one upper case letter - Not Satisfied");

		if (PasswordEvaluator.foundLowerCase)
			System.out.println("At least one lower case letter - Satisfied");
		else
			System.out.println("At least one lower case letter - Not Satisfied");

		if (PasswordEvaluator.foundNumericDigit)
			System.out.println("At least one digit - Satisfied");
		else
			System.out.println("At least one digit - Not Satisfied");

		if (PasswordEvaluator.foundSpecialChar)
			System.out.println("At least one special character - Satisfied");
		else
			System.out.println("At least one special character - Not Satisfied");

		if (PasswordEvaluator.foundLongEnough)
			System.out.println("At least 8 characters - Satisfied");
		else
			System.out.println("At least 8 characters - Not Satisfied");
	}
}
