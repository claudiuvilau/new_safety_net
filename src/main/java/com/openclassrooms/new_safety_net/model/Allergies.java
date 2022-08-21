package com.openclassrooms.new_safety_net.model;

import java.util.ArrayList;
import java.util.List;

public class Allergies {

    private List<String> allergie = new ArrayList<>();

    public Allergies() {

    }

    public Allergies(List<String> allergies) {
        super();
        this.allergie = allergies;
    }

    public List<String> getAllergies() {
        return allergie;
    }

    public void setAllergies(List<String> allergies) {
        this.allergie = allergies;
    }

    @Override
    public String toString() {
        return "Allergies [allergies=" + allergie + "]";
    }

}