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
    JsonToFile fileJson = new JsonToFile();

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
    public boolean postPerson(Persons person) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(loggerApiNewSafetyNet.loggerDebug(person.toString()));
        }

        // read the Json File
        List<Persons> listPersons;
        listPersons = getPersons("persons");

        if (LOGGER.isDebugEnabled()) {
            messageLogger = "The persons are: " + listPersons;
            LOGGER.debug(messageLogger);
        }

        // verify if the persons is exist in the persons if not = add
        boolean findperson = false;
        for (Persons element : listPersons) {
            if ((element.getFirstName() + element.getLastName())
                    .equals(person.getFirstName() + person.getLastName())) {
                findperson = true;

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("This person is already in the list.");
                }
                break;
            }
        }

        if (!findperson) {

            Boolean filecreated = false;
            // add the persons if find_persons is false
            listPersons.add(person); // add the body

            if (LOGGER.isDebugEnabled()) {
                messageLogger = "The person is added in the list: " + listPersons;
                LOGGER.debug(messageLogger);
            }

            // create the new file json

            // create list fire stations
            List<Firestations> listFirestations;
            listFirestations = getFirestations("firestations");
            // create list medical records
            List<Medicalrecords> listMedicalrecords;
            listMedicalrecords = getMedicalrecords("medicalrecords");
            filecreated = createNewFileJson(listPersons, listFirestations, listMedicalrecords, person);
            return filecreated;
        }

        return false;
    }

    @Override
    public boolean updatePerson(Persons person, String firstName, String lastName) {

        String elemjson = "persons";

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(loggerApiNewSafetyNet.loggerDebug(firstName + " " + lastName));
        }

        List<Persons> listPersons;
        listPersons = getPersons(elemjson);

        if (LOGGER.isDebugEnabled()) {
            messageLogger = "The list of all persons is: " + listPersons;
            LOGGER.debug(messageLogger);
        }

        // find the person and update
        String firstNamelastName = firstName + lastName;
        boolean updated;

        updated = updatePersonFinded(person, listPersons, firstNamelastName);

        return updated;

    }

    private boolean updatePersonFinded(Persons person, List<Persons> listPersons, String firstNamelastName) {

        for (Persons element : listPersons) {
            if ((element.getFirstName() + element.getLastName()).equalsIgnoreCase(firstNamelastName)) {
                verifyAndUpdate(element, person);

                Boolean filecreated = false;

                if (LOGGER.isDebugEnabled()) {
                    messageLogger = "The list is updated. This is the list updated: " + listPersons;
                    LOGGER.debug(messageLogger);
                    messageLogger = "The person is : " + person;
                    LOGGER.debug(messageLogger);

                }

                // create the new file json
                // create list fire stations
                List<Firestations> listFirestations;
                listFirestations = getFirestations("firestations");
                // create list medical records
                List<Medicalrecords> listMedicalrecords;
                listMedicalrecords = getMedicalrecords("medicalrecords");
                filecreated = createNewFileJson(listPersons, listFirestations, listMedicalrecords, person);
                return filecreated;
            }
        }
        return false;
    }

    private void verifyAndUpdate(Persons element, Persons persons) {
        if (persons.getAddress() != null) {
            element.setAddress(persons.getAddress());
        }
        if (persons.getCity() != null) {
            element.setCity(persons.getCity());
        }
        if (persons.getEmail() != null) {
            element.setEmail(persons.getEmail());
        }
        if (persons.getPhone() != null) {
            element.setPhone(persons.getPhone());
        }
        if (persons.getZip() != null) {
            element.setZip(persons.getZip());
        }

    }

    private boolean createNewFileJson(List<Persons> listPersons, List<Firestations> listFirestations,
            List<Medicalrecords> listMedicalrecords, Persons persons) {

        CollectionsRessources collectionsRessources = new CollectionsRessources();
        collectionsRessources.setPersons(listPersons);
        collectionsRessources.setFirestations(listFirestations);
        collectionsRessources.setMedicalrecords(listMedicalrecords);

        String jsonstream = JsonStream.serialize(collectionsRessources); // here we transform the list in json
                                                                         // object
        FileWriter writer = null;
        try {
            writer = new FileWriter(fileJson.filepathjson);
        } catch (IOException e) {
            LOGGER.error(loggerApiNewSafetyNet.loggerErr(e, persons.toString()));
            return false;
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
            return false;
        }

        if (LOGGER.isDebugEnabled()) {
            messageLogger = "The new file is writed: " + jsonstream;
            LOGGER.debug(messageLogger);
        }
        return true;
    }

    @Override
    public boolean deletePerson(String firstName, String lastName) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(loggerApiNewSafetyNet.loggerDebug(firstName + " " + lastName));
        }

        String elemjson = "persons";
        List<Persons> listPersons;
        listPersons = getPersons(elemjson);

        if (LOGGER.isDebugEnabled()) {
            messageLogger = "The list of all persons is: " + listPersons;
            LOGGER.debug(messageLogger);
        }

        // find the person and delete
        String firstNamelastName = firstName + lastName;
        for (Persons element : listPersons) {
            if ((element.getFirstName() + element.getLastName()).equals(firstNamelastName)) {

                listPersons.remove(element);

                if (LOGGER.isDebugEnabled()) {
                    messageLogger = "The person is deleted: " + listPersons;
                    LOGGER.debug(messageLogger);
                }

                Boolean filecreated = false;

                // create the new file json
                // create list fire stations
                List<Firestations> listFirestations;
                listFirestations = getFirestations("firestations");
                // create list medical records
                List<Medicalrecords> listMedicalrecords;
                listMedicalrecords = getMedicalrecords("medicalrecords");
                filecreated = createNewFileJson(listPersons, listFirestations, listMedicalrecords, element);
                return filecreated;
            }
        }
        return false;
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