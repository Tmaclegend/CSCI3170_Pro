import java.util.*;
import java.io.*;
import java.sql.*;
import java.text.*;


public class Passenger {
	public static void printDriver(){
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

		//Statement stmt = con.createStatement();

		Scanner scanner = new Scanner(System.in);
		printDriver();
		while (scanner.hasNextInt()) {
			int input = scanner.nextInt();
			if (input == 1) {
				requestRide(con);
			} else if (input == 2) {
				checkRecord(con);
			} else if (input == 3) {
				rate(con);
			} else if (input == 4) {
				return;
			}
			else
			{
				System.out.println("invalid command");
			}
			printDriver();
		}

	}
}
