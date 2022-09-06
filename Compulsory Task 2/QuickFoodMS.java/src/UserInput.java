import java.util.Scanner;

public class UserInput {

    // A method used to get input from the user in String format
    public static String readString(String prompt, Scanner input){

	// Presenting a prompt to the user to indicate the nature of input required
	System.out.println(prompt);

	// Return of the read line using the Scanner instance
	return input.nextLine();
    }

    // A generic method used to get integer input from the user
    public static int readInteger(String prompt, Scanner input) {

	// Declaration of the return variable outside the while loop so that it can be accessed in the return statement.
	int userIntegerInput;

	// A while loop that will continue running until an integer is entered as input
	while (true) {

	    System.out.println(prompt);

	    // A try/catch block to handle exceptions where the user enters non-numeric values
	    try {
		userIntegerInput = Integer.parseInt(input.nextLine());
		break;
	    } catch (NumberFormatException e) {
		System.out.println("You entered non-numeric input. Please only enter numeric input as indicated.");
	    }
	}

	return userIntegerInput;
    }

    // A generic method used to get double input from the user
    public static double readDouble(String prompt, Scanner input) {

	//Declaration of the return variable outside the while loop so that it can be accessed in the return statement.
	double userDoubleInput;

	// A while loop that will continue running until a double is entered as input
	while (true) {

	    System.out.println(prompt);

	    // A try/catch block to handle exceptions where the user enters decimal values
	    try {
		userDoubleInput = Double.parseDouble(input.nextLine());
		break;
	    } catch (NumberFormatException e) {
		System.out.println("You entered non-decimal input. Please only enter decimal input as indicated.");
	    }
	}

	return userDoubleInput;
    }
}