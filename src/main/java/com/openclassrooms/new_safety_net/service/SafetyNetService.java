package com.openclassrooms.new_safety_net.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.openclassrooms.new_safety_net.model.JsonToFile;
import com.openclassrooms.new_safety_net.model.Persons;
import com.openclassrooms.new_safety_net.repository.SafetyNetRepository;

import lombok.Data;

@Data
@Service
public class SafetyNetService implements SafetyNetRepository {

    // @Autowired
    // pourquoi pas de Autowired ?
    private JsonToFile jsonToFile = new JsonToFile();
    private Persons persons = new Persons();

    // pourquoi Override ?
    @Override
    public List<Persons> getPerson() throws IOException {
        List<Persons> listPersons = new ArrayList<>();
        byte[] objetfile = null;
        objetfile = jsonToFile.readJsonFile();
        JsonIterator iter = JsonIterator.parse(objetfile);
        Any any = null;
        try {
            any = iter.readAny();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (any != null) {
            Any personsAny = any.get("persons");
            for (Any element : personsAny) {
                // transforming the json in java object
                persons = JsonIterator.deserialize(element.toString(), Persons.class);
                listPersons.add(persons);
            }
        }
        return listPersons;
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