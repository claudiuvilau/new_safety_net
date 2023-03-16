package com.openclassrooms.new_safety_net.model;

import org.springframework.stereotype.Component;

@Component
public class Firestations {

    private String address;
    private String station;

    public Firestations() {

    }

    public Firestations(String address, String station) {
        super();
        this.address = address;
        this.station = station;
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
     * @return String return the station
     */
    public String getStation() {
        return station;
    }

    /**
     * @param station the station to set
     */
    public void setStation(String station) {
        this.station = station;
    }

    @Override
    public String toString() {
        return "Firestations [address=" + address + ", station=" + station + "]";
    }

}