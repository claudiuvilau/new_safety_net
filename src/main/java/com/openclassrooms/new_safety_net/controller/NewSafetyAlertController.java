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
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.new_safety_net.model.Persons;
import com.openclassrooms.new_safety_net.repository.SafetyNetRepository;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class NewSafetyAlertController {

    @Autowired
    private SafetyNetRepository repository;

    List<Persons> listPersons = new ArrayList<>();

    // Récupération de notre logger.
    private static final Logger LOGGER = LogManager.getLogger(NewSafetyAlertController.class);

    // pourquoi si on met l'objet JSON il est mal affiché dans le postman, mais en
    // list cela va bien
    @GetMapping("/persons")
    public ResponseEntity<List<Persons>> getPersons(HttpServletResponse response) {
        try {
            listPersons = repository.getPerson();
        } catch (IOException e) {
            response.setStatus(404);
        }

        if (listPersons.isEmpty()) {
            // 204 Requête traitée avec succès mais pas d’information à renvoyer.
            response.setStatus(204);
        }
        LOGGER.info(HttpStatus.valueOf(response.getStatus()));
        return new ResponseEntity<>(listPersons, HttpStatus.valueOf(response.getStatus()));
    }

}