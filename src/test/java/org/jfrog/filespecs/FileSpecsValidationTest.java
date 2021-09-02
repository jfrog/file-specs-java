package org.jfrog.filespecs;

import org.jfrog.filespecs.entities.InvalidFileSpecException;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class FileSpecsValidationTest {
    @Test(dataProvider = "validateSearchBasedFileSpecProvider")
    public void testValidateSearchBasedFileSpec(String fileSpecStr, boolean expectedException) {
        try {
            FileSpec fileSpec = FileSpec.fromString(fileSpecStr);
            FileSpecsValidation.validateSearchBasedFileSpec(fileSpec);
            if (expectedException) {
                Assert.fail("Expected an exception to be thrown, but it didn't. fileSpecStr: " + fileSpecStr);
            }
        } catch (InvalidFileSpecException e) {
            if (!expectedException) {
                Assert.fail("An exception was thrown, but it shouldn't have. fileSpecStr: " + fileSpecStr);
            }
        }
    }

    @DataProvider
    private static Object[][] validateSearchBasedFileSpecProvider() {
        return new Object[][]{
                {"", true},
                {"{}", true},
                {"{\"files\": []}", true},
                {"{\"files\": [{\"flat\": \"false\"}]}", true},
                {"{\"files\": [{\"build\": \"test-build/1\"}]}", false},
                {"{\"files\": [{\"pattern\": \"  \"}]}", true},
                {"{\"files\": [{\"pattern\": \"repo-local/file.sfx\"}]}", false},
                {"{\"files\": [{\"pattern\": \"repo-local/file.sfx\", \"build\": \"test-build/1\"}]}", false},
                {"{\"files\": [{\"aql\": {\"items.find\": {\"repo\": \"a\", \"path\": \"b\", \"name\": \"c\"}}}]}", false},
                {"{\"files\": [{\"aql\": {\"items.find\": {\"repo\": \"a\", \"path\": \"b\", \"name\": \"c\"}}, \"build\": \"test-build/1\"}]}", false},
                {"{\"files\": [{\"pattern\": \"repo-local/file.sfx\", \"aql\": {\"items.find\": {\"repo\": \"a\", \"path\": \"b\", \"name\": \"c\"}}}]}", true},
                {"{\"files\": [{\"pattern\": \"repo-local/file.sfx\", \"exclusions\": [\"exclude-that\"]}]}", false},
                {"{\"files\": [{\"pattern\": \"repo-local/file.sfx\", \"exclusions\": [\"exclude-that\"], \"excludePatterns\": [\"and-that-too\"]}]}", true},
                {"{\"files\": [{\"aql\": {\"items.find\": {\"repo\": \"a\", \"path\": \"b\", \"name\": \"c\"}}, \"exclusions\": [\"exclude-that\"]}]}", true}
        };
    }

    @Test(dataProvider = "validateUploadFileSpecProvider")
    public void testValidateUploadFileSpec(String fileSpecStr, boolean expectedException) {
        try {
            FileSpec fileSpec = FileSpec.fromString(fileSpecStr);
            FileSpecsValidation.validateUploadFileSpec(fileSpec);
            if (expectedException) {
                Assert.fail("Expected an exception to be thrown, but it didn't. fileSpecStr: " + fileSpecStr);
            }
        } catch (InvalidFileSpecException e) {
            if (!expectedException) {
                Assert.fail("An exception was thrown, but it shouldn't have. fileSpecStr: " + fileSpecStr);
            }
        }
    }

    @DataProvider
    private static Object[][] validateUploadFileSpecProvider() {
        return new Object[][]{
                {"", true},
                {"{}", true},
                {"{\"files\": []}", true},
                {"{\"files\": [{\"flat\": \"false\"}]}", true},
                {"{\"files\": [{\"pattern\": \"repo-local/file.sfx\"}]}", true},
                {"{\"files\": [{\"target\": \"local/path.sfx\"}]}", true},
                {"{\"files\": [{\"pattern\": \"  \", \"target\": \"local/path.sfx\"}]}", true},
                {"{\"files\": [{\"pattern\": \"repo-local/file.sfx\", \"target\": \"  \"}]}", true},
                {"{\"files\": [{\"pattern\": \"repo-local/file.sfx\", \"target\": \"local/path.sfx\"}]}", false},
                {"{\"files\": [{\"aql\": {\"items.find\": {\"repo\": \"a\", \"path\": \"b\", \"name\": \"c\"}}, \"target\": \"local/path.sfx\"}]}", true},
                {"{\"files\": [{\"pattern\": \"repo-local/file.sfx\", \"aql\": {\"items.find\": {\"repo\": \"a\", \"path\": \"b\", \"name\": \"c\"}}}]}", true},
                {"{\"files\": [{\"pattern\": \"repo-local/file.sfx\", \"exclusions\": [\"exclude-that\"], \"target\": \"local/path.sfx\"}]}", false},
                {"{\"files\": [{\"pattern\": \"repo-local/file.sfx\", \"exclusions\": [\"exclude-that\"], \"excludePatterns\": [\"and-that-too\"], \"target\": \"local/path.sfx\"}]}", true},
                {"{\"files\": [{\"aql\": {\"items.find\": {\"repo\": \"a\", \"path\": \"b\", \"name\": \"c\"}}, \"exclusions\": [\"exclude-that\"], \"target\": \"local/path.sfx\"}]}", true}
        };
    }
}
