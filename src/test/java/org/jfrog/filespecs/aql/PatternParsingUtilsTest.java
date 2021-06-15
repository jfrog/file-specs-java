package org.jfrog.filespecs.aql;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class PatternParsingUtilsTest {
    @Test(dataProvider = "createRepoPathFileTriplesProvider")
    public void testCreateRepoPathFileTriples(String pattern, boolean recursive, List<RepoPathFile> expected) {
        List<RepoPathFile> actual = PatternParsingUtils.createRepoPathFileTriples(pattern, recursive);
        validateRepoPathFile(actual, expected, pattern);
    }

    @Test(dataProvider = "createPathFilePairsProvider")
    public void testCreatePathFilePairs(String pattern, boolean recursive, List<RepoPathFile> expected) {
        List<RepoPathFile> actual = PatternParsingUtils.createPathFilePairs("r", pattern, recursive);
        validateRepoPathFile(actual, expected, pattern);
    }

    @DataProvider
    private static Object[][] createRepoPathFileTriplesProvider() {
        return new Object[][]{
                {"a/*", true, new ArrayList<>(Collections.singletonList(
                        repoPathFile("a", "*", "*")))},
                {"a/a*b", true, new ArrayList<>(Arrays.asList(
                        repoPathFile("a", "a*", "*b"),
                        repoPathFile("a", ".", "a*b")))},
                {"a/a*b*", true, new ArrayList<>(Arrays.asList(
                        repoPathFile("a", "a*b*", "*"),
                        repoPathFile("a", "a*", "*b*"),
                        repoPathFile("a", ".", "a*b*")))},
                {"a/a*b*/a/b", true, new ArrayList<>(Collections.singletonList(
                        repoPathFile("a", "a*b*/a", "b")))},
                {"*a/b*/*c*d*", true, new ArrayList<>(Arrays.asList(
                        repoPathFile("*", "*a/b*/*c*d*", "*"),
                        repoPathFile("*", "*a/b*/*c*", "*d*"),
                        repoPathFile("*", "*a/b*/*", "*c*d*"),
                        repoPathFile("*", "*a/b*", "*c*d*"),
                        repoPathFile("*a", "b*", "*c*d*"),
                        repoPathFile("*a", "b*/*c*", "*d*"),
                        repoPathFile("*a", "b*/*", "*c*d*"),
                        repoPathFile("*a", "b*/*c*d*", "*")))},
                {"*aa/b*/*c*d*", true, new ArrayList<>(Arrays.asList(
                        repoPathFile("*", "*aa/b*/*c*d*", "*"),
                        repoPathFile("*", "*aa/b*/*c*", "*d*"),
                        repoPathFile("*", "*aa/b*/*", "*c*d*"),
                        repoPathFile("*", "*aa/b*", "*c*d*"),
                        repoPathFile("*aa", "b*", "*c*d*"),
                        repoPathFile("*aa", "b*/*c*", "*d*"),
                        repoPathFile("*aa", "b*/*", "*c*d*"),
                        repoPathFile("*aa", "b*/*c*d*", "*")))},
                {"*/a*/*b*a*", true, new ArrayList<>(Arrays.asList(
                        repoPathFile("*", "*a*/*b*a*", "*"),
                        repoPathFile("*", "*a*", "*b*a*"),
                        repoPathFile("*", "*a*/*b*", "*a*"),
                        repoPathFile("*", "*a*/*", "*b*a*")))},
                {"*", true, new ArrayList<>(Collections.singletonList(
                        repoPathFile("*", "*", "*")))},
                {"*/*", true, new ArrayList<>(Collections.singletonList(
                        repoPathFile("*", "*", "*")))},
                {"*/a.z", true, new ArrayList<>(Collections.singletonList(
                        repoPathFile("*", "*", "a.z")))},
                {"a/b", true, new ArrayList<>(Collections.singletonList(
                        repoPathFile("a", ".", "b")))},
                {"a/b", false, new ArrayList<>(Collections.singletonList(
                        repoPathFile("a", ".", "b")))},
                {"a//*", false, new ArrayList<>(Collections.singletonList(
                        repoPathFile("a", "", "*")))},
                {"r//a*b", false, new ArrayList<>(Collections.singletonList(
                        repoPathFile("r", "", "a*b")))},
                {"a*b", true, new ArrayList<>(Arrays.asList(
                        repoPathFile("a*", "*", "*b"),
                        repoPathFile("a*b", "*", "*")))},
                {"a*b*", true, new ArrayList<>(Arrays.asList(
                        repoPathFile("a*", "*b*", "*"),
                        repoPathFile("a*", "*", "*b*"),
                        repoPathFile("a*b*", "*", "*")))},
        };
    }

    @DataProvider
    private static Object[][] createPathFilePairsProvider() {
        return new Object[][]{
                {"a", true, new ArrayList<>(Collections.singletonList(
                        repoPathFile("r", ".", "a")))},
                {"a/*", true, new ArrayList<>(Arrays.asList(
                        repoPathFile("r", "a", "*"),
                        repoPathFile("r", "a/*", "*")))},
                {"a/a*b", true, new ArrayList<>(Arrays.asList(
                        repoPathFile("r", "a", "a*b"),
                        repoPathFile("r", "a/a*", "*b")))},
                {"a/a*b*", true, new ArrayList<>(Arrays.asList(
                        repoPathFile("r", "a/a*", "*b*"),
                        repoPathFile("r", "a/a*", "*b*"),
                        repoPathFile("r", "a/a*b*", "*")))},
                {"a/a*b*/a/b", true, new ArrayList<>(Collections.singletonList(
                        repoPathFile("r", "a/a*b*/a", "b")))},
                {"*/a*/*b*a*", true, new ArrayList<>(Arrays.asList(
                        repoPathFile("r", "*/a*", "*b*a*"),
                        repoPathFile("r", "*/a*/*", "*b*a*"),
                        repoPathFile("r", "*/a*/*b*", "*a*"),
                        repoPathFile("r", "*/a*/*b*a*", "*")))},
                {"*", true, new ArrayList<>(Collections.singletonList(
                        repoPathFile("r", "*", "*")))},
                {"*/*", true, new ArrayList<>(Arrays.asList(
                        repoPathFile("r", "*", "*"),
                        repoPathFile("r", "*/*", "*")))},
                {"*/a.z", true, new ArrayList<>(Collections.singletonList(
                        repoPathFile("r", "*", "a.z")))},
                {"a", false, new ArrayList<>(Collections.singletonList(
                        repoPathFile("r", ".", "a")))},
                {"/*", false, new ArrayList<>(Collections.singletonList(
                        repoPathFile("r", "", "*")))},
                {"/a*b", false, new ArrayList<>(Collections.singletonList(
                        repoPathFile("r", "", "a*b")))},
                {"a*b*", true, new ArrayList<>(Arrays.asList(
                        repoPathFile("r", "a*", "*b*"),
                        repoPathFile("r", "a*b*", "*"),
                        repoPathFile("r", ".", "a*b*")))},
                {"*b*", true, new ArrayList<>(Arrays.asList(
                        repoPathFile("r", "*b*", "*"),
                        repoPathFile("r", "*", "*b*")))}
        };
    }

    private void validateRepoPathFile(List<RepoPathFile> actual, List<RepoPathFile> expected, String pattern) {
        // Validate length.
        assertEquals(actual.size(), expected.size(),
                String.format("Wrong triple.\nPattern: %s\nExcpected: %s\nActual: %s", pattern, expected, actual));

        for (RepoPathFile triple : expected) {
            boolean found = false;
            for (RepoPathFile actualTriple : actual) {
                if (triple.equals(actualTriple)) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, String.format("Wrong triple for pattern: '%s'. Missing %s in %s", pattern, triple, actual));
        }
    }

    private static RepoPathFile repoPathFile(String repo, String path, String file) {
        return new RepoPathFile(repo, path, file);
    }
}
