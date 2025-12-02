package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class Member {
    private final int id;
    String url = "jdbc:postgresql://localhost:5432/3005FinalProject";
    String username = "postgres";
    String password = "postgres";
    public Member(int id) {
        this.id = id;
    }

    public void userInterface() {

        Scanner sc = new Scanner(System.in);
        String input = "";
        while(!input.equals("Logout")) {
            System.out.println("Member ID " + id + ": \nUpdate your profile (Profile), enter new health metrics (Health), " +
                    "schedule a training session (Training) or log out (Logout)");
            input = sc.nextLine();
            if(input.equals("Logout")) {
                System.out.println("Logging out...\n");
            } else if(input.equals("Profile")) {
                updateProfile();
            } else if(input.equals("Health")) {
                healthHistory();
            } else if(input.equals("Training")) {
                schedulePT();
            } else {
                System.out.println("Invalid Input");
            }
        }
    }

    void updateProfile() {
        Scanner sc = new Scanner(System.in);
        String input = "";

        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM members WHERE id = " + id);
            rs.next();
            String firstname = rs.getString("first_name");
            String lastName = rs.getString("last_name");
            String email = rs.getString("email");
            String password = rs.getString("password");
            System.out.println("Current details: \nFirst name:" + firstname + "\nLast name:" + lastName + "\nEmail:" +
                    email + "\nPassword:" + password + "\n What would you like to update? (FName, LName, Email, Password)");
            input = sc.nextLine();
            if(input.equals("FName")) {
                System.out.println("Please enter your first name");
                firstname = sc.nextLine();
                stmt.executeUpdate("UPDATE members SET first_name = '" + firstname + "' WHERE id = " + id);
            } else if(input.equals("LName")) {
                System.out.println("Please enter your last name");
                lastName = sc.nextLine();
                stmt.executeUpdate("UPDATE members SET last_name = '" + lastName + "' WHERE id = " + id);
            } else if(input.equals("Email")) {
                System.out.println("Please enter your email");
                email = sc.nextLine();
                stmt.executeUpdate("UPDATE members SET email = '" + email + "' WHERE id = " + id);
            } else if(input.equals("Password")) {
                System.out.println("Please enter your password");
                password = sc.nextLine();
                stmt.executeUpdate("UPDATE members SET password = '" + password + "' WHERE id = " + id);
            }
            else {
                System.out.println("Invalid Input");
                return;
            }
            System.out.println("Profile updated successfully\n");
        } catch (Exception e) {
            System.out.println("Update Profile Error");
        }
    }

    void healthHistory() {
        Scanner sc = new Scanner(System.in);
        String input = "";
        System.out.println("Please enter heart rate");
        int heart = sc.nextInt();
        while(heart < 0) {
            System.out.println("Invalid Input");
            heart = sc.nextInt();
        }
        System.out.println("Please enter your weight");
        int weight = sc.nextInt();
        while(weight < 0) {
            System.out.println("Invalid Input");
            weight = sc.nextInt();
        }
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO health_logs (m_id, heart_rate, weight) VALUES ("+id+","+heart+","+weight+")");
            System.out.println("Successfully updated health record\n");
        } catch (Exception e) {
            System.out.println("Health History Error");
        }
    }

    void schedulePT() {
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            Statement stmt = conn.createStatement();

            Scanner sc = new Scanner(System.in);
            System.out.println("What day would you like to book? (YYYY-MM-DD)");
            String day = sc.nextLine();

            ResultSet rs = stmt.executeQuery("SELECT first_name, last_name, start_time, end_time FROM " +
                    "(SELECT * FROM training_sessions WHERE date = '"+day+"' AND room IS NOT null AND m_id ISNULL ) s JOIN trainers t on s.t_id = t.id;");
            if(rs.next()) {
                System.out.println("Here are the available training sessions for that day:");
                int counter = 1;
                System.out.println(counter + " " + rs.getString("first_name") + " " +
                        rs.getString("last_name") + " " + rs.getString("start_time") + "-" + rs.getString("end_time"));
                counter++;
                while(rs.next()) {
                    System.out.println(counter + " " + rs.getString("first_name") + " " +
                            rs.getString("last_name") + " " + rs.getString("start_time") + "-" + rs.getString("end_time"));
                    counter++;
                }
                System.out.println("Please enter the id of the session you would like to book (Number on the left)");
                int id = sc.nextInt();

                rs = stmt.executeQuery("SELECT t_id, start_time FROM training_sessions WHERE date = '"+day+"' AND room IS NOT null AND m_id ISNULL;");

                for (int i = 0; i < id; i++) {
                    rs.next();
                }

                stmt.executeUpdate("UPDATE training_sessions SET m_id = " + id + " WHERE start_time = '" + rs.getString("start_time") +
                        "' AND date = '" + day + "' AND t_id = " + rs.getInt("t_id"));

                System.out.println("Successfully updated training session\n");

            } else {
                System.out.println("There are no available training sessions for that day");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }
}
