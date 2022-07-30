package com.openclassrooms.new_safety_net.model;

public class ChildrenOrAdults {

    private String firstName;
    private String lastName;
    private String old;

    public ChildrenOrAdults() {

    }

    public ChildrenOrAdults(String firstName, String lastName, String old) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.old = old;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOld() {
        return old;
    }

    public void setOld(String old) {
        this.old = old;
    }

    @Override
    public String toString() {
        return "ChildrenOrAdults [firstName=" + firstName + ", lastName=" + lastName + ", old=" + old
                + "]";
    }

}