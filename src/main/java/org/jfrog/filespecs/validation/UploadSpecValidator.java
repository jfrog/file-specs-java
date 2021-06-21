package org.jfrog.filespecs.validation;

import org.apache.commons.lang.StringUtils;
import org.jfrog.filespecs.FileSpec;
import org.jfrog.filespecs.entities.FilesGroup;
import org.jfrog.filespecs.entities.InvalidFileSpecException;

/**
 * Created by tamirh on 19/06/2017.
 */
public class UploadSpecValidator extends SpecsValidator {

    @Override
    public void validate(FileSpec fileSpec) throws InvalidFileSpecException {
        if (fileSpec.getFiles().size() == 0) {
            throw new InvalidFileSpecException("Spec must contain at least one files group.");
        }
        for (FilesGroup filesGroup : fileSpec.getFiles()) {
            boolean isAql = StringUtils.isNotBlank(filesGroup.getAql());
            boolean isPattern = StringUtils.isNotBlank(filesGroup.getPattern());

            if (!isAql && !isPattern) {
                throw new InvalidFileSpecException("Upload Spec must contain AQL or Pattern key");
            }
            if (StringUtils.isBlank(filesGroup.getTarget())) {
                throw new InvalidFileSpecException("The argument 'target' is missing from the upload spec.");
            }
            if (StringUtils.isBlank(filesGroup.getPattern())) {
                throw new InvalidFileSpecException("The argument 'pattern' is missing from the upload spec.");
            }
            validateQueryInputs(filesGroup);
        }
    }
}