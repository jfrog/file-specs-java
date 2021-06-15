package org.jfrog.filespecs.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;

/**
 * Created by romang on 4/26/16.
 */
public class Aql {
    private String find;

    public String getFind() {
        return this.find;
    }

    @JsonProperty("items.find")
    public void setFind(LinkedHashMap find) throws JsonProcessingException {
        this.find = new ObjectMapper().writeValueAsString(find);
    }
}
