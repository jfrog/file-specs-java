package org.jfrog.filespecs;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jfrog.filespecs.entities.FilesGroup;
import org.jfrog.filespecs.entities.InvalidFileSpecException;
import org.jfrog.filespecs.utils.Log;
import org.jfrog.filespecs.utils.NullLog;

public class FileSpecsValidation {
    public static void validateSearchBasedFileSpec(FileSpec fileSpec, Log log) throws InvalidFileSpecException {
        if (fileSpec.getFiles() == null || fileSpec.getFiles().size() == 0) {
            throw new InvalidFileSpecException("A file spec must contain at least one files group");
        }
        for (FilesGroup filesGroup : fileSpec.getFiles()) {
            boolean isAql = StringUtils.isNotBlank(filesGroup.getAql());
            boolean isPattern = StringUtils.isNotBlank(filesGroup.getPattern());
            boolean isBuild = StringUtils.isNotBlank(filesGroup.getBuild());
            boolean isExcludePatterns = !ArrayUtils.isEmpty(filesGroup.getExcludePatterns()) && StringUtils.isNotBlank(filesGroup.getExcludePattern(0));

            if (!isAql && !isPattern && !isBuild) {
                throw new InvalidFileSpecException("A search-based file spec must contain 'aql' or 'pattern', and/or 'build'");
            }
            validateQueryInputs(filesGroup);

            if (isExcludePatterns) {
                log.warn("The 'excludePatterns' File Spec property is deprecated.\n" +
                        "Please use the 'exclusions' property instead.\n" +
                        "Unlike 'excludePatterns', 'exclusions' take into account the repository as part of the pattern.\n" +
                        "For example: \n" +
                        "\"excludePatterns\": [\"a.zip\"]\n" +
                        "can be translated to\n" +
                        "\"exclusions\": [\"repo-name/a.zip\"]\n" +
                        "or\n" +
                        "\"exclusions\": [\"*/a.zip\"]");
            }
        }
    }

    public static void validateSearchBasedFileSpec(FileSpec fileSpec) throws InvalidFileSpecException {
        validateSearchBasedFileSpec(fileSpec, new NullLog());
    }

    public static void validateUploadFileSpec(FileSpec fileSpec, Log log) throws InvalidFileSpecException {
        if (fileSpec.getFiles().size() == 0) {
            throw new InvalidFileSpecException("A file spec must contain at least one files group");
        }
        for (FilesGroup filesGroup : fileSpec.getFiles()) {
            boolean isAql = StringUtils.isNotBlank(filesGroup.getAql());
            boolean isPattern = StringUtils.isNotBlank(filesGroup.getPattern());
            boolean isExcludePatterns = !ArrayUtils.isEmpty(filesGroup.getExcludePatterns());

            if (isAql) {
                throw new InvalidFileSpecException("AQL is not supported in upload file spec");
            }
            if (!isPattern) {
                throw new InvalidFileSpecException("Upload file spec must contain 'pattern'");
            }
            if (StringUtils.isBlank(filesGroup.getTarget())) {
                throw new InvalidFileSpecException("Upload file spec must contain 'target'");
            }

            validateQueryInputs(filesGroup);

            if (isExcludePatterns) {
                log.warn("The 'excludePatterns' File Spec property is deprecated.\n" +
                        "Please use the 'exclusions' property instead.\n" +
                        "Unlike 'excludePatterns', 'exclusions' take into account the repository as part of the pattern.\n" +
                        "For example: \n" +
                        "\"excludePatterns\": [\"a.zip\"]\n" +
                        "can be translated to\n" +
                        "\"exclusions\": [\"repo-name/a.zip\"]\n" +
                        "or\n" +
                        "\"exclusions\": [\"*/a.zip\"]");
            }
        }
    }

    public static void validateUploadFileSpec(FileSpec fileSpec) throws InvalidFileSpecException {
        validateUploadFileSpec(fileSpec, new NullLog());
    }

    protected static void validateQueryInputs(FilesGroup filesGroup) throws InvalidFileSpecException {
        boolean isAql = StringUtils.isNotBlank(filesGroup.getAql());
        boolean isPattern = StringUtils.isNotBlank(filesGroup.getPattern());
        boolean isExclusion = !ArrayUtils.isEmpty(filesGroup.getExclusions()) && StringUtils.isNotBlank(filesGroup.getExclusion(0));
        boolean isExcludePattern = !ArrayUtils.isEmpty(filesGroup.getExcludePatterns()) && StringUtils.isNotBlank(filesGroup.getExcludePattern(0));

        if (isAql && isPattern) {
            throw new InvalidFileSpecException("Spec can't contain both AQL and Pattern keys");
        }
        if (isAql && (isExclusion || isExcludePattern)) {
            throw new InvalidFileSpecException("Spec can't contain both AQL and Exclusions keys");
        }
        if (isExcludePattern && isExclusion) {
            throw new InvalidFileSpecException("Spec can't contain both Exclusions and ExcludePatterns keys");
        }
    }
}