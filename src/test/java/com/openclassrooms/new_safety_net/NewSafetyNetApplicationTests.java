package com.openclassrooms.new_safety_net;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.openclassrooms.new_safety_net.model.Persons;
import com.openclassrooms.new_safety_net.service.SafetyNetService;

@SpringBootTest
class NewSafetyNetApplicationTests {

	@Test
	void testGetPersons() {
		// ARANGE
		String elementJson = "persons";
		// "{\"persons\": [{ \"firstName\":\"John\", \"lastName\":\"Boyd\",
		// \"address\":\"1509 Culver St\", \"city\":\"Culver\", \"zip\":\"97451\",
		// \"phone\":\"841-874-6512\", \"email\":\"jaboyd@email.com\" }";
		SafetyNetService safetyNetService = new SafetyNetService();
		List<Persons> listPersons;

		// ACT (action)
		listPersons = safetyNetService.getPersons(elementJson);

		// ASSERT (vérification)
		assertEquals(true, !listPersons.isEmpty());

	}

	@Test
	void testGetAPersons() {
		// ARANGE
		String elementJson = "persons";
		String firstName = "John";
		String lastName = "Boyd";
		SafetyNetService safetyNetService = new SafetyNetService();
		List<Persons> listPersons = new ArrayList<>();

		// ACT (action)
		try {
			listPersons = safetyNetService.getAPerson(firstName + lastName, elementJson);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ASSERT (vérification)
		assertEquals(true, !listPersons.isEmpty());

	}

}
