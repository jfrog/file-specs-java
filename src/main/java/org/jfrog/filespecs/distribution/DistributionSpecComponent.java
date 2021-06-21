package org.jfrog.filespecs.distribution;

import org.jfrog.filespecs.properties.Property;

import java.util.List;
import java.util.Objects;

public class DistributionSpecComponent {
    private String aql;
    private List<PathMapping> mappings;
    private List<Property> addedProps;

    public DistributionSpecComponent(String aql, List<PathMapping> mappings, List<Property> addedProps) {
        this.aql = aql;
        this.mappings = mappings;
        this.addedProps = addedProps;
    }

    public String getAql() {
        return aql;
    }

    public List<PathMapping> getMappings() {
        return mappings;
    }

    public List<Property> getAddedProps() {
        return addedProps;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DistributionSpecComponent that = (DistributionSpecComponent) o;
        return aql.equals(that.aql) && Objects.equals(mappings, that.mappings) && Objects.equals(addedProps, that.addedProps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aql, mappings, addedProps);
    }
}
