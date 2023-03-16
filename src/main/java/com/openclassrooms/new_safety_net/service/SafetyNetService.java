package com.openclassrooms.new_safety_net.service;

import java.io.IOException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openclassrooms.new_safety_net.model.AddressListFirestation;
import com.openclassrooms.new_safety_net.model.ChildAlert;
import com.openclassrooms.new_safety_net.model.ChildrenOrAdults;
import com.openclassrooms.new_safety_net.model.CommunityEmail;
import com.openclassrooms.new_safety_net.model.FireAddress;
import com.openclassrooms.new_safety_net.model.Firestations;
import com.openclassrooms.new_safety_net.model.FoyerChildrenAdultsToFireStation;
import com.openclassrooms.new_safety_net.model.Medicalrecords;
import com.openclassrooms.new_safety_net.model.PersonInfo;
import com.openclassrooms.new_safety_net.model.Persons;
import com.openclassrooms.new_safety_net.model.PersonsFireStation;
import com.openclassrooms.new_safety_net.model.PersonsOfFireStation;
import com.openclassrooms.new_safety_net.model.PhoneAlert;
import com.openclassrooms.new_safety_net.repository.SafetyNetInterface;

import lombok.Data;

@Data
@Service
public class SafetyNetService implements SafetyNetInterface {

    @Autowired
    private Persons personsObj;

    @Autowired
    private Firestations firestationsObj;

    @Autowired
    private Medicalrecords medicalrecordsObj;

    @Autowired
    private LoggerApiNewSafetyNet loggerApiNewSafetyNet;

    @Autowired
    private ObjetFromJson objetFromJson;

    @Autowired
    private NewFileJson newFileJson;

    @Autowired
    private GetListsElementsJson getListsElementsJsonRepository;

    @Autowired
    FireAddress fireAddress;

    @Autowired
    FoyerChildrenAdultsToFireStation foyerChildrenAdultsToFireStation;

    @Autowired
    ChildrenOrAdults childrenOrAdults;

    @Autowired
    ChildAlert childAlertobj;

    @Autowired
    PhoneAlert phoneAlertObj;

    @Autowired
    PersonsFireStation personsFireStation;

    @Autowired
    AddressListFirestation addressListFirestationObj;

    // Récupération de notre logger.
    private static final Logger LOGGER = LogManager.getLogger(SafetyNetService.class);

    private static final String MSGLISTFIRESTATION = "The list of all fire stations is: ";
    private static final String ELEMJSONPERSONS = "persons";
    private static final String ELEMJSONFIRESTATIONS = "firestations";
    private static final String ELEMJSONMEDICALRECORDS = "medicalrecords";
    private static final int CHILDOLD = 17; // 17 max age for the children.
    private String messageLogger = "";

    @Override
    public List<Persons> getAPerson(String firstNamelastName, String elemjson) throws IOException {
        List<Persons> listPersons = new ArrayList<>();
        List<Persons> listP = listPersons;
        listPersons = getListsElementsJsonRepository.getPersons(elemjson); // here we have a list of objects Persons
                                                                           // from json

        for (Persons element : listPersons) {
            if ((element.getFirstName().trim() + element.getLastName().trim()).equalsIgnoreCase(firstNamelastName)) {
                listP.add(element);
            }
        }
        LOGGER.debug("Get the person is ok.");
        return listP;
    }

    @Override
    public List<Persons> postPerson(Persons person) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(loggerApiNewSafetyNet.loggerDebug(person.toString()));
        }

        // read the Json File
        List<Persons> listPersons;
        listPersons = createListPersons();

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
            listFirestations = createListFirestations();
            // create list medical records
            List<Medicalrecords> listMedicalrecords;
            listMedicalrecords = createListMedicalrecords();
            newFileJson.createNewFileJson(listPersons, listFirestations, listMedicalrecords, person.toString());
            filecreated = newFileJson.isFileCreated();
            if (Boolean.TRUE.equals(filecreated)) { // return the person from file
                List<Persons> listP;
                listP = verifyIfTheNewPersonInTheFile(person);
                return listP;
            }
        }
        return Collections.emptyList();
    }

    private List<Persons> verifyIfTheNewPersonInTheFile(Persons person) {
        List<Persons> listP = new ArrayList<>();
        try {
            listP = getAPerson(person.getFirstName() + person.getLastName(), ELEMJSONPERSONS);
            if (!listP.isEmpty()) {
                return listP;
            }
        } catch (IOException e) {
            return Collections.emptyList();
        }
        return Collections.emptyList();
    }

    @Override
    public boolean putPerson(Persons person, String firstName, String lastName) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(loggerApiNewSafetyNet.loggerDebug(firstName + " " + lastName));
        }

        List<Persons> listPersons;
        listPersons = createListPersons();

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
                listFirestations = createListFirestations();
                // create list medical records
                List<Medicalrecords> listMedicalrecords;
                listMedicalrecords = createListMedicalrecords();
                newFileJson.createNewFileJson(listPersons, listFirestations, listMedicalrecords, person.toString());
                filecreated = newFileJson.isFileCreated();
                return filecreated;
            }
        }
        return false;
    }

    @Override
    public boolean deletePerson(String firstName, String lastName) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(loggerApiNewSafetyNet.loggerDebug(firstName + " " + lastName));
        }

        List<Persons> listPersons;
        listPersons = createListPersons();

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
                listFirestations = createListFirestations();
                // create list medical records
                List<Medicalrecords> listMedicalrecords;
                listMedicalrecords = createListMedicalrecords();
                newFileJson.createNewFileJson(listPersons, listFirestations, listMedicalrecords, element.toString());
                filecreated = newFileJson.isFileCreated();
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
        listFirestations = createListFirestations();
        if (LOGGER.isDebugEnabled()) {
            messageLogger = MSGLISTFIRESTATION + listFirestations;
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
            listPersons = createListPersons();
            // create list medical records
            List<Medicalrecords> listMedicalrecords;
            listMedicalrecords = createListMedicalrecords();
            newFileJson.createNewFileJson(listPersons, listFirestations, listMedicalrecords, firestation.toString());
            filecreated = newFileJson.isFileCreated();
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
        listFirestations = createListFirestations();

        if (LOGGER.isDebugEnabled()) {
            messageLogger = MSGLISTFIRESTATION + listFirestations;
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
                listPersons = createListPersons();
                // create list medical records
                List<Medicalrecords> listMedicalrecords;
                listMedicalrecords = createListMedicalrecords();
                newFileJson.createNewFileJson(listPersons, listFirestations, listMedicalrecords,
                        firestation.toString());
                filecreated = newFileJson.isFileCreated();
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
    public boolean deleteFirestation(String address, String stationNumber) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(loggerApiNewSafetyNet.loggerDebug(stationNumber));
        }

        // if only one request parameter is used
        if ((address != null && stationNumber == null) || (address == null && stationNumber != null)) {

            List<Firestations> listF;
            List<Firestations> listFirestations = new ArrayList<>();
            listF = createListFirestations();

            if (LOGGER.isDebugEnabled()) {
                messageLogger = MSGLISTFIRESTATION + listF;
                LOGGER.debug(messageLogger);
            }

            // if there are the address in URI
            if (address != null) {
                verifyIfAddressInURI(address, listF, listFirestations);
            }
            // if there are station number in URI
            if (stationNumber != null) {
                verifyIfStationNumberInURI(stationNumber, listF, listFirestations);
            }

            if (LOGGER.isDebugEnabled()) {
                messageLogger = "The fire station is deleted: " + listFirestations.toString();
                LOGGER.debug(messageLogger);
            }

            // create persons
            List<Persons> listPersons;
            listPersons = createListPersons();
            // create medical records
            List<Medicalrecords> listMedicalrecords;
            listMedicalrecords = createListMedicalrecords();
            Boolean filecreated = false;
            newFileJson.createNewFileJson(listPersons, listFirestations, listMedicalrecords,
                    address + " " + stationNumber);
            filecreated = newFileJson.isFileCreated();
            return filecreated;
        }
        return false;

    }

    private void verifyIfAddressInURI(String address, List<Firestations> listF, List<Firestations> listFirestations) {
        Firestations firestations;
        for (Firestations element : listF) {
            if (!element.getAddress().equals(address)) {
                firestations = new Firestations();
                firestations.setAddress(element.getAddress());
                firestations.setStation(element.getStation());
                listFirestations.add(firestations);
            }
        }
    }

    private void verifyIfStationNumberInURI(String stationNumber, List<Firestations> listF,
            List<Firestations> listFirestations) {
        Firestations firestations;
        for (Firestations element : listF) {
            if (!element.getStation().equals(stationNumber)) {
                firestations = new Firestations();
                firestations.setAddress(element.getAddress());
                firestations.setStation(element.getStation());
                listFirestations.add(firestations);
            }
        }

    }

    @Override
    public boolean postMedicalRecord(Medicalrecords medicalRecord) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(loggerApiNewSafetyNet.loggerDebug(medicalRecord.toString()));
        }

        List<Medicalrecords> listMedicalrecords;
        listMedicalrecords = createListMedicalrecords();

        if (LOGGER.isDebugEnabled()) {
            messageLogger = "The list of all medical records is: " + listMedicalrecords;
            LOGGER.debug(messageLogger);
        }
        // verify if the persons is exist in the medical records if not = add
        boolean findperson = false;
        for (Medicalrecords element : listMedicalrecords) {
            if ((element.getFirstName() + element.getLastName())
                    .equals(medicalRecord.getFirstName() + medicalRecord.getLastName())) {
                findperson = true;
                break;
            }
        }

        // add the medical records if findperson is false, so if not in the medical
        // records
        if (!findperson) {
            listMedicalrecords.add(medicalRecord); // add the body

            // create persons
            List<Persons> listPersons;
            listPersons = createListPersons();
            // create fire stations
            List<Firestations> listFirestations;
            listFirestations = createListFirestations();
            Boolean filecreated = false;
            newFileJson.createNewFileJson(listPersons, listFirestations, listMedicalrecords, medicalRecord.toString());
            filecreated = newFileJson.isFileCreated();
            return filecreated;
        }
        return false;
    }

    @Override
    public boolean putMedicalRecord(Medicalrecords medicalrecords, String firstName, String lastName) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(loggerApiNewSafetyNet.loggerDebug(firstName + " " + lastName));
        }

        List<Medicalrecords> listMedicalrecords;
        listMedicalrecords = createListMedicalrecords();

        if (LOGGER.isDebugEnabled()) {
            messageLogger = "The list of all medical records is: " + listMedicalrecords;
            LOGGER.debug(messageLogger);
        }

        // find the person in medicalrecords and update
        String firstNamelastName = firstName + lastName;
        Boolean filecreated = false;
        filecreated = findPersonInMedicalrecordsAndUpdate(medicalrecords, listMedicalrecords, firstNamelastName);
        return filecreated;
    }

    private boolean findPersonInMedicalrecordsAndUpdate(Medicalrecords medicalrecords,
            List<Medicalrecords> listMedicalrecords, String firstNamelastName) {

        for (Medicalrecords element : listMedicalrecords) {
            if ((element.getFirstName() + element.getLastName()).equals(firstNamelastName)) {
                if (medicalrecords.getAllergies() != null) {
                    element.setAllergies(medicalrecords.getAllergies());
                }
                if (medicalrecords.getBirthdate() != null) {
                    element.setBirthdate(medicalrecords.getBirthdate());
                }
                if (medicalrecords.getMedications() != null) {
                    element.setMedications(medicalrecords.getMedications());
                }

                if (LOGGER.isDebugEnabled()) {
                    messageLogger = "The medical record is updated. This is the list updated: " + listMedicalrecords;
                    LOGGER.debug(messageLogger);
                }

                // create list persons
                List<Persons> listPersons;
                listPersons = createListPersons();
                // create list fire stations
                List<Firestations> listFirestations;
                listFirestations = createListFirestations();

                Boolean filecreated = false;
                newFileJson.createNewFileJson(listPersons, listFirestations, listMedicalrecords,
                        medicalrecords.toString());
                filecreated = newFileJson.isFileCreated();
                return filecreated;
            }
        }
        return false;

    }

    @Override
    public boolean deleteMedicalRecord(String firstName, String lastName) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(loggerApiNewSafetyNet.loggerDebug(firstName + " " + lastName));
        }

        List<Medicalrecords> listMedicalrecords;
        listMedicalrecords = createListMedicalrecords();

        if (LOGGER.isDebugEnabled()) {
            messageLogger = "The all medical records are: " + listMedicalrecords;
            LOGGER.debug(messageLogger);
        }

        // find the person and delete
        String firstNamelastName = firstName + lastName;
        for (Medicalrecords element : listMedicalrecords) {
            if ((element.getFirstName() + element.getLastName()).equals(firstNamelastName)) {
                listMedicalrecords.remove(element);

                if (LOGGER.isDebugEnabled()) {
                    messageLogger = "The medical record is deleted: " + listMedicalrecords;
                    LOGGER.debug(messageLogger);
                }

                if (LOGGER.isDebugEnabled()) {
                    messageLogger = "The medical record is deleted: " + listMedicalrecords;
                    LOGGER.debug(messageLogger);
                }

                // create persons
                List<Persons> listPersons;
                listPersons = createListPersons();
                // create fire stations
                List<Firestations> listFirestations;
                listFirestations = createListFirestations();

                Boolean filecreated = false;
                newFileJson.createNewFileJson(listPersons, listFirestations, listMedicalrecords,
                        firstName + " " + lastName);
                filecreated = newFileJson.isFileCreated();
                return filecreated;
            }
        }
        return false;

    }

    @Override
    public List<FoyerChildrenAdultsToFireStation> personsOfStationAdultsAndChild(String stationNumber) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(loggerApiNewSafetyNet.loggerDebug(stationNumber));
        }

        List<Firestations> listFirestations;
        List<Firestations> listF;
        List<Persons> listPersons;
        List<Persons> listP;
        List<FoyerChildrenAdultsToFireStation> listFoyerChildrenAdults;

        // find the list of the persons from number of fire station = listF
        listFirestations = createListFirestations();
        listF = checkFirestationWithThisStation(stationNumber, listFirestations);

        // find the persons in persons = address of list fire station
        listPersons = createListPersons();
        listP = checkPersonsFromThisAddressStation(listPersons, listF, stationNumber);

        // make the list Foyer after finded the persons in medical records = persons of
        // list persons of fire station
        listFoyerChildrenAdults = checkMedicalrecordsFromThisListPAndMakeListFoyer(listP);

        return listFoyerChildrenAdults;
    }

    private List<FoyerChildrenAdultsToFireStation> checkMedicalrecordsFromThisListPAndMakeListFoyer(
            List<Persons> listP) {

        List<ChildrenOrAdults> listChildren;
        List<ChildrenOrAdults> listAdults;
        List<PersonsOfFireStation> listPersonsChildren;
        List<PersonsOfFireStation> listPersonsAdults;
        List<Medicalrecords> listMedicalrecords;
        List<FoyerChildrenAdultsToFireStation> listFoyerChildrenAdults = new ArrayList<>();
        foyerChildrenAdultsToFireStation = new FoyerChildrenAdultsToFireStation();

        listMedicalrecords = createListMedicalrecords();

        boolean childboolean = true;
        listChildren = getChildrenOrAdults(listP, listMedicalrecords, childboolean);
        listPersonsChildren = getListPersonsChildren(listP, listChildren);

        childboolean = false;
        listAdults = getChildrenOrAdults(listP, listMedicalrecords, childboolean);
        listPersonsAdults = getListPersonsChildren(listP, listAdults);

        String personsstringchild = "person";
        if (listPersonsChildren.size() > 1) {
            personsstringchild = personsstringchild + "s";
        }
        String personsstringadult = "person";
        if (listPersonsAdults.size() > 1) {
            personsstringadult = personsstringadult + "s";
        }

        String childolddmax = personsstringchild + " < " + (CHILDOLD + 1) + " years old.";
        String adultoldmin = personsstringadult + " >= " + (CHILDOLD + 1) + " years old.";
        // add in list foyer the list of children and the list of adults
        foyerChildrenAdultsToFireStation.setDecompte(Integer.toString(listPersonsChildren.size()) + " " + childolddmax);
        foyerChildrenAdultsToFireStation.setlistPersonsOfFireStations(listPersonsChildren);
        listFoyerChildrenAdults.add(foyerChildrenAdultsToFireStation);
        foyerChildrenAdultsToFireStation.setDecompte(Integer.toString(listPersonsAdults.size()) + " " + adultoldmin);
        foyerChildrenAdultsToFireStation.setlistPersonsOfFireStations(listPersonsAdults);
        listFoyerChildrenAdults.add(foyerChildrenAdultsToFireStation);

        return listFoyerChildrenAdults;
    }

    private List<PersonsOfFireStation> getListPersonsChildren(List<Persons> listP,
            List<ChildrenOrAdults> listChildren) {

        List<PersonsOfFireStation> listPersonsChildren = new ArrayList<>();
        PersonsOfFireStation personsOfFireStation;
        String lastfirstnamepersons = "";
        String lastfirstnamechildrenoradults = "";
        for (ChildrenOrAdults elementchildrenoradults : listChildren) {
            lastfirstnamechildrenoradults = (elementchildrenoradults.getFirstName()
                    + elementchildrenoradults.getLastName()).toLowerCase();
            for (Persons elementpersons : listP) {
                lastfirstnamepersons = (elementpersons.getFirstName() + elementpersons.getLastName()).toLowerCase();
                if (lastfirstnamepersons.equals(lastfirstnamechildrenoradults)) {
                    personsOfFireStation = new PersonsOfFireStation(
                            Integer.toString(listPersonsChildren.size() + 1),
                            elementpersons.getFirstName(), elementpersons.getLastName(),
                            elementpersons.getAddress(), elementpersons.getPhone());
                    listPersonsChildren.add(personsOfFireStation);
                }
            }

        }
        return listPersonsChildren;
    }

    private List<ChildrenOrAdults> getChildrenOrAdults(List<Persons> listP, List<Medicalrecords> listMedicalrecords,
            boolean childboolean) {

        List<ChildrenOrAdults> listchildrenOrAdults = new ArrayList<>();

        String lastfirstnameperson = "";
        String lastfirstnamemedicalrecords = "";
        Period periode;
        for (Persons elementpersons : listP) {
            lastfirstnameperson = (elementpersons.getFirstName() + elementpersons.getLastName()).toLowerCase();
            for (Medicalrecords elementmedicalrecords : listMedicalrecords) {
                lastfirstnamemedicalrecords = (elementmedicalrecords.getFirstName()
                        + elementmedicalrecords.getLastName()).toLowerCase();
                if (lastfirstnameperson.equals(lastfirstnamemedicalrecords)) {
                    periode = extractDateFromText(elementmedicalrecords.getBirthdate());
                    if (periode == null) {
                        return Collections.emptyList();
                    }
                    childrenOrAdults = makeTheListChildrenOrAdult(childboolean, periode, childrenOrAdults,
                            listchildrenOrAdults, elementpersons);
                }
            }
        }
        return listchildrenOrAdults;
    }

    private ChildrenOrAdults makeTheListChildrenOrAdult(boolean childboolean, Period periode,
            ChildrenOrAdults childrenOrAdults, List<ChildrenOrAdults> listchildrenOrAdults, Persons elementpersons) {

        if (childboolean && periode.getYears() <= CHILDOLD) {
            childrenOrAdults = new ChildrenOrAdults(elementpersons.getFirstName(),
                    elementpersons.getLastName(), Integer.toString(periode.getYears()));
            listchildrenOrAdults.add(childrenOrAdults);
        }
        if (!childboolean && periode.getYears() > CHILDOLD) {
            childrenOrAdults = new ChildrenOrAdults(elementpersons.getFirstName(),
                    elementpersons.getLastName(), Integer.toString(periode.getYears()));
            listchildrenOrAdults.add(childrenOrAdults);
        }

        return childrenOrAdults;
    }

    private Period extractDateFromText(String elementtext) {

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Calendar calendar = new GregorianCalendar();
        LocalDate now = LocalDate.now();
        java.util.Date datebirthday;
        LocalDate birthdate;
        Period periode;
        try {
            datebirthday = sdf.parse(elementtext);
        } catch (ParseException e) {
            return null;
        }
        calendar.setTime(datebirthday);
        birthdate = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
        periode = Period.between(birthdate, now);

        return periode;
    }

    private List<Persons> checkPersonsFromThisAddressStation(List<Persons> listPersons, List<Firestations> listF,
            String stationNumber) {

        List<Persons> listP = new ArrayList<>();

        for (Firestations elementfirestation : listF) {
            for (Persons elementperson : listPersons) {
                if (elementfirestation.getAddress().equals(elementperson.getAddress())) {
                    personsObj = new Persons(elementperson.getFirstName(), elementperson.getLastName(),
                            elementperson.getAddress(),
                            elementperson.getCity(), elementperson.getZip(), elementperson.getPhone(),
                            elementperson.getEmail());
                    listP.add(personsObj);
                }
            }
        }
        if (LOGGER.isDebugEnabled()) {
            if (listP.isEmpty()) {
                messageLogger = "The list is empty. No person with this number station " + stationNumber + ".";
            } else {
                messageLogger = "The list of persons with the station number " + stationNumber + " is: "
                        + listP.toString();
            }
            LOGGER.debug(messageLogger);
        }

        return listP;
    }

    private List<Firestations> checkFirestationWithThisStation(String stationNumber,
            List<Firestations> listFirestations) {

        List<Firestations> listF = new ArrayList<>();

        for (Firestations element : listFirestations) {
            if (element.getStation().equals(stationNumber)) {
                firestationsObj = new Firestations(element.getAddress(), element.getStation());
                listF.add(firestationsObj);
            }
        }
        if (LOGGER.isDebugEnabled()) {
            if (listF.isEmpty()) {
                messageLogger = "The list is empty. No fire station with this number " + stationNumber + ".";
            } else {
                messageLogger = "The list of the station number " + stationNumber + " is: " + listF.toString();
            }
            LOGGER.debug(messageLogger);
        }

        return listF;
    }

    @Override
    public List<ChildAlert> childPersonsAlertAddress(String address) throws IOException, ParseException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(loggerApiNewSafetyNet.loggerDebug(address));
        }

        List<Persons> listPersons;
        listPersons = createListPersons();
        // find the list of persons from address
        List<Persons> listP = new ArrayList<>();
        int indexpersons = 0;
        for (Persons elementpersons : listPersons) {
            if (elementpersons.getAddress().equalsIgnoreCase(address)) {
                listP.add(indexpersons, elementpersons);
                indexpersons += 1;
            }
        }

        List<Medicalrecords> listMedicalrecords;
        listMedicalrecords = createListMedicalrecords();
        List<ChildrenOrAdults> listChildren;
        List<ChildrenOrAdults> listAdults;
        boolean childboolean = true; // get the list of children
        listChildren = getChildrenOrAdults(listP, listMedicalrecords, childboolean);
        childboolean = false; // get the list of adults
        listAdults = getChildrenOrAdults(listP, listMedicalrecords, childboolean);

        List<ChildAlert> listChildAlert = new ArrayList<>();
        if (!listChildren.isEmpty()) {
            childAlertobj = new ChildAlert(listChildren, listAdults);
            listChildAlert.add(childAlertobj);
        }

        return listChildAlert;
    }

    @Override
    public List<PhoneAlert> phoneAlertFirestation(String stationNumber) throws IOException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(loggerApiNewSafetyNet.loggerDebug(stationNumber));
        }

        List<Firestations> listFirestations;
        List<Firestations> listF;
        // find the list of the persons from number of fire station = listF
        listFirestations = createListFirestations();
        listF = checkFirestationWithThisStation(stationNumber, listFirestations);

        // find the persons in persons = address of list fire station
        List<Persons> listPersons;
        List<Persons> listP;
        listPersons = createListPersons();
        listP = checkPersonsFromThisAddressStation(listPersons, listF, stationNumber);
        phoneAlertObj = new PhoneAlert();

        List<String> listPhones = new ArrayList<>();
        for (Persons element : listP) {
            listPhones.add(element.getPhone());
        }
        // find the duplicate. If duplicate make ""
        for (int i = 0; i < listPhones.size(); i++) {
            for (int j = i + 1; j < listPhones.size(); j++) {
                if (listPhones.get(i).equals(listPhones.get(j))) {
                    listPhones.set(j, "");
                }
            }
        }
        // make a new list without ""
        List<String> listPhonesNoDuplicate = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < listPhones.size(); i++) {
            if (!listPhones.get(i).isEmpty()) {
                listPhonesNoDuplicate.add(index, listPhones.get(i));
                index++;
            }
        }
        List<PhoneAlert> listPhoneAlerts = new ArrayList<>();
        phoneAlertObj.setListPhones(listPhonesNoDuplicate);
        listPhoneAlerts.add(phoneAlertObj);

        return listPhoneAlerts;
    }

    @Override
    public List<FireAddress> fireAddress(String address) throws IOException, ParseException {

        String noFirestation = "";

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(loggerApiNewSafetyNet.loggerDebug(address));
        }

        List<Firestations> listFirestations = createListFirestations();
        // find the first number of fire staition
        for (Firestations element : listFirestations) {
            if (element.getAddress().equalsIgnoreCase(address)) {
                noFirestation = element.getStation();
                break;
            }
        }

        List<Persons> listPersons = createListPersons();
        List<Persons> listP = createNewPersonsArrayList();
        // get the list of persons leave in this address
        int index = 0;
        for (Persons element : listPersons) {
            if (element.getAddress().equalsIgnoreCase(address)) {
                listP.add(index, element);
                index++;
            }
        }

        List<Medicalrecords> listMedicalrecords = createListMedicalrecords();
        String firstlastNamePerson = "";
        String firstlastNameMedicalrecords = "";
        List<FireAddress> listFireAddress = createNewFireAddressArrayList();
        for (Persons elemPersons : listP) {
            firstlastNamePerson = (elemPersons.getFirstName() + elemPersons.getLastName()).toLowerCase();
            for (Medicalrecords elemMedicalrecords : listMedicalrecords) {
                firstlastNameMedicalrecords = (elemMedicalrecords.getFirstName() + elemMedicalrecords.getLastName())
                        .toLowerCase();
                if (firstlastNamePerson.equals(firstlastNameMedicalrecords)) {
                    fireAddress = new FireAddress();
                    fireAddress.setFirestation(noFirestation);
                    fireAddress.setFirstName(elemPersons.getFirstName());
                    fireAddress.setLastName(elemPersons.getLastName());
                    fireAddress.setListMedications(elemMedicalrecords.getMedications());
                    fireAddress.setListAllergies(elemMedicalrecords.getAllergies());
                    setOldFireAddress(elemMedicalrecords, fireAddress);
                    fireAddress.setPhone(elemPersons.getPhone());
                    listFireAddress.add(fireAddress);
                }
            }
        }

        return listFireAddress;
    }

    private void setOldFireAddress(Medicalrecords elemMedicalrecords, FireAddress fireAddress) {
        Period old;
        old = extractDateFromText(elemMedicalrecords.getBirthdate());
        if (old != null) {
            fireAddress.setOld(Integer.toString(old.getYears()));
        }
    }

    @Override
    public List<PersonsFireStation> stationListFirestation(List<String> station) throws IOException, ParseException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(loggerApiNewSafetyNet.loggerDebug(station.toString()));
        }

        List<Persons> listPersons = createListPersons();
        List<Firestations> listFirestations = createListFirestations();
        List<Persons> listP;
        List<String> listAddressStation = createNewStringArrayList();
        int index = 0;
        // get list address from list fire station
        for (String elemeStations : station) {
            for (Firestations elementFirestations : listFirestations) {
                if (elemeStations.equalsIgnoreCase(elementFirestations.getStation())) {
                    listAddressStation.add(index, elementFirestations.getAddress());
                    index++;
                }
            }
        }

        List<AddressListFirestation> listAddressListFirestation;
        List<PersonsFireStation> listPersonsFireStations = createNewPersonsFireStationArrayList();
        for (String elementAddressStation : listAddressStation) {
            listP = createNewPersonsArrayList();
            for (Persons elementPersons : listPersons) {
                if (elementAddressStation.equalsIgnoreCase(elementPersons.getAddress())) {
                    listP.add(elementPersons);
                }
            }
            listAddressListFirestation = createAddressListFirestation(listP);
            personsFireStation = new PersonsFireStation();
            personsFireStation.setAddress(elementAddressStation);
            personsFireStation.setListAddressFirestations(listAddressListFirestation);
            listPersonsFireStations.add(personsFireStation);
        }
        return listPersonsFireStations;

    }

    private List<AddressListFirestation> createAddressListFirestation(List<Persons> listP) {
        List<Medicalrecords> listMedicalrecords = createListMedicalrecords();
        List<AddressListFirestation> listAddressListFirestation = createNewAddressListFirestationArrayList();
        String firstAndLastNamePerson = "";
        String firstAndLastNameMedicalrecords = "";
        for (Persons elementPersons : listP) {
            firstAndLastNamePerson = elementPersons.getFirstName() + elementPersons.getLastName();
            for (Medicalrecords elementMedicalrecords : listMedicalrecords) {
                firstAndLastNameMedicalrecords = elementMedicalrecords.getFirstName()
                        + elementMedicalrecords.getLastName();
                if (firstAndLastNamePerson.equalsIgnoreCase(firstAndLastNameMedicalrecords)) {
                    addressListFirestationObj = new AddressListFirestation();
                    addressListFirestationObj.setFirstName(elementMedicalrecords.getFirstName());
                    addressListFirestationObj.setLastName(elementMedicalrecords.getLastName());
                    addressListFirestationObj.setListAllergies(elementMedicalrecords.getAllergies());
                    addressListFirestationObj.setListMedications(elementMedicalrecords.getMedications());
                    setOldAddressListFirestation(elementMedicalrecords, addressListFirestationObj);
                    addressListFirestationObj.setPhone(elementPersons.getPhone());
                    listAddressListFirestation.add(addressListFirestationObj);
                    break;
                }
            }
        }
        return listAddressListFirestation;
    }

    private void setOldAddressListFirestation(Medicalrecords elementMedicalrecords,
            AddressListFirestation addressListFirestationObj) {
        Period old;
        old = extractDateFromText(elementMedicalrecords.getBirthdate());
        if (old != null) {
            addressListFirestationObj.setOld(Integer.toString(old.getYears()));
        }
    }

    @Override
    public List<PersonInfo> personInfo(String firstName, String lastName) throws IOException, ParseException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(loggerApiNewSafetyNet.loggerDebug(firstName + " " + lastName));
        }

        List<PersonInfo> listPersonInfo = createNewListPersonInfoArrayList();

        // create a list of persons
        List<Persons> listP = createNewPersonsArrayList();
        List<Persons> listPersons;
        listPersons = createListPersons();

        if (LOGGER.isDebugEnabled()) {
            messageLogger = "The list of persons: " + listPersons;
            LOGGER.debug(messageLogger);
        }

        // create a list of medical records
        List<Medicalrecords> listMedicalrecords;
        listMedicalrecords = createListMedicalrecords();

        if (LOGGER.isDebugEnabled()) {
            messageLogger = "The list of medical records is: " + listMedicalrecords;
            LOGGER.debug(messageLogger);
        }

        // add person with the first and last name from end point
        boolean findPerson = false;
        for (Persons elementPersons : listPersons) {
            if (elementPersons.getFirstName().equalsIgnoreCase(firstName)
                    && elementPersons.getLastName().equalsIgnoreCase(lastName)) {
                listP.add(elementPersons);
                findPerson = true;
                break;
            }
        }
        // add all persons with the same name if we have a person
        if (findPerson) {
            for (Persons elementPersons : listPersons) {
                if (!elementPersons.getFirstName().equalsIgnoreCase(firstName)
                        && elementPersons.getLastName().equalsIgnoreCase(lastName)) {
                    listP.add(elementPersons);
                }
            }
        }

        listPersonInfo = addAgeAndMedicalRecords(listP, listMedicalrecords, listPersonInfo);

        return listPersonInfo;
    }

    private List<PersonInfo> addAgeAndMedicalRecords(List<Persons> listP, List<Medicalrecords> listMedicalrecords,
            List<PersonInfo> listPersonInfo) {

        // add the age and medical records
        PersonInfo personInfo;
        String firstAndLastNamePersons = "";
        String firstAndLastNameMedicalrecords = "";
        Period periode;
        for (Persons elementPersons : listP) {
            firstAndLastNamePersons = elementPersons.getFirstName() + elementPersons.getLastName();
            for (Medicalrecords elementMedicalrecords : listMedicalrecords) {
                firstAndLastNameMedicalrecords = elementMedicalrecords.getFirstName()
                        + elementMedicalrecords.getLastName();
                if (firstAndLastNamePersons.equalsIgnoreCase(firstAndLastNameMedicalrecords)) {
                    personInfo = new PersonInfo();
                    personInfo.setFirstName(elementPersons.getFirstName());
                    personInfo.setLastName(elementPersons.getLastName());
                    periode = extractDateFromText(elementMedicalrecords.getBirthdate());
                    if (periode == null) {
                        return Collections.emptyList();
                    }
                    personInfo.setOld(Integer.toString(periode.getYears()));
                    personInfo.setAddress(elementPersons.getAddress());
                    personInfo.setEmail(elementPersons.getEmail());
                    personInfo.setListAllergies(elementMedicalrecords.getAllergies());
                    personInfo.setListMedications(elementMedicalrecords.getMedications());
                    listPersonInfo.add(personInfo);
                }
            }
        }

        return listPersonInfo;
    }

    @Override
    public List<CommunityEmail> communityEmail(String city) throws IOException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(loggerApiNewSafetyNet.loggerDebug(city));
        }

        // create a list of persons
        List<Persons> listPersons;
        listPersons = createListPersons();

        if (LOGGER.isDebugEnabled()) {
            messageLogger = "The list of persons: " + listPersons;
            LOGGER.debug(messageLogger);
        }

        CommunityEmail communityEmail;
        List<String> listEmail = createNewStringArrayList();
        for (Persons elementPersons : listPersons) {
            if (elementPersons.getCity().equals(city)) {
                listEmail.add(elementPersons.getEmail());
            }
        }

        if (LOGGER.isDebugEnabled()) {
            messageLogger = "The list of emails is created(before remove the duplicate): " + listEmail;
            LOGGER.debug(messageLogger);
        }

        // remove the the duplicate email
        List<String> listEmailNoDuplicate;
        listEmailNoDuplicate = removeDuplicateEmail(listEmail);

        List<CommunityEmail> listCommunityEmail = createNewCommunityEmail();
        communityEmail = new CommunityEmail();
        communityEmail.setListEmails(listEmailNoDuplicate);
        listCommunityEmail.add(communityEmail);

        return listCommunityEmail;

    }

    private List<String> removeDuplicateEmail(List<String> listEmail) {

        List<String> listEmailNoDuplicate = createNewStringArrayList();

        for (int i = 0; i < listEmail.size(); i++) {
            if (!listEmail.get(i).isEmpty()) { // if empty it was duplicate
                for (int j = i + 1; j < listEmail.size(); j++) {
                    if (listEmail.get(i).equals(listEmail.get(j))) {
                        listEmail.set(j, ""); // if duplicate set to empty
                    }
                }
                listEmailNoDuplicate.add(listEmail.get(i)); // we create the new list without duplicate
            }
        }

        return listEmailNoDuplicate;
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

    private List<Persons> createNewPersonsArrayList() {
        return new ArrayList<>();
    }

    private List<FireAddress> createNewFireAddressArrayList() {
        return new ArrayList<>();
    }

    private List<String> createNewStringArrayList() {
        return new ArrayList<>();
    }

    private List<AddressListFirestation> createNewAddressListFirestationArrayList() {
        return new ArrayList<>();
    }

    private List<PersonsFireStation> createNewPersonsFireStationArrayList() {
        return new ArrayList<>();
    }

    private List<PersonInfo> createNewListPersonInfoArrayList() {
        return new ArrayList<>();
    }

    private List<CommunityEmail> createNewCommunityEmail() {
        return new ArrayList<>();
    }

    private List<Persons> createListPersons() {
        List<Persons> listPersons;
        listPersons = getListsElementsJsonRepository.getPersons(ELEMJSONPERSONS);
        return listPersons;
    }

    private List<Firestations> createListFirestations() {
        List<Firestations> listFirestations;
        listFirestations = getListsElementsJsonRepository.getFirestations(ELEMJSONFIRESTATIONS);
        return listFirestations;
    }

    private List<Medicalrecords> createListMedicalrecords() {
        List<Medicalrecords> listMedicalrecords;
        listMedicalrecords = getListsElementsJsonRepository.getMedicalrecords(ELEMJSONMEDICALRECORDS);
        return listMedicalrecords;
    }

}