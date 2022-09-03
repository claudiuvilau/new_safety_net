package com.openclassrooms.new_safety_net.service;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.openclassrooms.new_safety_net.model.JsonToFile;
import com.openclassrooms.new_safety_net.repository.ObjetFromJsonRepository;

public class ObjetFromJson implements ObjetFromJsonRepository {

    // Récupération de notre logger.
    private static final Logger LOGGER = LogManager.getLogger(ObjetFromJson.class);

    private JsonToFile jsonToFile = createClasseJsonToFile();

    private LoggerApiNewSafetyNet loggerApiNewSafetyNet = createLoggerApiNewSafetyNet();

    private Any any;

    public static Logger getLogger() {
        return LOGGER;
    }

    public JsonToFile getJsonToFile() {
        return jsonToFile;
    }

    public void setJsonToFile(JsonToFile jsonToFile) {
        this.jsonToFile = jsonToFile;
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

    public ObjetFromJson(JsonToFile jsonToFile, LoggerApiNewSafetyNet loggerApiNewSafetyNet, Any any) {
        this.jsonToFile = jsonToFile;
        this.loggerApiNewSafetyNet = loggerApiNewSafetyNet;
        this.any = any;
    }

    @Override
    public String toString() {
        return "ObjetFromJson [any=" + any + ", jsonToFile=" + jsonToFile + ", loggerApiNewSafetyNet="
                + loggerApiNewSafetyNet + "]";
    }

    public void anyAny(String elemjson) {
        any = null;
        setAny(any);
        byte[] objetfile = null;
        objetfile = jsonToFile.readJsonFile();
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

    private JsonToFile createClasseJsonToFile() {
        return new JsonToFile();
    }

    private LoggerApiNewSafetyNet createLoggerApiNewSafetyNet() {
        return new LoggerApiNewSafetyNet();
    }

}