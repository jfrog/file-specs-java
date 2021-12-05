package org.jfrog.filespecs;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jfrog.filespecs.entities.FilesGroup;

public class FileSpecsParsingUtils {
    public static void pathToUnixFormat(FileSpec fileSpec) {
        for (FilesGroup filesGroup : fileSpec.getFiles()) {
            // In case of regex double backslashes are separator
            String separator = StringUtils.equalsIgnoreCase(filesGroup.getRegexp(), Boolean.TRUE.toString()) ? "\\\\\\\\" : "\\\\";
            if (filesGroup.getTarget() != null) {
                filesGroup.setTarget(filesGroup.getTarget().replaceAll("\\\\", "/"));
            }
            if (filesGroup.getPattern() != null) {
                filesGroup.setPattern(filesGroup.getPattern().replaceAll(separator, "/"));
            }
            if (!ArrayUtils.isEmpty(filesGroup.getExclusions())) {
                filesGroup.setExclusions(fixExclusionsPathToUnixFormat(filesGroup.getExclusions(), separator));
            } else if (!ArrayUtils.isEmpty(filesGroup.getExcludePatterns())) {
                filesGroup.setExcludePatterns(fixExclusionsPathToUnixFormat(filesGroup.getExcludePatterns(), separator));
            }
        }
    }

    private static String[] fixExclusionsPathToUnixFormat(String[] exclusions, String separator) {
        for (int i = 0; i < exclusions.length; i++) {
            String exclusion = exclusions[i];
            exclusions[i] = exclusion.replaceAll(separator, "/");
        }
        return exclusions;
    }
}
