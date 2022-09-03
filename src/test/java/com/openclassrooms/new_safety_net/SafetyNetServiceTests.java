package com.openclassrooms.new_safety_net;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import java.util.GregorianCalendar;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.new_safety_net.model.ChildAlert;
import com.openclassrooms.new_safety_net.model.CommunityEmail;
import com.openclassrooms.new_safety_net.model.FireAddress;
import com.openclassrooms.new_safety_net.model.Firestations;
import com.openclassrooms.new_safety_net.model.FoyerChildrenAdultsToFireStation;
import com.openclassrooms.new_safety_net.model.Medicalrecords;
import com.openclassrooms.new_safety_net.model.PersonInfo;
import com.openclassrooms.new_safety_net.model.Persons;
import com.openclassrooms.new_safety_net.model.PersonsFireStation;
import com.openclassrooms.new_safety_net.model.PhoneAlert;
import com.openclassrooms.new_safety_net.service.GetListsElementsJson;
import com.openclassrooms.new_safety_net.service.LoggerApiNewSafetyNet;
import com.openclassrooms.new_safety_net.service.NewFileJson;
import com.openclassrooms.new_safety_net.service.ObjetFromJson;
import com.openclassrooms.new_safety_net.service.SafetyNetService;

@ExtendWith(MockitoExtension.class)
public class SafetyNetServiceTests {

    @Mock
    GetListsElementsJson getListsElementsJson;

    @Mock
    ObjetFromJson objetFromJson;

    @Mock
    NewFileJson newFileJson;

    @Mock
    LoggerApiNewSafetyNet loggerApiNewSafetyNet;

    @InjectMocks
    SafetyNetService safetyNetService;

    @Test
    void testGetAPerson() throws IOException {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        String elemjson = "persons";
        String firstNamelastName = firstName + lastName;
        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        List<Persons> listP = listPersons;

        when(getListsElementsJson.getPersons(elemjson)).thenReturn(listPersons);

        listP = safetyNetService.getAPerson(firstNamelastName, elemjson);

        assertEquals(firstNamelastName, listP.get(0).getFirstName().toString() + listP.get(0).getLastName().toString());
    }

    @Test
    void testGetNoPerson() throws IOException {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        String elemjson = "persons";
        String firstNamelastName = firstName + lastName;
        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        List<Persons> listP = listPersons;

        when(getListsElementsJson.getPersons(elemjson)).thenReturn(listPersons);

        listP = safetyNetService.getAPerson(firstNamelastName + "NoThisPerson", elemjson);

        assertEquals(true, listP.isEmpty());
    }

    @Test
    void testPostPerson() {

        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        person = new Persons();
        person.setFirstName(firstName + "AddedFirstName");
        person.setLastName(firstName + "AddedLastName");
        person.setAddress("TEST999_address");
        person.setCity("city");
        person.setEmail("email");
        person.setPhone("phone");
        person.setZip("zip");
        String elemjson = "persons";
        boolean fileCreated = false;

        when(getListsElementsJson.getPersons(elemjson)).thenReturn(listPersons);
        when(newFileJson.isFileCreated()).thenReturn(true);

        fileCreated = safetyNetService.postPerson(person);

        assertEquals(true, fileCreated);

    }

    @Test
    void testPostPersonExistInList() {

        String elemjson = "persons";
        boolean fileCreated = false;

        List<Persons> listPersons = new ArrayList<>();
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);

        when(getListsElementsJson.getPersons(elemjson)).thenReturn(listPersons);

        fileCreated = safetyNetService.postPerson(person);

        assertEquals(false, fileCreated);
    }

    @Test
    void testPutPerson() {

        String elemjson = "persons";
        boolean updated = false;

        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);

        Persons personPut = new Persons();
        personPut.setFirstName(firstName);
        personPut.setLastName(lastName);
        personPut.setAddress("addressPut");
        personPut.setCity("cityPut");
        personPut.setEmail("emailPut");
        personPut.setPhone("phonePut");
        personPut.setZip("zipPut");

        when(getListsElementsJson.getPersons(elemjson)).thenReturn(listPersons);
        when(newFileJson.isFileCreated()).thenReturn(true);

        updated = safetyNetService.putPerson(personPut, firstName, lastName);

        assertEquals(true, updated);
    }

    @Test
    void testPutNoPerson() {

        String elemjson = "persons";
        boolean updated = false;
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);

        when(getListsElementsJson.getPersons(elemjson)).thenReturn(listPersons);

        updated = safetyNetService.putPerson(person, firstName + "Test", lastName + "Test");

        assertEquals(false, updated);
    }

    @Test
    void testDeletePerson() {
        String elemjson = "persons";
        boolean deleted = false;
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);

        when(getListsElementsJson.getPersons(elemjson)).thenReturn(listPersons);
        when(newFileJson.isFileCreated()).thenReturn(true);

        deleted = safetyNetService.deletePerson(firstName, lastName);

        assertEquals(true, deleted);
    }

    @Test
    void testDeleteNoPerson() {
        String elemjson = "persons";
        boolean deleted = true;
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);

        when(getListsElementsJson.getPersons(elemjson)).thenReturn(listPersons);

        deleted = safetyNetService.deletePerson(firstName + "ToDelete", lastName + "ToDelete");

        assertEquals(false, deleted);
    }

    @Test
    void testPostFirestationExist() {

        String addressStation = "TEST999_address";
        String noStation = "999";
        List<Firestations> listFirestations;
        Firestations firestation;
        firestation = createFirestation(addressStation, noStation);
        listFirestations = createListFirestationsTest(addressStation, noStation, firestation);

        String elemjson = "firestations";
        boolean fileCreated = false;

        when(getListsElementsJson.getFirestations(elemjson)).thenReturn(listFirestations);

        fileCreated = safetyNetService.postFirestation(firestation);

        assertEquals(false, fileCreated);

    }

    @Test
    void testPostFirestation() {

        String addressStation = "TEST999_address";
        String noStation = "999";
        List<Firestations> listFirestations;
        Firestations firestation;
        firestation = createFirestation(addressStation, noStation);
        listFirestations = createListFirestationsTest(addressStation, noStation, firestation);

        // add another firestation
        firestation = new Firestations();
        firestation.setAddress(addressStation + "Address_added");
        firestation.setStation(noStation);

        String elemjson = "firestations";
        boolean fileCreated = false;

        when(getListsElementsJson.getFirestations(elemjson)).thenReturn(listFirestations);
        when(newFileJson.isFileCreated()).thenReturn(true);

        fileCreated = safetyNetService.postFirestation(firestation);

        assertEquals(true, fileCreated);

    }

    @Test
    void testPutFirestation() {

        String addressStation = "TEST999_address";
        String noStation = "999";
        List<Firestations> listFirestations;
        Firestations firestation;
        firestation = createFirestation(addressStation, noStation);
        listFirestations = createListFirestationsTest(addressStation, noStation, firestation);

        // add another firestation
        Firestations firestationUpdate = new Firestations();
        firestationUpdate.setAddress(addressStation);
        firestationUpdate.setStation(noStation + "99");

        String elemjson = "firestations";
        boolean fileCreated = false;

        when(getListsElementsJson.getFirestations(elemjson)).thenReturn(listFirestations);
        when(newFileJson.isFileCreated()).thenReturn(true);

        fileCreated = safetyNetService.putFirestation(firestationUpdate, firestationUpdate.getAddress());

        assertEquals(true, fileCreated);

    }

    @Test
    void testPutFirestationAddressNoExist() {

        String addressStation = "TEST999_address";
        String noStation = "999";
        List<Firestations> listFirestations;
        Firestations firestation;
        firestation = createFirestation(addressStation, noStation);
        listFirestations = createListFirestationsTest(addressStation, noStation, firestation);

        // add another firestation
        Firestations firestationUpdate = new Firestations();
        firestationUpdate.setAddress(addressStation + "NoExist");
        firestationUpdate.setStation(noStation + "99");

        String elemjson = "firestations";
        boolean fileCreated = false;

        when(getListsElementsJson.getFirestations(elemjson)).thenReturn(listFirestations);

        fileCreated = safetyNetService.putFirestation(firestationUpdate, firestationUpdate.getAddress());

        assertEquals(false, fileCreated);

    }

    @Test
    void testPutFirestationSameStation() {

        String addressStation = "TEST999_address";
        String noStation = "999";
        List<Firestations> listFirestations;
        Firestations firestation;
        firestation = createFirestation(addressStation, noStation);
        listFirestations = createListFirestationsTest(addressStation, noStation, firestation);

        // add another firestation
        Firestations firestationUpdate = new Firestations();
        firestationUpdate.setAddress(addressStation);
        firestationUpdate.setStation(noStation); // same station

        String elemjson = "firestations";
        boolean fileCreated = false;

        when(getListsElementsJson.getFirestations(elemjson)).thenReturn(listFirestations);
        when(newFileJson.isFileCreated()).thenReturn(true);

        fileCreated = safetyNetService.putFirestation(firestationUpdate, firestationUpdate.getAddress());

        assertEquals(true, fileCreated);

    }

    @Test
    void testPutFirestationNostation() {

        String addressStation = "TEST999_address";
        String noStation = "999";
        List<Firestations> listFirestations;
        Firestations firestation;
        firestation = createFirestation(addressStation, noStation);
        listFirestations = createListFirestationsTest(addressStation, noStation, firestation);

        // add another firestation
        Firestations firestationUpdate = new Firestations();
        firestationUpdate.setAddress(addressStation);
        // firestationUpdate.setStation(noStation); // no station

        String elemjson = "firestations";
        boolean fileCreated = false;

        when(getListsElementsJson.getFirestations(elemjson)).thenReturn(listFirestations);
        when(newFileJson.isFileCreated()).thenReturn(true);

        fileCreated = safetyNetService.putFirestation(firestationUpdate, firestationUpdate.getAddress());

        assertEquals(true, fileCreated);

    }

    @Test
    void testDeleteFirestationByAddress() {

        String addressStation = "TEST999_address";
        String noStation = "999";
        List<Firestations> listFirestations;
        Firestations firestation;
        firestation = createFirestation(addressStation, noStation);
        listFirestations = createListFirestationsTest(addressStation, noStation, firestation);
        String elemjson = "firestations";
        boolean fileCreated = false;

        when(getListsElementsJson.getFirestations(elemjson)).thenReturn(listFirestations);
        when(newFileJson.isFileCreated()).thenReturn(true);

        fileCreated = safetyNetService.deleteFirestation(addressStation, null);

        assertEquals(true, fileCreated);

    }

    @Test
    void testDeleteFirestationByStation() {

        String addressStation = "TEST999_address";
        String noStation = "999";
        List<Firestations> listFirestations;
        Firestations firestation;
        firestation = createFirestation(addressStation, noStation);
        listFirestations = createListFirestationsTest(addressStation, noStation, firestation);
        String elemjson = "firestations";
        boolean fileCreated = false;

        when(getListsElementsJson.getFirestations(elemjson)).thenReturn(listFirestations);
        when(newFileJson.isFileCreated()).thenReturn(true);

        fileCreated = safetyNetService.deleteFirestation(null, noStation);

        assertEquals(true, fileCreated);

    }

    @Test
    void testDeleteFirestationNoStationNoAddress() {

        String addressStation = "TEST999_address";
        String noStation = "999";
        boolean fileCreated = false;

        fileCreated = safetyNetService.deleteFirestation(addressStation, noStation);

        assertEquals(false, fileCreated);

    }

    @Test
    void testDeleteFirestationByOtherAddress() {

        String addressStation = "TEST999_address";
        String noStation = "999";
        List<Firestations> listFirestations;
        Firestations firestation;
        firestation = createFirestation(addressStation, noStation);
        listFirestations = createListFirestationsTest(addressStation, noStation, firestation);
        String elemjson = "firestations";
        boolean fileCreated = false;

        when(getListsElementsJson.getFirestations(elemjson)).thenReturn(listFirestations);
        when(newFileJson.isFileCreated()).thenReturn(true);

        fileCreated = safetyNetService.deleteFirestation(addressStation + "anotherAddress", null);

        assertEquals(true, fileCreated);

    }

    @Test
    void testDeleteFirestationByOtherStation() {

        String addressStation = "TEST999_address";
        String noStation = "999";
        List<Firestations> listFirestations;
        Firestations firestation;
        firestation = createFirestation(addressStation, noStation);
        listFirestations = createListFirestationsTest(addressStation, noStation, firestation);
        String elemjson = "firestations";
        boolean fileCreated = false;

        when(getListsElementsJson.getFirestations(elemjson)).thenReturn(listFirestations);
        when(newFileJson.isFileCreated()).thenReturn(true);

        fileCreated = safetyNetService.deleteFirestation(null, "999999");

        assertEquals(true, fileCreated);

    }

    @Test
    void testPostMedicalRecord() {

        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        List<String> listAllergies;
        listAllergies = createListAllergiesTest();
        List<String> listMedications;
        listMedications = createListMedicationsTest();
        List<Medicalrecords> listMedicalrecords;
        Medicalrecords medicalrecords;
        medicalrecords = createMedicalrecords(firstName, lastName, listAllergies, listMedications);
        listMedicalrecords = createListMedicalrecordsTest(medicalrecords);

        Medicalrecords medicalrecordsAdd = new Medicalrecords();
        medicalrecordsAdd.setFirstName(firstName + "AddedFirstName");
        medicalrecordsAdd.setLastName(firstName + "AddedLastName");
        medicalrecordsAdd.setAllergies(listAllergies);
        medicalrecordsAdd.setMedications(listMedications);
        medicalrecordsAdd.setBirthdate("12/31/2099");
        String elemjson = "medicalrecords";
        boolean fileCreated = false;

        when(getListsElementsJson.getMedicalrecords(elemjson)).thenReturn(listMedicalrecords);
        when(newFileJson.isFileCreated()).thenReturn(true);

        fileCreated = safetyNetService.postMedicalRecord(medicalrecordsAdd);

        assertEquals(true, fileCreated);

    }

    @Test
    void testPostMedicalRecordExist() {

        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        List<String> listAllergies;
        listAllergies = createListAllergiesTest();
        List<String> listMedications;
        listMedications = createListMedicationsTest();
        List<Medicalrecords> listMedicalrecords;
        Medicalrecords medicalrecords;
        medicalrecords = createMedicalrecords(firstName, lastName, listAllergies, listMedications);
        listMedicalrecords = createListMedicalrecordsTest(medicalrecords);

        Medicalrecords medicalrecordsAdd = new Medicalrecords();
        medicalrecordsAdd = medicalrecords; // add the same person
        String elemjson = "medicalrecords";
        boolean fileCreated = false;

        when(getListsElementsJson.getMedicalrecords(elemjson)).thenReturn(listMedicalrecords);

        fileCreated = safetyNetService.postMedicalRecord(medicalrecordsAdd);

        assertEquals(false, fileCreated);

    }

    @Test
    void testPutMedicalRecord() {

        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        List<String> listAllergies;
        listAllergies = createListAllergiesTest();
        List<String> listMedications;
        listMedications = createListMedicationsTest();
        List<Medicalrecords> listMedicalrecords;
        Medicalrecords medicalrecords;
        medicalrecords = createMedicalrecords(firstName, lastName, listAllergies, listMedications);
        listMedicalrecords = createListMedicalrecordsTest(medicalrecords);

        String elemjson = "medicalrecords";
        boolean fileCreated = false;

        when(getListsElementsJson.getMedicalrecords(elemjson)).thenReturn(listMedicalrecords);
        when(newFileJson.isFileCreated()).thenReturn(true);

        fileCreated = safetyNetService.putMedicalRecord(medicalrecords, firstName, lastName);

        assertEquals(true, fileCreated);

    }

    @Test
    void testPutMedicalRecordNoMedicalrecords() {

        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        List<String> listAllergies;
        listAllergies = createListAllergiesTest();
        List<String> listMedications;
        listMedications = createListMedicationsTest();
        List<Medicalrecords> listMedicalrecords;
        Medicalrecords medicalrecords;
        medicalrecords = createMedicalrecords(firstName, lastName, listAllergies, listMedications);
        listMedicalrecords = createListMedicalrecordsTest(medicalrecords);

        String elemjson = "medicalrecords";
        boolean fileCreated = false;

        when(getListsElementsJson.getMedicalrecords(elemjson)).thenReturn(listMedicalrecords);

        fileCreated = safetyNetService.putMedicalRecord(medicalrecords, firstName + "AnotherFirstName",
                lastName + "AnotherLastName");

        assertEquals(false, fileCreated);

    }

    @Test
    void testPutMedicalRecordNoAllergieNoMedicationNoBirthday() {

        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        List<String> listAllergies;
        listAllergies = createListAllergiesTest();
        List<String> listMedications;
        listMedications = createListMedicationsTest();
        List<Medicalrecords> listMedicalrecords;
        Medicalrecords medicalrecords;
        medicalrecords = createMedicalrecords(firstName, lastName, listAllergies, listMedications);
        listMedicalrecords = createListMedicalrecordsTest(medicalrecords);

        String elemjson = "medicalrecords";
        boolean fileCreated = false;

        when(getListsElementsJson.getMedicalrecords(elemjson)).thenReturn(listMedicalrecords);
        when(newFileJson.isFileCreated()).thenReturn(true);

        medicalrecords.setAllergies(null);
        medicalrecords.setMedications(null);
        medicalrecords.setBirthdate(null);
        fileCreated = safetyNetService.putMedicalRecord(medicalrecords, firstName, lastName);

        assertEquals(true, fileCreated);

    }

    @Test
    void testDeleteMedicalRecord() {

        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        List<String> listAllergies;
        listAllergies = createListAllergiesTest();
        List<String> listMedications;
        listMedications = createListMedicationsTest();
        List<Medicalrecords> listMedicalrecords;
        Medicalrecords medicalrecords;
        medicalrecords = createMedicalrecords(firstName, lastName, listAllergies, listMedications);
        listMedicalrecords = createListMedicalrecordsTest(medicalrecords);

        String elemjson = "medicalrecords";
        boolean fileCreated = false;

        when(getListsElementsJson.getMedicalrecords(elemjson)).thenReturn(listMedicalrecords);
        when(newFileJson.isFileCreated()).thenReturn(true);

        fileCreated = safetyNetService.deleteMedicalRecord(firstName, lastName);

        assertEquals(true, fileCreated);

    }

    @Test
    void testDeleteMedicalRecordNoExist() {

        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        List<String> listAllergies;
        listAllergies = createListAllergiesTest();
        List<String> listMedications;
        listMedications = createListMedicationsTest();
        List<Medicalrecords> listMedicalrecords;
        Medicalrecords medicalrecords;
        medicalrecords = createMedicalrecords(firstName, lastName, listAllergies, listMedications);
        listMedicalrecords = createListMedicalrecordsTest(medicalrecords);

        String elemjson = "medicalrecords";
        boolean fileCreated = false;

        when(getListsElementsJson.getMedicalrecords(elemjson)).thenReturn(listMedicalrecords);

        fileCreated = safetyNetService.deleteMedicalRecord(firstName + "AnotherFirstName",
                lastName + "AnotherLastName");

        assertEquals(false, fileCreated);

    }

    @Test
    void testPersonsOfStationAdultsAndChild() {
        String addressStation = "TEST999_address";
        String noStation = "999";
        List<Firestations> listFirestations;
        Firestations firestation;
        firestation = createFirestation(addressStation, noStation);
        listFirestations = createListFirestationsTest(addressStation, noStation, firestation);
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        List<String> listAllergies;
        listAllergies = createListAllergiesTest();
        List<String> listMedications;
        listMedications = createListMedicationsTest();
        List<Medicalrecords> listMedicalrecords;
        Medicalrecords medicalrecords;
        medicalrecords = createMedicalrecords(firstName, lastName, listAllergies, listMedications);

        boolean childOrAdult = false; // true = child
        medicalrecords = setBirthdayIfAdult(childOrAdult, medicalrecords);
        listMedicalrecords = createListMedicalrecordsTest(medicalrecords);

        String elemjsonFirestation = "firestations";
        String elemjsonPerson = "persons";
        String elemjsonMedicalrecord = "medicalrecords";
        when(getListsElementsJson.getFirestations(elemjsonFirestation)).thenReturn(listFirestations);
        when(getListsElementsJson.getPersons(elemjsonPerson)).thenReturn(listPersons);
        when(getListsElementsJson.getMedicalrecords(elemjsonMedicalrecord)).thenReturn(listMedicalrecords);

        List<FoyerChildrenAdultsToFireStation> listFoyerChildrenAdults;
        listFoyerChildrenAdults = safetyNetService.personsOfStationAdultsAndChild(noStation);

        assertEquals(true, !listFoyerChildrenAdults.isEmpty());

    }

    @Test
    void testPersonsOfStationAdultsAndChildChildBirthday() {
        String addressStation = "TEST999_address";
        String noStation = "999";
        List<Firestations> listFirestations;
        Firestations firestation;
        firestation = createFirestation(addressStation, noStation);
        listFirestations = createListFirestationsTest(addressStation, noStation, firestation);
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        List<String> listAllergies;
        listAllergies = createListAllergiesTest();
        List<String> listMedications;
        listMedications = createListMedicationsTest();
        List<Medicalrecords> listMedicalrecords;
        Medicalrecords medicalrecords;
        medicalrecords = createMedicalrecords(firstName, lastName, listAllergies, listMedications);

        boolean childOrAdult = true; // true = child
        medicalrecords = setBirthdayIfAdult(childOrAdult, medicalrecords);
        listMedicalrecords = createListMedicalrecordsTest(medicalrecords);

        String elemjsonFirestation = "firestations";
        String elemjsonPerson = "persons";
        String elemjsonMedicalrecord = "medicalrecords";
        when(getListsElementsJson.getFirestations(elemjsonFirestation)).thenReturn(listFirestations);
        when(getListsElementsJson.getPersons(elemjsonPerson)).thenReturn(listPersons);
        when(getListsElementsJson.getMedicalrecords(elemjsonMedicalrecord)).thenReturn(listMedicalrecords);

        List<FoyerChildrenAdultsToFireStation> listFoyerChildrenAdults;
        listFoyerChildrenAdults = safetyNetService.personsOfStationAdultsAndChild(noStation);

        assertEquals(true, !listFoyerChildrenAdults.isEmpty());

    }

    @Test
    void testPersonsOfStationAdultsAndChildPersonStationNoExist() {
        String addressStation = "TEST999_address";
        String noStation = "999";
        List<Firestations> listFirestations;
        Firestations firestation;
        firestation = createFirestation(addressStation, noStation);
        listFirestations = createListFirestationsTest(addressStation, noStation, firestation);
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        List<String> listAllergies;
        listAllergies = createListAllergiesTest();
        List<String> listMedications;
        listMedications = createListMedicationsTest();
        List<Medicalrecords> listMedicalrecords;
        Medicalrecords medicalrecords;
        medicalrecords = createMedicalrecords(firstName, lastName, listAllergies, listMedications);

        boolean childOrAdult = true; // true = child
        medicalrecords = setBirthdayIfAdult(childOrAdult, medicalrecords);
        listMedicalrecords = createListMedicalrecordsTest(medicalrecords);

        String elemjsonFirestation = "firestations";
        String elemjsonPerson = "persons";
        String elemjsonMedicalrecord = "medicalrecords";
        when(getListsElementsJson.getFirestations(elemjsonFirestation)).thenReturn(listFirestations);
        when(getListsElementsJson.getPersons(elemjsonPerson)).thenReturn(listPersons);
        when(getListsElementsJson.getMedicalrecords(elemjsonMedicalrecord)).thenReturn(listMedicalrecords);

        List<FoyerChildrenAdultsToFireStation> listFoyerChildrenAdults;
        listFoyerChildrenAdults = safetyNetService.personsOfStationAdultsAndChild(noStation + "99");

        assertEquals(true, !listFoyerChildrenAdults.isEmpty());

    }

    @Test
    void testPersonsOfStationAdultsAndChildPersonStationPersonNoExistInMedicalrecords() {
        String addressStation = "TEST999_address";
        String noStation = "999";
        List<Firestations> listFirestations;
        Firestations firestation;
        firestation = createFirestation(addressStation, noStation);
        listFirestations = createListFirestationsTest(addressStation, noStation, firestation);
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        List<String> listAllergies;
        listAllergies = createListAllergiesTest();
        List<String> listMedications;
        listMedications = createListMedicationsTest();
        List<Medicalrecords> listMedicalrecords;
        Medicalrecords medicalrecords;
        medicalrecords = createMedicalrecords(firstName, lastName, listAllergies, listMedications);

        boolean childOrAdult = true; // true = child
        medicalrecords = setBirthdayIfAdult(childOrAdult, medicalrecords);
        medicalrecords.setFirstName(firstName + "AnotherFirstName");
        medicalrecords.setLastName(lastName + "AnotherLastName");
        listMedicalrecords = createListMedicalrecordsTest(medicalrecords);

        String elemjsonFirestation = "firestations";
        String elemjsonPerson = "persons";
        String elemjsonMedicalrecord = "medicalrecords";
        when(getListsElementsJson.getFirestations(elemjsonFirestation)).thenReturn(listFirestations);
        when(getListsElementsJson.getPersons(elemjsonPerson)).thenReturn(listPersons);
        when(getListsElementsJson.getMedicalrecords(elemjsonMedicalrecord)).thenReturn(listMedicalrecords);

        List<FoyerChildrenAdultsToFireStation> listFoyerChildrenAdults;
        listFoyerChildrenAdults = safetyNetService.personsOfStationAdultsAndChild(noStation);

        assertEquals(true, !listFoyerChildrenAdults.isEmpty());

    }

    @Test
    void testChildPersonsAlertAddress() throws IOException, ParseException {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);

        List<String> listAllergies;
        listAllergies = createListAllergiesTest();
        List<String> listMedications;
        listMedications = createListMedicationsTest();
        List<Medicalrecords> listMedicalrecords;
        Medicalrecords medicalrecords;
        medicalrecords = createMedicalrecords(firstName, lastName, listAllergies, listMedications);
        boolean childOrAdult = true; // true = child
        medicalrecords = setBirthdayIfAdult(childOrAdult, medicalrecords);
        listMedicalrecords = createListMedicalrecordsTest(medicalrecords);

        String elemjsonPerson = "persons";
        String elemjsonMedicalrecord = "medicalrecords";
        when(getListsElementsJson.getPersons(elemjsonPerson)).thenReturn(listPersons);
        when(getListsElementsJson.getMedicalrecords(elemjsonMedicalrecord)).thenReturn(listMedicalrecords);

        List<ChildAlert> listChildAlert = new ArrayList<>();
        listChildAlert = safetyNetService.childPersonsAlertAddress(person.getAddress());

        assertEquals(true, !listChildAlert.isEmpty());
    }

    @Test
    void testChildPersonsAlertAddressNoExist() throws IOException, ParseException {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);

        List<String> listAllergies;
        listAllergies = createListAllergiesTest();
        List<String> listMedications;
        listMedications = createListMedicationsTest();
        List<Medicalrecords> listMedicalrecords;
        Medicalrecords medicalrecords;
        medicalrecords = createMedicalrecords(firstName, lastName, listAllergies, listMedications);

        boolean childOrAdult = true; // true = child
        medicalrecords = setBirthdayIfAdult(childOrAdult, medicalrecords);
        listMedicalrecords = createListMedicalrecordsTest(medicalrecords);

        String elemjsonPerson = "persons";
        String elemjsonMedicalrecord = "medicalrecords";
        when(getListsElementsJson.getPersons(elemjsonPerson)).thenReturn(listPersons);
        when(getListsElementsJson.getMedicalrecords(elemjsonMedicalrecord)).thenReturn(listMedicalrecords);

        List<ChildAlert> listChildAlert = new ArrayList<>();
        listChildAlert = safetyNetService.childPersonsAlertAddress(person.getAddress() + "TestNoAddressExist");

        assertEquals(true, listChildAlert.isEmpty());
    }

    @Test
    void testPhoneAlertFirestation() throws IOException {
        String addressStation = "TEST999_address";
        String noStation = "999";
        List<Firestations> listFirestations;
        Firestations firestation;
        firestation = createFirestation(addressStation, noStation);
        listFirestations = createListFirestationsTest(addressStation, noStation, firestation);
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);

        String elemjsonFirestation = "firestations";
        String elemjsonPerson = "persons";
        when(getListsElementsJson.getFirestations(elemjsonFirestation)).thenReturn(listFirestations);
        when(getListsElementsJson.getPersons(elemjsonPerson)).thenReturn(listPersons);

        List<PhoneAlert> listPhoneAlerts = new ArrayList<>();
        listPhoneAlerts = safetyNetService.phoneAlertFirestation(noStation);

        assertEquals(true, !listPhoneAlerts.isEmpty());
    }

    @Test
    void testPhoneAlertFirestationMorePersons() throws IOException {
        String addressStation = "TEST999_address";
        String noStation = "999";
        List<Firestations> listFirestations;
        Firestations firestation;
        firestation = createFirestation(addressStation, noStation);
        listFirestations = createListFirestationsTest(addressStation, noStation, firestation);
        firestation = createFirestation(addressStation, noStation);
        listFirestations.add(firestation);

        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        person = createPerson(firstName, lastName);
        person.setFirstName(firstName + "AnotherPersonFirstName");
        person.setLastName(firstName + "AnotherPersonLastName");
        person.setPhone("000999");
        listPersons.add(person);

        String elemjsonFirestation = "firestations";
        String elemjsonPerson = "persons";
        when(getListsElementsJson.getFirestations(elemjsonFirestation)).thenReturn(listFirestations);
        when(getListsElementsJson.getPersons(elemjsonPerson)).thenReturn(listPersons);

        List<PhoneAlert> listPhoneAlerts = new ArrayList<>();
        listPhoneAlerts = safetyNetService.phoneAlertFirestation(noStation);

        assertEquals(true, !listPhoneAlerts.isEmpty());
    }

    @Test
    void testFireAddress() throws IOException, ParseException {
        String addressStation = "TEST999_address";
        String noStation = "999";
        List<Firestations> listFirestations;
        Firestations firestation;
        firestation = createFirestation(addressStation, noStation);
        listFirestations = createListFirestationsTest(addressStation, noStation, firestation);

        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        List<String> listAllergies;
        listAllergies = createListAllergiesTest();
        List<String> listMedications;
        listMedications = createListMedicationsTest();
        List<Medicalrecords> listMedicalrecords;
        Medicalrecords medicalrecords;
        medicalrecords = createMedicalrecords(firstName, lastName, listAllergies, listMedications);
        listMedicalrecords = createListMedicalrecordsTest(medicalrecords);
        String elemjsonMedicalrecord = "medicalrecords";
        String elemjsonFirestation = "firestations";
        String elemjsonPerson = "persons";
        List<FireAddress> listFireAddress = new ArrayList<>();

        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);

        when(getListsElementsJson.getPersons(elemjsonPerson)).thenReturn(listPersons);
        when(getListsElementsJson.getMedicalrecords(elemjsonMedicalrecord)).thenReturn(listMedicalrecords);
        when(getListsElementsJson.getFirestations(elemjsonFirestation)).thenReturn(listFirestations);

        listFireAddress = safetyNetService.fireAddress(addressStation);

        assertEquals(true, !listFireAddress.isEmpty());
    }

    @Test
    void testFireAddressAddressNoExist() throws IOException, ParseException {
        String addressStation = "TEST999_address";
        String noStation = "999";
        List<Firestations> listFirestations;
        Firestations firestation;
        firestation = createFirestation(addressStation, noStation);
        listFirestations = createListFirestationsTest(addressStation, noStation, firestation);

        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        List<String> listAllergies;
        listAllergies = createListAllergiesTest();
        List<String> listMedications;
        listMedications = createListMedicationsTest();
        List<Medicalrecords> listMedicalrecords;
        Medicalrecords medicalrecords;
        medicalrecords = createMedicalrecords(firstName, lastName, listAllergies, listMedications);
        listMedicalrecords = createListMedicalrecordsTest(medicalrecords);
        String elemjsonMedicalrecord = "medicalrecords";
        String elemjsonFirestation = "firestations";
        String elemjsonPerson = "persons";
        List<FireAddress> listFireAddress = new ArrayList<>();

        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);

        when(getListsElementsJson.getPersons(elemjsonPerson)).thenReturn(listPersons);
        when(getListsElementsJson.getMedicalrecords(elemjsonMedicalrecord)).thenReturn(listMedicalrecords);
        when(getListsElementsJson.getFirestations(elemjsonFirestation)).thenReturn(listFirestations);

        listFireAddress = safetyNetService.fireAddress(addressStation + "AnotherAddress");

        assertEquals(true, listFireAddress.isEmpty());
    }

    @Test
    void testFireAddressAddressPersonNoExist() throws IOException, ParseException {
        String addressStation = "TEST999_address";
        String noStation = "999";
        List<Firestations> listFirestations;
        Firestations firestation;
        firestation = createFirestation(addressStation, noStation);
        listFirestations = createListFirestationsTest(addressStation, noStation, firestation);

        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        List<String> listAllergies;
        listAllergies = createListAllergiesTest();
        List<String> listMedications;
        listMedications = createListMedicationsTest();
        List<Medicalrecords> listMedicalrecords;
        Medicalrecords medicalrecords;
        medicalrecords = createMedicalrecords(firstName + "AnotherFirstName", lastName + "AnotherLastName",
                listAllergies, listMedications);

        listMedicalrecords = createListMedicalrecordsTest(medicalrecords);
        String elemjsonMedicalrecord = "medicalrecords";
        String elemjsonFirestation = "firestations";
        String elemjsonPerson = "persons";
        List<FireAddress> listFireAddress = new ArrayList<>();

        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);

        when(getListsElementsJson.getPersons(elemjsonPerson)).thenReturn(listPersons);
        when(getListsElementsJson.getMedicalrecords(elemjsonMedicalrecord)).thenReturn(listMedicalrecords);
        when(getListsElementsJson.getFirestations(elemjsonFirestation)).thenReturn(listFirestations);

        listFireAddress = safetyNetService.fireAddress(addressStation);

        assertEquals(true, listFireAddress.isEmpty());
    }

    @Test
    void testStationListFirestation() throws IOException, ParseException {
        String addressStation = "TEST999_address";
        String noStation = "999";
        List<Firestations> listFirestations;
        Firestations firestation;
        firestation = createFirestation(addressStation, noStation);
        listFirestations = createListFirestationsTest(addressStation, noStation, firestation);

        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        String elemjsonFirestation = "firestations";
        String elemjsonPerson = "persons";
        String elemjsonMedicalrecord = "medicalrecords";

        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);

        List<String> listAllergies;
        listAllergies = createListAllergiesTest();
        List<String> listMedications;
        listMedications = createListMedicationsTest();
        List<Medicalrecords> listMedicalrecords;
        Medicalrecords medicalrecords;
        medicalrecords = createMedicalrecords(firstName, lastName, listAllergies, listMedications);

        listMedicalrecords = createListMedicalrecordsTest(medicalrecords);

        List<PersonsFireStation> listPersonsFireStations = new ArrayList<>();

        when(getListsElementsJson.getPersons(elemjsonPerson)).thenReturn(listPersons);
        when(getListsElementsJson.getFirestations(elemjsonFirestation)).thenReturn(listFirestations);
        when(getListsElementsJson.getMedicalrecords(elemjsonMedicalrecord)).thenReturn(listMedicalrecords);

        List<String> station = new ArrayList<>();
        station.add(0, noStation);
        listPersonsFireStations = safetyNetService.stationListFirestation(station);

        assertEquals(true, !listPersonsFireStations.isEmpty());
    }

    @Test
    void testStationListFirestationNoStation() throws IOException, ParseException {
        String addressStation = "TEST999_address";
        String noStation = "999";
        List<Firestations> listFirestations;
        Firestations firestation;
        firestation = createFirestation(addressStation, noStation);
        listFirestations = createListFirestationsTest(addressStation, noStation, firestation);

        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        String elemjsonFirestation = "firestations";
        String elemjsonPerson = "persons";

        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);

        List<PersonsFireStation> listPersonsFireStations = new ArrayList<>();

        when(getListsElementsJson.getPersons(elemjsonPerson)).thenReturn(listPersons);
        when(getListsElementsJson.getFirestations(elemjsonFirestation)).thenReturn(listFirestations);

        List<String> station = new ArrayList<>();
        station.add(0, noStation + "99");
        listPersonsFireStations = safetyNetService.stationListFirestation(station);

        assertEquals(true, listPersonsFireStations.isEmpty());
    }

    @Test
    void testStationListFirestationAddressNotExist() throws IOException, ParseException {
        String addressStation = "TEST999_address";
        String noStation = "999";
        List<Firestations> listFirestations;
        Firestations firestation;
        firestation = createFirestation(addressStation, noStation);
        listFirestations = createListFirestationsTest(addressStation, noStation, firestation);

        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        String elemjsonFirestation = "firestations";
        String elemjsonPerson = "persons";
        String elemjsonMedicalrecord = "medicalrecords";

        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        person = createPerson(firstName, lastName);
        person.setAddress(addressStation + "AnotherAddress");
        listPersons = createListPersonsTest(firstName, lastName, person);

        List<String> listAllergies;
        listAllergies = createListAllergiesTest();
        List<String> listMedications;
        listMedications = createListMedicationsTest();
        List<Medicalrecords> listMedicalrecords;
        Medicalrecords medicalrecords;
        medicalrecords = createMedicalrecords(firstName, lastName, listAllergies, listMedications);

        listMedicalrecords = createListMedicalrecordsTest(medicalrecords);

        List<PersonsFireStation> listPersonsFireStations = new ArrayList<>();

        when(getListsElementsJson.getPersons(elemjsonPerson)).thenReturn(listPersons);
        when(getListsElementsJson.getFirestations(elemjsonFirestation)).thenReturn(listFirestations);
        when(getListsElementsJson.getMedicalrecords(elemjsonMedicalrecord)).thenReturn(listMedicalrecords);

        List<String> station = new ArrayList<>();
        station.add(0, noStation);
        listPersonsFireStations = safetyNetService.stationListFirestation(station);

        assertEquals(true, !listPersonsFireStations.isEmpty());
    }

    @Test
    void testStationListFirestationAddressNotMedicalRecords() throws IOException, ParseException {
        String addressStation = "TEST999_address";
        String noStation = "999";
        List<Firestations> listFirestations;
        Firestations firestation;
        firestation = createFirestation(addressStation, noStation);
        listFirestations = createListFirestationsTest(addressStation, noStation, firestation);

        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        String elemjsonFirestation = "firestations";
        String elemjsonPerson = "persons";
        String elemjsonMedicalrecord = "medicalrecords";

        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);

        List<String> listAllergies;
        listAllergies = createListAllergiesTest();
        List<String> listMedications;
        listMedications = createListMedicationsTest();
        List<Medicalrecords> listMedicalrecords;
        Medicalrecords medicalrecords;
        medicalrecords = createMedicalrecords(firstName + "AnotherFirstName", lastName + "AnotherLastName",
                listAllergies, listMedications);
        listMedicalrecords = createListMedicalrecordsTest(medicalrecords);

        List<PersonsFireStation> listPersonsFireStations = new ArrayList<>();

        when(getListsElementsJson.getPersons(elemjsonPerson)).thenReturn(listPersons);
        when(getListsElementsJson.getFirestations(elemjsonFirestation)).thenReturn(listFirestations);
        when(getListsElementsJson.getMedicalrecords(elemjsonMedicalrecord)).thenReturn(listMedicalrecords);

        List<String> station = new ArrayList<>();
        station.add(0, noStation);
        listPersonsFireStations = safetyNetService.stationListFirestation(station);

        assertEquals(true, !listPersonsFireStations.isEmpty());
    }

    @Test
    void testPersonInfo() throws IOException, ParseException {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        String elemjsonPerson = "persons";
        String elemjsonMedicalrecord = "medicalrecords";

        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);

        List<String> listAllergies;
        listAllergies = createListAllergiesTest();
        List<String> listMedications;
        listMedications = createListMedicationsTest();
        List<Medicalrecords> listMedicalrecords;
        Medicalrecords medicalrecords;
        medicalrecords = createMedicalrecords(firstName, lastName, listAllergies, listMedications);

        listMedicalrecords = createListMedicalrecordsTest(medicalrecords);

        List<PersonInfo> listPersonInfo = new ArrayList<>();

        when(getListsElementsJson.getPersons(elemjsonPerson)).thenReturn(listPersons);
        when(getListsElementsJson.getMedicalrecords(elemjsonMedicalrecord)).thenReturn(listMedicalrecords);

        listPersonInfo = safetyNetService.personInfo(firstName, lastName);

        assertEquals(true, !listPersonInfo.isEmpty());
    }

    @Test
    void testPersonInfoPersonNotExist() throws IOException, ParseException {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        String elemjsonPerson = "persons";
        String elemjsonMedicalrecord = "medicalrecords";

        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);

        List<String> listAllergies;
        listAllergies = createListAllergiesTest();
        List<String> listMedications;
        listMedications = createListMedicationsTest();
        List<Medicalrecords> listMedicalrecords;
        Medicalrecords medicalrecords;
        medicalrecords = createMedicalrecords(firstName, lastName, listAllergies, listMedications);

        listMedicalrecords = createListMedicalrecordsTest(medicalrecords);

        List<PersonInfo> listPersonInfo = new ArrayList<>();

        when(getListsElementsJson.getPersons(elemjsonPerson)).thenReturn(listPersons);
        when(getListsElementsJson.getMedicalrecords(elemjsonMedicalrecord)).thenReturn(listMedicalrecords);

        listPersonInfo = safetyNetService.personInfo(firstName + "AnotherPerson", lastName + "AnotherPerson");

        assertEquals(true, listPersonInfo.isEmpty());
    }

    @Test
    void testPersonInfoPersonFirstNameNotExist() throws IOException, ParseException {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        String elemjsonPerson = "persons";
        String elemjsonMedicalrecord = "medicalrecords";

        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);

        List<String> listAllergies;
        listAllergies = createListAllergiesTest();
        List<String> listMedications;
        listMedications = createListMedicationsTest();
        List<Medicalrecords> listMedicalrecords;
        Medicalrecords medicalrecords;
        medicalrecords = createMedicalrecords(firstName, lastName, listAllergies, listMedications);

        listMedicalrecords = createListMedicalrecordsTest(medicalrecords);

        List<PersonInfo> listPersonInfo = new ArrayList<>();

        when(getListsElementsJson.getPersons(elemjsonPerson)).thenReturn(listPersons);
        when(getListsElementsJson.getMedicalrecords(elemjsonMedicalrecord)).thenReturn(listMedicalrecords);

        listPersonInfo = safetyNetService.personInfo(firstName + "AnotherPerson", lastName);

        assertEquals(true, listPersonInfo.isEmpty());
    }

    @Test
    void testPersonInfoPersonLastNameNotExist() throws IOException, ParseException {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        String elemjsonPerson = "persons";
        String elemjsonMedicalrecord = "medicalrecords";

        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);

        List<String> listAllergies;
        listAllergies = createListAllergiesTest();
        List<String> listMedications;
        listMedications = createListMedicationsTest();
        List<Medicalrecords> listMedicalrecords;
        Medicalrecords medicalrecords;
        medicalrecords = createMedicalrecords(firstName, lastName, listAllergies, listMedications);

        listMedicalrecords = createListMedicalrecordsTest(medicalrecords);

        List<PersonInfo> listPersonInfo = new ArrayList<>();

        when(getListsElementsJson.getPersons(elemjsonPerson)).thenReturn(listPersons);
        when(getListsElementsJson.getMedicalrecords(elemjsonMedicalrecord)).thenReturn(listMedicalrecords);

        listPersonInfo = safetyNetService.personInfo(firstName, lastName + "AnotherPerson");

        assertEquals(true, listPersonInfo.isEmpty());
    }

    @Test
    void testPersonInfoAndAnotherFirstNamePerson() throws IOException, ParseException {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        String elemjsonPerson = "persons";
        String elemjsonMedicalrecord = "medicalrecords";

        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        person = createPerson(firstName + "AnotherFirstName", lastName);
        listPersons.add(person);

        List<String> listAllergies;
        listAllergies = createListAllergiesTest();
        List<String> listMedications;
        listMedications = createListMedicationsTest();
        List<Medicalrecords> listMedicalrecords;
        Medicalrecords medicalrecords;
        medicalrecords = createMedicalrecords(firstName, lastName, listAllergies, listMedications);

        listMedicalrecords = createListMedicalrecordsTest(medicalrecords);

        List<PersonInfo> listPersonInfo = new ArrayList<>();

        when(getListsElementsJson.getPersons(elemjsonPerson)).thenReturn(listPersons);
        when(getListsElementsJson.getMedicalrecords(elemjsonMedicalrecord)).thenReturn(listMedicalrecords);

        listPersonInfo = safetyNetService.personInfo(firstName, lastName);

        assertEquals(true, !listPersonInfo.isEmpty());
    }

    @Test
    void testPersonInfoAndAnotherFirstAndLastNamePerson() throws IOException, ParseException {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        String elemjsonPerson = "persons";
        String elemjsonMedicalrecord = "medicalrecords";

        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        person = createPerson(firstName + "AnotherFirstName", lastName + "AnotherLastName");
        listPersons.add(person);

        List<String> listAllergies;
        listAllergies = createListAllergiesTest();
        List<String> listMedications;
        listMedications = createListMedicationsTest();
        List<Medicalrecords> listMedicalrecords;
        Medicalrecords medicalrecords;
        medicalrecords = createMedicalrecords(firstName, lastName, listAllergies, listMedications);

        listMedicalrecords = createListMedicalrecordsTest(medicalrecords);

        List<PersonInfo> listPersonInfo = new ArrayList<>();

        when(getListsElementsJson.getPersons(elemjsonPerson)).thenReturn(listPersons);
        when(getListsElementsJson.getMedicalrecords(elemjsonMedicalrecord)).thenReturn(listMedicalrecords);

        listPersonInfo = safetyNetService.personInfo(firstName, lastName);

        assertEquals(true, !listPersonInfo.isEmpty());
    }

    @Test
    void testCommunityEmail() throws IOException, ParseException {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        String elemjsonPerson = "persons";

        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        List<CommunityEmail> listCommunityEmail = new ArrayList<>();

        when(getListsElementsJson.getPersons(elemjsonPerson)).thenReturn(listPersons);
        listCommunityEmail = safetyNetService.communityEmail(person.getCity());

        assertEquals(true, !listCommunityEmail.isEmpty());
    }

    @Test
    void testCommunityEmailCityNotExist() throws IOException, ParseException {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        String elemjsonPerson = "persons";

        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        List<CommunityEmail> listCommunityEmail = new ArrayList<>();

        when(getListsElementsJson.getPersons(elemjsonPerson)).thenReturn(listPersons);
        listCommunityEmail = safetyNetService.communityEmail(person.getCity() + "OtherCity");

        assertEquals(true, !listCommunityEmail.isEmpty());
    }

    @Test
    void testCommunityEmailOtheSameEmail() throws IOException, ParseException {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        String elemjsonPerson = "persons";

        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        person = createPerson(firstName, lastName);
        listPersons.add(person);
        List<CommunityEmail> listCommunityEmail = new ArrayList<>();

        when(getListsElementsJson.getPersons(elemjsonPerson)).thenReturn(listPersons);
        listCommunityEmail = safetyNetService.communityEmail(person.getCity());

        assertEquals(true, !listCommunityEmail.isEmpty());
    }

    @Test
    void testCommunityEmailOtheNotSameEmail() throws IOException, ParseException {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        String elemjsonPerson = "persons";

        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);
        person = createPerson(firstName, lastName);
        person.setEmail("otherEmail@Test");
        listPersons.add(person);
        List<CommunityEmail> listCommunityEmail = new ArrayList<>();

        when(getListsElementsJson.getPersons(elemjsonPerson)).thenReturn(listPersons);
        listCommunityEmail = safetyNetService.communityEmail(person.getCity());

        assertEquals(true, !listCommunityEmail.isEmpty());
    }

    private Medicalrecords setBirthdayIfAdult(boolean childOrAdult, Medicalrecords medicalrecords) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Calendar calendar = new GregorianCalendar();
        LocalDate now = LocalDate.now();
        java.util.Date datebirthday;
        LocalDate birthdate;
        Period periode;
        try {
            datebirthday = sdf.parse(medicalrecords.getBirthdate());
        } catch (ParseException e) {
            return null;
        }

        int adult = 18;
        calendar.setTime(datebirthday);
        birthdate = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
        periode = Period.between(birthdate, now);
        if (periode.getYears() < 18) { // child
            if (!childOrAdult) {
                String birthday = "01/01/" + (birthdate.getYear() - (adult - periode.getYears()));
                medicalrecords.setBirthdate(birthday);
            }
        }
        return medicalrecords;
    }

    private List<String> createListMedicationsTest() {
        List<String> listMedications = new ArrayList<>();
        listMedications.add(0, "medications0");
        listMedications.add(1, "medications1");
        listMedications.add(2, "medications2");
        return listMedications;
    }

    private List<String> createListAllergiesTest() {
        List<String> listAllergies = new ArrayList<>();
        listAllergies.add(0, "allergie0");
        listAllergies.add(1, "allergie1");
        listAllergies.add(2, "allergie2");
        return listAllergies;
    }

    private List<Medicalrecords> createListMedicalrecordsTest(Medicalrecords medicalrecords) {
        List<Medicalrecords> listMedicalrecords = new ArrayList<>();
        listMedicalrecords.add(medicalrecords);
        return listMedicalrecords;
    }

    private Medicalrecords createMedicalrecords(String firstName, String lastName, List<String> listAllergies,
            List<String> listMedications) {
        Medicalrecords medicalrecords = new Medicalrecords();
        medicalrecords.setFirstName(firstName);
        medicalrecords.setLastName(lastName);
        medicalrecords.setAllergies(listAllergies);
        medicalrecords.setMedications(listMedications);
        medicalrecords.setBirthdate("03/05/2010");
        return medicalrecords;
    }

    private List<Firestations> createListFirestationsTest(String addressStation, String noStation,
            Firestations firestation) {
        List<Firestations> listFirestations = new ArrayList<>();
        listFirestations.add(firestation);
        return listFirestations;
    }

    private Firestations createFirestation(String addressStation, String noStation) {
        Firestations firestation = new Firestations();
        firestation.setAddress(addressStation);
        firestation.setStation(noStation);
        return firestation;
    }

    private List<Persons> createListPersonsTest(String firstName, String lastName, Persons person) {
        List<Persons> listPersons = new ArrayList<>();
        listPersons.add(person);
        return listPersons;
    }

    private Persons createPerson(String firstName, String lastName) {
        Persons person = new Persons();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setAddress("TEST999_address");
        person.setCity("city");
        person.setEmail("email");
        person.setPhone("phone");
        person.setZip("zip");
        return person;
    }

}