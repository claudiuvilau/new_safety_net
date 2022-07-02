package com.openclassrooms.new_safety_net.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.new_safety_net.model.Persons;
import com.openclassrooms.new_safety_net.repository.SafetyNetRepository;

@RestController
public class NewSafetyAlertController {

    @Autowired
    private SafetyNetRepository repository;

    List<Persons> listPersons = new ArrayList<>();

    // pourquoi si on met l'objet JSON il est mal affiché dans le postman, mais en
    // list cela va bien
    @GetMapping("/person")
    public List<Persons> getPersons() throws IOException {
        listPersons = repository.getPerson();
        return listPersons;
    }

}