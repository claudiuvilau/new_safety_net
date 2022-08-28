package com.openclassrooms.new_safety_net;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.new_safety_net.model.Persons;
import com.openclassrooms.new_safety_net.service.GetListsElementsJson;
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
        person.setAddress("address");
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
        Persons person = new Persons();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setAddress("address");
        person.setCity("city");
        person.setEmail("email");
        person.setPhone("phone");
        person.setZip("zip");
        listPersons.add(person);
        Persons personsAdded = new Persons();
        personsAdded = person; // the same person

        when(getListsElementsJson.getPersons(elemjson)).thenReturn(listPersons);

        fileCreated = safetyNetService.postPerson(personsAdded);

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

    private List<Persons> createListPersonsTest(String firstName, String lastName, Persons person) {
        List<Persons> listPersons = new ArrayList<>();
        listPersons.add(person);
        return listPersons;
    }

    private Persons createPerson(String firstName, String lastName) {
        Persons person = new Persons();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setAddress("address");
        person.setCity("city");
        person.setEmail("email");
        person.setPhone("phone");
        person.setZip("zip");
        return person;
    }
}