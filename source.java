import java.util.*;
import java.io.*;
import java.sql.*;
import java.text.*;

public class CSCI3170Project {

    public static void print_mainmenu() {
        System.out.println("-----Main meun-----");
        System.out.println("What kinds of operation would you like to perform?");
        System.out.println("1. Operations for administrator");
        System.out.println("2. Operations for salesperson");
        System.out.println("3. Operations for manager");
        System.out.println("4. Exit this program");
        System.out.print("Enter You Choice: ");
    }

    public static void print_adminmenu() {
        System.out.println("-----Operations for administrator menu-----");
        System.out.println("What kinds of operation would you like to perform");
        System.out.println("1. Create all tables");
        System.out.println("2. Delete all tables");
        System.out.println("3. Load from datafile");
        System.out.println("4. Show nuber of records in each table");
        System.out.println("5. Return to the main menu");
        System.out.print("Enter Your Choice: ");
    }

    public static void print_salesmenu() {
        System.out.println("-----Operations for salesperson menu-----");
        System.out.println("1. Search for parts");
        System.out.println("2. Sell a part");
        System.out.println("3. Return to the main menu");
        System.out.print("Enter Your Choice: ");
    }

    public static void print_managermenu() {
        System.out.println("-----Operations for manager menu-----");
        System.out.println("What kinds of operation would you like to perform?");
        System.out.println("1. Count the no. of sales record of each sales person under a specific range on years of experience");
        System.out.println("2. Show the toal sales value of each manufacturer");
        System.out.println("3. Show the N most popular part");
        System.out.println("4. Return to the main menu");
        System.out.print("Enter Your Choice:");
    }

    public static void load_from_datafile(Statement stmt, Connection conn) throws ParseException, IOException, SQLException {
        PreparedStatement pstmt_manu;
        PreparedStatement pstmt_part;
        PreparedStatement pstmt_sale;
        PreparedStatement pstmt_tran;
        try {

            PreparedStatement pstmt_cate = conn.prepareStatement("insert into Category values(?,?)");
            pstmt_manu = conn.prepareStatement("insert into Manufacturer values(?,?,?,?)");
            pstmt_part = conn.prepareStatement("insert into Part values(?,?,?,?,?,?,?)");
            pstmt_sale = conn.prepareStatement("insert into Salesperson values(?,?,?,?,?)");
            pstmt_tran = conn.prepareStatement("insert into Transaction values(?,?,?,STR_TO_DATE(?,'%d/%m/%Y'))");
            //System.out.println("2");

            Scanner scanner = new Scanner(System.in);
            System.out.print("Type in the Source Data Folder Path: ");
            

            //System.out.println(""+inputfile);
            if (scanner.hasNextLine()){
                String inputfile = scanner.nextLine();
                System.out.print("Processing...");
                Scanner sale_file, cate_file, part_file, manu_file, tran_file;
                cate_file = new Scanner(new FileReader(inputfile+"/category.txt"));
                manu_file = new Scanner(new FileReader(inputfile+"/manufacturer.txt"));
                part_file = new Scanner(new FileReader(inputfile+"/part.txt"));
                sale_file = new Scanner(new FileReader(inputfile+"/salesperson.txt"));
                tran_file = new Scanner(new FileReader(inputfile+"/transaction.txt"));
                
                //System.out.println("5");
                while (cate_file.hasNextLine()) {
                    String tmp[] = cate_file.nextLine().split("\t");
                    //System.out.println(""+cate_file.nextLine());
                    //System.out.println(""+ tmp[0]);
                    int id = Integer.parseInt(tmp[0]);
                    //System.out.println("id=:"+id+"\n");
                    String name = tmp[1];
                    //System.out.println("name=:"+name+"\n");
                    pstmt_cate.setInt(1, id);
                    pstmt_cate.setString(2, name);
                    pstmt_cate.executeUpdate();
                }
                //System.out.println("6");
                while (manu_file.hasNextLine()) {
                    String tmp[] = manu_file.nextLine().split("\t");
                    int id = Integer.parseInt(tmp[0]);
                    String name = tmp[1];
                    String address = tmp[2];
                    int phoneno = Integer.parseInt(tmp[3]);
                    pstmt_manu.setInt(1, id);
                    pstmt_manu.setString(2, name);
                    pstmt_manu.setString(3, address);
                    pstmt_manu.setInt(4, phoneno);
                    pstmt_manu.executeUpdate();
                }
                //System.out.println("7");
                while (part_file.hasNextLine()) {
                    String tmp[] = part_file.nextLine().split("\t");
                    int id = Integer.parseInt(tmp[0]);
                    String name = tmp[1];
                    int price = Integer.parseInt(tmp[2]);
                    int mid = Integer.parseInt(tmp[3]);
                    int cid = Integer.parseInt(tmp[4]);
                    int warr = Integer.parseInt(tmp[5]);
                    int avail = Integer.parseInt(tmp[6]);
                    pstmt_part.setInt(1, id);
                    pstmt_part.setString(2, name);
                    pstmt_part.setInt(3, price);
                    pstmt_part.setInt(4, mid);
                    pstmt_part.setInt(5, cid);
                    pstmt_part.setInt(6, warr);
                    pstmt_part.setInt(7, avail);
                    pstmt_part.executeUpdate();
                }
                //System.out.println("8");
                while (sale_file.hasNextLine()) {
                    String tmp[] = sale_file.nextLine().split("\t");
                    int id = Integer.parseInt(tmp[0]);
                    String name = tmp[1];
                    String address = tmp[2];
                    int phone = Integer.parseInt(tmp[3]);
                    int exp = Integer.parseInt(tmp[4]);
                    pstmt_sale.setInt(1, id);
                    pstmt_sale.setString(2, name);
                    pstmt_sale.setString(3, address);
                    pstmt_sale.setInt(4, phone);
                    pstmt_sale.setInt(5, exp);
                    pstmt_sale.executeUpdate();
                }
                //System.out.println("9");
                while (tran_file.hasNextLine()) {
                    
                    String tmp[] = tran_file.nextLine().split("\t");
                    int tid = Integer.parseInt(tmp[0]);
                    int pid = Integer.parseInt(tmp[1]);
                    int sid = Integer.parseInt(tmp[2]);
                    String str_date = tmp[3];

   
                    pstmt_tran.setInt(1, tid);
                    pstmt_tran.setInt(2, pid);
                    pstmt_tran.setInt(3, sid);
                    //System.out.println("above ok");
                    pstmt_tran.setString(4, str_date);
                    //System.out.println("date ok" + str_date);
                    pstmt_tran.execute();
                }
                //System.out.println("10");
                //pstmt_manu.close();
                //pstmt_part.close();
                //pstmt_sale.close();
                //pstmt_tran.close();
                //pstmt_cate.close();
                //System.out.println("done");
            } else {
                System.out.println("invalid Folder Path!");
            }
            pstmt_manu.close();
            pstmt_part.close();
            pstmt_sale.close();
            pstmt_tran.close();
            pstmt_cate.close();
            System.out.println("Done! Data is inputted to the database");
        } catch (SQLException ex) {
            System.out.println("PreparedStatement fails");
        }

    }

    public static void show_number_of_record(Statement stmt) throws SQLException {
        try {
            System.out.println("Number of records in each table:");
            System.out.print("Category: ");
            String query = "SELECT COUNT(cID) FROM Category;";
            //System.out.println("1");

            ResultSet rs = stmt.executeQuery(query);
            //System.out.println("2");
            while (rs.next()) {
                int n = rs.getInt(1);
                System.out.println(n);
            }

            System.out.print("Manufacturer: ");
            query = "SELECT COUNT(*) FROM Manufacturer;";
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                int n = rs.getInt(1);
                System.out.println(n);
            }

            System.out.print("Part: ");
            query = "SELECT COUNT(*) FROM Part;";
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                int n = rs.getInt(1);
                System.out.println(n);
            }

            System.out.print("Saleperson: ");
            query = "SELECT COUNT(*) FROM Salesperson;";
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                int n = rs.getInt(1);
                System.out.println(n);
            }

            System.out.print("Transaction: ");
            query = "SELECT COUNT(*) FROM Transaction;";
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                int n = rs.getInt(1);
                System.out.println(n);
            }

        } catch (SQLException ex) {
            System.out.println("Show number of records in each table fails");
        }
    }

    public static void delete_all_tables(Statement stmt) throws SQLException {
        try {
            System.out.print("Processing...");
            stmt.executeUpdate("DELETE FROM Category;");
            stmt.executeUpdate("DELETE FROM Manufacturer;");
            stmt.executeUpdate("DELETE FROM Part;");
            stmt.executeUpdate("DELETE FROM Salesperson;");
            stmt.executeUpdate("DELETE FROM Transaction;");
            System.out.println("Done! Database is removed!");
        } catch (SQLException ex) {
            System.out.println("Delete table fail with SQL Exception");
        }
    }

    public static void create_all_tables(Statement stmt) throws SQLException {
        try {
        	System.out.print("Processing...");
            //Connection conn = DriverManager.getConnection("jdbc:mysql://projgw.cse.cuhk.edu.hk:2712/db109?autoReconnect=true&useSSL=false", "db109", "8e5a194a");
        	//Statement stmt = conn.createStatement();
        	stmt.executeUpdate("DROP TABLE IF EXISTS Category;");
        	stmt.executeUpdate("DROP TABLE IF EXISTS Manufacturer;");
        	stmt.executeUpdate("DROP TABLE IF EXISTS Part;");
        	stmt.executeUpdate("DROP TABLE IF EXISTS Salesperson;");
        	stmt.executeUpdate("DROP TABLE IF EXISTS Transaction;");

            //SET FOREIGN_KEY_CHECKS = 0;
			stmt.executeUpdate("CREATE TABLE Category"
                    + "(cID integer, cName varchar(20) NOT NULL,"
                    + "PRIMARY KEY (cID));");

			
            stmt.executeUpdate("CREATE TABLE Manufacturer"
                    + "(mID integer primary key, mName varchar(20) not null, mAddress varchar(50), "
                    + "mPhoneNumber integer);");

            //stmt.executeQuery("DESCRIBE Category;");
            //stmt.executeQuery("DESCRIBE Manufacturer;");

            stmt.executeUpdate("CREATE TABLE Part"
                    + "(pID integer primary key, pName varchar(20), pPrice integer, "
                    + "mID integer not null, cID integer not null, pWarrantyPeriod integer, "
                    + "pAvailableQuantity integer); ");
                    //+ "FOREIGN KEY (mID) REFERENCES Manufacturer);");
                    //+ "FOREIGN KEY (mID) REFERENCES Manufacturer "
                    //+ "ON DELETE CASCADE"
                    //+ "ON UPDATE NO ACTION);");
                    //+ "FOREIGN KEY (cID) REFERENCES Category "
                    //+ "ON DELETE CASCADE"
                    //+ "ON UPDATE NO ACTION);");
			//stmt.executeQuery("DESCRIBE Part;");

            
            stmt.executeUpdate("CREATE TABLE Salesperson"
                    + "(sID integer primary key, sName varchar(20), "
                    + "sAddress varchar(50), sPhoneNumber integer, "
                    + "sExperience integer);");
            //SET FOREIGN_KEY_CHECKS = 1;
            
            stmt.executeUpdate("CREATE TABLE Transaction"
                    + "(tID integer primary key, pID integer, sID integer, tDate DATE);");
                    //+ "FOREIGN KEY (pID) REFERENCES Part(pID)"
                    //+ "ON DELETE CASCADE"
                    //+ "ON UPDATE NO ACTION"
                    //+ "FOREIGN KEY (sID) REFERENCES Salesperson(sID)"
                    //+ "ON DELETE CASCADE"
                    //+ "ON UPDATE NO ACTION);");
			
            System.out.println("Done! Database is initialized!");
        } catch (SQLException ex) {
            System.out.println("Create table fail with SQL Exception");
        }
    }

public static void Saleperson_SellParts(Statement stmt) throws SQLException {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter The Part ID:");
            int part_id = scanner.nextInt();
            System.out.print("Enter The Salesperson ID:");
            int sale_id = scanner.nextInt();
            String query = "SELECT pName, pAvailableQuantity FROM Part WHERE pID =" + part_id 
                        + " AND "+ sale_id +" IN (SELECT sID FROM Salesperson);";
            ResultSet rs = stmt.executeQuery(query);

            int quan;
            String name;
            if (rs.next()) {
                name = rs.getString("pName");
                quan = rs.getInt("pAvailableQuantity");
            } else {
                System.out.println("No such part or such salesperson!");
                return;
            }
            if (quan <= 0) {
                System.out.println("No Available Part!");
            } else {
                stmt.executeUpdate("UPDATE Part SET pAvailableQuantity = pAvailableQuantity -1 WHERE "
                        + "pID = " + part_id);
                String query1 = "SELECT MAX(tID) AS tid FROM Transaction;";
                ResultSet rs1 = stmt.executeQuery(query1);
                //System.out.println("select max suc");
                int tid;
                   if(rs1.next()){
                       tid=rs1.getInt("tid");
                       //System.out.println(tid);
                   }
                   else{
                       System.out.println("Find tid error");
                       return;
                   }
                  tid++;
                  
                stmt.executeUpdate("INSERT INTO Transaction VALUES ("+tid +" , " +part_id+" , "+sale_id+ " , NOW())");
                //System.out.println("insert suc");
                System.out.println("Product: " + name + "(id:" + part_id + ") Remaining Quality: " + (quan - 1));
            }
        } catch (SQLException ex) {
            System.out.println("Select Part fail with SQL Exception");
        }
    }

public static void Manager_CountExperience(Statement stmt) {
        try {
            Scanner scanner = new Scanner(System.in);
            
            System.out.print("Type in the lower bound for years of experience: ");
            int lower = scanner.nextInt();
            System.out.print("Type in the upper bound for years of experience: ");
            int upper = scanner.nextInt();
            String query = "SELECT S.sID, S.sName, S.sExperience, COUNT(*) AS tTran"
                    + " FROM Salesperson S, Transaction T"
                    + " WHERE S.sID = T.sID "
                    + " AND S.sExperience <= " + upper + " AND S.sExperience >= " + lower
                    + " GROUP BY S.sID "
                    + " ORDER BY S.sID DESC;";
                    //+ " HAVING S.sExperience <= " + upper + " AND S.sExperience >= " + lower+";";
            System.out.println("query to string ok");
            ResultSet rs = stmt.executeQuery(query);
            System.out.println("query exe ok");
            int id;
            String name;
            int exp;
            int tran;
            System.out.println("| ID | Name | Years of Experience | Number of Transaction |");
            while (rs.next()) {
                id = rs.getInt("sID");
                name = rs.getString("sName");
                exp = rs.getInt("sExperience");
                tran = rs.getInt("tTran");
                System.out.println("| " + id + " | " + name + " | " + exp + " | " + tran + " |");
            }
            System.out.println("End of Query");
        } catch (SQLException ex) {
            System.out.println("Count fail with SQL Exception");
        }
    }

    public static void Manager_ShowSaleOfManu(Statement stmt) {
        try {
            String query = "SELECT M.mID, M.mName, SUM(P.pPrice) AS total "
            + "FROM Transaction T, Manufacturer M, Part P "
            + "WHERE T.pID = P.pID AND P.mID = M.mID "
            + "GROUP BY M.mID "
            + "ORDER BY total DESC;";
            //System.out.println("query to string ok");

            ResultSet rs = stmt.executeQuery(query);
            int id;
            String name;
            int total;
            System.out.println("| Manufacturer ID | Manufacturer Name | Total Sales Values |");
            while (rs.next()) {
                name = rs.getString("mName");
                id = rs.getInt("mID");
                total = rs.getInt("total");
                System.out.println("| " + id + " | " + name + " | " + total + " |");
            }
            System.out.println("End of Query");
        } catch (SQLException ex) {
            System.out.println("Select Part fail with SQL Exception");
        }
    }

    public static void Saleperson_SearchParts(Statement stmt) {
        System.out.println("Choose the Search criterion:");
        System.out.println("1. Part Name");
        System.out.println("2. Manufacturer Name");
        System.out.print("Choose the Search criterion: ");
        Scanner choice_1 = new Scanner(System.in);
        int int_order;
        if (choice_1.hasNextInt()) {
            int choice = choice_1.nextInt();
            if (choice == 1) {

                System.out.print("Type in the Search Keyword: ");
                Scanner temp1 = new Scanner(System.in);

                if (temp1.hasNextLine()) {
                    String keyword;
                    keyword = temp1.nextLine();
                    System.out.println("Choose ordering: ");
                    System.out.println("1. By price, ascending order");
                    System.out.println("2. By price, descending order");
                    System.out.print("Choose the search criterion: ");
                    Scanner order = new Scanner(System.in);
                   
                    int_order = order.nextInt();

                    String query = "";
                    if (int_order == 1) {
                        query = "SELECT P.pID, P.pName, M.mName, C.cName, P.pAvailableQuantity, P.pWarrantyPeriod, P.pPrice "
                                + "FROM Part P, Manufacturer M, Category C "
                                + "WHERE P.mID = M.mID AND P.pName LIKE '%" + keyword + "%' AND P.cID = C.cID "
                                + "ORDER BY P.pPrice ASC;";
                    } else if ( int_order == 2) {
                        query = "SELECT P.pID, P.pName, M.mName, C.cName, P.pAvailableQuantity, P.pWarrantyPeriod, P.pPrice "
                                + "FROM Part P, Manufacturer M, Category C "
                                + "WHERE P.mID = M.mID AND P.cID = C.cID AND P.pName LIKE '%" + keyword + "%' "
                                + "ORDER BY P.pPrice DESC;";
                    }

                    ResultSet rs = null;
                    try {
                        rs = stmt.executeQuery(query);
                    } catch (SQLException ex) {
                        System.out.println("executeQuery error!");
                    }
                    try {
                        System.out.println("| ID | Name | Manufacturer | Category | Quantity | Warranty | Price |");

                        while (rs.next()) {
                            int id, price, warranty, quantity;
                            String Name, Manufacturer, category;

                            id = rs.getInt("pID");
                            price = rs.getInt("pPrice");
                            warranty = rs.getInt("pWarrantyPeriod");
                            quantity = rs.getInt("pAvailableQuantity");
                            Name = rs.getString("pName");
                            Manufacturer = rs.getString("mName");
                            category = rs.getString("cName");

                            System.out.println("| "
                                    + id + " | "
                                    + Name + " | "
                                    + Manufacturer + " | "
                                    + category + " | "
                                    + quantity + " | "
                                    + warranty + " | "
                                    + price + " | ");
                        }
                        System.out.println("End of Query");
                    } catch (SQLException ex) {
                        System.out.println("executeQuery error!");
                    }
                }
            } else if (choice == 2) {

                System.out.print("Type in the Search Keyword: ");
                Scanner temp1 = new Scanner(System.in);

                if (temp1.hasNextLine()) {
                    String keyword;
                    keyword = temp1.nextLine();

                    System.out.println("Choose ordering");
                    System.out.println("1. By price, ascending order");
                    System.out.println("2. By price, descending order");
                    System.out.print("Choose the search criterion: ");
                    Scanner order = new Scanner(System.in);
                    int_order = order.nextInt();
                    //System.out.println(choice + "," + keyword+","+ int_order);
                    String query = "";
                    if (int_order == 1) {
                        query = "SELECT P.pID, P.pName, M.mName, C.cName, P.pAvailableQuantity, P.pWarrantyPeriod, P.pPrice "
                                + "FROM Part P, Manufacturer M, Category C "
                                + "WHERE P.mID = M.mID AND M.mName LIKE '%" + keyword + "%' AND P.cID = C.cID "
                                + "ORDER BY P.pPrice ASC;";
                    } else if (int_order == 2) {
                        query = "SELECT P.pID, P.pName, M.mName, C.cName, P.pAvailableQuantity, P.pWarrantyPeriod, P.pPrice "
                                + "FROM Part P, Manufacturer M, Category C "
                                + "WHERE P.mID = M.mID AND P.cID = C.cID AND M.mName LIKE '%" + keyword + "%' "
                                + "ORDER BY P.pPrice DESC;";
                    }

                    ResultSet rs = null;
                    try {
                        rs = stmt.executeQuery(query);
                    } catch (SQLException ex) {
                        System.out.println("executeQuery error!");
                    }
                    try {
                        System.out.println("| ID | Name | Manufacturer | Category | Quantity | Warranty | Price |");

                        while (rs.next()) {
                            int id, price, warranty, quantity;
                            String Name, Manufacturer, category;

                            id = rs.getInt("pID");
                            price = rs.getInt("pPrice");
                            warranty = rs.getInt("pWarrantyPeriod");
                            quantity = rs.getInt("pAvailableQuantity");
                            Name = rs.getString("pName");
                            Manufacturer = rs.getString("mName");
                            category = rs.getString("cName");

                            System.out.println("| "
                                    + id + " | "
                                    + Name + " | "
                                    + Manufacturer + " | "
                                    + category + " | "
                                    + quantity + " | "
                                    + warranty + " | "
                                    + price + " | ");
                        }
                        System.out.println("End of Query");
                    } catch (SQLException ex) {
                        System.out.println("executeQuery error!");
                    }
                }
            } else {
                System.out.println("please choose the Search criterion");
            }
        }
    }

    public static void Manager_ShowMostPopular(Statement stmt) throws SQLException {
        System.out.print("Type in the number of parts: ");
        Scanner no_of_parts = new Scanner(System.in);
        while (!no_of_parts.hasNextInt()) {
            System.out.print("Wrong or Invalid input\n Type in the number of parts again:  ");
        }
        int number = no_of_parts.nextInt();

        String query = "SELECT P.pID, P.pName, COUNT(T.pID) AS noOfTran "
                + "FROM Part P, Transaction T "
                + "WHERE P.pID = T.pID "
                + "GROUP BY T.pID "
                + "ORDER BY noOfTran DESC;";

        ResultSet rs = stmt.executeQuery(query);
        try {
            rs = stmt.executeQuery(query);
        } catch (SQLException ex) {
            System.out.println("Error in Show most popular");
        }
        try {
            System.out.println("| Part ID | Part Name | No. of Transaction |");
            int i = 0;
            while (rs.next() && i < number) {
                int id, number_T;
                String Name;

                id = rs.getInt("pID");
                Name = rs.getString("pName");
                number_T = rs.getInt("noOfTran");

                System.out.println("| "
                        + id + " | "
                        + Name + " | "
                        + number_T + " |");
                i++;
            }
            System.out.println("End of Query");
        } catch (SQLException ex) {
            System.out.println("Error");
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception x) {
            System.err.println("Unable to load the driver class!");
        }
        Connection conn = DriverManager.getConnection("jdbc:mysql://projgw.cse.cuhk.edu.hk:2712/db109?autoReconnect=true&useSSL=false", "db109", "8e5a194a");
        Statement stmt = conn.createStatement();

        Scanner scanner = new Scanner(System.in);
        print_mainmenu();
        while (scanner.hasNextInt()) {
            System.out.println("");
            int input = scanner.nextInt();
            if (input == 1) {
                print_adminmenu();
                while (scanner.hasNextInt()) {
                    System.out.println("");

                    int input2 = scanner.nextInt();
                    if (input2 == 1) {
                        create_all_tables(stmt);
                    } else if (input2 == 2) {
                        delete_all_tables(stmt);
                    } else if (input2 == 3) {
                        load_from_datafile(stmt, conn);
                    } else if (input2 == 4) {
                        show_number_of_record(stmt);
                    } else if (input2 == 5) {
                        break;
                    }
                    print_adminmenu();
                }
            } else if (input == 2) {
                print_salesmenu();
                while (scanner.hasNextInt()) {
                    System.out.println("");
                    int input2 = scanner.nextInt();
                    if (input2 == 1) {
                        Saleperson_SearchParts(stmt);
                        print_salesmenu();
                    } else if (input2 == 2) {
                        Saleperson_SellParts(stmt);
                        print_salesmenu();
                    } else if (input2 == 3) {
                        break;
                    }
                }
                
            } else if (input == 3) {

                print_managermenu();
                while (scanner.hasNextInt()) {
                    System.out.println("");
                    int input3 = scanner.nextInt();
                    if (input3 == 1) {
                        Manager_CountExperience(stmt);
                        print_managermenu();
                    } else if (input3 == 2) {
                        Manager_ShowSaleOfManu(stmt);
                        print_managermenu();
                    } else if (input3 == 3) {
                        Manager_ShowMostPopular(stmt);
                        print_managermenu();
                    } else if (input3 == 4) {
                        break;
                    }
                    
                }
            } else if (input == 4) {
                System.out.println("Exit!");
                break;
            }
            print_mainmenu();
        }
    }

}
