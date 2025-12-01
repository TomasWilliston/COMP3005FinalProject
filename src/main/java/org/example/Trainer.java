package org.example;

import java.sql.ResultSet;
import java.util.Scanner;

public class Trainer {

    private final int id;

    public Trainer(int id) {
        this.id = id;
    }

    public void userInteface() {
        Scanner sc = new Scanner(System.in);
        String input = "";
        while(!input.equals("Logout")) {
            System.out.println("Trainer ID " + id + ": \nSet availability (Availability), view your schedule (Schedule), or log out (Logout)");
            input = sc.nextLine();
            if(input.equals("Logout")) {
                System.out.println("Logging out...\n");
            } else if(input.equals("Availability")) {
                setAvailability();
            } else if(input.equals("Schedule")) {
                scheduleView();
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
            String in_time = sc.nextLine();
            System.out.println("Enter the duration this session in minutes:");
            String in_duration = sc.nextLine();

            System.out.println("Would you like to set another available time? (Yes/No)");
            input = sc.nextLine();
        }

    }

    private void scheduleView() {
        System.out.println("Unimplemented: scheduleView");
    }
}
