# Level-3-Capstone-Project-II-Databases

THE TASK AT HAND
You are asked to create a food delivery system for a company called “Food Quick”.

Food Quick is the company that receives the orders and distributes them to a driver based on their current load and their location. They want you to create a program that can help them keep track of the orders and distribute accordingly.

Food Quick stores the following information for each customer:

● Order number

● Customer name

● Contact number of the customer

● Address of the customer

● Location (city) of the customer

● Email address of the customer

● Name of the restaurant

● Location of the restaurant

● Contact number of the restaurant

● How many of each meal is being ordered

● The list of meals being ordered and their prices

● Any special preparation instructions given by the customer

● The total amount to be paid

Food Quick would like you to be able to create an invoice for a customer after the above information has been inputted into the program. The invoice should be a text file with the following format:

Order number 1234

Customer: Jill Jack

Email: jilljack@yahoo.com

Phone number: 123 456 7890

Location: Cape Town

You have ordered the following from Aesop’s Pizza in Cape Town:

1 x Pepperoni pizza (R78.00)

2 x Hawaiian pizza (R82.00)

Special instructions: Extra tomato base on the Pepperoni pizza

Total: R242.00

John Krill is nearest to the restaurant and so he will be delivering your

order to you at:

12 Cherry Road

Plumstead

If you need to contact the restaurant, their number is 098 765 4321.

Task 1
Follow these steps:

● Design and create a database called QuickFoodMS. Assume that each customer can only be assigned to one driver. Each customer will also only be ordering from one restaurant.

Submit the following:

○ Dependency diagrams for each table in the database.

○ An Entity Relationship Diagram that shows the relationships between the tables in your database.

○ Screenshots of your console that show how each table was created.

● Add at least two rows of data to each table in the database. Submit screenshots of your console that show how data is added to the tables.

Task 2
Follow these steps:

● Copy and paste the code that you wrote for the last Capstone Project(Level 2) into the Dropbox folder for this Capstone Project.

● Modify your code so that it:

○ Reads and writes data about restaurants and customers associated with drivers from your database instead of text files. Your program should not use any text files.

○ Capture information about new customers and add these to the database.

○ Update information about existing customers (e.g. if they change their address).

○ Finalise existing orders. When an order is finalised the following should happen:

■ An invoice should be generated for the customer. This invoice should contain the customer’s contact details and the total amount that the customer must pay.

■ The project should be marked as “finalised” and the completion data should be added.

○ Find all orders that have missing information and still need to be completed from the database.

○ Find and select an entry by entering either the order number or customer name.

● Besides meeting the above criteria, you should also do the following:

○ Include exception handling. Use try-catch blocks wherever appropriate.

○ Document your code.

○ Use Javadoc to generate API documentation from documentation comments for your program.

○ Create a Readme file for this project.

