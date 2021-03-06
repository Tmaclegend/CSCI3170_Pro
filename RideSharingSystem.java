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
            System.out.println("Error: sql select fail");
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
            System.out.println("Error: sql select fail");
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
            System.out.println("Error: sql select fail");
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
        try{
            pid = scanner.nextInt();
        }catch(Exception e){
            System.out.println("ERROR: Invalid Input");
            return;
        }

        System.out.println("Please enter number of passenger");
        try{
            seat= scanner.nextInt();
        }catch(Exception e){
            System.out.println("ERROR: Invalid Input");
            return;
        }

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
            System.out.println("Error: sql select fail");
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
            System.out.println("Error: sql insert fail");
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
        try{
            pid = scanner.nextInt();
        }catch(Exception e){
            System.out.println("ERROR: Invalid Input");
            return;
        }

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
        java.sql.Timestamp sqlQueryStart = null;
        java.sql.Timestamp sqlQueryEnd = null;
        try{
            sqlQueryStart = java.sql.Timestamp.valueOf(queryStart);
        }catch (Exception e){
            System.out.println("ERROR: Wrong start date format [yyyy-mm-dd]");
            return;
        }
        try{
            sqlQueryEnd = java.sql.Timestamp.valueOf(queryEnd);
        }catch (Exception e){
            System.out.println("ERROR: Wrong end date format [yyyy-mm-dd]");
            return;
        }
            
        try
        {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM trip t,driver d,vehicle v WHERE t.driver_id=d.id AND d.vehicle_id=v.id AND passenger_id=? AND t.start>=? AND t.end<=? ORDER BY t.start DESC");
            pstmt.setInt(1,pid);
            pstmt.setTimestamp(2,sqlQueryStart);
            pstmt.setTimestamp(3,sqlQueryEnd);

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
            System.out.println("Error: sql select fail");
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
        try{
            pid = scanner.nextInt();
        }catch(Exception e){
            System.out.println("ERROR: Invalid Input");
            return;
        }

        System.out.println("Please enter your trip ID.");
        try{
            tid = scanner.nextInt();
        }catch(Exception e){
            System.out.println("ERROR: Invalid Input");
            return;
        }

        System.out.println("Please enter your rating.");
        try{
            rating = scanner.nextInt();
        }catch(Exception e){
            System.out.println("ERROR: Invalid Input");
            return;
        }

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
            System.out.println("Error: sql update fail");
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
            System.out.println("Error: sql select fail");
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
            System.out.println("Error: sql select fail");
            System.out.println(e.getMessage());
        }

        return false;
    }
    
    public static Boolean checkUnfinishTrip(Connection con, int did)
    {
        try
        {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM trip WHERE driver_id=? AND END IS NULL");
            pstmt.setInt(1,did);

            ResultSet rs = pstmt.executeQuery();

            return rs.next();
        }
        catch (SQLException e)
        {
            System.out.println("Error: sql select fail");
            System.out.println(e.getMessage());
        }

        return false;
    }

    public static void TakeRequest(Connection con) throws SQLException 
    {
	    int did,myYear,mySeat,takeRequestID,count=0;
	    String myModel;
	    Set<Integer> set=new  HashSet<Integer>();
	    Map<Integer,Integer> map =new HashMap<Integer,Integer>();
	    Scanner scanner = new Scanner(System.in);

	    System.out.println("Please enter your ID");
	    try{
            did = scanner.nextInt();
	    }catch(Exception e){
            System.out.println("ERROR: Invalid Input");
            return;
        }

        if(!checkDid(con,did))
	    {
		    System.out.println("Error: no such driver");
		    return;
	    }
	    if(checkUnfinishTrip(con,did))
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

	    }
	    catch(SQLException e)
	    {
            System.out.println("Error: sql select fail");
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
				System.out.println(rs.getInt("r.id")+", "+rs.getString("p.name")+", "+rs.getInt("r.passengers"));
				set.add(rs.getInt("r.id"));
				map.put(rs.getInt("r.id"),rs.getInt("r.passenger_id"));
				count++;
		    }

		    if(count==0)
		    {
			    System.out.println("You have no request to take");
			    return;
		    }
	    } catch (SQLException e)
	    {
            System.out.println("Error: sql select fail");
		    System.out.println(e.getMessage());
            return;
	    }

	    // io and checking
	    System.out.println("Please enter the request ID");
	    try{
            takeRequestID=scanner.nextInt();
	    }catch(Exception e){
            System.out.println("ERROR: Invalid Input");
            return;
        }

        if(!set.contains(takeRequestID))
	    {
		    System.out.println("Please take a request ID from the above list");
		    return;
	    }

	    //update table request
	    try
	    {
		    PreparedStatement pstmt = con.prepareStatement("UPDATE request SET taken=1 WHERE id=?");
		    pstmt.setInt(1,takeRequestID);
		    pstmt.executeUpdate();
	    }catch(SQLException e)
	    {
            System.out.println("Error: sql update fail");
		    System.out.println(e.getMessage());
		    return;
	    }

	    //insert into trip
        java.sql.Timestamp start = null;
	    try
	    {
		    PreparedStatement pstmt = con.prepareStatement("INSERT INTO trip (driver_id,passenger_id,start,end,fee,rating) value(?,?,?,NULL,NULL,0)");
		    pstmt.setInt(1,did);
		    pstmt.setInt(2,map.get(takeRequestID));
		    start = new java.sql.Timestamp(System.currentTimeMillis());
		    pstmt.setTimestamp(3,start);
		    pstmt.executeUpdate();
	    }catch(SQLException e)
	    {
            System.out.println("Error: sql insert fail");
		    System.out.println(e.getMessage());
		    return;
	    }

        try
        {
            PreparedStatement pstmt = con.prepareStatement("SELECT t.id , p.name, t.start "
                                                            +"FROM trip t, passenger p "
                                                            +"WHERE t.passenger_id=p.id "
                                                            +"AND t.driver_id = ? "
                                                            +"AND t.passenger_id = ? "
                                                            +"AND t.start = ?;");
            pstmt.setInt(1,did);
            pstmt.setInt(2,map.get(takeRequestID));
            pstmt.setTimestamp(3,start);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("Trip ID, Passenger Name, Passengers");
            if(rs.next())
            {
                System.out.println(rs.getInt("t.id")+", "+rs.getString("p.name")+", "+rs.getTimestamp("t.start"));
            }
        } catch (SQLException e)
        {
            System.out.println("Error: sql select fail");
            System.out.println(e.getMessage());
        }
            

    }

    public static void FinishTrip(Statement stmt, Connection con) throws SQLException {
        //get the id
        System.out.println("Please enter your ID.");

        //get the did
        Scanner scanner = new Scanner(System.in);
        //while (!scanner.hasNextInt()) {
        int did = 0;
        try{
            did = scanner.nextInt(); 
        }catch(Exception e){
            System.out.println("ERROR: Invalid Input");
            return;
        }

        if(!checkDid(con,did)){
            System.out.println("Error: no such driver");
            return;
        }
        //get the current trip info
        String query = "SELECT t.id, p.name, t.start "
               + "FROM trip t,passenger p"
               + " WHERE t.passenger_id=p.id AND t.driver_id = " + did + " AND t.end IS NULL;";
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(query);
        } catch (SQLException ex) {
            System.out.println("Error in finding trip");
	    //System.out.println(ex.getMessage());
            return;
        }
        if (rs.next() ){
            System.out.println("Trip ID, Passnger ID, Start");
            System.out.println(rs.getInt("t.id") + ", " + rs.getString("p.name") + ", " +rs.getTimestamp("t.start"));
            System.out.println("Do you wish to finish the trip? [y/n]");
            // while(!scanner.hasNext()){ // stupid method
            //     System.out.println("Wrong or Invalid input\nPlease enter [y/n]."); 
            // }
            String input = scanner.next();
            // if(input.charAt(0) != 'y' && input.charAt(0) != 'n'){
            //     System.out.println("ERROR: Invalid input"); 
            //     return;
            //     // while(!scanner.hasNext()){
            //     //     System.out.println("Wrong or Invalid input\nPlease enter [y/n]."); 
            //     // }
            //     // input = scanner.next();
            // }
            if(input.equals("n")){
                return;
            }else if(input.equals("y")){
                //take current time
                //java.util.Date currentDate = Calendar.getInstance().getTime();
                //long diff = rs.getDate("start").getTime();

                //calculate the fee and record all thwe things to print
                int tid = rs.getInt("t.id");
                String name = rs.getString("p.name");
                java.sql.Timestamp  start = rs.getTimestamp("t.start");
                //int fee = (int)(diff / 60);
                java.sql.Timestamp end = new java.sql.Timestamp(System.currentTimeMillis());

                long milliseconds1 = start.getTime();
                long milliseconds2 = end.getTime();
                long diff = milliseconds2 - milliseconds1;
                int fee = (int)Math.floor(diff / (60 * 1000));
                //update the trip table
                query = "UPDATE trip "
                    + "SET end ='" + end + "', fee = '" + fee + "' "
                    + "WHERE id = '" + tid + "';";
                try {
                    stmt.executeUpdate(query);
                } catch (SQLException ex) {
                    System.out.println("Error in updating the trip");
                    System.out.println(ex);
                    return;
                }
                //print to the console
                System.out.println("Trip ID, Passnger name, Start, End, Fee");
                System.out.println(tid + ", " + name + ", "+ start + ", " + end + ", " + fee);

            }else{
                System.out.println("ERROR: Invalid input"); 
                return;
            }
        }else{
            System.out.println("You don't have any unfinished trip.");
        }
        

    }


    public static void checkDriverRating(Statement stmt, Connection con) throws SQLException {
	    float rating;
        System.out.println("Please enter your ID.");
        Scanner scanner = new Scanner(System.in);
        //while (!scanner.hasNextInt()) {
        // if (!scanner.hasNextInt()) {
        //     System.out.println("[Error] Invalid input!");
        //     return;
        // }
        int did = 0;
        try{
            did = scanner.nextInt();
        }catch(Exception e){
            System.out.println("ERROR: Invalid Input");
            return;
        }

        if(!checkDid(con,did)){
            System.out.println("Error: no such driver");
            return;
        }
        //query
        //String query = "Select round(avg(rating),2) as avgrating FROM trip t WHERE  t.driver_id = " + did + " AND  t.rating!=0";
        String getRatedTripCount = "select count(*) as count from trip where "
                                        +"driver_id = "+ did +" and "
                                        +"rating >0;";

        String getRating = "SELECT round(avg(rating),2) as rating FROM trip WHERE rating>0 AND driver_id="+did;
        
	ResultSet rs = null;
        try {
            rs = stmt.executeQuery(getRatedTripCount);
            rs.next();
            int ratedTripCount = rs.getInt("count");
            if(ratedTripCount >= 5 ){
                rs = stmt.executeQuery(getRating);
                rs.next();
                rating = rs.getFloat("rating");
                System.out.println("Your driver rating is "+rating+".");
            }else{
                System.out.println("You don't have enough rated trips.");
            }
            
        } catch (SQLException ex) {
            System.out.println("Error in Show Driver Rating");
            System.out.println(ex.getMessage());
        }
     //    try {
    	// 	rs = stmt.executeQuery(query);
    	// 	rs.next();
    	// 	rating=rs.getFloat("avgrating");
    	// 	System.out.println("Your driver rating is "+rating);
     //    } catch (SQLException ex) {
     //        System.out.println("Error in Show Driver Rating");
	    // System.out.println(ex.getMessage());
     //    }
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
        while (scanner.hasNext()) {
            String input = scanner.next();
            if (input.equals("1")) {
                printSystemAdmin();
                while (scanner.hasNext()) {
                    String innerInput = scanner.next();
                    if (innerInput.equals("1")) {
                        createTables(stmt);
                    } else if (innerInput.equals("2")) {
                        deleteTables(stmt);
                    } else if (innerInput.equals("3")) {
                        loadData(stmt, con);
                    } else if (innerInput.equals("4")) {
                        checkData(stmt);
                    } else if (innerInput.equals("5")) {
                        break;
                    } else {
                        System.out.println("[Error] Invalid input.");
                    }
                    printSystemAdmin();
                }
            }else if (input.equals("2")) {
                printPassenger();
                while (scanner.hasNext()) {
                    String innerInput = scanner.next();
                    if (innerInput.equals("1")) {
                        requestRide(con);
                    } else if (innerInput.equals("2")) {
                        checkRecord(con);
                    } else if (innerInput.equals("3")){
                        rate(con);
                    } else if (innerInput.equals("4")) {
                        break;
                    } else {
                        System.out.println("[Error] Invalid input.");
                    }
                    printPassenger();
                }
            }else if (input.equals("3")) {
                printDriver();
                while (scanner.hasNext()) {
                    String innerInput = scanner.next();
                    if (innerInput.equals("1")) {
                        TakeRequest(con);
                    } else if (innerInput.equals("2")) {
                        FinishTrip(stmt, con);
                    } else if (innerInput.equals("3")) {
                        checkDriverRating(stmt, con);
                    } else if (innerInput.equals("4")) {
                        break;
                    } else {
                        System.out.println("[Error] Invalid input.");
                    }
                    printDriver();
                }

            }else if (input.equals("4")) {
                System.out.println("Bye.");
                System.exit(0);
            } else {
                System.out.println("[Error] Invalid input.");
            }

            printRideSharingSystem();
        }
                    
    }
}
