import java.util.*;
import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.Date;

public class RideSharingSystem {

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

            stmt.executeUpdate("DROP TABLE IF EXISTS request;");
            stmt.executeUpdate("DROP TABLE IF EXISTS trip;");
            stmt.executeUpdate("DROP TABLE IF EXISTS driver;");
            stmt.executeUpdate("DROP TABLE IF EXISTS vehicle;");
            stmt.executeUpdate("DROP TABLE IF EXISTS passenger;");

            stmt.executeUpdate("CREATE TABLE driver( "
                    + "id integer NOT NULL, "
                    + "name varchar(20), "
                    + "vehicle_id varchar(20), "

                    + "PRIMARY KEY (id));");

            stmt.executeUpdate("CREATE TABLE vehicle( "
                    + "id varchar(20) NOT NULL, "
                    + "model varchar(50), "
                    + "model_year integer, "
                    + "seats integer, "

                    + "PRIMARY KEY (id)); ");

            stmt.executeUpdate("CREATE TABLE passenger( "
                    + "id integer NOT NULL, "
                    + "name varchar(20), "

                    + "PRIMARY KEY (id)); ");

            
            stmt.executeUpdate("CREATE TABLE request( "
                    + "id integer NOT NULL AUTO_INCREMENT, "
                    + "passenger_id integer, "
                    + "model_year integer, "
                    + "model varchar(50), "
                    + "passengers integer, "
                    + "taken integer, "

                    + "PRIMARY KEY (id), "
                    + "FOREIGN KEY (passenger_id) REFERENCES passenger(id) "
                    +"); ");

            stmt.executeUpdate("CREATE TABLE trip( "
                    + "id integer NOT NULL AUTO_INCREMENT, "
                    + "driver_id integer, "
                    + "passenger_id integer, "
                    + "start datetime, "
                    + "end datetime, "
                    + "fee integer, "
                    + "rating integer, "

                    + "PRIMARY KEY (id), "
                    + "FOREIGN KEY (driver_id) REFERENCES driver(id), "
                    + "FOREIGN KEY (passenger_id) REFERENCES passenger(id) "

                    +"); ");

            System.out.println("Done! Tables are created");
        } catch (SQLException e) {
            System.out.println("[Error] Create table failed");
            System.out.println(e);
        }
    }

    public static void deleteTables(Statement stmt) throws SQLException {
        try {
            System.out.print("Processing...");
            stmt.executeUpdate("DROP TABLE IF EXISTS request;");
            stmt.executeUpdate("DROP TABLE IF EXISTS trip;");
            stmt.executeUpdate("DROP TABLE IF EXISTS driver;");
            stmt.executeUpdate("DROP TABLE IF EXISTS vehicle;");
            stmt.executeUpdate("DROP TABLE IF EXISTS passenger;");
            System.out.println("Done! Tables are deleted!");
        } catch (SQLException e) {
            System.out.println("[Error] Detele table failed");
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
                System.out.println("[Error] Invalid folder path!");
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
            System.out.println("[Error] Load data failed");
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
            System.out.println("[Error] Show data failed");
            System.out.println(e);
        }
    }


    public static void printPassenger(){
        System.out.println("Passenger, what would you like to do?");
        System.out.println("1. Request a ride");
        System.out.println("2. Check trip records");
        System.out.println("3. Rate a trip");
        System.out.println("4. Go back");
        System.out.println("Please enter [1-4].");
    }
    
    //return true if such passenger and trip exist
    public static Boolean checkPidTid(Connection con, int pid, int tid)
    {
        try
        {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM trip WHERE passenger_id=? AND id=?");
            pstmt.setInt(1,pid);
            pstmt.setInt(2,tid);

            ResultSet rs = pstmt.executeQuery();

            return rs.next();
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }

        return false;
    }

    //return true if such passenger exist
    public static Boolean checkPid(Connection con, int pid)
    {
        try
        {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM passenger WHERE id=?");
            pstmt.setInt(1,pid);

            ResultSet rs = pstmt.executeQuery();

            return rs.next();
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }

        return false;
    }

    public static Boolean haveOpenRequest(Connection con, int pid)
    {

        try
        {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM request WHERE passenger_id=? AND taken=0");
            pstmt.setInt(1,pid);

            ResultSet rs = pstmt.executeQuery();

            return rs.next();
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }

        return false;
    }

    public static void requestRide(Connection con)
    {
        int pid,seat,noAvaliable;
        String model="",year;
        Scanner scanner = new Scanner(System.in);
        PreparedStatement pstmt;

        System.out.println("Please enter your ID");
        pid = scanner.nextInt();
        System.out.println("Please enter number of passenger");
        seat= scanner.nextInt();
        scanner.nextLine();
        System.out.println("Please enter earlist model year. (Press enter to skip)");
        year = scanner.nextLine();
        System.out.println("Please enter the model. (Press enter to skip)");
        model = scanner.nextLine();
        
        //check input
        if(!checkPid(con,pid))
        {
            System.out.println("ERROR: No such passenger");
            return;
        }
        if(haveOpenRequest(con,pid))
        {
            System.out.println("ERROR: You have open request");
            return;
        }
        if(year.equals(""))
        {
            year="0";
        }

        try
        {
            pstmt = con.prepareStatement("SELECT count(*) AS avaliable FROM driver d,vehicle v WHERE d.vehicle_id=v.id AND model_year>=? AND LOWER(model) LIKE ? AND seats>=?  AND d.id NOT IN ( SELECT driver_id FROM trip WHERE end IS NULL )");
            pstmt.setInt(1,Integer.parseInt(year));
            pstmt.setString(2,"%"+model.toLowerCase()+"%");
            pstmt.setInt(3,seat);

            ResultSet rs = pstmt.executeQuery();

            rs.next();
            noAvaliable = rs.getInt("avaliable");

            if(noAvaliable==0)
            {
                System.out.println("ERROR: no avaliable driver, please change your setting");
                return;
            }
            else
            {
                System.out.println("Your request will be placed. "+noAvaliable+" driver are able to take your request");
            }
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
            return;
        }


        //put reauest into database
        try
        {
            pstmt = con.prepareStatement("INSERT INTO request (passenger_id,model_year,model,passengers,taken) value(?,?,?,?,?)");
            pstmt.setInt(1,pid);
            pstmt.setInt(2,Integer.parseInt(year));
            pstmt.setString(3,model);
            pstmt.setInt(4,seat);
            pstmt.setInt(5,0);
            pstmt.executeUpdate();
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }

    }

    public static void checkRecord(Connection con)
    {
        int pid,tid,free,rating;
        String dname,vid,model,queryStart,queryEnd;
        java.sql.Timestamp start = new java.sql.Timestamp(System.currentTimeMillis());
        java.sql.Timestamp end = new java.sql.Timestamp(System.currentTimeMillis());
        Scanner scanner =  new Scanner(System.in);

        System.out.println("Please enter your ID.");
        pid = scanner.nextInt();
        System.out.println("Please enteer your start date.");
        queryStart = scanner.next();
        System.out.println("Please enteer your end date.");
        queryEnd = scanner.next();

        if(!checkPid(con,pid))
        {
            System.out.println("ERROR: No such passenger");
            return;
        }

        queryStart = queryStart+" 00:00:00";
        queryEnd = queryEnd+" 23:59:59";

        try
        {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM trip t,driver d,vehicle v WHERE t.driver_id=d.id AND d.vehicle_id=v.id AND passenger_id=? AND t.start>=? AND t.end<=? ORDER BY t.start DESC");
            pstmt.setInt(1,pid);
            pstmt.setTimestamp(2,java.sql.Timestamp.valueOf(queryStart));
            pstmt.setTimestamp(3,java.sql.Timestamp.valueOf(queryEnd));

            ResultSet rs = pstmt.executeQuery();

            System.out.println("Trip ID, Driver Name, Vehicle ID, Vehicle model, Start, End, Free, Rating");
            while(rs.next())
            {
                tid = rs.getInt("id");
                dname = rs.getString("d.name");
                vid = rs.getString("v.id");
                model = rs.getString("v.model");
                start = rs.getTimestamp("t.start");
                end = rs.getTimestamp("t.end");
                free = rs.getInt("t.fee");
                rating = rs.getInt("t.rating");

                System.out.println(tid+", "+dname+", "+vid+", "+model+", "+start.toString()+", "+end.toString()+", "+free+", "+rating);

            }
        } 
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }


    public static void rate(Connection con)
    {
        int pid,tid,free,rating;
        String dname,vid,model,queryStart,queryEnd;
        java.sql.Timestamp start = new java.sql.Timestamp(System.currentTimeMillis());
        java.sql.Timestamp end = new java.sql.Timestamp(System.currentTimeMillis());
        Scanner scanner = new Scanner(System.in);
        PreparedStatement pstmt;

        //input
        System.out.println("Please enter your ID.");
        pid = scanner.nextInt();
        System.out.println("Please enter your trip ID.");
        tid = scanner.nextInt();
        System.out.println("Please enter your rating.");
        rating = scanner.nextInt();

        //check input
        if(!checkPidTid(con, pid, tid))
        {
            System.out.println("ERROR: No such passenger associate this trip");
            return;
        }

        if(rating<1 || rating>5)
        {
            System.out.println("ERROR: rating should be between 1-5");
            return;
        }

        //update database
        try
        {
            pstmt = con.prepareStatement("UPDATE trip SET rating=? WHERE id=? AND passenger_id=?");
            pstmt.setInt(1,rating);
            pstmt.setInt(2,tid);
            pstmt.setInt(3,pid);
            pstmt.executeUpdate();
        } catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }

        //display
        try
        {
            pstmt = con.prepareStatement("SELECT * FROM trip t,driver d,vehicle v WHERE t.driver_id=d.id AND d.vehicle_id=v.id AND passenger_id=? AND t.id=?");
            pstmt.setInt(1,pid);
            pstmt.setInt(2,tid);

            ResultSet rs = pstmt.executeQuery();

            System.out.println("Trip ID, Driver Name, Vehicle ID, Vehicle model, Start, End, Free, Rating");
            while(rs.next())
            {
                tid = rs.getInt("id");
                dname = rs.getString("d.name");
                vid = rs.getString("v.id");
                model = rs.getString("v.model");
                start = rs.getTimestamp("t.start");
                end = rs.getTimestamp("t.end");
                free = rs.getInt("t.fee");
                rating = rs.getInt("t.rating");

                System.out.println(tid+", "+dname+", "+vid+", "+model+", "+start.toString()+", "+end.toString()+", "+free+", "+rating);

            }
        } 
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }

    }


    public static void printDriver(){
        System.out.println("Driver, what would you like to do");
        System.out.println("1. Take a request");
        System.out.println("2. Finish a trip");
        System.out.println("3. Check driver rating");
        System.out.println("4. Go back");
        System.out.println("Please enter [1-4].");
    }

     
    public static Boolean checkDid(Connection con, int did)
    {
        try
        {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM driver WHERE id=?");
            pstmt.setInt(1,did);

            ResultSet rs = pstmt.executeQuery();

            return rs.next();
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }

        return false;
    }
    
    public static Boolean checkUnfinishTrip(Connection con, int did)
    {
        try
        {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM trip WHERE id=? AND END IS NULL");
            pstmt.setInt(1,did);

            ResultSet rs = pstmt.executeQuery();

            return rs.next();
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }

        return false;
    }

    public static void TakeRequest(Connection con) throws SQLException 
    {
	    int did,myYear,mySeat,takeRequestID;
	    set<Integer> set=new  HashSet<Integer>(); 
	    String myModel;
	    Scanner scanner = new Scanner(System.in);

	    System.out.println("Please enter your ID");
	    did = scanner.nextInt();
	    if(!checkDid(con,did))
	    {
		    System.out.println("Error: no such driver");
		    return;
	    }
	    if(!checkUnfinishTrip(con,did))
	    {
		    System.out.println("Error: You have unfinished trip");
		    return;
	    }
	    //get all information about my cat
	    try
	    {
		    PreparedStatement pstmt=con.prepareStatement("SELECT * FROM driver d,vehicle v WHERE d.vehicle_id=v.id AND d.id=?");
		    pstmt.setInt(1,did);
		    ResultSet rs=pstmt.executeQuery();
		    rs.next();
		    myYear=rs.getInt("v.model_year");
		    mySeat=rs.getInt("v.seats");
		    myModel=rs.getString("v.model");

		    System.out.println(myYear+" "+mySeat+" "+myModel);
	    }
	    catch(SQLException e)
	    {
		    System.out.println(e.getMessage());
		    return;
	    }

	    //get all request that i can take
	    try
	    {
		    PreparedStatement pstmt = con.prepareStatement("SELECT * FROM request r, passenger p WHERE r.passenger_id=p.id AND r.taken=0 AND r.model_Year<=? AND r.passengers<=? AND ? LIKE  concat('%',concat(r.model,'%'))");
		    pstmt.setInt(1,myYear);
		    pstmt.setInt(2,mySeat);
		    pstmt.setString(3,myModel);
		    ResultSet rs = pstmt.executeQuery();

		    System.out.println("Request ID, Passenger Name, Passengers");
		    while(rs.next())
		    {
				System.out.println(rs.getInt("r.id")+" "+rs.getString("p.name")+" "+rs.getInt("r.passengers"));
				set.add(rs.getInt("r.id"));
		    }
	    } catch (SQLException e)
	    {
		    System.out.println(e.getMessage());
	    }

	    System.out.println("Please enter the request ID");
	    takeRequestID=scanner.nextInt();

	    if(!set.contain(takeRequestID))
	    {
		    System.out.println("Please take a request ID from the above list");
		    return;
	    }


    }

    public static void FinishTrip(Statement stmt) throws SQLException {
        //get the id
        System.out.println("Please enter your ID.");

        //get the did
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNextInt()) {
            System.out.println("Wrong or Invalid input\n Please enter your ID");
        }
        int did = scanner.nextInt(); 

        //get the current trip info
        String query = "SELECT t.id, t.passenger_id, t.start "
               + "FROM trip t "
               + "WHERE t.driver_id = '" + did + "' AND end IS NULL;";
        ResultSet rs = stmt.executeQuery(query); 
        try {
            rs = stmt.executeQuery(query);
        } catch (SQLException ex) {
            System.out.println("Error in finding trip");
        }
        System.out.println(rs.getInt("id") + ", " + rs.getInt("passenger_id") + ", " +rs.getDate("start"));
        System.out.println("Do you wish to finish the trip? [y/n]");
        while(!scanner.hasNext()){ // stupid method
            System.out.println("Wrong or Invalid input\n Please enter [y/n]."); 
        }
        String input = scanner.next();
        while(input.length() != 1 || input.charAt(0) != 'y' || input.charAt(0) != 'n'){
            System.out.println("Wrong or Invalid input\n Please enter [y/n]."); 
            while(!scanner.hasNext()){
                System.out.println("Wrong or Invalid input\n Please enter [y/n]."); 
            }
            input = scanner.next();
        }
        if(input.charAt(0) == 'n'){
            return;
        }else{
            //take current time
            java.util.Date currentDate = Calendar.getInstance().getTime();
            long diff = rs.getDate("start").getTime();

            //calculate the fee and record all thwe things to print
            int tid = rs.getInt("id");
            String name = rs.getString("passenger_id");
            Date start = rs.getDate("start");
            int fee = (int)(diff / 60);

            //update the trip table
            query = "UPDATE trip "
                + "SET end ='" + currentDate + "', fee = '" + fee + "' "
                + "WHERE id = '" + tid + "';";
            rs = stmt.executeQuery(query); 
            try {
                rs = stmt.executeQuery(query);
            } catch (SQLException ex) {
                System.out.println("Error in updating the trip");
            }
            //print to the console
            System.out.print(tid + ", " + name + ", "+ start + ", " + currentDate + ", " + fee);

        }

    }


    public static void checkDriverRating(Statement stmt) throws SQLException {
	int rating;
        System.out.println("Please enter your ID.");
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNextInt()) {
            System.out.println("Wrong or Invalid input\n Please enter your ID");
        }
        int did = scanner.nextInt();
        //query
        String query = "Select avg(rating) as avgrating FROM trip t WHERE  t.driver_id = " + did + " AND  t.rating!=0";

        try {
		ResultSet rs = stmt.executeQuery(query);
		rs.next();
		rating=rs.getInt("avgrating");
		System.out.println("Your driver rating is"+rating);
        } catch (SQLException ex) {
            System.out.println("Error in Show Driver Rating");
	    System.out.println(ex.getMessage());
        }
    }

    public static void printRideSharingSystem() {
        System.out.println("Welcome! Who are you?");
        System.out.println("1. An Administrator");
        System.out.println("2. A passenger");
        System.out.println("3. A driver");
        System.out.println("4. Non of the above");
        System.out.println("Please enter [1-4].");
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
            System.err.println("[Error] Java MySQL DB Driver not found!");
            System.out.println(e);
            System.exit(0);
        }catch (SQLException e){
            System.out.println(e);
        }

        Statement stmt = con.createStatement();

        Scanner scanner = new Scanner(System.in);
        printRideSharingSystem();
        while (scanner.hasNextInt()) {
            int input = scanner.nextInt();
            if (input == 1) {
                printSystemAdmin();
                while (scanner.hasNextInt()) {
                    int innerInput = scanner.nextInt();
                    if (innerInput == 1) {
                        createTables(stmt);
                    } else if (innerInput == 2) {
                        deleteTables(stmt);
                    } else if (innerInput == 3) {
                        loadData(stmt, con);
                    } else if (innerInput == 4) {
                        checkData(stmt);
                    } else if (innerInput == 5) {
                        break;
                    } else {
                        System.out.println("[Error] Invalid input.");
                    }
                    printSystemAdmin();
                }
            }else if (input == 2) {
                printPassenger();
                while (scanner.hasNextInt()) {
                    int innerInput = scanner.nextInt();
                    if (innerInput == 1) {
                        requestRide(con);
                    } else if (innerInput == 2) {
                        checkRecord(con);
                    } else if (innerInput == 3) {
                        rate(con);
                    } else if (innerInput == 4) {
                        break;
                    } else {
                        System.out.println("[Error] Invalid input.");
                    }
                    printPassenger();
                }
            }else if (input == 3) {
                printDriver();
                while (scanner.hasNextInt()) {
                    int innerInput = scanner.nextInt();
                    if (innerInput == 1) {
                        TakeRequest(con);
                    } else if (innerInput == 2) {
                        FinishTrip(stmt);
                    } else if (innerInput == 3) {
                        checkDriverRating(stmt);
                    } else if (innerInput == 4) {
                        break;
                    } else {
                        System.out.println("[Error] Invalid input.");
                    }
                    printDriver();
                }
            }else if (input == 4) {
                System.out.println("Bye.");
                System.exit(0);
            } else {
                System.out.println("[Error] Invalid input.");
            }

            printRideSharingSystem();
        }
                    
    }
}
