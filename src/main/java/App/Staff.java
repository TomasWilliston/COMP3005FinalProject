package App;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class Staff {
    private final int id;
    String url = "jdbc:postgresql://localhost:5432/3005FinalProject";
    String username = "postgres";
    String password = "postgres";
    public Staff(int id) {
        this.id = id;
    }

    public void userInterface() {
        Scanner sc = new Scanner(System.in);
        String input = "";
        while(!input.equals("Logout")) {
            System.out.println("Staff ID " + id + ": \n Book rooms for training sessions (Book), manage maintenance logs (Maintenance), or log out (Logout)");
            input = sc.nextLine();
            if(input.equals("Logout")) {
                System.out.println("Logging out...\n");
            } else if(input.equals("Book")) {
                roomBooking();
            } else if(input.equals("Maintenance")) {
                maintenanceLog();
            } else {
                System.out.println("Invalid Input");
            }
        }
    }

    private void roomBooking() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Here are the training sessions without assigned rooms:");
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT first_name, last_name, j.date, time, end_time FROM " +
                    "(unassigned_sessions u JOIN trainers t on t.id = u.t_id) j\n" +
                    "    JOIN training_sessions s on s.t_id = j.id and s.date = j.date and s.start_time = j.time");
            int counter = 1;
            while(rs.next()) {
                System.out.println(counter + " "+ rs.getString("first_name") + " " + rs.getString("last_name")
                        + " - " + rs.getString("date") + " " + rs.getString("time") + "-" + rs.getString("end_time"));
                counter++;
            }
            System.out.println("Which session would you like to assign a room? (Number on the left)");
            int input = sc.nextInt();

            rs = stmt.executeQuery("SELECT t.t_id, t.date, time, t.end_time FROM training_sessions t " +
                    "JOIN unassigned_sessions u on u.t_id = t.t_id and u.time = t.start_time and u.date = t.date;");

            for (int i = 0; i < input; i++) {
                rs.next();
            }

            String date = rs.getString("date");
            String time = rs.getString("time");
            String endTime = rs.getString("end_time");
            int t_id = rs.getInt("t_id");

            System.out.println("What room would you like to assign?");
            input = sc.nextInt();

            rs = stmt.executeQuery("SELECT * FROM training_sessions WHERE t_id = "+id+" AND '" +
                    date + "' = date and room = "+input+" and ('"+time+"' between start_time " +
                    "and end_time or '"+endTime+"' between start_time and end_time);");
            boolean valid = !rs.next();
            if(valid) {
                stmt.executeUpdate("UPDATE training_sessions SET room = " + input + " WHERE t_id = " + t_id + " and " +
                        "date = '" + date + "' and training_sessions.start_time = '" + time + "';");

                stmt.executeUpdate("DELETE FROM unassigned_sessions WHERE t_id = " + t_id + " and " +
                        "date = '" + date + "' and time = '" + time + "';");
                System.out.println("Successfully assigned room " + input + "\n");
            } else {
                System.out.println("This room is already booked at that time.\n");
                System.out.println(rs.getString("date"));
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void maintenanceLog() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Would you like to create a new entry (Create) or update one (Update)?");
        String input = "";
        while(!input.equals("Create") && !input.equals("Update")) {
            input = sc.nextLine();
            if(input.equals("Create")) {
                System.out.println("Enter Room number:");
                String roomNumber = sc.nextLine();
                System.out.println("Enter details about the entry:");
                String details = sc.nextLine();
                System.out.println("Enter current repair status:");
                String repairStatus = sc.nextLine();
                try {
                    Class.forName("org.postgresql.Driver");
                    Connection conn = DriverManager.getConnection(url, username, password);
                    Statement stmt = conn.createStatement();

                    stmt.executeUpdate("INSERT INTO maintenance_logs (details, room_number, status, s_id) VALUES " +
                            "('"+details+"',"+roomNumber+",'"+repairStatus+"',"+id+")");
                    ResultSet rs = stmt.executeQuery("SELECT MAX(id) FROM maintenance_logs");
                    rs.next();
                    System.out.println("New entry created with log id "+rs.getString("max")+"\n");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            } else if(input.equals("Update")) {
                System.out.println("Enter log ID:");
                String log_id = sc.nextLine();

                try {
                    Class.forName("org.postgresql.Driver");
                    Connection conn = DriverManager.getConnection(url, username, password);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("select * from maintenance_logs where id = " + log_id);
                    boolean exists = rs.next();
                    if(exists) {
                        System.out.println("Current Details:");
                        System.out.println("Description: " + rs.getString("details"));
                        System.out.println("Repair status: " + rs.getString("status"));
                        System.out.println("Room number: " + rs.getString("room_number"));
                        System.out.println("Responsible staff ID: " + rs.getString("s_id"));

                        System.out.println("Enter new status:");
                        String new_status = sc.nextLine();
                        stmt.executeUpdate("UPDATE maintenance_logs SET status = '" + new_status + "' WHERE id = " + log_id);
                        System.out.println("Status successfully updated!\n");
                    } else  {
                        System.out.println("No entry found");
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else {
                System.out.println("Invalid Input");
            }
        }
    }
}
