package org.example;

public class Staff {
    private final int id;

    public Staff(int id) {
        this.id = id;
    }

    public void userInteface() {
        System.out.println("UI goes here");
    }

    private void roomBooking() {
        System.out.println("Unimplemented: roomBooking");
    }

    private void maintenanceLog() {
        System.out.println("Unimplemented: maintenanceLog");
    }
}
