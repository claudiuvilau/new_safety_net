package com.openclassrooms.new_safety_net;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.openclassrooms.new_safety_net.model.Persons;
import com.openclassrooms.new_safety_net.service.SafetyNetService;

@SpringBootTest
class NewSafetyNetApplicationTests {

	@Test
	void testQQchose() {
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

}
