package com.openclassrooms.new_safety_net.model;

import java.util.ArrayList;
import java.util.List;

public class Medications {

    private List<String> listmedications = new ArrayList<>();

    public Medications() {

    }

    public Medications(List<String> medications) {
        super();
        this.listmedications = medications;
    }

    public List<String> getMedications() {
        return listmedications;
    }

    public void setMedications(List<String> medications) {
        this.listmedications = medications;
    }

    @Override
    public String toString() {
        return "Medications [medications=" + listmedications + "]";
    }

}