//retrieve student records from an DB
import java.io.*;
import java.sql.*;
import java.util.Scanner;

class Student{

 public static int act_sid;
 public static String username;
 public static String password;

 // the host name of the server and the server instance name/id
 public static final String oracleServer = "dbs2.cs.umb.edu";
 public static final String oracleServerSid = "dbs2";
 public static final String jdbcDriver = "oracle.jdbc.OracleDriver";
 public static final String connString = "jdbc:oracle:thin:@" + oracleServer + ":1521:" + oracleServerSid;

 public static void main(String args[]) {

 //get username, password and try connection
 firstConnection();

 boolean existSid = false;
 //get the active student ID
 selectActiveSid();

 //act according user's choice
 do{
  showMenu();
 }while(userChoice());

 }//end of main method


 //try connection with input username and password
 public static Boolean tryConn(String username, String password){
 // load the jdbcDriver
 try {
  Class.forName(jdbcDriver);
  } catch (Exception e) {
   e.printStackTrace();
   return false;
  }
 // Connect to the database
 Connection conn;
 try{
  conn = DriverManager.getConnection(connString, username, password);
  }catch(SQLException e){
   System.out.println("Connection ERROR");
   e.printStackTrace();
   System.out.println("You may want to try again!");
   return false;
  }
  System.out.println("Connection succeeded!");
  return true;
 }

 //for first time connection
 public static void firstConnection(){
 do{
    Scanner input = new Scanner(System.in);
    System.out.print("Please enter your username(-q to quit):");
    username = input.nextLine();
    if (username.equals("-q"))
     System.exit(1);
    System.out.print("Password:");
    //the following is used to mask the password
    Console console = System.console();
    password = new String(console.readPassword());
    System.out.println("Connecting to the database...");
   } while (!tryConn(username,password));
 }

 //for re-connection
 public static Connection reConnection(String username, String password){

  // load the jdbcDriver
  try {
   Class.forName(jdbcDriver);
  } catch (Exception e) {
   e.printStackTrace();
  }

  // Connect to the database
  Connection reConn;
  try{
   reConn = DriverManager.getConnection(connString, username, password);
   //System.out.println("Connection Successful");
  }
  catch(SQLException e){
   System.out.println("Connection ERROR");
   e.printStackTrace();
   return null;
  }
  return reConn;
 }

 //check existing sid
 public static boolean checkSid(int a_sid){
  try{
   Connection reConn = reConnection(username, password);
   Statement stmt = reConn.createStatement();
   ResultSet rs = stmt.executeQuery("select sid from students" + " where sid = "+ a_sid);
   if (rs.next())
    return true;
    else return false;
     }catch (SQLException e) {
    System.out.println ("ERROR OCCURRED");
    e.printStackTrace();
    return false;
    }
 }

 //add new student method
 public static boolean addStudent(){
  boolean existSid = false;
  Scanner input = new Scanner(System.in);
  String act_sname;
 do{
  System.out.print("Please enter a new student ID(8-digit number only,enter 0 to return without action):");
  act_sid=input.nextInt();
  if (act_sid == 0)
  return false;//return as unchanged current act_sid
  //check if input student ID already exists
  else
  {existSid = checkSid(act_sid);
  if (existSid)
  System.out.println("Student ID already exist, try another one");}
  } while(existSid);

 //if new sid, ask for sname
 System.out.println("Please enter the student name(maxium 20 characters):");
 act_sname = input.nextLine();

 //add new student to students table
 try {
  Connection reConn = reConnection(username, password);
  String insertStudent = "INSERT INTO students (sid, sname) VALUES (?,?)";
  PreparedStatement pstmt = reConn.prepareStatement(insertStudent);
  pstmt.setInt(1,act_sid);
  pstmt.setString(2,act_sname);
  pstmt.executeUpdate();
  }catch (SQLException e) {
    System.out.println ("ERROR OCCURRED, Adding failed.");
    e.printStackTrace();
    act_sid = 0;//reset active student ID
    return false;}//insertion failed

  System.out.println ("New student record added.");
  return true;
 }

 public static void selectActiveSid(){
  boolean existSid = false;
  Scanner input = new Scanner(System.in);
  //get the active student ID
  do{
   System.out.print("Please input query student ID(8-digit number only,enter -1 for adding new student):");
   act_sid=input.nextInt();
   if (act_sid == -1)
   {if (addStudent())//add student
    existSid = true;}
   else existSid = checkSid(act_sid); //verify the input student ID is on the record
    } while(!existSid);
  return;
 }

 //display student main menu
 public static void showMenu(){
  System.out.printf("\n\n%30s", "Student Menu:");
  System.out.print("\n\nPlease enter your choice as following(one letter)\n\n");
  System.out.print("\nL --List: lists all records in the course table\n");
  System.out.print("\nE --Enroll: enrolls the current active student in a course\n");
  System.out.print("\nW --Withdraw: withdraw a course for current active student\n");
  System.out.print("\nS --Search: find courses matching input\n");
  System.out.print("\nM --My Classes: lists all classes enrolled in by current active student\n");
  System.out.print("\nX --Exit: exit application\n");
  System.out.print("\n Please enter your choice : \n"); 
 }

 //get user choice
 public static boolean userChoice(){
 Scanner input = new Scanner(System.in);
 char choice = Character.toLowerCase(input.nextLine().charAt(0));
 switch(choice){
 case 'l': listCourseTable();
   return true;
 case 'e': enroll();
   return true;
 case 'w': withdraw();
   return true;
 case 's': searchCourse();
   return true;
 case 'm': myClasses();
   return true;
 case 'x': System.out.print("\n\nGood By!\n\n");
   return false;
 default: System.out.println("\nWrong input, please try again!\n");
   return true;
 }
 }
 
 public static void listCourseTable(){
 try{
  Connection reConn = reConnection(username, password);
  Statement stmt = reConn.createStatement();
  ResultSet rs = stmt.executeQuery("SELECT * FROM courses");
  System.out.printf("\n%60s","Listing all records in the Courses table\n");
  System.out.println("------------------------------------------------------------");
  while(rs.next()){
  System.out.printf("courseID: " + "%10d"
      + "    course name: " + "%23s"
      + "    credits: "+"%3d\n",
      rs.getInt("cid"), rs.getString("cname"),rs.getInt("credits") );

  }
  System.out.println("------------------------------------------------------------");
  } catch (SQLException e) {
   System.out.println ("ERROR OCCURRED");
   e.printStackTrace();}
  return;
 }

//check existing courseID
 public static boolean checkCid(int new_cid){
  try{
   Connection reConn = reConnection(username, password);
   Statement stmt = reConn.createStatement();
   ResultSet rs = stmt.executeQuery("SELECT cid FROM enrolled" + " WHERE sid = "+ act_sid + " AND cid = " + new_cid);
   if (rs.next())
   {System.out.println("Course enrolled on record");
   return true;}
    else return false;
     }catch (SQLException e) {
    System.out.println ("ERROR OCCURRED");
    e.printStackTrace();
    return false;
    }
 }

 //method for course enrollment
 public static void enroll(){
 System.out.println("\nPlease enter a course ID to enroll:");
 Scanner input = new Scanner(System.in);
int new_cid;
 //check conflicts
 do{
 new_cid = input.nextInt();
 }while(checkCid(new_cid));

 //add course enrollemnt
 try {
  Connection reConn = reConnection(username, password);
  String insertStudent = "INSERT INTO enrolled (sid, cid) VALUES (?,?)";
  PreparedStatement pstmt = reConn.prepareStatement(insertStudent);
  pstmt.setInt(1,act_sid);
  pstmt.setInt(2,new_cid);
  pstmt.executeUpdate();
  }catch (SQLException e) {
    System.out.println ("ERROR OCCURRED, Adding failed.");
    e.printStackTrace();}

  System.out.println ("New course enrollment succeeded.");
 return;
 }

 //method for withdraw course
 public static void withdraw(){
 System.out.println("\nPlease enter a course ID to withdraw, -1 to cancel: ");
 Scanner input = new Scanner(System.in);

 //check existing cid
 int new_cid = input.nextInt();
 if (new_cid == -1) return; //cancel withdraw
 while(!checkCid(new_cid)){
  System.out.println("Not enrolled. Please enter an enrolled course ID to withdraw,-1 to cancel:");
  new_cid = input.nextInt();
  if (new_cid == -1) return;//cancel withdraw
 };

 //delete course from enrollemnt
 try {
  Connection reConn = reConnection(username, password);
  String insertStudent = "DELETE FROM enrolled WHERE sid=? AND cid=?";
  PreparedStatement pstmt = reConn.prepareStatement(insertStudent);
  pstmt.setInt(1,act_sid);
  pstmt.setInt(2,new_cid);
  pstmt.executeUpdate();
  }catch (SQLException e) {
    System.out.println ("ERROR OCCURRED, deleting failed.");
    e.printStackTrace();}
  System.out.println ("withdraw succeeded.");
 return;
 }

 //search for Course
 public static void searchCourse(){
 System.out.println("\nPlease enter part of the course you are interested: ");
 Scanner input = new Scanner(System.in);
 String searchName = input.nextLine();
 String searchStr = "SELECT * FROM coursess WHERE cname like ?";
 try{
  Connection reConn = reConnection(username, password);
  PreparedStatement pstmt = reConn.prepareStatement(searchStr);
  pstmt.setString(1, "%" + searchName + "%");
  ResultSet rs = pstmt.executeQuery(searchStr);
  System.out.printf("\n%60s","Listing all matching Courses for search term\n");
  System.out.println("------------------------------------------------------------");
  while(rs.next()){
  System.out.printf("courseID: " + "%10d"
      + "    course name: " + "%23s"
      + "    credits: "+"%3d\n",
      rs.getInt("cid"), rs.getString("cname"),rs.getInt("credits") );
  }
  System.out.println("------------------------------------------------------------");
  } catch (SQLException e) {
   System.out.println ("ERROR OCCURRED");
   e.printStackTrace();}
  return;
 }

 //lists all classes enrolled in by the active student
 public static void myClasses(){
 System.out.println("lists all classes enrolled in by the current active student");
 System.out.println("----------------------------------------------------------------------");
 String queryStr = "SELECT C.cid, C.cname, C.credits FROM courses C, enrolled E WHERE C.cid = E.cid AND E.sid = ";
 try{
  Connection reConn = reConnection(username, password);
  Statement stmt = reConn.createStatement();
  ResultSet rs = stmt.executeQuery(queryStr + act_sid );
   while(rs.next())
   {System.out.printf("courseID: " + "%10d"
      + "    course name: " + "%23s"
      + "    credits: "+"%3d\n",
      rs.getInt("cid"), rs.getString("cname"),rs.getInt("credits") ); }
  System.out.println("----------------------------------------------------------------------");
     }catch (SQLException e) {
    System.out.println ("ERROR OCCURRED");
   e.printStackTrace();}
 }
}
