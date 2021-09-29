package org.jfrog.filespecs;

import org.jfrog.filespecs.aql.AqlConverter;
import org.jfrog.filespecs.distribution.DistributionSpecComponent;
import org.jfrog.filespecs.distribution.PathMapping;
import org.jfrog.filespecs.distribution.PathMappingGenerator;
import org.jfrog.filespecs.entities.FilesGroup;
import org.jfrog.filespecs.entities.InvalidFileSpecException;
import org.jfrog.filespecs.properties.PropertiesParser;
import org.jfrog.filespecs.properties.Property;

import java.util.ArrayList;
import java.util.List;

public class DistributionHelper {
    /**
     * Gets a file spec and returns a list of Distribution spec components - one for each files group in the file spec.
     * These components are used to build Distribution queries.
     * @param fileSpec the file spec from which to build the Distribution spec components.
     * @return a list of Distribution spec components matching the given file spec.
     * @throws InvalidFileSpecException if the given file spec is invalid.
     */
    public static List<DistributionSpecComponent> toSpecComponents(FileSpec fileSpec) throws InvalidFileSpecException {
        FileSpecsValidation.validateSearchBasedFileSpec(fileSpec);
        List<DistributionSpecComponent> components = new ArrayList<>();

        for (FilesGroup file : fileSpec.getFiles()) {
            String aql = AqlConverter.convertFilesGroupToAql(file);
            ArrayList<PathMapping> mappingList = new ArrayList<>();
            PathMapping mapping = PathMappingGenerator.createPathMapping(file);
            if (mapping != null) {
                mappingList.add(mapping);
            }
            List<Property> addedProps = PropertiesParser.parsePropertiesStringToList(file.getTargetProps());
            components.add(new DistributionSpecComponent(aql, mappingList, addedProps));
        }

        return components;
    }
}
