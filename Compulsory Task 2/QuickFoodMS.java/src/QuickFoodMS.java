import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This is a project management system for a fictional food delivery company called Quick Food. 
 * The purpose of this system is to keep track of orders and allocate a driver to each order.
 *
 * @author Juaneva du Plessis
 * @version QuickFoodMS-1.0.0
 * @since 2022-07-28
 */

public class QuickFoodMS {


    // This is the main method
    public static void main(String[] args) {

	System.out.println("Welcome to my Quick Food Management System.\n");

	// Scanner created to keep input stream open
	Scanner scanner = new Scanner(System.in);

	// Opening the JDBC connection to the QuickFoodMS database
	String dbURL = "jdbc:mysql://localhost:3306/QuickFoodMS";
	String username = "otheruser";
	String password = "swordfish";

	// Using a try / catch block to open the Connection resource and catch any SQL Exceptions
	try (Connection connection = DriverManager.getConnection(dbURL, username, password)) {

	    // A while loop that will continue running until the user chooses to exit the program
	    while (true) {

		/* Main menu of the application. */
		String promptMenuChoice = """
				Main Menu (Enter the Number that matches your choice):
				--------------------------------------------------
				1. Create a new order
				2. Add items to an existing order
				3. Find and display an existing order
				4. List all the orders with incomplete information
				5. List all pending orders
				6. List all orders that are allocated to a specific driver
				7. Finalise your order
				8. Create a new customer
				9. Update an existing customer
				10. Create a new restaurant
				11. Update an existing restaurant
				12. Add a new menu item
				13. Edit an existing menu item
				14. Create a new driver
				15. Update an existing driver
				16. Exit
				""";


		// Reading user input of menu choice and an if statement to handle any invalid numbers entered
		int menuSelection = UserInput.readInteger(promptMenuChoice, scanner);
		if (menuSelection < 1 || menuSelection > 16) {
		    System.out.println("The number you have selected is invalid. Please try again.");
		    continue;
		}

		// A switch statement to handle the different menu choices and to assign them to specific methods
		// Reference: https://www.baeldung.com/java-switch
		switch (menuSelection) {
		case 1 -> captureNewOrder(connection, scanner);
		case 2 -> addItemsToExistingOrder(connection, scanner);
		case 3 -> findAndDisplayOrder(connection, scanner);
		case 4 -> listOrdersWithIncompleteInfo(connection);
		case 5 -> listPendingOrders(connection);
		case 6 -> ordersAllocatedToDriver(connection, scanner);
		case 7 -> finaliseOrder(connection, scanner);
		case 8 -> captureNewCustomer(connection, scanner);
		case 9 -> updateCustomer(connection, scanner);
		case 10 -> captureNewRestaurant(connection, scanner);
		case 11 -> updateRestaurant(connection, scanner);
		case 12 -> addNewItem(connection, scanner);
		case 13 -> editItem(connection, scanner);
		case 14 -> captureNewDriver(connection, scanner);
		case 15 -> editDriver(connection, scanner);
		case 16 -> {
		    System.out.println("Thank you for using my QuickFoodMS application. Goodbye.");
		    System.exit(0);
		}
		}

	    }

	} catch (SQLException e) {
	    e.printStackTrace();
	    System.out.println("SQL Exception thrown in Main.\n");
	}

	/* Closing this resource to prevent resource leaking. */
	scanner.close();
    }

    /**
     * A method to capture new orders. This method will check if a customer is already in the database and if not it
     * will divert the user to the <code>createNewCustomer</code> method in the Customer class.
     *
     * @param connection The Connection instance from the <code>main</code> method needed for
     *                   <code>PreparedStatement</code>.
     * @param input      The Scanner instance from <code>main</code> needed for the <code>readUserInput</code>
     *                   and <code>readUserInputInteger</code>  methods.
     * @throws SQLException If underlying MySQL service fails.
     */
    public static void captureNewOrder(Connection connection, Scanner input) throws SQLException {

	// Determine if this is an existing customer.
	String existingCustomer = UserInput.readString("Is this an existing customer? (Y/N)", input);

	// Collection of the first and surname here, because it is needed in both use cases.
	String customerFirstName = UserInput.readString("Customer First Name: ", input);
	String customerSurname = UserInput.readString("Customer Surname: ", input);

	// If an existing customer then the order can be placed, 
	// or else the customer must first be created and stored to the "customer" table.

	if (existingCustomer.equalsIgnoreCase("n")) {

	    // Adding a new customer to the customer table using Customer class createNewCustomer method.
	    Customer.createNewCustomer(connection, input, customerFirstName, customerSurname);
	}


	// Reading the restaurant name after creating the customer
	String restaurantName = UserInput.readString("Restaurant Name: ", input);

	// Creating an order with order-number, customer_id and restaurant_id columns being populated
	int orderNumber = Order.openOrder(connection, customerFirstName, customerSurname, restaurantName);

	// The "add an item to order" while loop that will continue until a user enters finished
	while (true) {

	    // Add items to the order menu.
	    String addItemsToOrderPrompt = """
	    		Add Items Menu
	    		--------------
	    		1. Add an item.
	    		2. Exit
	    		""";

	    // Reading the menu choice
	    int addItemsToOrder = UserInput.readInteger(addItemsToOrderPrompt, input);

	    // Handling invalid menu choices
	    if (addItemsToOrder < 1 || addItemsToOrder > 2) {
		System.out.println("Invalid option selected. Please try again.\n");
	    }

	    // Handling of valid menu choices
	    if (addItemsToOrder == 1) {
		ItemsOrder.addItemToOrder(connection, input, orderNumber);
	    } else if (addItemsToOrder == 2) {
		break;
	    }
	}

	// Update the orders table with the total cost and allocating a driver to the order
	Order.addInitialOrderDetails(connection, restaurantName, orderNumber);

	// Displaying the order details
	Order.displayOrder(connection, orderNumber);
    }

    /**
     * Method to add items to the itemsOrder table and then to update the order in the orders table. It makes use
     * of the returnOrderNumber and updateWithNewItems methods from the Order class as well as addItemToOrder from
     * the ItemsOrder class.
     *
     * @param connection The Connection resource from the <code>main</code> method needed as a parameter for the
     *                   invocation of the class methods.
     * @param input The Scanner instance from the <code>main</code> method needed for the two invocation of the
     *              UserInput methods that require the instance as arguments when invoked.
     * @throws SQLException If the underlying MySQL service fails.
     */
    public static void addItemsToExistingOrder(Connection connection, Scanner input) throws SQLException {

	// Obtaining the order number either directly or from customer names and restaurant names.
	int orderNumber = Order.returnOrderNumber(connection, input);

	/* Using two methods from the classes to first add the item to the items_order table and
	   then to update the orders cost column. Invalid or unknown order numbers will cause an
	   error message.*/
	ItemsOrder.addItemToOrder(connection, input, orderNumber);
	Order.updateWithNewItems(connection, orderNumber);
    }

    /**
     * This method locates and display the order information based on user input. It can locate the order by either
     * order number or customer names.
     *
     * @param connection The Connection resource from the <code>main</code> method needed for the connection
     *                   arguments of both method calls.
     * @param input The Scanner instance from the <code>main</code> method needed for user input by the
     *              <code>Order.returnOrderNumber</code> method call.
     * @throws SQLException If the underlying MySQL service fails.
     */
    public static void findAndDisplayOrder(Connection connection, Scanner input) throws SQLException {

	/* Getting the order number using returnOrderNumber method from the Order class. 
	   This method can get either the order number, or the customer and restaurant details if the order 
	   number is not known */
	int orderNumber = Order.returnOrderNumber(connection, input);

	// Show the order details once found. If there is more than one order that fits the search criteria
	// then show a list of the orders and let user choose which one
	Order.displayOrder(connection, orderNumber);
    }

    //A method to find and display orders and/or customers with incomplete information in the database.
    public static void listOrdersWithIncompleteInfo(Connection connection) throws SQLException {

	// The method returns an array list of all order numbers that have NULL data cells.
	// If the ArrayList length = 0, there are no incomplete orders.
	// Incomplete records are then listed by the use of a for loop and the "displayOrder" method in the Order class.
	// The check for incomplete customer info below works in the same way, so it will not be discussed.
	ArrayList<Integer> incompleteOrders = Order.checkForIncompleteOrders(connection);
	if (incompleteOrders.size() == 0) {
	    System.out.println("There are no incomplete orders.\n");
	} else {
	    System.out.println("Incomplete Orders: \n");
	    for (Integer order : incompleteOrders) {
		Order.displayOrder(connection, order);
	    }
	    System.out.println("\n");
	}


	ArrayList<Integer> incompleteCustomerInfo = Customer.checkForIncompleteCustomerInfo(connection);
	if (incompleteCustomerInfo.size() == 0) {
	    System.out.println("There are no customers with incomplete information.\n");
	} else {
	    System.out.println("Incomplete Customer Information: ");
	    for (Integer customer : incompleteCustomerInfo) {
		Customer.displayCustomer(connection, customer);
	    }
	}


    }

    /*This method finds and list pending orders based on the <code>finalised</code> field in the <code>orders</code>
      table. It uses two utility methods from the Order class to do so. "findPendingOrders" and "displayOrder".*/
    public static void listPendingOrders(Connection connection) throws SQLException{

	/* The method returns an ArrayList with all orders numbers of records in the "orders" table that have
	   finalised fields that are false(TINYINT = 0) */
	ArrayList<Integer> pendingOrders = Order.findPendingOrders(connection);

	/* If the ArrayList length is 0, there are no pending orders, else the pending orders are listed by using
	   an advanced for loop and the Order.displayOrder method */
	if (pendingOrders.size() == 0) {
	    System.out.println("There are no pending orders.\n");
	} else {
	    System.out.println("Pending orders: \n");
	    for (Integer order : pendingOrders) {
		Order.displayOrder(connection, order);
	    }
	}
    }

    // This method finds and lists all the orders associated with a specific driver.
    public static void ordersAllocatedToDriver(Connection connection, Scanner input) throws SQLException {

	// The obtaining of the driverId and name by use of the two utility methods in the Driver class.
	int driverId = Driver.findDriverId(connection, input);
	String driverName = Driver.findDriverName(connection, driverId);

	// This method returns an ArrayList with the order numbers of all the orders allocated to the specified driver.
	ArrayList<Integer> ordersAllocatedToDriver = Driver.findOrdersAllocatedToDriver(connection, driverId);

	/* If the ArrayList length is 0 then there are none allocated to that driver. If > 0 then the orders are
	   listed with an advanced for loop and the Order.displayOrder method. */
	if (ordersAllocatedToDriver.size() == 0) {
	    System.out.println("There are currently no orders allocated to " + driverName + "\n");
	} else {
	    System.out.println("The following orders are allocated to" + driverName + ": \n");
	    for (Integer order : ordersAllocatedToDriver) {
		Order.displayOrder(connection, order);
	    }
	}
    }

    // A method to update the status of an order to "finalised" when the order has been completed and paid for. 
    public static void finaliseOrder(Connection connection, Scanner input) throws SQLException {

	// User input of a orderNumber and then the finalisation of that order by the Order.makeFinal method
	int orderNumber = UserInput.readInteger("Order Number To Finalise: ", input);
	Order.makeFinal(connection, orderNumber);
    }

    // The capturing of new customers are handled by this method
    public static void captureNewCustomer(Connection connection, Scanner input) throws SQLException {

	// The method below will input the customer's first name and surname to capture the new customer.
	String customerFirstName = UserInput.readString("Customer First Name: ", input);
	String customerSurname = UserInput.readString("Customer Surname: ", input);

	// The creation of the customer is almost completely handled by the utility method below
	Customer.createNewCustomer(connection, input, customerFirstName, customerSurname);
    }

    // This method allows the user to update any of the fields in the customer table.
    public static void updateCustomer(Connection connection, Scanner input) throws SQLException {

	// This while loop continues running until the user selects 7 from the menu
	while (true) {

	    // The Update Menu will be used with a switch statement to update the chosen field.
	    String updateMenuPrompt = """
	    		Customer Update Menu
	    		--------------------
	    		(Please select the number of the field you wish to update.)
	    		1. First Name
	    		2. Surname
	    		3. Phone Number
	    		4. Address
	    		5. Location (City)
	    		6. Email
	    		7. Return to Main Menu
	    		""";

	    int updateMenuChoice = UserInput.readInteger(updateMenuPrompt, input);

	    // Handling of invalid menu choices
	    if (updateMenuChoice < 1 || updateMenuChoice > 7) {
		System.out.println("Invalid selection. Please try again.");
		continue;
	    }

	    // Exiting the while loop and return to the main menu if the user chooses option 7
	    if (updateMenuChoice == 7) {
		break;
	    }

	    // Input the variables needed as arguments when calling the Customer.performFieldUpdate method
	    int customerId = UserInput.readInteger("Customer ID: ", input);
	    String newValueOfField = UserInput.readString("Enter the new value: ", input);

	    // Switch statement to handle the different menu changes with the "fieldToUpdate" based on menu choice
	    switch (updateMenuChoice) {
	    case 1 -> Customer.performFieldUpdate(connection, "customer_firstname", newValueOfField, customerId);
	    case 2 -> Customer.performFieldUpdate(connection, "customer_surname", newValueOfField, customerId);
	    case 3 -> Customer.performFieldUpdate(connection, "customer_phone_num", newValueOfField, customerId);
	    case 4 -> Customer.performFieldUpdate(connection, "customer_address", newValueOfField, customerId);
	    case 5 -> Customer.performFieldUpdate(connection, "customer_city", newValueOfField, customerId);
	    case 6 -> Customer.performFieldUpdate(connection, "customer_email", newValueOfField, customerId);
	    }
	}
    }

    // This method captures a new restaurant through the createNewRestaurant method in the class Restaurant. 
    public static void captureNewRestaurant(Connection connection, Scanner input) throws SQLException {

	Restaurant.createNewRestaurant(connection, input);
    }

    //This method updates any details associated with a restaurant
    public static void updateRestaurant(Connection connection, Scanner input) throws SQLException {

	// This while loop will keep running until the user enters 4 to return to the main menu
	while (true) {

	    String updateMenuPrompt = """
	    		Restaurant Update Menu
	    		--------------------
	    		(Please choose the number of the field you wish to update.)
	    		1. Restaurant Name
	    		2. Restaurant Phone Number
	    		3. Restaurant City
	    		4. Return to Main Menu
	    		""";

	    int updateMenuChoice = UserInput.readInteger(updateMenuPrompt, input);

	    // The handling of invalid menu choices
	    if (updateMenuChoice < 1 || updateMenuChoice > 4) {
		System.out.println("Invalid selection. Please try again.");
		continue;
	    }

	    // The break out of the while loop and return to the main menu if the user selects 4
	    if (updateMenuChoice == 4) {
		break;
	    }

	    // User input of the variables needed for the update
	    int restaurantId = UserInput.readInteger("Restaurant ID: ", input);
	    String newValueOfField = UserInput.readString("Please enter the new value: ", input);

	    // A switch statement linked to the menu choices
	    switch (updateMenuChoice) {
	    case 1 -> Restaurant.performFieldUpdate(connection, "restaurant_name", newValueOfField, restaurantId);
	    case 2 -> Restaurant.performFieldUpdate(connection, "restaurant_phone_num", newValueOfField,
		    restaurantId);
	    case 3 -> Restaurant.performFieldUpdate(connection, "restaurant_city", newValueOfField, restaurantId);
	    }
	}
    }

    // A method to add new items to the menu
    public static void addNewItem(Connection connection, Scanner input) throws SQLException {

	// This method handles the adding of new items to the menu
	Item.createNewItem(connection, input);
    }

    // This method allows the user to edit any item on the menu.
    public static void editItem(Connection connection, Scanner input) throws SQLException {

	// A while loop to keep running until the user selects 3 to return to the main menu
	while (true) {

	    String updateMenuPrompt = """
	    		Edit Menu Item
	    		--------------------
	    		(Please select the number of the field you wish to update.)
	    		1. Item Name
	    		2. Item Price
	    		3. Return to Main Menu
	    		""";

	    int updateMenuChoice = UserInput.readInteger(updateMenuPrompt, input);

	    // Handling invalid menu choices
	    if (updateMenuChoice < 1 || updateMenuChoice > 3) {
		System.out.println("Invalid selection. Please try again.");
		continue;
	    }

	    // Breaking the while loop and returning to the main menu when the user selects 3
	    if (updateMenuChoice == 3) {
		break;
	    }

	    // Item ID, user input
	    int itemId = UserInput.readInteger("Item ID: ", input);

	    // The declaration and initialisation of the variables needed for the update
	    String newValueOfFieldString = "";
	    double newValueOfFieldDouble = 0.00;

	    // The switch statement linked to menu choices
	    switch (updateMenuChoice) {
	    case 1 -> {
		newValueOfFieldString = UserInput.readString("What is the new value: ", input);
		String typeOfData = "String";
		Item.performFieldUpdate(connection, "item_name", newValueOfFieldString,
			newValueOfFieldDouble, typeOfData, itemId);
	    }
	    case 2 -> {
		newValueOfFieldDouble = UserInput.readDouble("What is the new value? ", input);
		String typeOfData = "Double";
		Item.performFieldUpdate(connection, "item_price", newValueOfFieldString,
			newValueOfFieldDouble, typeOfData, itemId);
	    }
	    }
	}
    }

    // This method captures a new driver into the database
    public static void captureNewDriver(Connection connection, Scanner input) throws SQLException {

	// Capturing a new driver is handled by the Driver class to keep the code as modular as possible
	Driver.createDriver(connection, input);
    }

    // This method allows the user to edit any details of an existing driver
    public static void editDriver(Connection connection, Scanner input) throws SQLException {

	// While loop keeps running until the user selects 3 from the menu
	while (true) {

	    String updateMenuPrompt = """
	    		Edit Menu Item
	    		--------------------
	    		(Please indicate the number the field you wish to update)
	    		1. Driver Name
	    		2. Driver Location (City)
	    		3. Return to Main Menu
	    		""";

	    int updateMenuChoice = UserInput.readInteger(updateMenuPrompt, input);

	    // Handling invalid menu choices
	    if (updateMenuChoice < 1 || updateMenuChoice > 3) {
		System.out.println("Invalid selection. Please try again.");
		continue;
	    }

	    // Breaking the while loop and returning to the main menu if the user chooses 3 from the menu
	    if (updateMenuChoice == 3) {
		break;
	    }

	    // User input of the variables needed as arguments for the method called below
	    int driverId = UserInput.readInteger("Driver ID: ", input);
	    String newValueOfField = UserInput.readString("Enter the new value: ", input);

	    // Switch statement that is linked to the menu
	    switch (updateMenuChoice) {
	    case 1 -> Driver.performFieldUpdate(connection, "driver_name", newValueOfField, driverId);
	    case 2 -> Driver.performFieldUpdate(connection, "driver_city", newValueOfField, driverId);
	    }
	}
    }
}