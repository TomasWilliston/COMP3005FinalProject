package App;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class Trainer {

    private final int id;
    String url = "jdbc:postgresql://localhost:5432/3005FinalProject";
    String username = "postgres";
    String password = "postgres";
    public Trainer(int id) {
        this.id = id;
    }

    public void userInterface() {
        Scanner sc = new Scanner(System.in);
        String input = "";
        while(!input.equals("Logout")) {
            System.out.println("Trainer ID " + id + ": \nSet availability (Availability), view a member's information? (Info), or log out (Logout)");
            input = sc.nextLine();
            if(input.equals("Logout")) {
                System.out.println("Logging out...\n");
            } else if(input.equals("Availability")) {
                setAvailability();
            } else if(input.equals("Info")) {
                memberView();
            } else {
                System.out.println("Invalid Input");
            }
        }

    }

    private void setAvailability() {
        Scanner sc = new Scanner(System.in);
        String input = "Yes";
        ResultSet rs = null;
        while(input.equals("Yes")) {
            System.out.println("Enter the date for which you want to create an available session (YYYY-MM-DD):");
            String in_date = sc.nextLine();
            System.out.println("Enter the start time for this session (HH:mm):");
            String in_start = sc.nextLine();
            System.out.println("Enter the end time for this session (HH:mm):");
            String in_end = sc.nextLine();
            try {
                Class.forName("org.postgresql.Driver");
                Connection conn = DriverManager.getConnection(url, username, password);
                Statement stmt = conn.createStatement();
                rs =  stmt.executeQuery("SELECT * FROM training_sessions WHERE t_id = "+id+" AND '" +
                        in_date + "' = date and ('"+in_start+"' between start_time and end_time or '"+in_end+"' between start_time and end_time);");
                boolean valid = !rs.next();
                if(valid) {
                    stmt.executeUpdate("INSERT INTO training_sessions (t_id, date, start_time, end_time) VALUES (" +
                            id+",'"+in_date+"','"+in_start+"','"+in_end+"');");
                    System.out.println("Session Created");
                } else {
                    System.out.println("Invalid time");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }


            System.out.println("Would you like to set another available time? (Yes/No)");
            input = sc.nextLine();
        }

    }

    private void memberView() {
        Scanner sc = new Scanner(System.in);
        System.out.println("What is the first name of the member you wish to view?");
        String firstName = sc.nextLine();
        System.out.println("What is the last name of the member you wish to view?");
        String lastName = sc.nextLine();
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM user_summary WHERE UPPER(first_name) LIKE UPPER('"+firstName+"') " +
                    "AND UPPER(last_name) LIKE UPPER('"+lastName+"');");
            boolean valid = rs.next();
            if(valid) {
                System.out.println("Here are the members with that name:");
                System.out.print(rs.getString("first_name") + " ");
                System.out.print(rs.getString("last_name")+": \nLatest heart rate: ");
                System.out.print(rs.getString("latest_hr")+"\nLatest weight: ");
                System.out.println(rs.getString("latest_w"));
                while(rs.next()) {
                    System.out.print(rs.getString("first_name") + " ");
                    System.out.print(rs.getString("last_name")+": \nLatest heart rate: ");
                    System.out.print(rs.getString("latest_hr")+"\nLatest weight: ");
                    System.out.println(rs.getString("latest_w"));
                }
            } else {
                System.out.println("No members with that name found.");
            }
            System.out.println();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
