package com.openclassrooms.new_safety_net.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.openclassrooms.new_safety_net.model.Firestations;
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
    private Persons personsObj = new Persons();
    private Firestations firestationsObj = new Firestations();

    // Récupération de notre logger.
    private static final Logger LOGGER = LogManager.getLogger(SafetyNetService.class);

    // pourquoi Override ?
    @Override
    public List<Persons> getPersons(String elemjson) {
        List<Persons> listPersons = new ArrayList<>();
        Any any = null;

        any = anyAny(elemjson, any);

        if (any != null) {
            Any personsAny = any.get(elemjson);
            for (Any element : personsAny) {
                // transforming the json in java object Persons.class
                personsObj = JsonIterator.deserialize(element.toString(), Persons.class);
                listPersons.add(personsObj);
            }
            LOGGER.debug("List persons is created.");
        }
        return listPersons;
    }

    @Override
    public List<Persons> getAPerson(String firstNamelastName, String elemjson) throws IOException {
        List<Persons> listPersons = new ArrayList<>();
        List<Persons> listP = listPersons;
        listPersons = getPersons(elemjson); // here we have a list of objects Persons from json

        for (Persons element : listPersons) {
            if ((element.getFirstName().trim() + element.getLastName().trim()).equalsIgnoreCase(firstNamelastName)) {
                listP.add(element);
            }
        }
        LOGGER.debug("Get the person is ok.");
        return listP;
    }

    @Override
    public List<Firestations> getFirestations(String elemjson) {
        List<Firestations> listFirestations = new ArrayList<>();
        Any any = null;

        any = anyAny(elemjson, any);

        if (any != null) {
            Any firestationsAny = any.get(elemjson);
            for (Any element : firestationsAny) {
                // transforming the json in java object Firestations.class
                firestationsObj = JsonIterator.deserialize(element.toString(), Firestations.class);
                listFirestations.add(firestationsObj);
            }
            LOGGER.debug("List firestations is created.");
        }
        return listFirestations;
    }

    private Any anyAny(String elemjson, Any any) {
        byte[] objetfile = null;
        objetfile = jsonToFile.readJsonFile();
        if (objetfile != null) {
            JsonIterator iter = JsonIterator.parse(objetfile);
            if (iter.currentBuffer().contains(elemjson)) {
                try {
                    any = iter.readAny();
                    LOGGER.debug("Json iterator is created.");
                } catch (IOException e) {
                    LOGGER.debug("No objet Java from Json !");
                }
            }
        }
        return any;
    }

    @Override
    public Persons postPerson(Persons persons) {
        return persons;
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