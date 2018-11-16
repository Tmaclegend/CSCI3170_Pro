import java.util.*;
import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.Date;



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
        System.out.println("Please enter your ID.");

        //get the did
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNextInt()) {
            System.out.println("Wrong or Invalid input\n Please enter your ID");
        }
        int did = scanner.nextInt(); 

        //print the record
        System.out.println("Request ID, Passenger name, Passengers"); 
        String query = "Select r.id, p.name, r.passengers "
                + "FROM request r, passenger p "
                + "WHERE r.id = p.id AND r.taken = '0';"; 
        ResultSet rs = stmt.executeQuery(query);
        try {
            rs = stmt.executeQuery(query);
        } catch (SQLException ex) {
            System.out.println("Error in Show Request");
        }
        try {
            while(rs.next()){
                System.out.println(rs.getInt("id") + ", " + rs.getString("name") + ", " + rs.getInt("passengers"));
            }
        } catch (SQLException ex) {
            System.out.println("Error");
        }

        //ask for take which request
        System.out.println("Please enter the request ID.");
        int rid = scanner.nextInt();

        //search for the driver taking request or not 
        query = "Select t.id "
            + "FROM trip t "
            + "WHERE t.driver_id = '" + did + "' AND t.end IS NULL;";
        rs = stmt.executeQuery(query);

        try {
            rs = stmt.executeQuery(query);
        } catch (SQLException ex) {
            System.out.println("Error in Show Request");
        }

        try {
            if(rs.next()){
                System.out.println("Error, you are taking a trip"); 
            }
            else {
                //get the info of the rid
                query = "Select r.passenger_id, r.passengers "
                    + "FROM request r "
                    + "WHERE r.id = '" + rid + "';"; 

                rs = stmt.executeQuery(query);

                try {
                    rs = stmt.executeQuery(query);
                } catch (SQLException ex) {
                    System.out.println("Error in getting the request info");
                }

                int r_pid = rs.getInt("passenger_id");
                int noOfPassengers = rs.getInt("passengers");
                //make the request taken to 1
                query = "UPDATE request r "
                    + "SET  r.taken = '1' "
                    + "WHERE r.id = '" + rid + "';";
                
                rs = stmt.executeQuery(query);
                
                try {
                    rs = stmt.executeQuery(query);
                } catch (SQLException ex) {
                    System.out.println("Error in getting the request info");
                }

                //insert info to trip
                java.util.Date currentDate = Calendar.getInstance().getTime();
                query = "INSERT INTO trip (driver_id, passenger_id, passenger, start) "
                    + "VALUES ('" + did + "', '" + r_pid + "', '" + noOfPassengers + "', '" + currentDate + "');";
                rs = stmt.executeQuery(query);
            
                try {
                    rs = stmt.executeQuery(query);
                } catch (SQLException ex) {
                    System.out.println("Error in adding the trip info");
                }
                //print result
                System.out.println("Trip ID, Passngers name, Start");
                query = "SELECT t.id, p.name, t.start "
                    + "FROM trip t, passenger p "
                    + "WHERE p.id = t.passenger_id AND p.id = '" + r_pid + "' AND t.driver_id = '" + did + "' AND end IS NULL;";
                
                rs = stmt.executeQuery(query); 
                try {
                    rs = stmt.executeQuery(query);
                } catch (SQLException ex) {
                    System.out.println("Error in adding the trip info");
                }

                try {
                    System.out.println(rs.getInt("id") + ", " + rs.getString("name") + ", " + rs.getDate("start"));
                } catch (SQLException ex) {
                    System.out.println("Error");
                }
                //end

            }
        } catch (SQLException ex) {
            System.out.println("Error");
        }
    }

    public static void FinishTrip(Statement stmt) throws SQLException {

    }
    public static void checkDriverRating(Statement stmt) throws SQLException {
        System.out.println("Please enter your ID.");
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNextInt()) {
            System.out.println("Wrong or Invalid input\n Please enter your ID");
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
            System.out.println("Your driver rating is " + result);
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