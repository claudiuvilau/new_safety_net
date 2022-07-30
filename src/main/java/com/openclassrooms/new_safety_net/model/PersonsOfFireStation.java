package com.openclassrooms.new_safety_net.model;

public class PersonsOfFireStation {

    private String decompte;
    private String firstName;
    private String lastName;
    private String address;
    private String phone;

    public PersonsOfFireStation() {
    }

    public PersonsOfFireStation(String decompte, String firstName, String lastName, String address, String phone) {
        this.decompte = decompte;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "PersonsOfFireStation [address=" + address + ", decompte=" + decompte + ", firstName=" + firstName
                + ", lastName=" + lastName + ", phone=" + phone + "]";
    }

    /**
     * @return String return the decompte
     */
    public String getDecompte() {
        return decompte;
    }

    /**
     * @param decompte the decompte to set
     */
    public void setDecompte(String decompte) {
        this.decompte = decompte;
    }

    /**
     * @return String return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return String return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return String return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return String return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

}