package com.openclassrooms.new_safety_net.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class JsonFromFile {

    public static final String FILEPATHJSON = "src\\main\\resources\\jsonFile.json";

    private byte[] objetfile;

    File fileInPath;

    // Récupération de notre logger.
    private static final Logger LOGGER = LogManager.getLogger(JsonFromFile.class);

    /**
     * @return String return the objetjson
     */
    public byte[] getObjetjson() {
        return objetfile;
    }

    /**
     * @param objetjson the objetjson to set
     */
    public void setObjetjson(byte[] objetjson) {
        this.objetfile = objetjson;
    }

    public byte[] readJsonFile() {

        fileInPath = new File(FILEPATHJSON);

        if (!fileInPath.exists()) {
            LOGGER.debug("Fichier introuvable. Vérifier le chemin du fichier .json");
            return objetfile;
        }

        try {
            objetfile = Files.readAllBytes(new File(FILEPATHJSON).toPath());
        } catch (IOException e) {
            LOGGER.debug("Erreur création objet JAVA à partir du fichier .json");
        }
        return objetfile;
    }

}