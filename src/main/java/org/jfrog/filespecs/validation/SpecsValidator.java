package org.jfrog.filespecs.validation;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jfrog.filespecs.entities.FilesGroup;
import org.jfrog.filespecs.entities.InvalidFileSpecException;
import org.jfrog.filespecs.FileSpec;

/**
 * Created by tamirh on 19/06/2017.
 */
public abstract class SpecsValidator {
    public abstract void validate(FileSpec fileSpec) throws InvalidFileSpecException;

    static void validateQueryInputs(FilesGroup filesGroup) throws InvalidFileSpecException {
        boolean isAql = StringUtils.isNotBlank(filesGroup.getAql());
        boolean isPattern = StringUtils.isNotBlank(filesGroup.getPattern());
        boolean isExclusion = !ArrayUtils.isEmpty(filesGroup.getExclusions()) && StringUtils.isNotBlank(filesGroup.getExclusion(0));

        if (isAql && isPattern) {
            throw new InvalidFileSpecException("Spec can't contain both AQL and Pattern keys");
        }
        if (isAql && isExclusion) {
            throw new InvalidFileSpecException("Spec can't contain both AQL and Exclusions keys");
        }
    }
}