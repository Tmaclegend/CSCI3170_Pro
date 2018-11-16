import java.util.*;
import java.io.*;
import java.sql.*;
import java.text.*;

public class SystemAdmin {

    public static void printSystemAdmin() {
        System.out.println("Administrator, what would you like to do");
        System.out.println("1. Create tables");
        System.out.println("2. Delete tables");
        System.out.println("3. Load data");
        System.out.println("4. Check data");
        System.out.println("5. Go back");
        System.out.println("Please enter [1-5].");
    }

    public static void createTables(Statement stmt) throws SQLException {
        try {
            System.out.print("Processing...");

            stmt.executeUpdate("DROP TABLE IF EXISTS driver;");
            stmt.executeUpdate("DROP TABLE IF EXISTS vehicle;");
            stmt.executeUpdate("DROP TABLE IF EXISTS passenger;");
            stmt.executeUpdate("DROP TABLE IF EXISTS request;");
            stmt.executeUpdate("DROP TABLE IF EXISTS trip;");

            stmt.executeUpdate("CREATE TABLE driver("
                    + "id integer NOT NULL, "
                    + "name varchar(20),"
                    + "vehicle_id varchar(20),"

                    + "PRIMARY KEY (id));");

            stmt.executeUpdate("CREATE TABLE vehicle("
                    + "id varchar(20) NOT NULL, "
                    + "model varchar(50),"
                    + "model_year integer,"
                    + "seats integer,"

                    + "PRIMARY KEY (id));");

            stmt.executeUpdate("CREATE TABLE passenger("
                    + "id integer NOT NULL, "
                    + "name varchar(20),"

                    + "PRIMARY KEY (id));");

            
            stmt.executeUpdate("CREATE TABLE request("
                    + "id integer NOT NULL AUTO_INCREMENT, "
                    + "passenger_id integer,"
                    + "model_year integer,"
                    + "model varchar(50),"
                    + "passengers integer,"
                    + "taken integer,"

                    + "PRIMARY KEY (id));");

            stmt.executeUpdate("CREATE TABLE trip("
                    + "id integer NOT NULL, "
                    + "driver_id integer,"
                    + "passenger_id integer,"
                    + "start datetime,"
                    + "end datetime,"
                    + "fee integer,"
                    + "rating integer,"

                    + "PRIMARY KEY (id));");

            System.out.println("Done! Tables are created");
        } catch (SQLException e) {
            System.out.println("Create table failed");
            System.out.println(e);
        }
    }

    public static void deleteTables(Statement stmt) throws SQLException {
        try {
            System.out.print("Processing...");
            stmt.executeUpdate("DROP TABLE IF EXISTS driver;");
            stmt.executeUpdate("DROP TABLE IF EXISTS vehicle;");
            stmt.executeUpdate("DROP TABLE IF EXISTS passenger;");
            stmt.executeUpdate("DROP TABLE IF EXISTS request;");
            stmt.executeUpdate("DROP TABLE IF EXISTS trip;");
            System.out.println("Done! Tables are deleted!");
        } catch (SQLException e) {
            System.out.println("Detele table failed");
            System.out.println(e);
        }
    }

    public static void loadData(Statement stmt, Connection con) throws ParseException, IOException, SQLException {
        
        PreparedStatement driverStmt, vehicleStmt, passengerStmt, tripStmt;

        try {
            driverStmt = con.prepareStatement("insert into driver values(?,?,?)");
            vehicleStmt = con.prepareStatement("insert into vehicle values(?,?,?,?)");
            passengerStmt = con.prepareStatement("insert into passenger values(?,?)");
            tripStmt = con.prepareStatement("insert into trip values(?,?,?,STR_TO_DATE(?,'%Y-%m-%d %T'),STR_TO_DATE(?,'%Y-%m-%d %T'),?,?)");

            Scanner scanner = new Scanner(System.in);
            System.out.print("Please enter the folder path: ");
            String folderPath = scanner.nextLine();
            Scanner driverCSV, vehicleCSV, passengerCSV, tripCSV;
            try{
                driverCSV = new Scanner(new FileReader(folderPath+"/drivers.csv"));
                vehicleCSV = new Scanner(new FileReader(folderPath+"/vehicles.csv"));
                passengerCSV = new Scanner(new FileReader(folderPath+"/passengers.csv"));
                tripCSV = new Scanner(new FileReader(folderPath+"/trips.csv"));
            }catch (IOException e){
                System.out.println("Invalid folder path!");
                System.out.println(e);
                return;
            }

            System.out.print("Processing...");

            while (driverCSV.hasNextLine()) {
                String line[] = driverCSV.nextLine().split(",");
                int id = Integer.parseInt(line[0]);
                String name = line[1];
                String vehicle_id = line[2];

                driverStmt.setInt(1, id);
                driverStmt.setString(2, name);
                driverStmt.setString(3, vehicle_id);

                driverStmt.executeUpdate();
            }
            while (vehicleCSV.hasNextLine()) {
                String line[] = vehicleCSV.nextLine().split(",");
                String id = line[0];
                String model = line[1];
                int model_year = Integer.parseInt(line[2]);
                int seats = Integer.parseInt(line[3]);

                vehicleStmt.setString(1, id);
                vehicleStmt.setString(2, model);
                vehicleStmt.setInt(3, model_year);
                vehicleStmt.setInt(4, seats);

                vehicleStmt.executeUpdate();
            }
            while (passengerCSV.hasNextLine()) {
                String line[] = passengerCSV.nextLine().split(",");
                int id = Integer.parseInt(line[0]);
                String name = line[1];

                passengerStmt.setInt(1, id);
                passengerStmt.setString(2, name);

                passengerStmt.executeUpdate();
            }
            while (tripCSV.hasNextLine()) {
                String line[] = tripCSV.nextLine().split(",");
                int id = Integer.parseInt(line[0]);
                int driverId = Integer.parseInt(line[1]);
                int passengerId = Integer.parseInt(line[2]);
                String start = line[3];
                String end = line[4];
                int fee = Integer.parseInt(line[5]);
                int rating = Integer.parseInt(line[6]);

                tripStmt.setInt(1, id);
                tripStmt.setInt(2, driverId);
                tripStmt.setInt(3, passengerId);
                tripStmt.setString(4, start);
                tripStmt.setString(5, end);
                tripStmt.setInt(6, fee);
                tripStmt.setInt(7, rating);

                tripStmt.executeUpdate();
            }
            
            driverStmt.close();
            vehicleStmt.close();
            passengerStmt.close();
            tripStmt.close();
            System.out.println("Done! Data is loaded");
        } catch (SQLException e) {
            System.out.println("Load data failed");
            System.out.println(e);
        }

    }

    public static void checkData(Statement stmt) throws SQLException {
        try {
            String query = "";
            ResultSet resultSet = null;
            System.out.println("Number of records in each table:");

            System.out.print("driver: ");
            query = "SELECT COUNT(*) FROM driver;";
            resultSet = stmt.executeQuery(query);
            if (resultSet.next()) {
                System.out.println(resultSet.getInt(1));
            }

            System.out.print("vehicle: ");
            query = "SELECT COUNT(*) FROM vehicle;";
            resultSet = stmt.executeQuery(query);
            if (resultSet.next()) {
                System.out.println(resultSet.getInt(1));
            }

            System.out.print("passenger: ");
            query = "SELECT COUNT(*) FROM passenger;";
            resultSet = stmt.executeQuery(query);
            if (resultSet.next()) {
                System.out.println(resultSet.getInt(1));
            }

            System.out.print("request: ");
            query = "SELECT COUNT(*) FROM request;";
            resultSet = stmt.executeQuery(query);
            if (resultSet.next()) {
                System.out.println(resultSet.getInt(1));
            }

            System.out.print("trip: ");
            query = "SELECT COUNT(*) FROM trip;";
            resultSet = stmt.executeQuery(query);
            if (resultSet.next()) {
                System.out.println(resultSet.getInt(1));
            }

        } catch (SQLException e) {
            System.out.println("Show data failed");
            System.out.println(e);
        }
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
