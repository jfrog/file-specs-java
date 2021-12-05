package org.jfrog.filespecs.properties;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class PropertiesParser {
    private static final String PROPS_SEPARATOR = ";";
    private static final String VALUES_SEPARATOR = ",";

    public static List<Property> parsePropertiesStringToList(String props) {
        Map<String, Set<String>> propsMap = parsePropertiesStringToMap(props);
        return propsMap.entrySet().stream().map(entry -> new Property(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    }

    /**
     * Parses a string of properties in this format: key1=value1;key2=value2,value3,...
     * to a map of keys with sets of values.
     * Duplicate values are ignored.
     */
    private static Map<String, Set<String>> parsePropertiesStringToMap(String props) {
        Map<String, Set<String>> propsMap = new LinkedHashMap<>();

        if (StringUtils.isEmpty(props)) {
            return propsMap;
        }

        String[] propsList = props.split(PROPS_SEPARATOR);
        for (String prop : propsList) {
            if (StringUtils.isEmpty(prop)) {
                continue;
            }

            String[] propParts = prop.split("=", 2);
            if (propParts.length != 2) {
                throw new IllegalArgumentException(String.format("Invalid property format: %s - format should be key=val1,val2,...", prop));
            }

            String key = propParts[0];
            String[] valuesParts = propParts[1].split(VALUES_SEPARATOR);

            if (!propsMap.containsKey(key)) {
                propsMap.put(key, new LinkedHashSet<>());
            }

            StringBuilder valueToAdd = new StringBuilder();

            for (int i = 0; i < valuesParts.length; i++) {
                valueToAdd.append(valuesParts[i]);

                // If "\" is found, then it means that the original string contains the "\," which indicates this "," is part of the value
                // and not a separator
                if (valuesParts[i].endsWith("\\") && i < valuesParts.length - 1) {
                    valueToAdd.setLength(valueToAdd.length() - 1);
                    valueToAdd.append(VALUES_SEPARATOR);
                    continue;
                }

                propsMap.get(key).add(valueToAdd.toString());
                // Clear the StringBuilder
                valueToAdd.setLength(0);
            }
        }
        return propsMap;
    }
}
