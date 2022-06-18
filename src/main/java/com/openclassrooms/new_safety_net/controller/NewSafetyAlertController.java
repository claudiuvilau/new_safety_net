package com.openclassrooms.new_safety_net.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.new_safety_net.model.Persons;
import com.openclassrooms.new_safety_net.service.SafetyNetService;

@RestController
public class NewSafetyAlertController {

    @Autowired
    private SafetyNetService safetyNetService;

    /**
     * Read - Get all employees
     * 
     * @return - An Iterable object of Employee full filled
     */
    @GetMapping("/employees")
    public Iterable<Persons> getEmployees() {
        return safetyNetService.getEmployees();
    }
}