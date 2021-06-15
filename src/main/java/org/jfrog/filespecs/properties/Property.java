package org.jfrog.filespecs.properties;

import java.util.Objects;
import java.util.Set;

public class Property {
    private String key;
    private Set<String> values;

    public Property(String key, Set<String> values) {
        this.key = key;
        this.values = values;
    }

    public String getKey() {
        return key;
    }

    public Set<String> getValues() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property property = (Property) o;
        return key.equals(property.key) && values.equals(property.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, values);
    }
}
