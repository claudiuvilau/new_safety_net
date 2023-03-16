package com.openclassrooms.new_safety_net.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class FoyerChildrenAdultsToFireStation {

    private String decompte;
    List<PersonsOfFireStation> listPersonsOfFireStations = new ArrayList<>();

    public FoyerChildrenAdultsToFireStation() {
    }

    public FoyerChildrenAdultsToFireStation(String decompte, List<PersonsOfFireStation> listPersonsOfFireStations) {
        this.decompte = decompte;
        this.listPersonsOfFireStations = listPersonsOfFireStations;
    }

    public String getDecompte() {
        return decompte;
    }

    public void setDecompte(String decompte) {
        this.decompte = decompte;
    }

    public List<PersonsOfFireStation> getlistPersonsOfFireStations() {
        return listPersonsOfFireStations;
    }

    public void setlistPersonsOfFireStations(List<PersonsOfFireStation> listPersonsOfFireStations) {
        this.listPersonsOfFireStations = listPersonsOfFireStations;
    }

    @Override
    public String toString() {
        return "FoyerChildrenAdultsToFireStation [decompte=" + decompte + ", listPersonsOfFireStations="
                + listPersonsOfFireStations + "]";
    }

}