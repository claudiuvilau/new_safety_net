package com.openclassrooms.new_safety_net.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.openclassrooms.new_safety_net.model.Firestations;
import com.openclassrooms.new_safety_net.model.Medicalrecords;
import com.openclassrooms.new_safety_net.model.Persons;
import com.openclassrooms.new_safety_net.repository.GetListsElementsJsonRepository;

import lombok.Data;

@Data
@Service
public class GetListsElementsJson implements GetListsElementsJsonRepository {

    // Récupération de notre logger.
    private static final Logger LOGGER = LogManager.getLogger(GetListsElementsJson.class);

    @Autowired
    private ObjetFromJson objetFromJson;

    @Autowired
    private Persons personsObj;

    @Autowired
    private Firestations firestationsObj;

    @Autowired
    private Medicalrecords medicalrecordsObj;

    @Override
    public List<Persons> getPersons(String elemjson) {
        List<Persons> listPersons = new ArrayList<>();

        objetFromJson.anyAny(elemjson);

        if (objetFromJson.getAny() != null) {
            Any personsAny = objetFromJson.getAny().get(elemjson);
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
    public List<Firestations> getFirestations(String elemjson) {
        List<Firestations> listFirestations = new ArrayList<>();

        objetFromJson.anyAny(elemjson);

        if (objetFromJson.getAny() != null) {
            Any firestationsAny = objetFromJson.getAny().get(elemjson);
            for (Any element : firestationsAny) {
                // transforming the json in java object Firestations.class
                firestationsObj = JsonIterator.deserialize(element.toString(), Firestations.class);
                listFirestations.add(firestationsObj);
            }
            String messageLogger = "";
            messageLogger = "List firestations is created: " + listFirestations;
            LOGGER.debug(messageLogger);
        }
        return listFirestations;
    }

    @Override
    public List<Medicalrecords> getMedicalrecords(String elemjson) {
        List<Medicalrecords> listMedicalrecords = new ArrayList<>();

        objetFromJson.anyAny(elemjson);

        if (objetFromJson.getAny() != null) {
            Any medicalrecordsAny = objetFromJson.getAny().get(elemjson);
            for (Any element : medicalrecordsAny) {
                // transforming the json in java object Firestations.class
                medicalrecordsObj = JsonIterator.deserialize(element.toString(), Medicalrecords.class);
                listMedicalrecords.add(medicalrecordsObj);
            }
            LOGGER.debug("List medicalrecords is created.");
        }
        return listMedicalrecords;
    }

}