package org.jfrog.filespecs.aql;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PatternParsingUtils {
    static List<RepoPathFile> createRepoPathFileTriples(String searchPattern, boolean recursive) {
        int firstSlashIndex = searchPattern.indexOf("/");
        List<Integer> asteriskIndices = new ArrayList<>();
        for (int i = 0; i < searchPattern.length(); i++) {
            if (searchPattern.charAt(i) == '*') {
                asteriskIndices.add(i);
            }
        }

        if (!asteriskIndices.isEmpty() && !isSlashPrecedeAsterisk(asteriskIndices.get(0), firstSlashIndex)) {
            List<RepoPathFile> triples = new ArrayList<>();
            int lastRepoAsteriskIndex = 0;
            for (int asteriskIndex : asteriskIndices) {
                if (isSlashPrecedeAsterisk(asteriskIndex, firstSlashIndex)) {
                    break;
                }
                String repo = searchPattern.substring(0, asteriskIndex + 1); // '<repo>*'
                String newPattern = searchPattern.substring(asteriskIndex);  // '*<pattern>'

                // If slashCount or asteriskCount are 1 or less, don't trim prefix of '*/' to allow specific-name enforce in triple.
                // For example, in case of pattern '*/a1.in', the calculated triple should contain 'a1.in' as the 'file'.
                int slashCount = StringUtils.countMatches(newPattern, "/");
                int asterixCount = StringUtils.countMatches(newPattern, "*");
                if (slashCount > 1 || asterixCount > 1) {
                    // Remove '/' character as the pattern precedes it may be the repository name.
                    // Leaving the '/' causes forcing another hierarchy in the 'path' of the triple, which isn't correct.
                    newPattern = newPattern.replaceFirst("^\\*/", "");
                    if (!newPattern.startsWith("*")) {
                        newPattern = "*" + newPattern;
                    }
                }

                triples.addAll(createPathFilePairs(repo, newPattern, recursive));
                lastRepoAsteriskIndex = asteriskIndex + 1;
            }

            // Handle characters between last asterisk before first slash: "a*handle-it/".
            if (lastRepoAsteriskIndex < firstSlashIndex) {
                String repo = searchPattern.substring(0, firstSlashIndex);        // '<repo>*'
                String newPattern = searchPattern.substring(firstSlashIndex + 1); // '*<pattern>'
                triples.addAll(createPathFilePairs(repo, newPattern, recursive));
            } else if (firstSlashIndex < 0 && !StringUtils.endsWith(searchPattern, "*")) {
                // Handle characters after last asterisk "a*handle-it".
                triples.addAll(createPathFilePairs(searchPattern, "*", recursive));
            }

            return triples;
        }

        if (firstSlashIndex < 0) {
            return createPathFilePairs(searchPattern, "*", recursive);
        }
        String repo = searchPattern.substring(0, firstSlashIndex);
        String pattern = searchPattern.substring(firstSlashIndex + 1);
        return createPathFilePairs(repo, pattern, recursive);
    }

    private static boolean isSlashPrecedeAsterisk(int asteriskIndex, int slashIndex) {
        return slashIndex < asteriskIndex && slashIndex >= 0;
    }

    // We need to translate the provided pattern to an AQL query.
    // In Artifactory, for each artifact the name and path of the artifact are saved separately.
    // We therefore need to build an AQL query that covers all possible repositories, paths and names the provided
    // pattern can include.
    // For example, the pattern repo/a/* can include the two following files:
    // repo/a/file1.tgz and also repo/a/b/file2.tgz
    // To achieve that, this function parses the pattern by splitting it by its * characters.
    // The end result is a list of RepoPathFile objects.
    // Each object represent a possible repository, path and file name triple to be included in AQL query with an "or" relationship.
    static List<RepoPathFile> createPathFilePairs(String repo, String pattern, boolean recursive) {
        List<RepoPathFile> res = new ArrayList<>();
        if (pattern.equals("*")) {
            res.add(new RepoPathFile(repo, getDefaultPath(recursive), "*"));
            return res;
        }

        String path;
        String name;
        List<RepoPathFile> triples = new ArrayList<>();

        // Handle non-recursive triples.
        int slashIndex = pattern.lastIndexOf("/");
        if (slashIndex < 0) {
            // Optimization - If pattern starts with '*', we'll have a triple with <repo>*<file>.
            // In that case we'd prefer to avoid <repo>.<file>.
            if (recursive && pattern.startsWith("*")) {
                path = "";
                name = pattern;
            } else {
                path = "";
                name = pattern;
                triples.add(new RepoPathFile(repo, ".", pattern));
            }
        } else {
            path = pattern.substring(0, slashIndex);
            name = pattern.substring(slashIndex + 1);
            triples.add(new RepoPathFile(repo, path, name));
        }

        if (!recursive) {
            return triples;
        }
        if (name.equals("*")) {
            triples.add(new RepoPathFile(repo, path + "/*", "*"));
            return triples;
        }

        populateTriplesWithRepoPathFile(triples, repo, path, name);

        return triples;
    }

    private static String getDefaultPath(boolean recursive) {
        if (recursive) {
            return "*";
        }
        return ".";
    }

    private static void populateTriplesWithRepoPathFile(List<RepoPathFile> triples, String repo, String path, String name) {
        String[] nameSplit = name.split("\\*", -1);
        for (int i = 0; i < nameSplit.length - 1; i++) {
            String str = "";
            for (int j = 0; j < nameSplit.length; j++) {
                String namePart = nameSplit[j];
                if (j > 0) {
                    str += "*";
                }
                if (j == i) {
                    str += nameSplit[i] + "*/";
                } else {
                    str += namePart;
                }
            }
            String[] slashSplit = str.split("/", -1);
            String filePath = slashSplit[0];
            String fileName = slashSplit[1];
            if (fileName.equals("")) {
                fileName = "*";
            }
            if (!path.equals("") && !path.endsWith("/")) {
                path += "/";
            }
            triples.add(new RepoPathFile(repo, path + filePath, fileName));
        }
    }
}
