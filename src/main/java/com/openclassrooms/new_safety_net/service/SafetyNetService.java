package com.openclassrooms.new_safety_net.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import com.openclassrooms.new_safety_net.model.JsonToFile;
import com.openclassrooms.new_safety_net.model.Persons;
import com.openclassrooms.new_safety_net.repository.SafetyNetRepository;

import lombok.Data;

@Data
@Service
public class SafetyNetService implements SafetyNetRepository {

    // @Autowired
    // pourquoi pas de Autowired ?
    private JsonToFile jsonToFile;
    private Persons persons;

    @Override
    public void getPerson() throws IOException {
        byte[] bytesFile = jsonToFile.readJsonFile();
        persons = JsonIterator.deserialize(bytesFile, Persons.class);
        JsonStream.serialize(persons);
    }

    @Override
    public void postPerson(String nomperson) {
        // TODO Auto-generated method stub

    }

    @Override
    public void putPerson(String nomperson) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deletePerson(String nomperson) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getFirestation(String firestation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void postFirestation(String firestation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void putFirestation(String firestation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteFirestation(String firestation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getMedicalRecord(String medicalrecord) {
        // TODO Auto-generated method stub

    }

    @Override
    public void postMedicalRecord(String medicalrecord) {
        // TODO Auto-generated method stub

    }

    @Override
    public void putMedicalRecord(String medicalrecord) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteMedicalRecord(String medicalrecord) {
        // TODO Auto-generated method stub

    }

}