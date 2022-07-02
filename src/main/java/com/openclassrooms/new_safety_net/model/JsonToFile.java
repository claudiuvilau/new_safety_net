package com.openclassrooms.new_safety_net.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class JsonToFile {

    private String filepathjson = "src\\main\\resources\\jsonFile.json";

    private byte[] objetfile;

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

    public byte[] readJsonFile() throws IOException {

        if (filepathjson != null) {
            byte[] bytesFile = null;
            try {
                bytesFile = Files.readAllBytes(new File(filepathjson).toPath());
                objetfile = bytesFile;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return objetfile;
    }

}