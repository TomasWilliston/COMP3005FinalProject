package org.example;

import java.sql.*;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    String url = "jdbc:postgresql://localhost:5432/3005FinalProject";
    String username = "postgres";
    String password = "postgres";

    public static void main(String[] args) {
        new Main().createTables();
        new Main().registerMember();
        new Main().updateProfile();
        new Main().healthHistory();
        new Main().PTSchedule();
        new Main().setAvailability();
        new Main().scheduleView();
        new Main().roomBooking();
        new Main().maintenanceLog();
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
                            "M_ID" integer NOT NULL,
                            "timestamp" timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            heart_rate integer,
                            weight integer,
                            PRIMARY KEY ("M_ID", "timestamp"),
                            FOREIGN KEY ("M_ID")
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
                            "ID" serial NOT NULL,
                            first_name text NOT NULL,
                            last_name text NOT NULL,
                            password text NOT NULL,
                            PRIMARY KEY ("ID")
                        )""");
            }

            tables = dbm.getTables(null, null, "staff", null);
            table_exists = tables.next();

            if (!table_exists) {
                stmt.executeUpdate("""
                        CREATE TABLE public.staff
                        (
                            "ID" serial NOT NULL,
                            first_name text NOT NULL,
                            last_name text NOT NULL,
                            password text NOT NULL,
                            PRIMARY KEY ("ID")
                        )""");
            }

            tables = dbm.getTables(null, null, "rooms", null);
            table_exists = tables.next();

            if (!table_exists) {
                stmt.executeUpdate("""
                        CREATE TABLE public.rooms
                        (
                            "number" integer NOT NULL,
                            PRIMARY KEY ("number"),
                            UNIQUE ("number")
                        )""");
            }

            tables = dbm.getTables(null, null, "maintenance_logs", null);
            table_exists = tables.next();

            if (!table_exists) {
                stmt.executeUpdate("""
                        CREATE TABLE public.maintenance_logs
                        (
                            "ID" serial NOT NULL,
                            details text NOT NULL,
                            room_number integer,
                            status text NOT NULL,
                            "S_ID" integer,
                            PRIMARY KEY ("ID"),
                            FOREIGN KEY ("S_ID")
                                REFERENCES public.staff ("ID") MATCH SIMPLE
                                ON UPDATE CASCADE
                                ON DELETE NO ACTION
                                NOT VALID,
                            FOREIGN KEY (room_number)
                                REFERENCES public.rooms ("number") MATCH SIMPLE
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
                            "T_ID" integer NOT NULL,
                            start_time timestamp without time zone NOT NULL,
                            "M_ID" integer,
                            room integer,
                            PRIMARY KEY ("T_ID", start_time),
                            FOREIGN KEY ("T_ID")
                                REFERENCES public.trainers ("ID") MATCH SIMPLE
                                ON UPDATE CASCADE
                                ON DELETE CASCADE
                                NOT VALID,
                            FOREIGN KEY ("M_ID")
                                REFERENCES public.members (id) MATCH SIMPLE
                                ON UPDATE CASCADE
                                ON DELETE SET NULL
                                NOT VALID,
                            FOREIGN KEY (room)
                                REFERENCES public.rooms ("number") MATCH SIMPLE
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
                            "T_ID" integer NOT NULL,
                            "time" timestamp without time zone NOT NULL,
                            PRIMARY KEY ("T_ID", "time")
                        )""");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void registerMember() {
        System.out.println("Unimplemented: registerMember");
    }

    void updateProfile() {
        System.out.println("Unimplemented: updateProfile");
    }

    void healthHistory() {
        System.out.println("Unimplemented: healthHistory");
    }

    void PTSchedule() {
        System.out.println("Unimplemented: PTSchedule");
    }

    void setAvailability() {
        System.out.println("Unimplemented: setAvailability");
    }

    void scheduleView() {
        System.out.println("Unimplemented: scheduleView");
    }

    void roomBooking() {
        System.out.println("Unimplemented: roomBooking");
    }

    void maintenanceLog() {
        System.out.println("Unimplemented: maintenanceLog");
    }
}