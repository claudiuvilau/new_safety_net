package com.openclassrooms.new_safety_net.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.new_safety_net.repository.SafetyNetRepository;

@RestController
public class NewSafetyAlertController {

    @Autowired
    private SafetyNetRepository repository;

    @GetMapping("/person")
    public void getPersons() throws IOException {

    }

}