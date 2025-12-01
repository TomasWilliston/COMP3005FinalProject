package org.example;

import java.sql.*;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    String url = "jdbc:postgresql://localhost:5432/3005FinalProject";
    String username = "postgres";
    String password = "postgres";

    public static void main(String[] args) {
        Main main = new Main();
        main.createTables();
        main.populateTables();
        main.login();
    }

    public void createTables() {
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
                            t_id integer NOT NULL,
                            date date NOT NULL,
                            time time(0) without time zone NOT NULL,
                            PRIMARY KEY (t_id, date, time)
                        )""");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void login() {
        Scanner sc = new Scanner(System.in);
        String input = "";
        ResultSet rs = null;

        while(!Objects.equals(input, "Exit")) {
            System.out.print("Select a user type to log in (Member, Trainer, or Staff), register a new member (Register), or quit the program (Exit).\n");
            input = sc.nextLine();
            if(input.equals("Exit")) {
                //ends loop
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
                            member.userInteface();
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
                            trainer.userInteface();
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
                            staff.userInteface();
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

    void populateTables() {
        System.out.println("Unimplemented: populateTables");
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