import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Restaurant {

    //Attributes
    String name;
    String phoneNumber;
    String city;

    // Global Variables
    public static int rowsAffected;

    //Methods

   // The constructor for the Restaurant class (It isn't used but is included for the sake of completeness.
    public Restaurant(String name, String phoneNumber, String city) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.city = city;
    }

    // This method handles the creation of new restaurants to add to the database.
    public static void createNewRestaurant(Connection connection, Scanner input) throws SQLException {

        System.out.println("""
                Capture New Restaurant
                ----------------------
                """);

        // User input of the details needed to register a new restaurant
        String restaurantName = UserInput.readString("Restaurant Name: ", input);
        String restaurantPhoneNumber = UserInput.readString("Restaurant Phone Number: ", input);
        String restaurantCity = UserInput.readString("Restaurant Location (City): ", input);

        // Use of PreparedStatement to set up a MySQL query statement
        String mySQLQueryCreateRestaurant = "INSERT INTO restaurant VALUES (?, ?, ?, ?)";
        PreparedStatement pstmtCreateRestaurant = connection.prepareStatement(mySQLQueryCreateRestaurant);
        pstmtCreateRestaurant.setString(1, null);
        pstmtCreateRestaurant.setString(2, restaurantName);
        pstmtCreateRestaurant.setString(3, restaurantPhoneNumber);
        pstmtCreateRestaurant.setString(4, restaurantCity);

        /* The execution of the statement and determination of whether the insertion was successful or not based on
           the return value of the method*/
        rowsAffected = pstmtCreateRestaurant.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("The restaurant was successfully created.\n");
        } else {
            System.out.println("The restaurant could not be created. Please review your input\n");
        }

        // Closing resource to prevent resource leaking
        pstmtCreateRestaurant.close();
    }

    // A method to find the restaurant's ID from the first name of the restaurant entered by the user. 
    public static int findRestaurantId(Connection connection, String restaurantName) throws SQLException {

        // Declaration and initialisation of this variable because it is needed outside the while loop
        int restaurantId = 0;

        /* Use of PreparedStatement to set up a MySQL query statement to find a restaurant ID from the restaurantName
           parameter of the method */
        String mySQLQueryRestaurantId =
                "SELECT restaurant_id  FROM restaurant  WHERE LOWER(restaurant_name) = LOWER(?);";
        PreparedStatement pstmtRestaurantId = connection.prepareStatement(mySQLQueryRestaurantId);
        pstmtRestaurantId.setString(1, restaurantName);

        // Execution of the query and saving the result in a ResultSet
        // The getter method of the ResultSet is used to obtain the restaurantId
        ResultSet resultsRestaurantId = pstmtRestaurantId.executeQuery();
        while (resultsRestaurantId.next()) {
            restaurantId = resultsRestaurantId.getInt("restaurant_id");
        }

        // Closing of these resources to prevent resource leaking
        pstmtRestaurantId.close();
        resultsRestaurantId.close();

        return restaurantId;

    }

    // This method finds the restaurant name from the restaurant id entered by the user
    public static String findRestaurantName(Connection connection, int restaurantId) throws SQLException {

        // Declaration and initialisation of this variable because it is needed outside the while loop
        String restaurantName = "";

        // Use of PreparedStatement to set up a MySQL query statement to find the restaurant name
        String mySQLQueryRestaurantName = "SELECT restaurant_name FROM restaurant WHERE restaurant_id = ?;";
        PreparedStatement pstmtRestaurantId = connection.prepareStatement(mySQLQueryRestaurantName);
        pstmtRestaurantId.setInt(1, restaurantId);

        // Execution of the statement and return from the method as a ResultSet. 
        // The getter method of the ResultSet is used to obtain the restaurantName variable.
        ResultSet resultsRestaurantId = pstmtRestaurantId.executeQuery();
        while (resultsRestaurantId.next()) {
            restaurantName = resultsRestaurantId.getString("restaurant_name");
        }

        // Closing of these resources to prevent resource leaking
        pstmtRestaurantId.close();
        resultsRestaurantId.close();

        return restaurantName;
    }

    // This method finds the restaurant location from the restaurant name
    public static String findRestaurantLocation(Connection connection, String restaurantName) throws SQLException{

        // Declaration and initialisation of this variable because it is needed outside the while loop
        String restaurantLocation = "";

        // Use of PreparedStatement to set up a MySQL query statement to find the restaurant city from the restaurantName
        String mySQLQueryRestaurantLocation = "SELECT restaurant_city FROM restaurant WHERE LOWER(restaurant_name) = LOWER(?); ";
        PreparedStatement pstmtRestaurantLocation = connection.prepareStatement(mySQLQueryRestaurantLocation);
        pstmtRestaurantLocation.setString(1, restaurantName);

        /* Execution of the statement with a returned ResultSet. The getter method of the ResultSet is used to obtain
           the restaurantLocation variable */
        ResultSet resultsRestaurantLocation = pstmtRestaurantLocation.executeQuery();
        while (resultsRestaurantLocation.next()) {
            restaurantLocation = resultsRestaurantLocation.getString("restaurant_city");
        }

        // Closing of resources to prevent resource leaking
        pstmtRestaurantLocation.close();
        resultsRestaurantLocation.close();

        return restaurantLocation;
    }

    // A method to find the restaurant's phone number from its unique id number. 
    public static String findRestaurantPhoneNumber(Connection connection, int restaurantId) throws SQLException{

        // Declaration and initialisation of this variable because it is needed outside the while loop. */
        String restaurantPhoneNumber = "";

        // Use of PreparedStatement to set up a query to find the restaurant's phone number using restaurantId 
        String mySQLQueryRestaurantPhone = "SELECT restaurant_phone_num FROM restaurant WHERE restaurant_id = ?;";
        PreparedStatement pstmtRestaurantPhone = connection.prepareStatement(mySQLQueryRestaurantPhone);
        pstmtRestaurantPhone.setInt(1, restaurantId);

        // Execution of statement with the return of a ResultSet. ResultSet is used to get restaurantPhoneNumber
        ResultSet resultsRestaurantPhone = pstmtRestaurantPhone.executeQuery();
        while (resultsRestaurantPhone.next()) {
            restaurantPhoneNumber = resultsRestaurantPhone.getString("restaurant_phone_num");
        }

        // Closing of resources to prevent resource leaking
        pstmtRestaurantPhone.close();
        resultsRestaurantPhone.close();

        return restaurantPhoneNumber;
    }

    // A generic method to update any field in the restaurant table except for the restaurant_id
    public static void performFieldUpdate(Connection connection, String fieldToUpdate, String newValue,
                                          int restaurantId) throws SQLException {

        // Use of PreparedStatement to set up a query to update any restaurant field
        String mySQLQueryFieldUpdate =
                "UPDATE restaurant SET " + fieldToUpdate  + " = '" + newValue + "' WHERE restaurant_id = ?;";
        PreparedStatement pstmtFieldUpdate = connection.prepareStatement(mySQLQueryFieldUpdate);
        pstmtFieldUpdate.setInt(1, restaurantId);

        // Execution and determination if the update was successful or not
        rowsAffected = pstmtFieldUpdate.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("The update was successful.\n");
        } else {
            System.out.println("The update could not be done. Please check that the customer ID is correct.\n");
        }

        // Closing of resource to prevent resource leaking
        pstmtFieldUpdate.close();

    }

}