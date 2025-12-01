package org.example;

public class Member {
    private final int id;

    public Member(int id) {
        this.id = id;
    }

    public void userInteface() {
        System.out.println("UI goes here");
    }

    void updateProfile() {
        System.out.println("Unimplemented: updateProfile");
    }

    void healthHistory() {
        System.out.println("Unimplemented: healthHistory");
    }

    void SchedulePT() {
        System.out.println("Unimplemented: SchedulePT");
    }
}
