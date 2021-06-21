package org.jfrog.filespecs.validation;

import org.apache.commons.lang.StringUtils;
import org.jfrog.filespecs.FileSpec;
import org.jfrog.filespecs.entities.FilesGroup;
import org.jfrog.filespecs.entities.InvalidFileSpecException;

/**
 * Created by tamirh on 19/06/2017.
 */
public class SearchBasedSpecValidator extends SpecsValidator {
    @Override
    public void validate(FileSpec fileSpec) throws InvalidFileSpecException {
        if (fileSpec.getFiles() == null || fileSpec.getFiles().size() == 0) {
            throw new InvalidFileSpecException("Spec must contain at least one file group.");
        }
        for (FilesGroup filesGroup : fileSpec.getFiles()) {
            boolean isAql = StringUtils.isNotBlank(filesGroup.getAql());
            boolean isPattern = StringUtils.isNotBlank(filesGroup.getPattern());

            if (!isAql && !isPattern) {
                throw new InvalidFileSpecException("A search based Spec must contain AQL or Pattern.");
            }
            validateQueryInputs(filesGroup);
        }
    }
}
