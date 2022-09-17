package com.openclassrooms.new_safety_net;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.new_safety_net.controller.NewSafetyAlertController;
import com.openclassrooms.new_safety_net.model.ChildAlert;
import com.openclassrooms.new_safety_net.model.ChildrenOrAdults;
import com.openclassrooms.new_safety_net.model.Firestations;
import com.openclassrooms.new_safety_net.model.FoyerChildrenAdultsToFireStation;
import com.openclassrooms.new_safety_net.model.Medicalrecords;
import com.openclassrooms.new_safety_net.model.Persons;
import com.openclassrooms.new_safety_net.model.PersonsOfFireStation;
import com.openclassrooms.new_safety_net.model.PhoneAlert;
import com.openclassrooms.new_safety_net.service.GetListsElementsJson;
import com.openclassrooms.new_safety_net.service.LoggerApiNewSafetyNet;
import com.openclassrooms.new_safety_net.service.SafetyNetService;

@WebMvcTest(controllers = NewSafetyAlertController.class)
public class NewSafetyAlertControllerTests {

    // Récupération de notre logger.
    private static final Logger LOGGER = LogManager.getLogger(NewSafetyAlertControllerTests.class);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SafetyNetService safetyNetServiceInterface;

    @MockBean
    private GetListsElementsJson getListsElementsJson;

    @BeforeAll
    public static void activateLoggerForTests() {
        LoggerApiNewSafetyNet loggerApiNewSafetyNet = new LoggerApiNewSafetyNet();
        loggerApiNewSafetyNet.setLoggerForTests();

    }

    @Test
    void testGetPersons() throws Exception {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        String elemjson = "persons";
        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);

        when(getListsElementsJson.getPersons(elemjson)).thenReturn(listPersons);

        mockMvc.perform(get("/persons")).andExpect(status().isOk());

        LOGGER.info("Fin test : Le système RETOURNE une liste de toutes les personnes");
    }

    @Test
    void testGetPersonsNoPerson() throws Exception {
        List<Persons> listPersons = new ArrayList<>();
        String elemjson = "persons";

        when(getListsElementsJson.getPersons(elemjson)).thenReturn(listPersons);

        mockMvc.perform(get("/persons")).andExpect(status().is(204));
        LOGGER.info("Fin test : Le système RETOURNE une liste vide.");
    }

    @Test
    void testGetPersonsNoElementPersonsInJsons() throws Exception {
        String elemjson = "persons";

        when(getListsElementsJson.getPersons(elemjson)).thenThrow(NullPointerException.class);

        mockMvc.perform(get("/persons")).andExpect(status().is(404));
        LOGGER.info("Fin test : la liste est nulle. Personne dans la liste.");
    }

    @Test
    void testGetPersonsException() throws Exception {
        String elemjson = "persons";

        when(getListsElementsJson.getPersons(elemjson)).thenThrow(IOException.class);

        mockMvc.perform(get("/persons")).andExpect(status().is(404));
        LOGGER.info("Fin test : Exception !. Personne dans la liste.");
    }

    @Test
    void testGetAPerson() throws Exception {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        String elemjson = "persons";
        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);

        when(getListsElementsJson.getPersons(elemjson)).thenReturn(listPersons);
        when(safetyNetServiceInterface.getAPerson(firstName + lastName, elemjson)).thenReturn(listPersons);

        mockMvc.perform(get("/person/" + firstName + lastName)).andExpect(status().isOk());

    }

    @Test
    void testGetAPersonException() throws Exception {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        String elemjson = "persons";
        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);

        when(getListsElementsJson.getPersons(elemjson)).thenReturn(listPersons);
        when(safetyNetServiceInterface.getAPerson(firstName + lastName, elemjson)).thenThrow(IOException.class);

        mockMvc.perform(get("/person/" + firstName + lastName)).andExpect(status().is(404));

    }

    @Test
    void testGetAPersonNoPersons() throws Exception {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        String elemjson = "persons";
        List<Persons> listPersons = new ArrayList<>();

        when(getListsElementsJson.getPersons(elemjson)).thenReturn(listPersons);
        when(safetyNetServiceInterface.getAPerson(firstName + lastName, elemjson)).thenReturn(listPersons);

        mockMvc.perform(get("/person/" + firstName + lastName)).andExpect(status().is(204));

    }

    @Test
    void testGetAPersonPersonsNull() throws Exception {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        String elemjson = "persons";
        List<Persons> listPersons = new ArrayList<>();

        when(getListsElementsJson.getPersons(elemjson)).thenReturn(listPersons);
        when(safetyNetServiceInterface.getAPerson(firstName + lastName, elemjson))
                .thenThrow(NullPointerException.class);

        mockMvc.perform(get("/person/" + firstName + lastName)).andExpect(status().is(404));

    }

    @Test
    void testAddPerson() throws Exception {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);

        String body = "{\r\n" + "\"firstName\": \"" + firstName + "\",\r\n" + "\"lastName\": \"" + lastName
                + "\",\r\n" + "\"address\": \"1509 Culver St\",\r\n" + "\"city\": \"Culver\",\r\n"
                + "\"zip\": \"97451\",\r\n" + "\"phone\": \"841-874-6512\",\r\n" + "\"email\": \"jaboyd@email.com\"\r\n"
                + "}";
        when(safetyNetServiceInterface.postPerson(any(Persons.class))).thenReturn(listPersons);
        // when(safetyNetServiceInterface.postPerson(person)).thenReturn(listPersons);

        mockMvc.perform(post("/person").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201));

    }

    @Test
    void testAddPersonNoListPersons() throws Exception {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        List<Persons> listPersons = new ArrayList<>();

        String body = "{\r\n" + "\"firstName\": \"" + firstName + "\",\r\n" + "\"lastName\": \"" + lastName
                + "\",\r\n" + "\"address\": \"1509 Culver St\",\r\n" + "\"city\": \"Culver\",\r\n"
                + "\"zip\": \"97451\",\r\n" + "\"phone\": \"841-874-6512\",\r\n" + "\"email\": \"jaboyd@email.com\"\r\n"
                + "}";
        when(safetyNetServiceInterface.postPerson(any(Persons.class))).thenReturn(listPersons);
        // when(safetyNetServiceInterface.postPerson(person)).thenReturn(listPersons);

        mockMvc.perform(post("/person").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));

    }

    @Test
    void testUpdatePerson() throws Exception {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        String body = "{\r\n" + "\"firstName\": \"" + firstName + "\",\r\n" + "\"lastName\": \"" + lastName
                + "\",\r\n" + "\"address\": \"1509 Culver St\",\r\n" + "\"city\": \"Culver\",\r\n"
                + "\"zip\": \"97451\",\r\n" + "\"phone\": \"841-874-6512\",\r\n" + "\"email\": \"jaboyd@email.com\"\r\n"
                + "}";

        when(safetyNetServiceInterface.putPerson(any(Persons.class), eq(firstName), eq(lastName))).thenReturn(true);
        // when(safetyNetServiceInterface.postPerson(person)).thenReturn(listPersons);

        mockMvc.perform(put("/person").content(body).param("firstName", firstName).param("lastName", lastName)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(200));

    }

    @Test
    void testUpdatePersonNotUpdated() throws Exception {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        String body = "{\r\n" + "\"firstName\": \"" + firstName + "\",\r\n" + "\"lastName\": \"" + lastName
                + "\",\r\n" + "\"address\": \"1509 Culver St\",\r\n" + "\"city\": \"Culver\",\r\n"
                + "\"zip\": \"97451\",\r\n" + "\"phone\": \"841-874-6512\",\r\n" + "\"email\": \"jaboyd@email.com\"\r\n"
                + "}";

        when(safetyNetServiceInterface.putPerson(any(Persons.class), eq(firstName), eq(lastName))).thenReturn(false);

        mockMvc.perform(put("/person").content(body).param("firstName", firstName).param("lastName", lastName)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(404));

    }

    @Test
    void testUpdatePersonNoFirstName() throws Exception {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        String body = "{\r\n" + "\"firstName\": \"" + firstName + "\",\r\n" + "\"lastName\": \"" + lastName
                + "\",\r\n" + "\"address\": \"1509 Culver St\",\r\n" + "\"city\": \"Culver\",\r\n"
                + "\"zip\": \"97451\",\r\n" + "\"phone\": \"841-874-6512\",\r\n" + "\"email\": \"jaboyd@email.com\"\r\n"
                + "}";

        when(safetyNetServiceInterface.putPerson(any(Persons.class), eq(firstName), eq(lastName))).thenReturn(true);

        mockMvc.perform(put("/person").content(body).param("firstName", "").param("lastName", lastName)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(400));

    }

    @Test
    void testUpdatePersonNoLastName() throws Exception {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        String body = "{\r\n" + "\"firstName\": \"" + firstName + "\",\r\n" + "\"lastName\": \"" + lastName
                + "\",\r\n" + "\"address\": \"1509 Culver St\",\r\n" + "\"city\": \"Culver\",\r\n"
                + "\"zip\": \"97451\",\r\n" + "\"phone\": \"841-874-6512\",\r\n" + "\"email\": \"jaboyd@email.com\"\r\n"
                + "}";

        when(safetyNetServiceInterface.putPerson(any(Persons.class), eq(firstName), eq(lastName))).thenReturn(true);

        mockMvc.perform(put("/person").content(body).param("firstName", firstName).param("lastName", "")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(400));

    }

    @Test
    void testUpdatePersonNoParam() throws Exception {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        String body = "{\r\n" + "\"firstName\": \"" + firstName + "\",\r\n" + "\"lastName\": \"" + lastName
                + "\",\r\n" + "\"address\": \"1509 Culver St\",\r\n" + "\"city\": \"Culver\",\r\n"
                + "\"zip\": \"97451\",\r\n" + "\"phone\": \"841-874-6512\",\r\n" + "\"email\": \"jaboyd@email.com\"\r\n"
                + "}";

        when(safetyNetServiceInterface.putPerson(any(Persons.class), eq(""), eq(""))).thenReturn(true);

        mockMvc.perform(put("/person").content(body).param("firstName", firstName).param("lastName", "")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(400));

    }

    @Test
    void testDeletePerson() throws Exception {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        when(safetyNetServiceInterface.deletePerson(firstName, lastName)).thenReturn(true);

        mockMvc.perform(delete("/person").param("firstName", firstName).param("lastName", lastName)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(200));

    }

    @Test
    void testDeletePersonNotDeleted() throws Exception {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        when(safetyNetServiceInterface.deletePerson(firstName, lastName)).thenReturn(false);

        mockMvc.perform(delete("/person").param("firstName", firstName).param("lastName", lastName)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(404));

    }

    @ParameterizedTest
    @ValueSource(strings = { "", "" })
    void testDeletePersonNoFirstNameAndLastName(String arg) throws Exception {

        mockMvc.perform(delete("/person").param("firstName", arg).param("lastName", arg)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(400));

    }

    @Test
    void testDeletePersonNoFirstName() throws Exception {
        String firstName = "";
        String lastName = "TEST999_LastName";

        mockMvc.perform(delete("/person").param("firstName", firstName).param("lastName", lastName)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(400));

    }

    @Test
    void testDeletePersonNoLastName() throws Exception {
        String firstName = "TEST999_FirstName";
        String lastName = "";

        mockMvc.perform(delete("/person").param("firstName", firstName).param("lastName", lastName)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(400));

    }

    @Test
    void testGetFirestations() throws Exception {
        String addressStation = "addressStation";
        String noStation = "999";
        String elemjson = "firestations";
        List<Firestations> listFirestations;
        Firestations firestations;
        firestations = createFirestation(addressStation, noStation);
        listFirestations = createListFirestationsTest(addressStation, noStation, firestations);

        when(getListsElementsJson.getFirestations(elemjson)).thenReturn(listFirestations);

        mockMvc.perform(get("/firestations")).andExpect(status().isOk());

        LOGGER.info("Fin test : Le système RETOURNE une liste de toutes les firestations");
    }

    @Test
    void testGetFirestationsNoFirestations() throws Exception {
        String elemjson = "firestations";
        List<Firestations> listFirestations = new ArrayList<>();

        when(getListsElementsJson.getFirestations(elemjson)).thenReturn(listFirestations);

        mockMvc.perform(get("/firestations")).andExpect(status().is(204));

        LOGGER.info("Fin test : Le système ne RETOURNE pas une liste de toutes les firestations");
    }

    @Test
    void testAddFirestations() throws Exception {
        String addressStation = "addressStation";
        String noStation = "999";

        String body = "{\r\n" + "\"address\": \"" + addressStation + "\",\r\n" + "\"station\": \"" + noStation
                + "\"\r\n" + "}";

        when(safetyNetServiceInterface.postFirestation(any(Firestations.class))).thenReturn(true);
        // when(safetyNetServiceInterface.postPerson(person)).thenReturn(listPersons);

        mockMvc.perform(post("/firestation").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201));
    }

    @Test
    void testAddFirestationsNoFirestation() throws Exception {
        String addressStation = "addressStation";
        String noStation = "999";

        String body = "{\r\n" + "\"address\": \"" + addressStation + "\",\r\n" + "\"station\": \"" + noStation
                + "\"\r\n" + "}";

        when(safetyNetServiceInterface.postFirestation(any(Firestations.class))).thenReturn(false);
        // when(safetyNetServiceInterface.postPerson(person)).thenReturn(listPersons);

        mockMvc.perform(post("/firestation").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    void testUpdateFirestations() throws Exception {
        String addressStation = "addressStation";
        String noStation = "999";

        String body = "{\r\n" + "\"address\": \"" + addressStation + "\",\r\n" + "\"station\": \"" + noStation
                + "\"\r\n" + "}";

        when(safetyNetServiceInterface.putFirestation(any(Firestations.class), eq(addressStation))).thenReturn(true);

        mockMvc.perform(put("/firestation").content(body).param("address", addressStation)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(200));
    }

    @Test
    void testUpdateFirestationsNotUpdated() throws Exception {
        String addressStation = "addressStation";
        String noStation = "999";

        String body = "{\r\n" + "\"address\": \"" + addressStation + "\",\r\n" + "\"station\": \"" + noStation
                + "\"\r\n" + "}";

        when(safetyNetServiceInterface.putFirestation(any(Firestations.class), eq(addressStation))).thenReturn(false);

        mockMvc.perform(put("/firestation").content(body).param("address", addressStation)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(404));
    }

    @Test
    void testUpdateFirestationsNoAddress() throws Exception {
        String addressStation = "addressStation";
        String noStation = "999";

        String body = "{\r\n" + "\"address\": \"" + addressStation + "\",\r\n" + "\"station\": \"" + noStation
                + "\"\r\n" + "}";

        when(safetyNetServiceInterface.putFirestation(any(Firestations.class), eq(addressStation))).thenReturn(true);

        mockMvc.perform(put("/firestation").content(body).param("address", "")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(400));
    }

    @Test
    void testDeleteFirestation() throws Exception {
        String addressStation = "addressStation";
        String noStation = "999";

        when(safetyNetServiceInterface.deleteFirestation(addressStation, noStation)).thenReturn(true);

        mockMvc.perform(
                delete("/firestation").param("address", addressStation).param("stationNumber", noStation)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));

    }

    @Test
    void testDeleteFirestationNotDeleted() throws Exception {
        String addressStation = "addressStation";
        String noStation = "999";

        when(safetyNetServiceInterface.deleteFirestation(addressStation, noStation)).thenReturn(false);

        mockMvc.perform(
                delete("/firestation").param("address", addressStation).param("stationNumber", noStation)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));

    }

    @Test
    void testDeleteFirestationNoParam() throws Exception {
        mockMvc.perform(
                delete("/firestation").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));

    }

    @Test
    void testDeleteFirestationNoParam1() throws Exception {
        String addressStation = "addressStation";
        String noStation = "999";

        when(safetyNetServiceInterface.deleteFirestation(addressStation, noStation)).thenReturn(false);
        mockMvc.perform(
                delete("/firestation").param("address", addressStation).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));

    }

    @Test
    void testDeleteFirestationNoParam2() throws Exception {
        String addressStation = "addressStation";
        String noStation = "999";

        when(safetyNetServiceInterface.deleteFirestation(addressStation, noStation)).thenReturn(false);
        mockMvc.perform(
                delete("/firestation").param("stationNumber", noStation).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));

    }

    @Test
    void testGetMedicalrecords() throws Exception {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        List<String> listAllergies;
        listAllergies = createListAllergiesTest();
        List<String> listMedications;
        listMedications = createListMedicationsTest();
        String elemjson = "medicalrecords";
        List<Medicalrecords> listMedicalrecords;
        Medicalrecords medicalrecords;
        medicalrecords = createMedicalrecords(firstName, lastName, listAllergies, listMedications);
        listMedicalrecords = createListMedicalrecordsTest(medicalrecords);

        when(getListsElementsJson.getMedicalrecords(elemjson)).thenReturn(listMedicalrecords);

        mockMvc.perform(get("/medicalrecords")).andExpect(status().isOk());

        LOGGER.info("Fin test : Le système RETOURNE une liste de tous les medicalrecords");
    }

    @Test
    void testGetMedicalrecordsNoMedicalrecords() throws Exception {
        String elemjson = "medicalrecords";
        List<Medicalrecords> listMedicalrecords = new ArrayList<>();

        when(getListsElementsJson.getMedicalrecords(elemjson)).thenReturn(listMedicalrecords);

        mockMvc.perform(get("/medicalrecords")).andExpect(status().is(204));

        LOGGER.info("Fin test : Le système ne RETOURNE pas une liste de tous les medicalrecords");
    }

    @Test
    void testAddMedicalRecord() throws Exception {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        String body = "{\r\n" + "\"firstName\": \"" + firstName + "\",\r\n"
                + "\"lastName\": \"" + lastName + "\",\r\n" + "\"birthdate\": \"03/06/1984\",\r\n"
                + "\"medications\": [\r\n" + "\"Testaznol:350mg\",\r\n" + "\"hydrapermazol:100mg\"\r\n" + "],\r\n"
                + "\"allergies\": [\r\n" + "\"nillacilan\"\r\n" + "]\r\n" + "}";

        when(safetyNetServiceInterface.postMedicalRecord(any(Medicalrecords.class))).thenReturn(true);

        mockMvc.perform(post("/medicalrecord").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201));

    }

    @Test
    void testAddMedicalRecordNotAdded() throws Exception {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        String body = "{\r\n" + "\"firstName\": \"" + firstName + "\",\r\n"
                + "\"lastName\": \"" + lastName + "\",\r\n" + "\"birthdate\": \"03/06/1984\",\r\n"
                + "\"medications\": [\r\n" + "\"Testaznol:350mg\",\r\n" + "\"hydrapermazol:100mg\"\r\n" + "],\r\n"
                + "\"allergies\": [\r\n" + "\"nillacilan\"\r\n" + "]\r\n" + "}";

        when(safetyNetServiceInterface.postMedicalRecord(any(Medicalrecords.class))).thenReturn(false);

        mockMvc.perform(post("/medicalrecord").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));

    }

    @Test
    void testUpdateMedicalRecord() throws Exception {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        String body = "{\r\n" + "\"firstName\": \"" + firstName + "\",\r\n"
                + "\"lastName\": \"" + lastName + "\",\r\n" + "\"birthdate\": \"03/06/1984\",\r\n"
                + "\"medications\": [\r\n" + "\"Testaznol:350mg\",\r\n" + "\"hydrapermazol:100mg\"\r\n" + "],\r\n"
                + "\"allergies\": [\r\n" + "\"nillacilan\"\r\n" + "]\r\n" + "}";

        when(safetyNetServiceInterface.putMedicalRecord(any(Medicalrecords.class), eq(firstName), eq(lastName)))
                .thenReturn(true);

        mockMvc.perform(put("/medicalrecord").content(body).param("firstName", firstName).param("lastName", lastName)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(200));
    }

    @Test
    void testUpdateMedicalRecordNoUpdate() throws Exception {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        String body = "{\r\n" + "\"firstName\": \"" + firstName + "\",\r\n"
                + "\"lastName\": \"" + lastName + "\",\r\n" + "\"birthdate\": \"03/06/1984\",\r\n"
                + "\"medications\": [\r\n" + "\"Testaznol:350mg\",\r\n" + "\"hydrapermazol:100mg\"\r\n" + "],\r\n"
                + "\"allergies\": [\r\n" + "\"nillacilan\"\r\n" + "]\r\n" + "}";

        when(safetyNetServiceInterface.putMedicalRecord(any(Medicalrecords.class), eq(firstName), eq(lastName)))
                .thenReturn(false);

        mockMvc.perform(put("/medicalrecord").content(body).param("firstName", firstName).param("lastName", lastName)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(404));
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "" })
    void testUpdateMedicalRecordBlankParam(String arg) throws Exception {
        String firstName = "";
        String lastName = "";

        String body = "{\r\n" + "\"firstName\": \"" + firstName + "\",\r\n"
                + "\"lastName\": \"" + lastName + "\",\r\n" + "\"birthdate\": \"03/06/1984\",\r\n"
                + "\"medications\": [\r\n" + "\"Testaznol:350mg\",\r\n" + "\"hydrapermazol:100mg\"\r\n" + "],\r\n"
                + "\"allergies\": [\r\n" + "\"nillacilan\"\r\n" + "]\r\n" + "}";

        mockMvc.perform(put("/medicalrecord").content(body).param("firstName", arg).param("lastName", arg)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(400));
    }

    @Test
    void testUpdateMedicalRecordBlankParam1() throws Exception {
        String firstName = "";
        String lastName = "TEST999_LastName";

        String body = "{\r\n" + "\"firstName\": \"" + firstName + "\",\r\n"
                + "\"lastName\": \"" + lastName + "\",\r\n" + "\"birthdate\": \"03/06/1984\",\r\n"
                + "\"medications\": [\r\n" + "\"Testaznol:350mg\",\r\n" + "\"hydrapermazol:100mg\"\r\n" + "],\r\n"
                + "\"allergies\": [\r\n" + "\"nillacilan\"\r\n" + "]\r\n" + "}";

        mockMvc.perform(put("/medicalrecord").content(body).param("firstName", firstName).param("lastName", lastName)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(400));
    }

    @Test
    void testUpdateMedicalRecordBlankParam2() throws Exception {
        String firstName = "TEST999_FirstName";
        String lastName = "";

        String body = "{\r\n" + "\"firstName\": \"" + firstName + "\",\r\n"
                + "\"lastName\": \"" + lastName + "\",\r\n" + "\"birthdate\": \"03/06/1984\",\r\n"
                + "\"medications\": [\r\n" + "\"Testaznol:350mg\",\r\n" + "\"hydrapermazol:100mg\"\r\n" + "],\r\n"
                + "\"allergies\": [\r\n" + "\"nillacilan\"\r\n" + "]\r\n" + "}";

        mockMvc.perform(put("/medicalrecord").content(body).param("firstName", firstName).param("lastName", lastName)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(400));
    }

    @Test
    void testDeleteMedicalRecord() throws Exception {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        when(safetyNetServiceInterface.deleteMedicalRecord(firstName, lastName)).thenReturn(true);

        mockMvc.perform(
                delete("/medicalrecord").param("firstName", firstName).param("lastName", lastName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));

    }

    @Test
    void testDeleteMedicalRecordNoDeleted() throws Exception {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";

        when(safetyNetServiceInterface.deleteMedicalRecord(firstName, lastName)).thenReturn(false);

        mockMvc.perform(
                delete("/medicalrecord").param("firstName", firstName).param("lastName", lastName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    void testDeleteMedicalRecordEmptyParam1() throws Exception {
        String firstName = "";
        String lastName = "TEST999_LastName";

        mockMvc.perform(
                delete("/medicalrecord").param("firstName", firstName).param("lastName", lastName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));

    }

    @Test
    void testDeleteMedicalRecordEmptyParam2() throws Exception {
        String firstName = "TEST999_FirstName";
        String lastName = "";

        mockMvc.perform(
                delete("/medicalrecord").param("firstName", firstName).param("lastName", lastName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));

    }

    @Test
    void testDeleteMedicalRecordEmptyParam() throws Exception {
        String firstName = "";
        String lastName = "";

        mockMvc.perform(
                delete("/medicalrecord").param("firstName", firstName).param("lastName", lastName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));

    }

    /*
     * L'utilisateur accède à l’URL :
     * 
     * http://localhost:9090/firestation?stationNumber=<station_number>
     * 
     * Le système retourne une liste des personnes (prénom, nom, adresse, numéro de
     * téléphone) couvertes par la caserne de pompiers correspondante ainsi qu’un
     * décompte du nombre d’adultes (>18 ans) et du nombre d’enfants (<=18 ans)
     * 
     */

    @Test
    void testGetpersonsOfStationAdultsAndChild() throws Exception {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        Persons person;
        person = createPerson(firstName, lastName);

        String noStation = "999";

        List<PersonsOfFireStation> listPersonsChildren = new ArrayList<>();
        PersonsOfFireStation personsOfFireStation = new PersonsOfFireStation();
        personsOfFireStation.setDecompte("1");
        personsOfFireStation.setAddress(person.getAddress());
        personsOfFireStation.setFirstName(firstName);
        personsOfFireStation.setLastName(lastName);
        personsOfFireStation.setPhone(person.getPhone());
        listPersonsChildren.add(personsOfFireStation);

        List<FoyerChildrenAdultsToFireStation> listFoyerChildrenAdultsToFireStation = new ArrayList<>();
        listFoyerChildrenAdultsToFireStation.add(new FoyerChildrenAdultsToFireStation("1", listPersonsChildren));

        when(safetyNetServiceInterface.personsOfStationAdultsAndChild(noStation))
                .thenReturn(listFoyerChildrenAdultsToFireStation);

        mockMvc.perform(get("/firestation").param("stationNumber", noStation)).andExpect(status().isOk());
        LOGGER.info("Fin test : Le système RETOURNE une liste des personnes (prénom, nom, adresse, numéro de\r\n"
                + "téléphone) couvertes par la caserne de pompiers correspondante ainsi qu’un\r\n"
                + "décompte du nombre d’adultes (>18 ans) et du nombre d’enfants (<=18 ans)");
    }

    @Test
    void testGetpersonsOfStationAdultsAndChildNotPersons() throws Exception {
        String noStation = "999";

        List<PersonsOfFireStation> listPersonsChildren = new ArrayList<>();

        List<FoyerChildrenAdultsToFireStation> listFoyerChildrenAdultsToFireStation = new ArrayList<>();
        listFoyerChildrenAdultsToFireStation.add(new FoyerChildrenAdultsToFireStation("1", listPersonsChildren));

        when(safetyNetServiceInterface.personsOfStationAdultsAndChild(noStation))
                .thenReturn(listFoyerChildrenAdultsToFireStation);

        mockMvc.perform(get("/firestation").param("stationNumber", noStation)).andExpect(status().is(204));
        LOGGER.info(
                "Fin test : Pas de station. Le système NE RETOURNE PAS une liste des personnes (prénom, nom, adresse, numéro de\r\n"
                        + "téléphone) couvertes par la caserne de pompiers correspondante ainsi qu’un\r\n"
                        + "décompte du nombre d’adultes (>18 ans) et du nombre d’enfants (<=18 ans)");
    }

    @Test
    void testGetpersonsOfStationAdultsAndChildListPersonsNull() throws Exception {
        String noStation = "999";

        List<PersonsOfFireStation> listPersonsChildren = new ArrayList<>();

        List<FoyerChildrenAdultsToFireStation> listFoyerChildrenAdultsToFireStation = new ArrayList<>();
        listFoyerChildrenAdultsToFireStation.add(new FoyerChildrenAdultsToFireStation("1", listPersonsChildren));

        when(safetyNetServiceInterface.personsOfStationAdultsAndChild(noStation))
                .thenReturn(null);

        mockMvc.perform(get("/firestation").param("stationNumber", noStation)).andExpect(status().is(404));
        LOGGER.info(
                "Fin test : list persons null");
    }

    @Test
    void testGetpersonsOfStationAdultsAndChildNoStationNumber() throws Exception {
        String noStation = "";

        List<PersonsOfFireStation> listPersonsChildren = new ArrayList<>();

        List<FoyerChildrenAdultsToFireStation> listFoyerChildrenAdultsToFireStation = new ArrayList<>();
        listFoyerChildrenAdultsToFireStation.add(new FoyerChildrenAdultsToFireStation("1", listPersonsChildren));

        when(safetyNetServiceInterface.personsOfStationAdultsAndChild(noStation))
                .thenReturn(listFoyerChildrenAdultsToFireStation);

        mockMvc.perform(get("/firestation").param("stationNumber", noStation)).andExpect(status().is(400));
        LOGGER.info(
                "Fin test : Pas de station.");
    }

    /*
     * L'utilisateur accède à l’URL :
     *
     * http://localhost:9090/childAlert?address=<address>
     * 
     * Le système retourne une liste des enfants (<=18 ans) habitant à cette
     * adresse. La liste doit comprendre : prénom, nom, âge et une liste des autres
     * membres du foyer. S’il n’y a pas d’enfant, cette url peut renvoyer une chaîne
     * vide.
     */

    @Test
    void testChildAlert() throws Exception {
        String address = "AddressTest";
        List<ChildAlert> listChild = new ArrayList<>();
        List<ChildrenOrAdults> listChildren = new ArrayList<>();
        List<ChildrenOrAdults> listAdult = new ArrayList<>();
        ChildAlert childAlert = new ChildAlert();
        childAlert.setListAdult(listAdult);
        childAlert.setListChildren(listChildren);

        when(safetyNetServiceInterface.childPersonsAlertAddress(address)).thenReturn(listChild);

        mockMvc.perform(get("/childAlert").param("address", address)).andExpect(status().isOk());

    }

    @Test
    void testChildAlertNotAddress() throws Exception {
        String address = "";
        List<ChildAlert> listChild = new ArrayList<>();
        List<ChildrenOrAdults> listChildren = new ArrayList<>();
        List<ChildrenOrAdults> listAdult = new ArrayList<>();
        ChildAlert childAlert = new ChildAlert();
        childAlert.setListAdult(listAdult);
        childAlert.setListChildren(listChildren);

        when(safetyNetServiceInterface.childPersonsAlertAddress(address)).thenReturn(listChild);

        mockMvc.perform(get("/childAlert").param("address", address)).andExpect(status().is(400));

    }

    @Test
    void testChildAlertListNull() throws Exception {
        String address = "AddressTest";

        when(safetyNetServiceInterface.childPersonsAlertAddress(address)).thenReturn(null);

        mockMvc.perform(get("/childAlert").param("address", address)).andExpect(status().is(404));

    }

    @Test
    void testChildAlertException() throws Exception {
        String address = "AddressTest";
        when(safetyNetServiceInterface.childPersonsAlertAddress(address)).thenThrow(IOException.class);

        mockMvc.perform(get("/childAlert").param("address", address)).andExpect(status().is(404));

    }

    @Test
    void testChildAlertParseException() throws Exception {
        String address = "AddressTest";
        when(safetyNetServiceInterface.childPersonsAlertAddress(address)).thenThrow(ParseException.class);

        mockMvc.perform(get("/childAlert").param("address", address)).andExpect(status().is(404));

    }

    @Test
    void testPhoneAlertStationNumber() throws Exception {
        String firestation = "999";
        List<String> listPhonesString = new ArrayList<>();
        listPhonesString.add(0, "01-001001");
        listPhonesString.add(1, "01-001001");
        listPhonesString.add(2, "01-001001");
        List<PhoneAlert> listPhoneAlert = createListPhone(listPhonesString);

        when(safetyNetServiceInterface.phoneAlertFirestation(firestation)).thenReturn(listPhoneAlert);

        mockMvc.perform(get("/phoneAlert").param("firestation", firestation)).andExpect(status().is(200));

    }

    @Test
    void testPhoneAlertStationNumberNoListPhone() throws Exception {
        String firestation = "999";
        List<String> listPhonesString = new ArrayList<>();
        List<PhoneAlert> listPhoneAlert = createListPhone(listPhonesString);

        when(safetyNetServiceInterface.phoneAlertFirestation(firestation)).thenReturn(listPhoneAlert);

        mockMvc.perform(get("/phoneAlert").param("firestation", firestation)).andExpect(status().is(404));

    }

    @Test
    void testPhoneAlertStationNumberException() throws Exception {
        String firestation = "999";

        when(safetyNetServiceInterface.phoneAlertFirestation(firestation)).thenThrow(IOException.class);

        mockMvc.perform(get("/phoneAlert").param("firestation", firestation)).andExpect(status().is(404));

    }

    @Test
    void testPhoneAlertStationNumberBlankFirestation() throws Exception {
        String firestation = "";
        List<String> listPhonesString = new ArrayList<>();
        List<PhoneAlert> listPhoneAlert = createListPhone(listPhonesString);

        when(safetyNetServiceInterface.phoneAlertFirestation(firestation)).thenReturn(listPhoneAlert);

        mockMvc.perform(get("/phoneAlert").param("firestation", firestation)).andExpect(status().is(400));

    }

    private List<PhoneAlert> createListPhone(List<String> listPhonesString) {
        List<PhoneAlert> listPhones = new ArrayList<>();
        PhoneAlert phoneAlert = new PhoneAlert();
        phoneAlert.setListPhones(listPhonesString);
        listPhones.add(phoneAlert);
        return listPhones;
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
}