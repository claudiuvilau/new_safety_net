package com.openclassrooms.new_safety_net.service;

import com.openclassrooms.new_safety_net.repository.NewFileJsonRepository;
import com.jsoniter.output.JsonStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.openclassrooms.new_safety_net.model.CollectionsRessources;
import com.openclassrooms.new_safety_net.model.Firestations;
import com.openclassrooms.new_safety_net.model.JsonToFile;
import com.openclassrooms.new_safety_net.model.Medicalrecords;
import com.openclassrooms.new_safety_net.model.Persons;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NewFileJson implements NewFileJsonRepository {

    // Récupération de notre logger.
    private static final Logger LOGGER = LogManager.getLogger(NewFileJson.class);

    private LoggerApiNewSafetyNet loggerApiNewSafetyNet = createLoggerApiNewSafetyNet();

    private boolean fileCreated;

    public NewFileJson() {
    }

    public NewFileJson(boolean fileCreated) {
        this.fileCreated = fileCreated;
    }

    public boolean isFileCreated() {
        return fileCreated;
    }

    public void setFileCreated(boolean fileCreated) {
        this.fileCreated = fileCreated;
    }

    @Override
    public String toString() {
        return "NewFileJson [fileCreated=" + fileCreated + "]";
    }

    @Override
    public void createNewFileJson(List<Persons> listPersons, List<Firestations> listFirestations,
            List<Medicalrecords> listMedicalrecords, String param) {
        CollectionsRessources collectionsRessources = new CollectionsRessources();
        collectionsRessources.setPersons(listPersons);
        collectionsRessources.setFirestations(listFirestations);
        collectionsRessources.setMedicalrecords(listMedicalrecords);

        String messageLogger = "";
        String jsonstream = JsonStream.serialize(collectionsRessources); // here we transform the list in json
                                                                         // object
        FileWriter writer = null;
        try {
            writer = new FileWriter(JsonToFile.FILEPATHJSON);
        } catch (IOException e) {
            LOGGER.error(loggerApiNewSafetyNet.loggerErr(e, param));
            setFileCreated(false);
        }
        try {
            if (writer != null) {
                writer.write(jsonstream);
            }
        } catch (IOException e) {
            LOGGER.error(loggerApiNewSafetyNet.loggerErr(e, param));
        }
        try {
            if (writer != null) {
                writer.flush();
            }
        } catch (IOException e) {
            LOGGER.error(loggerApiNewSafetyNet.loggerErr(e, param));
        }
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            LOGGER.error(loggerApiNewSafetyNet.loggerErr(e, param));
            setFileCreated(false);
        }

        if (LOGGER.isDebugEnabled()) {
            messageLogger = "The new file is writed: " + jsonstream;
            LOGGER.debug(messageLogger);
        }
        setFileCreated(true);
    }

    private LoggerApiNewSafetyNet createLoggerApiNewSafetyNet() {
        return new LoggerApiNewSafetyNet();
    }

}