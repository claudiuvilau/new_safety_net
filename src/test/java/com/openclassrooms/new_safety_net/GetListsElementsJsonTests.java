package com.openclassrooms.new_safety_net;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.openclassrooms.new_safety_net.model.Firestations;
import com.openclassrooms.new_safety_net.model.Medicalrecords;
import com.openclassrooms.new_safety_net.model.Persons;
import com.openclassrooms.new_safety_net.service.GetListsElementsJson;
import com.openclassrooms.new_safety_net.service.ObjetFromJson;

@ExtendWith(MockitoExtension.class)
class GetListsElementsJsonTests {

	@Mock
	ObjetFromJson objetFromJson;

	@InjectMocks
	GetListsElementsJson getListsElementsJson;

	private Any any = null;

	@Test
	void testGetPersons() throws IOException {

		String elementJson = "persons";
		String firstNameATester = "TEST999";
		String anyString = "{\"persons\": [{ \"firstName\":\"" + firstNameATester
				+ "\", \"lastName\":\"Boyd\",\"address\":\"1509 Culver St\", \"city\":\"Culver\", \"zip\":\"97451\",\"phone\":\"841-874-6512\", \"email\":\"jaboyd@email.com\" }]}";
		JsonIterator iter = JsonIterator.parse(anyString);
		any = iter.readAny();

		List<Persons> listPersons;

		when(objetFromJson.getAny()).thenReturn(any);

		listPersons = getListsElementsJson.getPersons(elementJson);

		assertEquals(firstNameATester, listPersons.get(0).getFirstName().toString());

	}

	@Test
	void testGetNoPersons() throws IOException {

		String elementJson = "persons";
		List<Persons> listPersons;

		when(objetFromJson.getAny()).thenReturn(null);

		listPersons = getListsElementsJson.getPersons(elementJson);

		assertEquals(true, listPersons.isEmpty());

	}

	@Test
	void testGetFirestations() throws IOException {

		String elementJson = "firestations";
		String addressFirestationATester = "TEST999";
		String anyString = "{\"firestations\": [{ \"address\":\"" + addressFirestationATester
				+ "\", \"station\":\"3\" }]}";
		JsonIterator iter = JsonIterator.parse(anyString);
		any = iter.readAny();

		List<Firestations> listFirestations;

		when(objetFromJson.getAny()).thenReturn(any);

		listFirestations = getListsElementsJson.getFirestations(elementJson);

		assertEquals(addressFirestationATester, listFirestations.get(0).getAddress().toString());
	}

	@Test
	void testGetNoFirestations() throws IOException {

		String elementJson = "firestations";

		List<Firestations> listFirestations;

		when(objetFromJson.getAny()).thenReturn(null);

		listFirestations = getListsElementsJson.getFirestations(elementJson);

		assertEquals(true, listFirestations.isEmpty());
	}

	@Test
	void testGetMedicalrecords() throws IOException {

		String elementJson = "medicalrecords";
		String firstNameATester = "TEST999";
		String anyString = "{\"medicalrecords\": [{\"firstName\":\"" + firstNameATester
				+ "\", \"lastName\":\"Boyd\",\"birthdate\":\"01/08/1986\", \"medications\":[\"tetracyclaz:650mg\"], \"allergies\":[\"xilliathal\"]}]}";
		JsonIterator iter = JsonIterator.parse(anyString);
		any = iter.readAny();

		List<Medicalrecords> listMedicalrecords;

		when(objetFromJson.getAny()).thenReturn(any);

		listMedicalrecords = getListsElementsJson.getMedicalrecords(elementJson);

		assertEquals(firstNameATester, listMedicalrecords.get(0).getFirstName().toString());
	}

	@Test
	void testGetNoMedicalrecords() throws IOException {

		String elementJson = "medicalrecords";

		List<Medicalrecords> listMedicalrecords;

		when(objetFromJson.getAny()).thenReturn(null);

		listMedicalrecords = getListsElementsJson.getMedicalrecords(elementJson);

		assertEquals(true, listMedicalrecords.isEmpty());
	}
}
