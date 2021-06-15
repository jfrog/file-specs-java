package org.jfrog.filespecs.validation;

import org.jfrog.filespecs.entities.FileSpec;
import org.jfrog.filespecs.entities.InvalidFileSpecException;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SearchBasedSpecValidatorTest {
    @Test(dataProvider = "validateProvider")
    public void testValidate(FileSpec fileSpec, boolean expectedException) {
        try {
            new SearchBasedSpecValidator().validate(fileSpec);
            if (expectedException) {
                Assert.fail("Expected an exception to be thrown, but it didn't. fileSpec: " + fileSpec);
            }
        } catch (InvalidFileSpecException e) {
            if (!expectedException) {
                Assert.fail("An exception was thrown, but it shouldn't have. fileSpec: " + fileSpec);
            }
        }
    }

    @DataProvider
    private static Object[][] validateProvider() throws InvalidFileSpecException {
        return new Object[][]{
                {FileSpec.fromString("{}"), true},
                {FileSpec.fromString("{\"files\": []}"), true},
                {FileSpec.fromString("{\"files\": [{\"flat\": \"false\"}]}"), true},
                {FileSpec.fromString("{\"files\": [{\"pattern\": \"repo-local/file.sfx\"}]}"), false},
                {FileSpec.fromString("{\"files\": [{\"aql\": {\"items.find\": {\"repo\": \"a\", \"path\": \"b\", \"name\": \"c\"}}}]}"), false},
                {FileSpec.fromString("{\"files\": [{\"pattern\": \"repo-local/file.sfx\", \"aql\": {\"items.find\": {\"repo\": \"a\", \"path\": \"b\", \"name\": \"c\"}}}]}"), true},
                {FileSpec.fromString("{\"files\": [{\"pattern\": \"repo-local/file.sfx\", \"exclusions\": [\"exclude-that\"]}]}"), false},
                {FileSpec.fromString("{\"files\": [{\"aql\": {\"items.find\": {\"repo\": \"a\", \"path\": \"b\", \"name\": \"c\"}}, \"exclusions\": [\"exclude-that\"]}]}"), true}
        };
    }
}
