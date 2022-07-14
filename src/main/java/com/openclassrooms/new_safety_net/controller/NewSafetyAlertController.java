package com.openclassrooms.new_safety_net.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.new_safety_net.model.Firestations;
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
        }
        loggerInfoRequete(response, request, elemjson);
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
            response.setStatus(404);
            return ResponseEntity.status(response.getStatus()).build();
        }

        loggerInfoRequete(response, request, firstNamelastName);
        return new ResponseEntity<>(listP, HttpStatus.valueOf(response.getStatus()));
    }

    @GetMapping("/firestations")
    public ResponseEntity<List<Firestations>> getFirestations(HttpServletResponse response,
            HttpServletRequest request) {
        String elemjson = "firestations";
        listFirestations = repository.getFirestations(elemjson);

        if (listPersons.isEmpty()) {
            // 204 Requête traitée avec succès mais pas d’information à renvoyer.
            response.setStatus(204);
        }
        loggerInfoRequete(response, request, elemjson);
        return new ResponseEntity<>(listFirestations, HttpStatus.valueOf(response.getStatus()));
    }

    private void loggerInfoRequete(HttpServletResponse response, HttpServletRequest request, String param) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(loggerApiNewSafetyNet.loggerInfo(request, response, param));
        }
    }

}