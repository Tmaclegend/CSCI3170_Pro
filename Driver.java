import java.util.*;
import java.io.*;
import java.sql.*;
import java.text.*;


public class Driver {
    public static void printDriver(){
        System.out.println("Driver, what would you like to do");
        System.out.println("1. Take a request");
        System.out.println("2. Finish a trip");
        System.out.println("3. Check driver rating");
        System.out.println("4. Go back");
        System.out.println("Please enter [1-4].");
    }
    public static void TakeRequest(Statement stmt) throws SQLException {

    }

    public static void FinishTrip(Statement stmt) throws SQLException {

    }
    public static void checkDriverRating(Statement stmt) throws SQLException {
        System.out.print("Please enter your ID.");
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNextInt()) {
            System.out.print("Wrong or Invalid input\n Please enter your ID");
        }
        int did = scanner.nextInt();
        //query
        String query = "Select t.rating "
                    + "FROM trip t, driver d "
                    + "WHERE d.id = t.driver_id AND t.driver_id = '" + did + "' AND  t.rating IS NOT NULL;";

        ResultSet rs = stmt.executeQuery(query);
        try {
            rs = stmt.executeQuery(query);
        } catch (SQLException ex) {
            System.out.println("Error in Show Driver Rating");
        }
        try {
            int rate = 0;
            int count = 0;
            while(rs.next()){
                rate += rs.getInt("rating");
                count++;
            }
            double result = rate / count;
            System.out.print("Your driver rating is " + result + "\n");
        } catch (SQLException ex) {
            System.out.println("Error");
        }
    }
    public static void main(String[] args) throws Exception {
        
        String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/db1";
        String dbUsername = "Group1";
        String dbPassword = "csci3170group1";
        
        Connection con = null;
        //connect the database
        try{
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(dbAddress, dbUsername, dbPassword);
        }catch (ClassNotFoundException e) {
            System.err.println("[Error]: Java MySQL DB Driver not found!");
            System.out.println(e);
            System.exit(0);
        }catch (SQLException e){
            System.out.println(e);
        }

        Statement stmt = con.createStatement();

        Scanner scanner = new Scanner(System.in);
        printDriver();
        while (scanner.hasNextInt()) {
            int input = scanner.nextInt();
            if (input == 1) {
                TakeRequest(stmt);
            } else if (input == 2) {
                FinishTrip(stmt);
            } else if (input == 3) {
                checkDriverRating(stmt);
            } else if (input == 4) {
                continue;
            }
            printDriver();
        }
            
    }
}