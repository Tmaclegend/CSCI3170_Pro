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

	//return true if such passenger
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
	public static void requestRide(Connection con)
	{
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
