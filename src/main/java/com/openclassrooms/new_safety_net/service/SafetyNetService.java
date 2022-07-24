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
            filecreated = createNewFileJson(listPersons, listFirestations, listMedicalrecords, person.toString());
            return filecreated;
        }

        return false;
    }

    @Override
    public boolean putPerson(Persons person, String firstName, String lastName) {

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
                verifyAndUpdatePerson(element, person);

                Boolean filecreated = false;

                if (LOGGER.isDebugEnabled()) {
                    messageLogger = "The persons is updateds. This is the list updated: " + listPersons;
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
                filecreated = createNewFileJson(listPersons, listFirestations, listMedicalrecords, person.toString());
                return filecreated;
            }
        }
        return false;
    }

    private void verifyAndUpdatePerson(Persons element, Persons person) {

        // no update for the first and last name :
        setNamePerson(element, person);

        if (person.getAddress() != null) {
            setElementAddress(element, person);
        }
        if (person.getCity() != null) {
            setElementCity(element, person);
        }
        if (person.getEmail() != null) {
            setElementEmail(element, person);
        }
        if (person.getPhone() != null) {
            setElementPhone(element, person);
        }
        if (person.getZip() != null) {
            setElementZip(element, person);
        }
    }

    private void setNamePerson(Persons element, Persons person) {
        person.setFirstName("No update for the firsts names ! (" + element.getFirstName() + ")");
        person.setLastName("No update for the lasts names ! (" + element.getLastName() + ")");
    }

    private void setElementAddress(Persons element, Persons person) {
        if (element.getAddress().equals(person.getAddress())) {
            person.setAddress("No update ! the same address : " + element.getAddress());
        } else
            element.setAddress(person.getAddress());
    }

    private void setElementCity(Persons element, Persons person) {
        if (element.getCity().equals(person.getCity())) {
            person.setCity("No update ! the same city : " + element.getCity());
        } else
            element.setCity(person.getCity());
    }

    private void setElementEmail(Persons element, Persons person) {
        if (element.getEmail().equals(person.getEmail())) {
            person.setEmail("No update ! the same e-mail : " + element.getEmail());
        } else
            element.setEmail(person.getEmail());
    }

    private void setElementPhone(Persons element, Persons person) {
        if (element.getPhone().equals(person.getPhone())) {
            person.setPhone("No update ! the same phone : " + element.getPhone());
        } else
            element.setPhone(person.getPhone());
    }

    private void setElementZip(Persons element, Persons person) {
        if (element.getZip().equals(person.getZip())) {
            person.setZip("No update ! the same zip : " + element.getZip());
        } else
            element.setZip(person.getZip());
    }

    private boolean createNewFileJson(List<Persons> listPersons, List<Firestations> listFirestations,
            List<Medicalrecords> listMedicalrecords, String param) {

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
            LOGGER.error(loggerApiNewSafetyNet.loggerErr(e, param));
            return false;
        }
        try {
            writer.write(jsonstream);
        } catch (IOException e) {
            LOGGER.error(loggerApiNewSafetyNet.loggerErr(e, param));
        }
        try {
            writer.flush();
        } catch (IOException e) {
            LOGGER.error(loggerApiNewSafetyNet.loggerErr(e, param));
        }
        try {
            writer.close();
        } catch (IOException e) {
            LOGGER.error(loggerApiNewSafetyNet.loggerErr(e, param));
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
                filecreated = createNewFileJson(listPersons, listFirestations, listMedicalrecords, element.toString());
                return filecreated;
            }
        }
        return false;
    }

    @Override
    public boolean postFirestation(Firestations firestation) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(loggerApiNewSafetyNet.loggerDebug(firestation.toString()));
        }
        // read the Json File
        List<Firestations> listFirestations;
        listFirestations = getFirestations("firestations");
        if (LOGGER.isDebugEnabled()) {
            messageLogger = "The list of all fire stations is: " + listFirestations;
            LOGGER.debug(messageLogger);
        }
        // verify if the findfirestation is exist in the findfirestations if not = add
        boolean findfirestation = false;
        for (Firestations element : listFirestations) {
            if (element.getAddress().equals(firestation.getAddress())) {
                findfirestation = true;
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("This fire station is already in the list.");
                }
                break;
            }
        }
        if (!findfirestation) {
            Boolean filecreated = false;
            // add the firestation if findfirestation is false
            listFirestations.add(firestation); // add the body

            if (LOGGER.isDebugEnabled()) {
                messageLogger = "The fire station is added in the list: " + listFirestations;
                LOGGER.debug(messageLogger);
            }
            // create the new file json
            // create list fire stations
            List<Persons> listPersons;
            listPersons = getPersons("persons");
            // create list medical records
            List<Medicalrecords> listMedicalrecords;
            listMedicalrecords = getMedicalrecords("medicalrecords");
            filecreated = createNewFileJson(listPersons, listFirestations, listMedicalrecords, firestation.toString());
            return filecreated;
        }
        return false;
    }

    @Override
    public boolean putFirestation(Firestations firestation, String address) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(loggerApiNewSafetyNet.loggerDebug(address));
        }

        List<Firestations> listFirestations;
        listFirestations = getFirestations("firestations");

        if (LOGGER.isDebugEnabled()) {
            messageLogger = "The list of all fire stations is: " + listFirestations;
            LOGGER.debug(messageLogger);
        }
        // find the fire station and update
        boolean updated;
        updated = updateFirestationFinded(firestation, listFirestations, address);

        return updated;

    }

    private boolean updateFirestationFinded(Firestations firestation, List<Firestations> listFirestations,
            String address) {

        for (Firestations element : listFirestations) {
            if (element.getAddress().equalsIgnoreCase(address)) {
                verifyAndUpdateFirestation(element, firestation);

                Boolean filecreated = false;

                if (LOGGER.isDebugEnabled()) {
                    messageLogger = "The fire stations is updateds. This is the list updated: " + listFirestations;
                    LOGGER.debug(messageLogger);
                    messageLogger = "The fire station is : " + firestation;
                    LOGGER.debug(messageLogger);

                }

                // create the new file json
                // create persons
                List<Persons> listPersons;
                listPersons = getPersons("persons");
                // create list medical records
                List<Medicalrecords> listMedicalrecords;
                listMedicalrecords = getMedicalrecords("medicalrecords");
                filecreated = createNewFileJson(listPersons, listFirestations, listMedicalrecords,
                        firestation.toString());
                return filecreated;
            }
        }
        return false;
    }

    private void verifyAndUpdateFirestation(Firestations element, Firestations firestation) {

        firestation.setAddress("No update for the addresses ! (" + element.getAddress() + ")");

        if (firestation.getStation() != null) {
            if (element.getStation().equals(firestation.getStation())) {
                firestation.setStation("No update ! the same station : " + element.getStation());
            } else
                element.setStation(firestation.getStation());
        }
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