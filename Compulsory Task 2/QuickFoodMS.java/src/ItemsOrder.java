import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Scanner;

public class ItemsOrder {

    // Attributes
    int itemId;
    int orderNumber;
    String preparationInstructions;
    int itemQuantity;
    double item_cost;

    // Methods

    // The constructor for the ItemsOrder class. It is not used, but is included for the sake of completeness.
    public ItemsOrder(int itemId, int orderNumber, String preparationInstructions, int itemQuantity, double item_cost) {
        this.itemId = itemId;
        this.orderNumber = orderNumber;
        this.preparationInstructions = preparationInstructions;
        this.itemQuantity = itemQuantity;
        this.item_cost = item_cost;
    }

    // A utility method to add an item "line" to an order.
    public static void addItemToOrder(Connection connection, Scanner input, int orderNumber) throws SQLException {

        System.out.println("Please enter items details:");

        // User input of the variables needed to add an item to an order. The last variable is a calculated value.
        int itemId = UserInput.readInteger("Item ID: ", input);
        String preparationInstructions = UserInput.readString("Preparation Instructions: ", input);
        int itemQuantity = UserInput.readInteger("Item Quantity: ", input);
        double itemPrice = Item.findItemPrice(connection, itemId);
        double totalCost = itemPrice * itemQuantity;

        // The use of PreparedStatement to insert the variables into a MySQL statement
        String mySQLQueryAddItem = "INSERT INTO items_order VALUES (?, ?, ?, ?, ?);";
        PreparedStatement pstmtAddItem = connection.prepareStatement(mySQLQueryAddItem);
        pstmtAddItem.setInt(1, itemId);
        pstmtAddItem.setInt(2, orderNumber);
        pstmtAddItem.setString(3, preparationInstructions);
        pstmtAddItem.setInt(4, itemQuantity);
        pstmtAddItem.setDouble(5, totalCost);

        // A try/catch block to intercept cases where an unknown (non-existent) item ID is entered
        try {
            // The execution of the statement and setting the return equal to a variable. 
            // If this variable is > 0 then the execution was successful, 
            // so it is used here as a check and feedback to the user.
            int rowsAffected = pstmtAddItem.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("The item was added successfully.\n");
            } else {
                System.out.println("The item could not be added. Please review your input.");
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Please make sure that all information fields are only filled with information that " +
                    "exists in the database.\n");
        }

        // Closed this resource to prevent a resource leak
        pstmtAddItem.close();
    }
}