package com.openclassrooms.new_safety_net.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class JsonToFile {

    private String filePathJson = "resources/jsonFile.json";

    private String objetfile = "";

    /**
     * @return String return the objetjson
     */
    public String getObjetjson() {
        return objetfile;
    }

    /**
     * @param objetjson the objetjson to set
     */
    public void setObjetjson(String objetjson) {
        this.objetfile = objetjson;
    }

    public byte[] readJsonFile() throws IOException {

        if (filePathJson != null) {
            byte[] bytesFile = Files.readAllBytes(new File(filePathJson).toPath());
            return bytesFile;
        }

        return null;
    }

}