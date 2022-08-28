package com.openclassrooms.new_safety_net.repository;

import java.io.IOException;
import java.util.List;

import com.openclassrooms.new_safety_net.model.Firestations;
import com.openclassrooms.new_safety_net.model.Medicalrecords;
import com.openclassrooms.new_safety_net.model.Persons;

public interface NewFileJsonRepository {

    public void createNewFileJson(List<Persons> listPersons, List<Firestations> listFirestations,
            List<Medicalrecords> listMedicalrecords, String param) throws IOException;

}