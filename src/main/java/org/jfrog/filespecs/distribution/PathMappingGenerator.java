package org.jfrog.filespecs.distribution;

import org.jfrog.filespecs.entities.FilesGroup;
import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathMappingGenerator {
    /**
     * Create a PathMapping from a files group.
     * If the 'target' field of the group is empty, null is returned.
     * @param filesGroup a files group from which to create a mapping.
     * @return the mapping based on the given files group.
     */
    public static PathMapping createPathMapping(FilesGroup filesGroup) {
        if (StringUtils.isEmpty(filesGroup.getTarget())) {
            return null;
        }

        String input = convertWildcardStringToRegexp(filesGroup.getPattern());
        String output = convertPlaceholdersToApiFormat(filesGroup.getTarget());
        PathMapping mapping = new PathMapping(input, output);
        return mapping;
    }

    private static String convertWildcardStringToRegexp(String wildcardStr) {
        if (wildcardStr == null) {
            return "^$";
        }

        String wildcard = ".*";
        StringBuilder sb = new StringBuilder(wildcardStr.length());
        int length = wildcardStr.length();
        for (int i = 0; i < length; i++) {
            char c = wildcardStr.charAt(i);
            switch(c) {
                case '*':
                    sb.append(wildcard);
                    break;
                case '?':
                    sb.append(".");
                    break;
                // Escape special regexp-characters
                case '[': case ']': case '$': case '^':
                case '.': case '{': case '}': case '|':
                case '\\': case '+':
                    sb.append("\\");
                    sb.append(c);
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }

        String regexStr = sb.toString();
        if (regexStr.endsWith("/")) {
            regexStr += wildcard;
        }
        return  "^" + regexStr + "$";
    }

    /**
     * The method gets a string with placeholders, like: {1},
     * and replaces them with the theirs REST API format, like: $1.
     */
    private static String convertPlaceholdersToApiFormat(String str) {
        Matcher matcher = Pattern.compile("(\\{\\d\\})").matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "\\$" + matcher.group().charAt(1));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }
}
