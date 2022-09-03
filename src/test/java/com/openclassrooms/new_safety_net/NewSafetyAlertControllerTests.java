package com.openclassrooms.new_safety_net;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.new_safety_net.controller.NewSafetyAlertController;
import com.openclassrooms.new_safety_net.model.Firestations;
import com.openclassrooms.new_safety_net.model.FoyerChildrenAdultsToFireStation;
import com.openclassrooms.new_safety_net.model.Persons;
import com.openclassrooms.new_safety_net.model.PersonsOfFireStation;
import com.openclassrooms.new_safety_net.repository.SafetyNetRepository;
import com.openclassrooms.new_safety_net.service.GetListsElementsJson;
import com.openclassrooms.new_safety_net.service.LoggerApiNewSafetyNet;

@WebMvcTest(controllers = NewSafetyAlertController.class)
public class NewSafetyAlertControllerTests {

    // Récupération de notre logger.
    private static final Logger LOGGER = LogManager.getLogger(NewSafetyAlertController.class);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SafetyNetRepository repository;

    @MockBean
    GetListsElementsJson getListsElementsJson;

    @BeforeAll
    private static void activateLoggerForTests() {
        LoggerApiNewSafetyNet loggerApiNewSafetyNet = new LoggerApiNewSafetyNet();
        loggerApiNewSafetyNet.setLoggerForTests();
    }

    @Test
    public void testGetPersons() throws Exception {
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
    public void testGetPersonsNoPerson() throws Exception {
        List<Persons> listPersons = new ArrayList<>();
        String elemjson = "persons";

        when(getListsElementsJson.getPersons(elemjson)).thenReturn(listPersons);

        mockMvc.perform(get("/persons")).andExpect(status().is(204));
        LOGGER.info("Fin test : Le système RETOURNE une liste vide.");
    }

    @Test
    public void testGetPersonsNoElementPersonsInJsons() throws Exception {
        String elemjson = "persons";

        when(getListsElementsJson.getPersons(elemjson)).thenThrow(NullPointerException.class);

        mockMvc.perform(get("/persons")).andExpect(status().is(404));
        LOGGER.info("Fin test : la liste est nulle. Personne dans la liste.");
    }

    @Test
    public void testGetPersonsException() throws Exception {
        String elemjson = "persons";

        when(getListsElementsJson.getPersons(elemjson)).thenThrow(IOException.class);

        mockMvc.perform(get("/persons")).andExpect(status().is(404));
        LOGGER.info("Fin test : Exception !. Personne dans la liste.");
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
    public void testGetpersonsOfStationAdultsAndChild() throws Exception {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        String elemjson = "persons";
        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);

        String addressStation = "TEST999_address";
        String noStation = "999";
        List<Firestations> listFirestations;
        Firestations firestation;
        firestation = createFirestation(addressStation, noStation);
        listFirestations = createListFirestationsTest(addressStation, noStation, firestation);

        String elemjsonFirestation = "firestations";

        List<PersonsOfFireStation> listPersonsChildren = new ArrayList<>();

        List<FoyerChildrenAdultsToFireStation> listFoyerChildrenAdultsToFireStation = new ArrayList<>();
        listFoyerChildrenAdultsToFireStation.add(new FoyerChildrenAdultsToFireStation("1", listPersonsChildren));

        when(getListsElementsJson.getPersons(elemjson)).thenReturn(listPersons);
        when(getListsElementsJson.getFirestations(elemjsonFirestation)).thenReturn(listFirestations);
        when(repository.personsOfStationAdultsAndChild(noStation))
                .thenReturn(listFoyerChildrenAdultsToFireStation);

        mockMvc.perform(get("/firestations").param("stationNumber", noStation)).andExpect(status().isOk());
        LOGGER.info("Fin test : Le système RETOURNE une liste des personnes (prénom, nom, adresse, numéro de\r\n"
                + "téléphone) couvertes par la caserne de pompiers correspondante ainsi qu’un\r\n"
                + "décompte du nombre d’adultes (>18 ans) et du nombre d’enfants (<=18 ans)");
    }

    @Test
    public void testGetpersonsOfStationAdultsAndChildNotPersons() throws Exception {
        String firstName = "TEST999_FirstName";
        String lastName = "TEST999_LastName";
        String elemjson = "persons";
        List<Persons> listPersons;
        Persons person;
        person = createPerson(firstName, lastName);
        listPersons = createListPersonsTest(firstName, lastName, person);

        List<Firestations> listFirestations = new ArrayList<>();
        String elemjsonFirestation = "firestations";

        List<PersonsOfFireStation> listPersonsChildren = new ArrayList<>();

        List<FoyerChildrenAdultsToFireStation> listFoyerChildrenAdultsToFireStation = new ArrayList<>();
        listFoyerChildrenAdultsToFireStation.add(new FoyerChildrenAdultsToFireStation("1", listPersonsChildren));

        when(getListsElementsJson.getPersons(elemjson)).thenReturn(listPersons);
        when(getListsElementsJson.getFirestations(elemjsonFirestation)).thenReturn(listFirestations);
        when(repository.personsOfStationAdultsAndChild("99"))
                .thenReturn(listFoyerChildrenAdultsToFireStation);

        mockMvc.perform(get("/firestations").param("stationNumber", "99")).andExpect(status().is(204));
        LOGGER.info(
                "Fin test : Pas de station. Le système NE RETOURNE PAS une liste des personnes (prénom, nom, adresse, numéro de\r\n"
                        + "téléphone) couvertes par la caserne de pompiers correspondante ainsi qu’un\r\n"
                        + "décompte du nombre d’adultes (>18 ans) et du nombre d’enfants (<=18 ans)");
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
}