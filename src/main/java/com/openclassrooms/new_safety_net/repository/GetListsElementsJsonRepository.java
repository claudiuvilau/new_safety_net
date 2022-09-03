package com.openclassrooms.new_safety_net.repository;

import java.io.IOException;
import java.util.List;

import com.openclassrooms.new_safety_net.model.Firestations;
import com.openclassrooms.new_safety_net.model.Medicalrecords;
import com.openclassrooms.new_safety_net.model.Persons;

public interface GetListsElementsJsonRepository {

    public List<Persons> getPersons(String elemjson) throws IOException;

    public List<Firestations> getFirestations(String elemjson);

    public List<Medicalrecords> getMedicalrecords(String elemjson);

}