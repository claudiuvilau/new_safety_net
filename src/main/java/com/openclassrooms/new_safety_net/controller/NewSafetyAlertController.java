package com.openclassrooms.new_safety_net.controller;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.openclassrooms.new_safety_net.model.Firestations;
import com.openclassrooms.new_safety_net.model.Medicalrecords;
import com.openclassrooms.new_safety_net.model.Persons;
import com.openclassrooms.new_safety_net.repository.SafetyNetRepository;
import com.openclassrooms.new_safety_net.service.LoggerApiNewSafetyNet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class NewSafetyAlertController {

    @Autowired
    private SafetyNetRepository repository;

    List<Persons> listPersons = new ArrayList<>();
    List<Firestations> listFirestations = new ArrayList<>();
    List<Medicalrecords> listMedicalrecords = new ArrayList<>();

    String messagelogger = "";
    private static final String NOCONTENT = "No Content";

    // Récupération de notre logger.
    private static final Logger LOGGER = LogManager.getLogger(NewSafetyAlertController.class);

    LoggerApiNewSafetyNet loggerApiNewSafetyNet = new LoggerApiNewSafetyNet();

    // pourquoi si on met l'objet JSON il est mal affiché dans le postman, mais en
    // list cela va bien
    @GetMapping("/persons")
    public ResponseEntity<List<Persons>> getPersons(HttpServletResponse response, HttpServletRequest request) {
        String elemjson = "persons";
        try {
            listPersons = repository.getPersons(elemjson);
        } catch (IOException e) {
            response.setStatus(404);
        }

        if (listPersons.isEmpty()) {
            // 204 Requête traitée avec succès mais pas d’information à renvoyer.
            response.setStatus(204);
            messagelogger = NOCONTENT + loggerApiNewSafetyNet.loggerInfo(request, response, elemjson);
            LOGGER.info(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }
        messagelogger = "OK" + loggerApiNewSafetyNet.loggerInfo(request, response, elemjson);
        LOGGER.info(messagelogger);
        return new ResponseEntity<>(listPersons, HttpStatus.valueOf(response.getStatus()));
    }

    // get a person
    @GetMapping(value = "person/{firstNamelastName}")
    public ResponseEntity<List<Persons>> getAPerson(@PathVariable String firstNamelastName, HttpServletRequest request,
            HttpServletResponse response) {

        String elemjson = "persons";
        List<Persons> listP = new ArrayList<>();
        try {
            listP = repository.getAPerson(firstNamelastName, elemjson);
        } catch (IOException e) {
            response.setStatus(404);

            return ResponseEntity.status(response.getStatus()).build();
        }

        if (listP.isEmpty()) {
            response.setStatus(204);
            messagelogger = "No Content. The person does not exist."
                    + loggerApiNewSafetyNet.loggerInfo(request, response, firstNamelastName);
            LOGGER.info(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }
        messagelogger = "OK" + loggerApiNewSafetyNet.loggerInfo(request, response, firstNamelastName);
        LOGGER.info(messagelogger);
        return new ResponseEntity<>(listP, HttpStatus.valueOf(response.getStatus()));
    }

    // add a person
    @PostMapping(value = "/person")
    public ResponseEntity<Persons> addPerson(@RequestBody Persons person, HttpServletRequest request,
            HttpServletResponse response) {

        Boolean addperson = false;

        addperson = repository.postPerson(person);

        if (Boolean.FALSE.equals(addperson)) {
            response.setStatus(404);
            messagelogger = "No persons added. Response status " + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, "");
            LOGGER.info(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }

        response.setStatus(201);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{firstName+lastName}")
                .buildAndExpand(person.getFirstName() + person.getLastName()).toUri();
        messagelogger = "A new person is added successful. The URL is : " + location;
        LOGGER.info(messagelogger);
        messagelogger = "Created " + loggerApiNewSafetyNet.loggerInfo(request, response, "");
        LOGGER.info(messagelogger);
        // c'est quoi la différence entre les 2 lignes suivantes created(location).build
        // et
        // https.valuesOf(response.getstatus):
        return new ResponseEntity<>(person, HttpStatus.valueOf(response.getStatus()));
    }

    // update person
    // exemple http://localhost:9000/person?firstName=John2&lastName=Boyd2
    @PutMapping(value = "/person")
    public ResponseEntity<Persons> updatePerson(@RequestBody Persons person, @RequestParam String firstName,
            @RequestParam String lastName, HttpServletRequest request, HttpServletResponse response) {

        if (firstName.isBlank() || lastName.isBlank()) {
            response.setStatus(400);
            messagelogger = "The params does not exist. Response status " + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, "");
            LOGGER.warn(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }

        boolean update = false;

        update = repository.updatePerson(person, firstName, lastName);

        if (!update) {
            response.setStatus(404);
            messagelogger = "The person is not updeted. Response status " + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, firstName + " " + lastName);
            LOGGER.info(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }

        response.setStatus(200);
        messagelogger = "Response status " + response.getStatus() + ":"
                + loggerApiNewSafetyNet.loggerInfo(request, response, firstName + " " + lastName);
        LOGGER.info(messagelogger);
        return new ResponseEntity<>(person, HttpStatus.valueOf(response.getStatus()));
    }

    // delete person
    @DeleteMapping(value = "/person")
    public ResponseEntity<Void> deletePerson(@RequestParam String firstName, @RequestParam String lastName,
            HttpServletRequest request, HttpServletResponse response) {

        if (firstName.isBlank() || lastName.isBlank()) {
            response.setStatus(400);
            messagelogger = "The params does not exist. Response status " + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, "");
            LOGGER.warn(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }

        boolean del = false;

        del = repository.deletePerson(firstName, lastName);

        if (!del) {
            response.setStatus(404);
            messagelogger = "The person is not deleted. Response status " + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, firstName + " " + lastName);
            LOGGER.info(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }

        response.setStatus(200);
        messagelogger = "Response status " + response.getStatus() + ":"
                + loggerApiNewSafetyNet.loggerInfo(request, response, firstName + " " + lastName);
        LOGGER.info(messagelogger);
        return ResponseEntity.status(response.getStatus()).build();
    }

    @GetMapping("/firestations")
    public ResponseEntity<List<Firestations>> getFirestations(HttpServletResponse response,
            HttpServletRequest request) {
        String elemjson = "firestations";
        listFirestations = repository.getFirestations(elemjson);

        if (listFirestations.isEmpty()) {
            // 204 Requête traitée avec succès mais pas d’information à renvoyer.
            response.setStatus(204);
            messagelogger = NOCONTENT + loggerApiNewSafetyNet.loggerInfo(request, response, elemjson);
            LOGGER.info(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }
        messagelogger = "OK" + loggerApiNewSafetyNet.loggerInfo(request, response, elemjson);
        LOGGER.info(messagelogger);
        return new ResponseEntity<>(listFirestations, HttpStatus.valueOf(response.getStatus()));
    }

    @GetMapping("/medicalrecords")
    public ResponseEntity<List<Medicalrecords>> getMedicalrecords(HttpServletResponse response,
            HttpServletRequest request) {
        String elemjson = "medicalrecords";
        listMedicalrecords = repository.getMedicalrecords(elemjson);

        if (listMedicalrecords.isEmpty()) {
            // 204 Requête traitée avec succès mais pas d’information à renvoyer.
            response.setStatus(204);
            messagelogger = NOCONTENT + loggerApiNewSafetyNet.loggerInfo(request, response, elemjson);
            LOGGER.info(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }
        messagelogger = "OK" + loggerApiNewSafetyNet.loggerInfo(request, response, elemjson);
        LOGGER.info(messagelogger);
        return new ResponseEntity<>(listMedicalrecords, HttpStatus.valueOf(response.getStatus()));
    }

}