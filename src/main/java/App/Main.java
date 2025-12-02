package App;

import java.sql.*;
import java.util.Scanner;

public class Main {

    String url = "jdbc:postgresql://localhost:5432/3005FinalProject";
    String username = "postgres";
    String password = "postgres";

    public static void main(String[] args) {
        Main main = new Main();
        main.setup();
        main.login();
    }

    public void setup() {
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            Statement stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();

            ResultSet tables = dbm.getTables(null, null, "members", null);
            boolean table_exists = tables.next();

            if (!table_exists) {
                stmt.executeUpdate("""
                        CREATE TABLE public.members
                        (
                            id serial,
                            first_name text NOT NULL,
                            last_name text NOT NULL,
                            email text NOT NULL,
                            password text NOT NULL,
                            PRIMARY KEY (id),
                            UNIQUE (email)
                        )""");

                stmt.executeUpdate("insert into members (first_name, last_name, email, password) " +
                        "values ('Tomas', 'Williston', 'TW@example.com', 'password')");

                stmt.executeUpdate("insert into members (first_name, last_name, email, password) " +
                        "values ('Fake', 'Person', 'fake@email.com', 'password2')");

            }

            tables = dbm.getTables(null, null, "health_logs", null);
            table_exists = tables.next();



            if (!table_exists) {
                stmt.executeUpdate("""
                        CREATE TABLE public.health_logs
                        (
                            m_id integer NOT NULL,
                            time timestamp(0) without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            heart_rate integer,
                            weight integer,
                            PRIMARY KEY (m_id, time),
                            FOREIGN KEY (m_id)
                                REFERENCES public.members (id) MATCH SIMPLE
                                ON UPDATE CASCADE
                                ON DELETE CASCADE
                                NOT VALID
                        )""");

                stmt.executeUpdate("insert into health_logs (m_id, heart_rate, weight) " +
                        "values (1, 100, 80)");
            }

            tables = dbm.getTables(null, null, "trainers", null);
            table_exists = tables.next();

            if (!table_exists) {
                stmt.executeUpdate("""
                        CREATE TABLE public.trainers
                        (
                            id serial NOT NULL,
                            first_name text NOT NULL,
                            last_name text NOT NULL,
                            password text NOT NULL,
                            PRIMARY KEY (id)
                        )""");


                stmt.executeUpdate("insert into trainers (first_name, last_name, password) " +
                        "values ('John', 'Trainer', 'I<3training')");

                stmt.executeUpdate("insert into trainers (first_name, last_name, password) " +
                        "values ('Jane', 'Trainer', 'trainingTime')");

            }

            tables = dbm.getTables(null, null, "staff", null);
            table_exists = tables.next();

            if (!table_exists) {
                stmt.executeUpdate("""
                        CREATE TABLE public.staff
                        (
                            id serial NOT NULL,
                            first_name text NOT NULL,
                            last_name text NOT NULL,
                            password text NOT NULL,
                            PRIMARY KEY (id)
                        )""");

                stmt.executeUpdate("insert into staff (first_name, last_name, password) " +
                        "values ('Bob', 'Staff',  'admin')");

            }

            tables = dbm.getTables(null, null, "rooms", null);
            table_exists = tables.next();

            if (!table_exists) {
                stmt.executeUpdate("""
                        CREATE TABLE public.rooms
                        (
                            r_number integer NOT NULL,
                            PRIMARY KEY (r_number),
                            UNIQUE (r_number)
                        )""");

                stmt.executeUpdate("insert into rooms " +
                        "values (1)");

            }

            tables = dbm.getTables(null, null, "maintenance_logs", null);
            table_exists = tables.next();

            if (!table_exists) {
                stmt.executeUpdate("""
                        CREATE TABLE public.maintenance_logs
                        (
                            id serial NOT NULL,
                            details text NOT NULL,
                            room_number integer,
                            status text NOT NULL,
                            s_id integer,
                            PRIMARY KEY (id),
                            FOREIGN KEY (s_id)
                                REFERENCES public.staff (id) MATCH SIMPLE
                                ON UPDATE CASCADE
                                ON DELETE NO ACTION
                                NOT VALID,
                            FOREIGN KEY (room_number)
                                REFERENCES public.rooms (r_number) MATCH SIMPLE
                                ON UPDATE NO ACTION
                                ON DELETE NO ACTION
                                NOT VALID
                        )""");
            }

            tables = dbm.getTables(null, null, "training_sessions", null);
            table_exists = tables.next();

            if (!table_exists) {
                stmt.executeUpdate("""
                        CREATE TABLE public.training_sessions
                        (
                            t_id integer NOT NULL,
                            date date NOT NULL,
                            start_time time(0) without time zone NOT NULL,
                            end_time time(0) without time zone NOT NULL,
                            m_id integer,
                            room integer,
                            PRIMARY KEY (t_id, date, start_time),
                            FOREIGN KEY (t_id)
                                REFERENCES public.trainers (id) MATCH SIMPLE
                                ON UPDATE CASCADE
                                ON DELETE CASCADE
                                NOT VALID,
                            FOREIGN KEY (m_id)
                                REFERENCES public.members (id) MATCH SIMPLE
                                ON UPDATE CASCADE
                                ON DELETE SET NULL
                                NOT VALID,
                            FOREIGN KEY (room)
                                REFERENCES public.rooms (r_number) MATCH SIMPLE
                                ON UPDATE CASCADE
                                ON DELETE SET NULL
                                NOT VALID
                        )""");
            }

            tables = dbm.getTables(null, null, "unassigned_sessions", null);
            table_exists = tables.next();

            if (!table_exists) {
                stmt.executeUpdate("""
                        CREATE TABLE public.unassigned_sessions
                        (
                            t_id integer,
                            date date,
                            "time" time(0) without time zone,
                            PRIMARY KEY (t_id, date, "time"),
                            FOREIGN KEY (t_id, date, "time")
                                REFERENCES public.training_sessions (t_id, date, start_time) MATCH SIMPLE
                                ON UPDATE NO ACTION
                                ON DELETE NO ACTION
                                NOT VALID
                        )""");
            }

            tables = dbm.getTables(null, null, "user_summary", null);
            table_exists = tables.next();

            if (!table_exists) {
                stmt.executeUpdate("""
                    CREATE OR REPLACE VIEW public.user_summary
                        AS
                        SELECT id, first_name, last_name, heart_rate AS latest_hr, weight AS latest_w FROM members t JOIN (SELECT h.m_id,
                               h.heart_rate,
                               h.weight,
                            m."time"
                           FROM ( SELECT health_logs.m_id,
                            max(health_logs."time") AS "time"
                           FROM health_logs
                          GROUP BY health_logs.m_id) m
                           JOIN health_logs h ON h.m_id = m.m_id AND m."time" = h."time"
                    ) f ON t.id = f.m_id;""");
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void login() {
        Scanner sc = new Scanner(System.in);
        String input = "";
        ResultSet rs = null;

        while(!input.equals("Exit")) {
            System.out.println("Select a user type to log in (Member, Trainer, or Staff), register a new member (Register), or quit the program (Exit).");
            input = sc.nextLine();
            if(input.equals("Exit")) {
                System.out.println("Exiting program...");
            } else if(input.equals("Register")) {
                registerMember();
            } else if(input.equals("Member")) {
                System.out.print("Enter ID: ");
                String  id = sc.nextLine();
                try {
                    Class.forName("org.postgresql.Driver");
                    Connection conn = DriverManager.getConnection(url, username, password);
                    Statement stmt = conn.createStatement();

                    //search for member with that id
                    rs = stmt.executeQuery("select * from members where id = " + id);
                    boolean valid = rs.next();

                    if(!valid) {
                        System.out.println("Invalid ID");
                    } else {
                        System.out.print("Enter Password: ");
                        String  password = sc.nextLine();

                        //check password
                        if (password.equals(rs.getString("password"))) {

                            //run member UI
                            Member member = new Member(Integer.parseInt(id));
                            member.userInterface();
                        } else {
                            System.out.println("Wrong password!");
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if(input.equals("Trainer")) {
                System.out.print("Enter ID: ");
                String  id = sc.nextLine();
                try {
                    Class.forName("org.postgresql.Driver");
                    Connection conn = DriverManager.getConnection(url, username, password);
                    Statement stmt = conn.createStatement();

                    //search for trainer with that id
                    rs = stmt.executeQuery("select * from trainers where id = " + id);
                    boolean valid = rs.next();

                    if(!valid) {
                        System.out.println("Invalid ID");
                    } else {
                        System.out.print("Enter Password: ");
                        String  password = sc.nextLine();

                        //check password
                        if (password.equals(rs.getString("password"))) {

                            //run trainer UI
                            Trainer trainer = new Trainer(Integer.parseInt(id));
                            trainer.userInterface();
                        } else {
                            System.out.println("Wrong password!");
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if(input.equals("Staff")) {

                System.out.print("Enter ID: ");
                String  id = sc.nextLine();
                try {
                    Class.forName("org.postgresql.Driver");
                    Connection conn = DriverManager.getConnection(url, username, password);
                    Statement stmt = conn.createStatement();

                    //search for trainer with that id
                    rs = stmt.executeQuery("select * from staff where id = " + id);
                    boolean valid = rs.next();

                    if(!valid) {
                        System.out.println("Invalid ID");
                    } else {
                        System.out.print("Enter Password: ");
                        String  password = sc.nextLine();

                        //check password
                        if (password.equals(rs.getString("password"))) {

                            //run staff UI
                            Staff staff = new Staff(Integer.parseInt(id));
                            staff.userInterface();
                        } else {
                            System.out.println("Wrong password!");
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("Invalid input");
            }
        }
    }


    void registerMember() {
        Scanner sc = new Scanner(System.in);

        System.out.println("Please enter your email: ");
        String email = sc.nextLine();

        System.out.println("Please enter your password: ");
        String pass = sc.nextLine();

        System.out.println("Please enter your first name: ");
        String first_name = sc.nextLine();

        System.out.println("Please enter your last name: ");
        String last_name = sc.nextLine();

        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            //prepared statement formatted to insert the appropriate values
            PreparedStatement pstmt = conn.prepareStatement("insert into members (first_name, last_name, email, password) values (?, ?, ?, ?)");

            //set values
            pstmt.setString(1, first_name);
            pstmt.setString(2, last_name);
            pstmt.setString(3, email);
            pstmt.setString(4, pass);

            //execute statement
            pstmt.executeUpdate();
            System.out.println("Member successfully registered!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }



    }








}