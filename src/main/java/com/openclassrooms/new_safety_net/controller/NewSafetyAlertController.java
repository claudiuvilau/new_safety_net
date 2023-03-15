package com.openclassrooms.new_safety_net.controller;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
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

import com.openclassrooms.new_safety_net.model.ChildAlert;
import com.openclassrooms.new_safety_net.model.CommunityEmail;
import com.openclassrooms.new_safety_net.model.FireAddress;
import com.openclassrooms.new_safety_net.model.Firestations;
import com.openclassrooms.new_safety_net.model.FoyerChildrenAdultsToFireStation;
import com.openclassrooms.new_safety_net.model.Medicalrecords;
import com.openclassrooms.new_safety_net.model.PersonInfo;
import com.openclassrooms.new_safety_net.model.Persons;
import com.openclassrooms.new_safety_net.model.PersonsFireStation;
import com.openclassrooms.new_safety_net.model.PhoneAlert;
import com.openclassrooms.new_safety_net.repository.GetListsElementsJsonRepository;
import com.openclassrooms.new_safety_net.repository.SafetyNetInterface;
import com.openclassrooms.new_safety_net.service.LoggerApiNewSafetyNet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class NewSafetyAlertController {

    @Autowired
    private SafetyNetInterface repositorySafetyNetInterface;

    @Autowired
    private GetListsElementsJsonRepository repositoryElementJson;

    @Autowired
    LoggerApiNewSafetyNet loggerApiNewSafetyNet;
    // LoggerApiNewSafetyNet loggerApiNewSafetyNet = new LoggerApiNewSafetyNet();

    List<Persons> listPersons = new ArrayList<>();
    List<Firestations> listFirestations = new ArrayList<>();
    List<Medicalrecords> listMedicalrecords = new ArrayList<>();

    String messagelogger = "";
    private static final String NOCONTENT = "No Content";

    // Récupération de notre logger.
    private static final Logger LOGGER = LogManager.getLogger(NewSafetyAlertController.class);
    private static final String RESPONSSTATUS = "Response status ";
    private static final String PARAMNOTEXIST = "The param does not exist. ";

    @GetMapping("/persons")
    public ResponseEntity<List<Persons>> getPersons(HttpServletResponse response, HttpServletRequest request) {
        String elemjson = "persons";
        try {
            listPersons = repositoryElementJson.getPersons(elemjson);
            if (listPersons.isEmpty()) {
                // 204 Requête traitée avec succès, mais pas d’information à renvoyer.
                response.setStatus(204);
                messagelogger = NOCONTENT + loggerApiNewSafetyNet.loggerInfo(request, response, elemjson);
                LOGGER.info(messagelogger);
                return ResponseEntity.status(response.getStatus()).build();
            }
            messagelogger = "OK" + loggerApiNewSafetyNet.loggerInfo(request, response, elemjson);
            LOGGER.info(messagelogger);
            return new ResponseEntity<>(listPersons, HttpStatus.valueOf(response.getStatus()));
        } catch (IOException e) {
            response.setStatus(404);
            messagelogger = loggerApiNewSafetyNet.loggerErr(e, response + " " + request);
            LOGGER.error(messagelogger);
            return null;
        } catch (NullPointerException nulle) {
            response.setStatus(404);
            messagelogger = "Null List !" + loggerApiNewSafetyNet.loggerInfo(request, response, elemjson);
            LOGGER.error(messagelogger);
            return null;
        }
    }

    // get a person
    @GetMapping(value = "person/{firstNamelastName}")
    public ResponseEntity<List<Persons>> getAPerson(@PathVariable String firstNamelastName, HttpServletRequest request,
            HttpServletResponse response) {

        String elemjson = "persons";
        List<Persons> listP = new ArrayList<>();
        try {
            listP = repositorySafetyNetInterface.getAPerson(firstNamelastName, elemjson);
            if (listP.isEmpty()) {
                response.setStatus(204);
                messagelogger = "No Content. The person does not exist."
                        + loggerApiNewSafetyNet.loggerInfo(request, response, firstNamelastName);
                LOGGER.info(messagelogger);
                return ResponseEntity.status(response.getStatus()).build();
            }
        } catch (IOException e) {
            response.setStatus(404);
            LOGGER.error(loggerApiNewSafetyNet.loggerErr(e, firstNamelastName));
            return ResponseEntity.status(response.getStatus()).build();
        } catch (NullPointerException nulle) {
            response.setStatus(404);
            messagelogger = "Null List !" + loggerApiNewSafetyNet.loggerInfo(request, response, elemjson);
            LOGGER.error(messagelogger);
            return null;
        }

        messagelogger = "OK" + loggerApiNewSafetyNet.loggerInfo(request, response, firstNamelastName);
        LOGGER.info(messagelogger);
        return new ResponseEntity<>(listP, HttpStatus.valueOf(response.getStatus()));
    }

    // add a person
    @PostMapping(value = "/person")
    public ResponseEntity<Persons> addPerson(@RequestBody Persons person, HttpServletRequest request,
            HttpServletResponse response) {

        List<Persons> listP;
        listP = repositorySafetyNetInterface.postPerson(person);

        if (listP.isEmpty()) {
            response.setStatus(404);
            messagelogger = "No persons added. " + RESPONSSTATUS + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, "");
            LOGGER.error(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }

        response.setStatus(201);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{firstName+lastName}")
                .buildAndExpand(person.getFirstName() + person.getLastName()).toUri();
        messagelogger = "A new person is added successful. The URL is : " + location;
        LOGGER.info(messagelogger);
        messagelogger = "Created " + loggerApiNewSafetyNet.loggerInfo(request, response, "");
        LOGGER.info(messagelogger);
        return new ResponseEntity<>(person, HttpStatus.valueOf(response.getStatus()));
    }

    // update person
    // exemple http://localhost:9000/person?firstName=John2&lastName=Boyd2
    @PutMapping(value = "/person")
    public ResponseEntity<Persons> updatePerson(@RequestBody Persons person, @RequestParam String firstName,
            @RequestParam String lastName, HttpServletRequest request, HttpServletResponse response) {

        if (firstName.isBlank() || lastName.isBlank()) {
            response.setStatus(400);
            messagelogger = "The params does not exist. " + RESPONSSTATUS + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, "");
            LOGGER.warn(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }

        boolean update = false;

        update = repositorySafetyNetInterface.putPerson(person, firstName, lastName);

        if (!update) {
            response.setStatus(404);
            messagelogger = "The person is not updeted. " + RESPONSSTATUS + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, firstName + " " + lastName);
            LOGGER.error(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }

        response.setStatus(200);
        messagelogger = RESPONSSTATUS + response.getStatus() + ":"
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
            messagelogger = "The params does not exist. " + RESPONSSTATUS + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, "");
            LOGGER.warn(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }

        boolean del = false;

        del = repositorySafetyNetInterface.deletePerson(firstName, lastName);

        if (!del) {
            response.setStatus(404);
            messagelogger = "The person is not deleted. " + RESPONSSTATUS + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, firstName + " " + lastName);
            LOGGER.error(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }

        response.setStatus(200);
        messagelogger = RESPONSSTATUS + response.getStatus() + ":"
                + loggerApiNewSafetyNet.loggerInfo(request, response, firstName + " " + lastName);
        LOGGER.info(messagelogger);
        return ResponseEntity.status(response.getStatus()).build();
    }

    @GetMapping("/firestations")
    public ResponseEntity<List<Firestations>> getFirestations(HttpServletResponse response,
            HttpServletRequest request) {
        String elemjson = "firestations";
        listFirestations = repositoryElementJson.getFirestations(elemjson);

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

    // add fire station
    @PostMapping(value = "/firestation")
    public ResponseEntity<Firestations> addFirestations(@RequestBody Firestations firestation,
            HttpServletRequest request,
            HttpServletResponse response) {

        boolean newFirestation = false;
        newFirestation = repositorySafetyNetInterface.postFirestation(firestation);
        if (!newFirestation) {
            response.setStatus(404);
            messagelogger = "No new fire station was added. " + RESPONSSTATUS + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, "");
            LOGGER.error(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }
        response.setStatus(201);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{station}")
                .buildAndExpand(firestation.getStation()).toUri();
        messagelogger = "A new fire station is added successful. The URL is : " + location;
        LOGGER.info(messagelogger);
        messagelogger = RESPONSSTATUS + response.getStatus() + ":"
                + loggerApiNewSafetyNet.loggerInfo(request, response, "");
        LOGGER.info(messagelogger);
        return new ResponseEntity<>(firestation, HttpStatus.valueOf(response.getStatus()));
    }

    // update fire station
    @PutMapping(value = "/firestation")
    public ResponseEntity<Firestations> updateFirestations(@RequestBody Firestations firestation,
            @RequestParam String address,
            HttpServletRequest request, HttpServletResponse response) {

        if (address.isBlank()) {
            response.setStatus(400);
            messagelogger = PARAMNOTEXIST + RESPONSSTATUS + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, "");
            LOGGER.warn(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }
        boolean updatestation = false;
        updatestation = repositorySafetyNetInterface.putFirestation(firestation, address);
        if (!updatestation) {
            response.setStatus(404);
            messagelogger = "The fire station was not updated. " + RESPONSSTATUS + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, address);
            LOGGER.error(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }
        response.setStatus(200);
        messagelogger = RESPONSSTATUS + response.getStatus() + ":"
                + loggerApiNewSafetyNet.loggerInfo(request, response, address);
        LOGGER.info(messagelogger);
        return new ResponseEntity<>(firestation, HttpStatus.valueOf(response.getStatus()));
    }

    // delete fire station
    @DeleteMapping(value = "/firestation")
    public ResponseEntity<Void> deleteFirestation(@RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "stationNumber", required = false) String stationNumber, HttpServletRequest request,
            HttpServletResponse response) {

        if (address == null && stationNumber == null) {
            response.setStatus(400);
            messagelogger = PARAMNOTEXIST + RESPONSSTATUS + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, "");
            LOGGER.warn(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }

        boolean del = false;

        del = repositorySafetyNetInterface.deleteFirestation(address, stationNumber);

        if (!del) {
            response.setStatus(404);
            messagelogger = "The fire station was not deleted. " + RESPONSSTATUS + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, address + " " + stationNumber);
            LOGGER.error(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }

        response.setStatus(200);
        messagelogger = RESPONSSTATUS + response.getStatus() + ":"
                + loggerApiNewSafetyNet.loggerInfo(request, response, address + " " + stationNumber);
        LOGGER.info(messagelogger);
        return ResponseEntity.status(response.getStatus()).build();
    }

    @GetMapping("/medicalrecords")
    public ResponseEntity<List<Medicalrecords>> getMedicalrecords(HttpServletResponse response,
            HttpServletRequest request) {
        String elemjson = "medicalrecords";
        listMedicalrecords = repositoryElementJson.getMedicalrecords(elemjson);

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

    // add a medical records
    @PostMapping(value = "/medicalrecord")
    public ResponseEntity<Medicalrecords> addMedicalRecord(@RequestBody Medicalrecords medicalRecord,
            HttpServletRequest request,
            HttpServletResponse response) {

        boolean newMedicalRecord = false;
        newMedicalRecord = repositorySafetyNetInterface.postMedicalRecord(medicalRecord);

        if (!newMedicalRecord) {
            response.setStatus(404);
            messagelogger = "The medical record was not added. " + RESPONSSTATUS + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, "");
            LOGGER.error(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }

        response.setStatus(201);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{firstName+lastName}")
                .buildAndExpand(medicalRecord.getFirstName() + medicalRecord.getLastName()).toUri();
        messagelogger = "A new medical record is added successful. The URL is : " + location;
        LOGGER.info(messagelogger);
        messagelogger = RESPONSSTATUS + response.getStatus() + ":"
                + loggerApiNewSafetyNet.loggerInfo(request, response, "");
        LOGGER.info(messagelogger);
        return new ResponseEntity<>(medicalRecord, HttpStatus.valueOf(response.getStatus()));
    }

    // update medical records
    @PutMapping(value = "/medicalrecord")
    public ResponseEntity<Void> updateMedicalRecord(@RequestBody Medicalrecords medicalRecord,
            @RequestParam String firstName, @RequestParam String lastName, HttpServletRequest request,
            HttpServletResponse response) {

        if (firstName.isBlank() || lastName.isBlank()) {
            response.setStatus(400);
            messagelogger = "The params does not exist. Response status " + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, "");
            LOGGER.warn(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }

        boolean update = false;

        update = repositorySafetyNetInterface.putMedicalRecord(medicalRecord, firstName, lastName);

        if (!update) {
            response.setStatus(404);
            messagelogger = "The medical record was not updated. " + RESPONSSTATUS + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, firstName + " " + lastName);
            LOGGER.error(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }

        response.setStatus(200);
        messagelogger = RESPONSSTATUS + response.getStatus() + ":"
                + loggerApiNewSafetyNet.loggerInfo(request, response, firstName + " " + lastName);
        LOGGER.info(messagelogger);
        return ResponseEntity.status(response.getStatus()).build();
    }

    // delete medical records
    @DeleteMapping(value = "/medicalrecord")
    public ResponseEntity<Void> deleteMedicalRecord(@RequestParam String firstName, @RequestParam String lastName,
            HttpServletRequest request, HttpServletResponse response) {

        if (firstName.isEmpty() || lastName.isEmpty()) {
            response.setStatus(400);
            messagelogger = "The params does not exist. Response status " + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, "");
            LOGGER.warn(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }

        boolean del = false;

        del = repositorySafetyNetInterface.deleteMedicalRecord(firstName, lastName);

        if (!del) {
            response.setStatus(404);
            messagelogger = "The medical record is not deleted. " + RESPONSSTATUS + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, firstName + " " + lastName);
            LOGGER.error(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }

        response.setStatus(200);
        messagelogger = RESPONSSTATUS + response.getStatus() + ":"
                + loggerApiNewSafetyNet.loggerInfo(request, response, firstName + " " + lastName);
        LOGGER.info(messagelogger);
        return ResponseEntity.status(response.getStatus()).build();
    }

    @GetMapping("firestation")
    public ResponseEntity<List<FoyerChildrenAdultsToFireStation>> firestationStationNumber(
            @RequestParam String stationNumber,
            HttpServletRequest request, HttpServletResponse response) {

        if (stationNumber.isBlank()) {
            response.setStatus(400);
            messagelogger = PARAMNOTEXIST + RESPONSSTATUS + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, "");
            LOGGER.warn(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }

        List<FoyerChildrenAdultsToFireStation> listFoyerChildrenAdultsToFireStation;
        listFoyerChildrenAdultsToFireStation = repositorySafetyNetInterface
                .personsOfStationAdultsAndChild(stationNumber);

        // if we have 0 adult 0 children or list is empty"

        if (listFoyerChildrenAdultsToFireStation == null) {
            response.setStatus(404);
            messagelogger = "The list is null. " + RESPONSSTATUS + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, stationNumber);
            LOGGER.error(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        } else {
            if (listFoyerChildrenAdultsToFireStation.get(0).getlistPersonsOfFireStations().isEmpty()) {
                response.setStatus(204);
                messagelogger = "The list is empty. No children and no adult. " + RESPONSSTATUS + response.getStatus()
                        + ":"
                        + loggerApiNewSafetyNet.loggerInfo(request, response, stationNumber);
                LOGGER.info(messagelogger);
                return ResponseEntity.status(response.getStatus()).build();
            }
        }

        response.setStatus(200);
        messagelogger = RESPONSSTATUS + response.getStatus() + ":"
                + loggerApiNewSafetyNet.loggerInfo(request, response, stationNumber);
        LOGGER.info(messagelogger);
        return new ResponseEntity<>(listFoyerChildrenAdultsToFireStation, HttpStatus.valueOf(response.getStatus()));
    }

    @GetMapping("childAlert")
    public ResponseEntity<List<ChildAlert>> childAlert(@RequestParam String address, HttpServletRequest request,
            HttpServletResponse response) {

        if (address.isBlank()) {
            response.setStatus(400);
            messagelogger = PARAMNOTEXIST + RESPONSSTATUS + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, "");
            LOGGER.warn(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }

        List<ChildAlert> listChildren = new ArrayList<>();
        try {
            listChildren = repositorySafetyNetInterface.childPersonsAlertAddress(address);
        } catch (IOException | ParseException e) {
            response.setStatus(404);
            LOGGER.error(loggerApiNewSafetyNet.loggerErr(e, address));
            return ResponseEntity.status(response.getStatus()).build();
        }
        if (listChildren == null) {
            response.setStatus(404);
            messagelogger = "The list is null. " + RESPONSSTATUS + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, address);
            LOGGER.error(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }

        response.setStatus(200);
        messagelogger = RESPONSSTATUS + response.getStatus() + ":"
                + loggerApiNewSafetyNet.loggerInfo(request, response, address);
        LOGGER.info(messagelogger);
        return new ResponseEntity<>(listChildren, HttpStatus.valueOf(response.getStatus()));

    }

    @GetMapping("phoneAlert")
    public ResponseEntity<List<PhoneAlert>> phoneAlertStationNumber(@RequestParam String firestation,
            HttpServletRequest request, HttpServletResponse response) {

        if (firestation.isBlank()) {
            response.setStatus(400);
            messagelogger = PARAMNOTEXIST + RESPONSSTATUS + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, "");
            LOGGER.warn(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }

        List<PhoneAlert> listPhoneAlert = new ArrayList<>();
        try {
            listPhoneAlert = repositorySafetyNetInterface.phoneAlertFirestation(firestation);
        } catch (IOException e) {
            response.setStatus(404);
            LOGGER.error(loggerApiNewSafetyNet.loggerErr(e, firestation));
            return ResponseEntity.status(response.getStatus()).build();
        }

        if (listPhoneAlert.get(0).getListPhones().isEmpty()) {
            response.setStatus(404);
            messagelogger = "No phone for the alerts. " + RESPONSSTATUS + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, firestation);
            LOGGER.error(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }

        response.setStatus(200);
        messagelogger = RESPONSSTATUS + response.getStatus() + ":"
                + loggerApiNewSafetyNet.loggerInfo(request, response, firestation);
        LOGGER.info(messagelogger);
        return new ResponseEntity<>(listPhoneAlert, HttpStatus.valueOf(response.getStatus()));
    }

    @GetMapping("fire")
    public ResponseEntity<List<FireAddress>> fireAddress(@RequestParam String address, HttpServletRequest request,
            HttpServletResponse response) {

        if (address.isBlank()) {
            response.setStatus(400);
            messagelogger = PARAMNOTEXIST + RESPONSSTATUS + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, "");
            LOGGER.warn(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }

        List<FireAddress> listFireAddress = new ArrayList<>();
        try {
            listFireAddress = repositorySafetyNetInterface.fireAddress(address);
        } catch (IOException | ParseException e) {
            response.setStatus(404);
            LOGGER.error(loggerApiNewSafetyNet.loggerErr(e, address));
            return ResponseEntity.status(response.getStatus()).build();
        }

        if (listFireAddress.isEmpty()) {
            response.setStatus(404);
            messagelogger = "No fire station for this address. " + RESPONSSTATUS + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, address);
            LOGGER.error(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }
        response.setStatus(200);
        messagelogger = RESPONSSTATUS + response.getStatus() + ":"
                + loggerApiNewSafetyNet.loggerInfo(request, response, address);
        LOGGER.info(messagelogger);
        return new ResponseEntity<>(listFireAddress, HttpStatus.valueOf(response.getStatus()));
    }

    @GetMapping("flood/station")
    public ResponseEntity<List<PersonsFireStation>> fireAddressListFireStation(@RequestParam List<String> station,
            HttpServletRequest request, HttpServletResponse response) {

        if (station.isEmpty()) {
            response.setStatus(400);
            messagelogger = PARAMNOTEXIST + RESPONSSTATUS + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, "");
            LOGGER.warn(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }

        List<PersonsFireStation> listPersonsFireStation = new ArrayList<>();
        try {
            listPersonsFireStation = repositorySafetyNetInterface.stationListFirestation(station);
        } catch (IOException | ParseException e) {
            response.setStatus(404);
            LOGGER.error(loggerApiNewSafetyNet.loggerErr(e, station.toString()));
            return ResponseEntity.status(response.getStatus()).build();
        }

        if (listPersonsFireStation.isEmpty()) {
            response.setStatus(404);
            messagelogger = "No person in theses fire stations. " + RESPONSSTATUS + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, station.toString());
            LOGGER.error(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }
        response.setStatus(200);
        messagelogger = RESPONSSTATUS + response.getStatus() + ":"
                + loggerApiNewSafetyNet.loggerInfo(request, response, station.toString());
        LOGGER.info(messagelogger);
        return new ResponseEntity<>(listPersonsFireStation, HttpStatus.valueOf(response.getStatus()));
    }

    @GetMapping("personInfo")
    public ResponseEntity<List<PersonInfo>> personInfo(@RequestParam String firstName, @RequestParam String lastName,
            HttpServletRequest request, HttpServletResponse response) {

        if (firstName.isBlank() || lastName.isBlank()) {
            response.setStatus(400);
            messagelogger = PARAMNOTEXIST + RESPONSSTATUS + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, "");
            LOGGER.warn(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }

        List<PersonInfo> listPeronInfo = new ArrayList<>();
        try {
            listPeronInfo = repositorySafetyNetInterface.personInfo(firstName, lastName);
        } catch (IOException | ParseException e) {
            response.setStatus(404);
            LOGGER.error(loggerApiNewSafetyNet.loggerErr(e, firstName + " " + lastName));
            return ResponseEntity.status(response.getStatus()).build();
        }
        if (listPeronInfo.isEmpty()) {
            response.setStatus(404);
            messagelogger = "No person with this name. " + RESPONSSTATUS + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, firstName + " " + lastName);
            LOGGER.error(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }

        response.setStatus(200);
        messagelogger = RESPONSSTATUS + response.getStatus() + ":"
                + loggerApiNewSafetyNet.loggerInfo(request, response, firstName + " " + lastName);
        LOGGER.info(messagelogger);
        return new ResponseEntity<>(listPeronInfo, HttpStatus.valueOf(response.getStatus()));
    }

    @GetMapping("communityEmail")
    public ResponseEntity<List<CommunityEmail>> communityEmail(@RequestParam String city, HttpServletRequest request,
            HttpServletResponse response) {

        if (city.isBlank()) {
            response.setStatus(400);
            messagelogger = PARAMNOTEXIST + RESPONSSTATUS + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, "");
            LOGGER.warn(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();
        }

        List<CommunityEmail> listCommunityEmail = new ArrayList<>();
        try {
            listCommunityEmail = repositorySafetyNetInterface.communityEmail(city);
        } catch (IOException e) {
            response.setStatus(404);
            LOGGER.error(loggerApiNewSafetyNet.loggerErr(e, city));
            return ResponseEntity.status(response.getStatus()).build();
        }

        if (listCommunityEmail.get(0).getListEmails().isEmpty()) {
            response.setStatus(404);
            messagelogger = "No emails for this city. " + RESPONSSTATUS + response.getStatus() + ":"
                    + loggerApiNewSafetyNet.loggerInfo(request, response, city);
            LOGGER.error(messagelogger);
            return ResponseEntity.status(response.getStatus()).build();

        }

        response.setStatus(200);
        messagelogger = RESPONSSTATUS + response.getStatus() + ":"
                + loggerApiNewSafetyNet.loggerInfo(request, response, city);
        LOGGER.info(messagelogger);
        return new ResponseEntity<>(listCommunityEmail, HttpStatus.valueOf(response.getStatus()));
    }

}