package com.openclassrooms.new_safety_net.service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.jsoniter.output.JsonStream;
import com.openclassrooms.new_safety_net.model.CollectionsRessources;
import com.openclassrooms.new_safety_net.model.Firestations;
import com.openclassrooms.new_safety_net.model.JsonToFile;
import com.openclassrooms.new_safety_net.model.Medicalrecords;
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
    private Medicalrecords medicalrecordsObj = new Medicalrecords();

    // Récupération de notre logger.
    private static final Logger LOGGER = LogManager.getLogger(SafetyNetService.class);

    LoggerApiNewSafetyNet loggerApiNewSafetyNet = new LoggerApiNewSafetyNet();

    private String messageLogger = "";

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

    @Override
    public List<Medicalrecords> getMedicalrecords(String elemjson) {
        List<Medicalrecords> listMedicalrecords = new ArrayList<>();
        Any any = null;

        any = anyAny(elemjson, any);

        if (any != null) {
            Any medicalrecordsAny = any.get(elemjson);
            for (Any element : medicalrecordsAny) {
                // transforming the json in java object Firestations.class
                medicalrecordsObj = JsonIterator.deserialize(element.toString(), Medicalrecords.class);
                listMedicalrecords.add(medicalrecordsObj);
            }
            LOGGER.debug("List medicalrecords is created.");
        }
        return listMedicalrecords;
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

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(loggerApiNewSafetyNet.loggerDebug(persons.toString()));
        }

        // read the Json File
        List<Persons> listPersons;
        listPersons = getPersons("persons");

        if (LOGGER.isDebugEnabled()) {
            messageLogger = "The persons are: " + listPersons;
            LOGGER.debug(messageLogger);
        }

        // verify if the persons is exist in the persons if not = add
        boolean findpersons = false;
        for (Persons element : listPersons) {
            if ((element.getFirstName() + element.getLastName())
                    .equals(persons.getFirstName() + persons.getLastName())) {
                findpersons = true;

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("This person is already in the list.");
                }
                break;
            }
        }

        if (!findpersons) {
            // create the new file json
            persons = createdNewFileJson(listPersons, persons);
            return persons;
        }

        return null;
    }

    private Persons createdNewFileJson(List<Persons> listPersons, Persons persons) {

        // add the persons if find_persons is false
        listPersons.add(persons); // add the body

        if (LOGGER.isDebugEnabled()) {
            messageLogger = "The person is added in the list: " + listPersons;
            LOGGER.debug(messageLogger);
        }

        // create list fire stations
        List<Firestations> listFirestations;
        listFirestations = getFirestations("firestations");
        // create list medical records
        List<Medicalrecords> listMedicalrecords;
        listMedicalrecords = getMedicalrecords("medicalrecords");

        CollectionsRessources collectionsRessources = new CollectionsRessources();
        collectionsRessources.setPersons(listPersons);
        collectionsRessources.setFirestations(listFirestations);
        collectionsRessources.setMedicalrecords(listMedicalrecords);

        String jsonstream = JsonStream.serialize(collectionsRessources); // here we transform the list in json
                                                                         // object
        FileWriter writer = null;
        JsonToFile fileJson = new JsonToFile();
        try {
            writer = new FileWriter(fileJson.filepathjson);
        } catch (IOException e) {
            LOGGER.error(loggerApiNewSafetyNet.loggerErr(e, persons.toString()));
            return null;
        }
        try {
            writer.write(jsonstream);
        } catch (IOException e) {
            LOGGER.error(loggerApiNewSafetyNet.loggerErr(e, persons.toString()));
        }
        try {
            writer.flush();
        } catch (IOException e) {
            LOGGER.error(loggerApiNewSafetyNet.loggerErr(e, persons.toString()));
        }
        try {
            writer.close();
        } catch (IOException e) {
            LOGGER.error(loggerApiNewSafetyNet.loggerErr(e, persons.toString()));
            return null;
        }

        if (LOGGER.isDebugEnabled()) {
            messageLogger = "The new file is writed: " + jsonstream;
            LOGGER.debug(messageLogger);
        }
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