package com.openclassrooms.new_safety_net.service;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.openclassrooms.new_safety_net.model.JsonFromFile;
import com.openclassrooms.new_safety_net.repository.ObjetFromJsonRepository;

public class ObjetFromJson implements ObjetFromJsonRepository {

    // Récupération de notre logger.
    private static final Logger LOGGER = LogManager.getLogger(ObjetFromJson.class);

    private JsonFromFile jsonFromFile = createClasseJsonFromFile();

    private LoggerApiNewSafetyNet loggerApiNewSafetyNet = createLoggerApiNewSafetyNet();

    private Any any;

    public static Logger getLogger() {
        return LOGGER;
    }

    public JsonFromFile getJsonFromFile() {
        return jsonFromFile;
    }

    public void setJsonToFile(JsonFromFile jsonToFile) {
        this.jsonFromFile = jsonToFile;
    }

    public LoggerApiNewSafetyNet getLoggerApiNewSafetyNet() {
        return loggerApiNewSafetyNet;
    }

    public void setLoggerApiNewSafetyNet(LoggerApiNewSafetyNet loggerApiNewSafetyNet) {
        this.loggerApiNewSafetyNet = loggerApiNewSafetyNet;
    }

    public Any getAny() {
        return any;
    }

    public void setAny(Any any) {
        this.any = any;
    }

    public ObjetFromJson() {
    }

    public ObjetFromJson(JsonFromFile jsonToFile, LoggerApiNewSafetyNet loggerApiNewSafetyNet, Any any) {
        this.jsonFromFile = jsonToFile;
        this.loggerApiNewSafetyNet = loggerApiNewSafetyNet;
        this.any = any;
    }

    @Override
    public String toString() {
        return "ObjetFromJson [any=" + any + ", jsonFromFile=" + jsonFromFile + ", loggerApiNewSafetyNet="
                + loggerApiNewSafetyNet + "]";
    }

    public void anyAny(String elemjson) {
        any = null;
        setAny(any);
        byte[] objetfile = null;
        objetfile = jsonFromFile.readJsonFile();
        if (objetfile != null) {
            JsonIterator iter = JsonIterator.parse(objetfile);
            if (iter.currentBuffer().contains(elemjson)) {
                try {
                    any = iter.readAny();
                    setAny(any);
                    LOGGER.debug("Json iterator is created.");
                } catch (IOException e) {
                    LOGGER.debug("No objet Java from Json !");
                }
            }
        }
    }

    private JsonFromFile createClasseJsonFromFile() {
        return new JsonFromFile();
    }

    private LoggerApiNewSafetyNet createLoggerApiNewSafetyNet() {
        return new LoggerApiNewSafetyNet();
    }

}