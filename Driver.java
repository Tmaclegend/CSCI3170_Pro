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
    public static void TakeRequest(){

    }

    public static void FinishTrip(){

    }
    public static void checkDriverRating(){

    }
    public static void main(String[] args) throws Exception {
        
        String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/db1";
        String dbUsername = "Group1";
        String dbPassword = "csci3170group1";
        
        Connection con = null;
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
        printSystemAdmin();
        while (scanner.hasNextInt()) {
            int input = scanner.nextInt();
            if (input == 1) {
                createTables(stmt);
            } else if (input == 2) {
                deleteTables(stmt);
            } else if (input == 3) {
                loadData(stmt, con);
            } else if (input == 4) {
                checkData(stmt);
            } else if (input == 5) {
                continue;
            }
            printSystemAdmin();
        }
            
    }
}