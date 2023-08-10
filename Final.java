import java.util.*;
import java.sql.*;

public class Final {
    int AccNo,balance;
    String name;

    static ResultSet res;
    static Scanner sc = new Scanner(System.in);
    static Connection conn;

    static {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank management system","root","root123");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static Statement statement;

    static {
        try {
            statement = conn.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public int exists() throws SQLException {
        System.out.println("Create Account = 1\n Log Into Existing Account = 2\n Enter your choice  :");
        int choice = Integer.parseInt(sc.nextLine());
        if (choice == 1){
            openAcc();
            return 1;
        }else if (choice == 2){
            logIn();
            return 2;
        }else{
            return exists();
        }
    }

    public static String newName(){
        String name;
        System.out.print("Enter your name  : ");
        name = sc.nextLine();
        System.out.print("Confirm name  :");
        if (name.trim().equals(sc.nextLine().trim())){
            return name;
        }else{
            System.out.println("Names do not match");
            return newName();
        }
    }
    public static String createPass(){
        String pass,check;
        System.out.print("Enter your password  : ");
        pass = sc.nextLine();
        System.out.print("Re-enter your password  : ");
        check = sc.nextLine();
        if (!check.equals(pass)){
            System.out.println("Passwords do not match");
            return createPass();
        }return pass;
    }
    public static int getBal(){
        int balance;
        System.out.print("Enter initial balance  (more than or equal to 1000) : ");
        balance = Integer.parseInt(sc.nextLine());
        if (balance>=1000){
            return balance;
        }return getBal();

    }public boolean passCheck() throws SQLException {
        System.out.println("Enter your password  : ");
        String pass = sc.nextLine();
        String stmt = String.format("SELECT Pass FROM `Database` WHERE AccNo=%d",this.AccNo);
        ResultSet res = statement.executeQuery(stmt);
        res.next();
        if (res.getString(1).trim().equals(pass.trim())){
            return true;
        }else{
            return passCheck();
        }
    }


    public void openAcc() throws SQLException {
        String name=newName();
        int count;
        String stmt="SELECT MAX(AccNo) FROM `Database`";
        res = statement.executeQuery(stmt);
        res.next();
        count = res.getInt(1);
        int AccNo = count+1;
        String pass=createPass();
        int balance = getBal();
        stmt = String.format("INSERT INTO `Database` VALUES (%d,'%s','%s',%d)",AccNo,name,pass,balance);
        statement.executeUpdate(stmt);
        System.out.println("Account Created Successfully");
        System.out.println("Your Account number is  :" + AccNo);
        this.name = name;
        this.AccNo = AccNo;
        this.balance = balance;
        exists();
    }


    public void getDetails() throws SQLException {
        String stmt = String.format("SELECT * FROM `Database` WHERE AccNo=%d",this.AccNo);
        res = statement.executeQuery(stmt);
        res.next();
        this.balance = res.getInt(4);
        this.name = res.getString(2);
    }


    public void printDetails() throws SQLException {
        System.out.println("Account Number  : "+this.AccNo);
        System.out.println("Name            : "+this.name);
        System.out.println("Balance         : "+this.balance);
        choice();
    }


    public void logIn() throws SQLException {
        System.out.println("Enter your account number  : ");
        int AccNo = Integer.parseInt(sc.nextLine());
        String stmt = String.format("SELECT COUNT(AccNo) FROM `Database` WHERE AccNo = %d",AccNo);
        res = statement.executeQuery(stmt);
        res.next();
        if (res.getInt(1)!=1){
            System.out.println("Account number does not exist");
            exists();
        }
        this.AccNo = AccNo;
        if (this.passCheck()){
            getDetails();
            System.out.println("You are now Logged In As "+this.name);
            choice();
        }else{
            System.out.println("Login Failed");
            System.out.println("Do you want to try again (1 = Yes, 0 = No)");
            if (Integer.parseInt(sc.nextLine())==1){
                logIn();
            }else{
                System.out.println("Thank You");
            }
        }
    }



    public void withDraw() throws SQLException {
        System.out.print("Enter amount to be withdrawn  : ");
        int amount=Integer.parseInt(sc.nextLine());
        int updated = this.balance - amount;
        if (updated>=1000){
            String stmt = String.format("UPDATE `Database` SET Balance = %d WHERE AccNo=%d",updated,this.AccNo);
            statement.executeUpdate(stmt);
            System.out.println("Withdrawal Successful");
            printDetails();
            choice();
        }else{
            System.out.println("Not Enough balance");
            choice();
        }
    }

    public void deposit() throws SQLException {
        System.out.print("Enter the amount to be deposited  :");
        int amount = Integer.parseInt(sc.nextLine());
        int updated = amount + this.balance;
        String stmt = String.format("UPDATE `Database` SET Balance = %d WHERE AccNo=%d",updated,this.AccNo);
        statement.executeUpdate(stmt);
        System.out.println("Deposition successful");
        printDetails();
        choice();
    }

    public void choice() throws SQLException {
        System.out.println("Which operation do you want to perform\n1=Withdraw\n2=Deposit\n3=View Details\n4 = Exit");
        int choice = Integer.parseInt(sc.nextLine());
        if (choice == 1){
            withDraw();
        }else if (choice == 2){
            deposit();
        }else if (choice == 3){
            printDetails();
        }else if (choice == 4){
            System.out.println("Thank You");
        }else{
            System.out.println("Invalid choice");
            choice();
        }
    }

    public static void main(String[] args) throws SQLException{
        Final person = new Final();
        person.exists();
    }
}