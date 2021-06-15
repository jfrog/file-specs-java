package org.jfrog.filespecs.aql;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class AqlBuildingUtils {
    static String buildQuerySuffix(String[] sortBy, String sortOrder, String offset, String limit) {
        StringBuilder query = new StringBuilder();
        if (sortBy != ArrayUtils.EMPTY_STRING_ARRAY) {
            sortOrder = StringUtils.defaultIfEmpty(sortOrder, "asc");
            query.append(".sort({\"$").append(sortOrder).append("\":");
            query.append("[").append(prepareSortFieldsForQuery(sortBy)).append("]})");
        }
        if (StringUtils.isNotBlank(offset)) {
            query.append(".offset(").append(offset).append(")");
        }
        if (StringUtils.isNotBlank(limit)) {
            query.append(".limit(").append(limit).append(")");
        }
        return query.toString();
    }

    private static String prepareSortFieldsForQuery(String[] sortByFields) {
        StringBuilder fields = new StringBuilder();
        int size = sortByFields.length;
        for (int i = 0; i < size; i++) {
            fields.append("\"").append(sortByFields[i]).append("\"");
            if (i < size - 1) {
                fields.append(",");
            }
        }
        return fields.toString();
    }

    static String buildIncludeQueryPart(String[] sortByFields, String suffix) {
        List<String> fieldsToInclude = getQueryReturnFields(sortByFields);
        if (StringUtils.isBlank(suffix)) {
            fieldsToInclude.add("property");
        }
        return ".include(" + StringUtils.join(prepareFieldsForQuery(fieldsToInclude), ',') + ")";
    }

    private static List<String> getQueryReturnFields(String[] sortByFields) {
        ArrayList<String> includeFields = new ArrayList<String>(
                Arrays.asList("name", "repo", "path", "actual_md5", "actual_sha1", "size", "type", "modified", "created"));
        for (String field : sortByFields) {
            if (includeFields.indexOf(field) == -1) {
                includeFields.add(field);
            }
        }
        return includeFields;
    }

    private static List<String> prepareFieldsForQuery(List<String> fields) {
        fields.forEach((field) -> fields.set(fields.indexOf(field), '"' + field + '"'));
        return fields;
    }

    static String buildAqlSearchQuery(String searchPattern, String[] exclusions, boolean recursive, String props) {
        // Prepare.
        searchPattern = prepareSearchPattern(searchPattern);

        // Create triples.
        List<RepoPathFile> repoPathFileTriples = PatternParsingUtils.createRepoPathFileTriples(searchPattern, recursive);
        boolean includeRoot = StringUtils.countMatches(searchPattern, "/") < 2;
        int triplesSize = repoPathFileTriples.size();

        // Build query.
        String excludeQuery = buildExcludeQuery(exclusions,triplesSize == 0 || recursive, recursive);
        String nePath = buildNePathQuery(triplesSize == 0 || includeRoot);
        String json = String.format("{%s\"$or\":[", buildPropsQuery(props) + nePath + excludeQuery);
        StringBuilder aqlQuery = new StringBuilder(json);
        aqlQuery.append(handleRepoPathFileTriples(repoPathFileTriples, triplesSize)).append("]}");

        return aqlQuery.toString();
    }

    private static String prepareSearchPattern(String pattern) {
        if (pattern.endsWith("/")) {
            pattern += "*";
        }
        return pattern.replaceAll("[()]", "");
    }

    private static String buildExcludeQuery(String[] exclusions, boolean useLocalPath, boolean recursive) {
        if (ArrayUtils.isEmpty(exclusions)) {
            return "";
        }
        List<RepoPathFile> excludeTriples = new ArrayList<>();
        for (String exclusion : exclusions) {
            excludeTriples.addAll(PatternParsingUtils.createRepoPathFileTriples(prepareSearchPattern(exclusion), recursive));
        }

        String excludeQuery = "";
        for (RepoPathFile excludeTriple : excludeTriples) {
            String excludePath = excludeTriple.getPath();
            if (!useLocalPath && excludePath.equals(".")) {
                excludePath = "*";
            }
            String excludeRepoStr = "";
            if (StringUtils.isNotEmpty(excludeTriple.getRepo())) {
                excludeRepoStr = String.format("\"repo\":{\"$nmatch\":\"%s\"},", excludeTriple.getRepo());
            }
            excludeQuery += String.format("\"$or\":[{%s\"path\":{\"$nmatch\":\"%s\"},\"name\":{\"$nmatch\":\"%s\"}}],",
                    excludeRepoStr, excludePath, excludeTriple.getFile());
        }
        return excludeQuery;
    }

    private static String buildNePathQuery(boolean includeRoot) {
        return includeRoot ? "" : "\"path\":{\"$ne\":\".\"},";
    }

    private static String buildPropsQuery(String props) {
        if (props == null || props.equals("")) {
            return "";
        }
        String[] propList = props.split(";");
        StringBuilder query = new StringBuilder();
        for (String prop : propList) {
            String[] keyVal = prop.split("=");
            if (keyVal.length != 2) {
                System.out.print("Invalid props pattern: " + prop);
            }
            String key = keyVal[0];
            String value = keyVal[1];
            query.append("\"@").append(key).append("\": {\"$match\" : \"").append(value).append("\"},");
        }
        return query.toString();
    }

    private static String handleRepoPathFileTriples(List<RepoPathFile> repoPathFiles, int repoPathFileSize) {
        String query = "";
        for (int i = 0; i < repoPathFileSize; i++) {
            query += buildInnerQuery(repoPathFiles.get(i));

            if (i + 1 < repoPathFileSize) {
                query += ",";
            }
        }
        return query;
    }

    private static String buildInnerQuery(RepoPathFile triple) {
        return String.format(
                "{\"$and\":[{" +
                        "\"repo\":%s," +
                        "\"path\":%s," +
                        "\"name\":%s" +
                        "}]}",
                getAqlValue(triple.getRepo()), getAqlValue(triple.getPath()), getAqlValue(triple.getFile()));
    }

    // Optimization - If value is wildcard pattern, return '{"$match":"value"}'.
    // Otherwise, return '"value"'.
    private static String getAqlValue(String value) {
        String aqlValuePattern;
        if (value.contains("*")) {
            aqlValuePattern = "{\"$match\":\"%s\"}";
        } else {
            aqlValuePattern = "\"%s\"";
        }
        return String.format(aqlValuePattern, value);
    }
}
