package org.jfrog.filespecs.aql;

import org.jfrog.filespecs.entities.FilesGroup;
import org.jfrog.filespecs.entities.InvalidFileSpecException;

public class AqlConverter {
    public static String convertFilesGroupToAql(FilesGroup file) throws InvalidFileSpecException {
        String queryBody;
        switch (file.getSpecType()) {
            case PATTERN: {
                queryBody = convertPatternFileSpecToAql(file);
                break;
            }
            case AQL: {
                queryBody = file.getAql();
                break;
            }
            default: {
                throw new InvalidFileSpecException("The files group must have either pattern or aql filled to be converted to AQL.");
            }
        }

        String querySuffix = AqlBuildingUtils.buildQuerySuffix(file.getSortBy(), file.getSortOrder(), file.getOffset(), file.getLimit());
        String includeFields = AqlBuildingUtils.buildIncludeQueryPart(file.getSortBy(), querySuffix);
        return String.format("items.find(%s)%s%s", queryBody, includeFields, querySuffix);
    }

    private static String convertPatternFileSpecToAql(FilesGroup file) {
        boolean recursive = !"false".equalsIgnoreCase(file.getRecursive());
        return AqlBuildingUtils.buildAqlSearchQuery(file.getPattern(), file.getExclusions(), recursive, file.getProps());
    }
}
