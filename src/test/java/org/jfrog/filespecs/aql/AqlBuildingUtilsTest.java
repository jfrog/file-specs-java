package org.jfrog.filespecs.aql;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class AqlBuildingUtilsTest {
    @Test(dataProvider = "buildAqlSearchQueryProvider")
    public void testBuildAqlSearchQuery(String pattern, boolean recursive, String expectedAql) {
        String result = AqlBuildingUtils.buildAqlSearchQuery(pattern, null, null, recursive, null);
        assertEquals(result, expectedAql);
    }

    @DataProvider
    private static Object[][] buildAqlSearchQueryProvider() {
        return new Object[][]{
                {"repo-local", true, "{\"$or\":[{\"$and\":[{\"repo\":\"repo-local\",\"path\":{\"$match\":\"*\"},\"name\":{\"$match\":\"*\"}}]}]}"},
                {"repo-w*ldcard", true, "{\"$or\":[{\"$and\":[{\"repo\":{\"$match\":\"repo-w*\"},\"path\":{\"$match\":\"*\"},\"name\":{\"$match\":\"*ldcard\"}}]},{\"$and\":[{\"repo\":{\"$match\":\"repo-w*ldcard\"},\"path\":{\"$match\":\"*\"},\"name\":{\"$match\":\"*\"}}]}]}"},
                {"repo-local2/a*b*c/dd/", true, "{\"path\":{\"$ne\":\".\"},\"$or\":[{\"$and\":[{\"repo\":\"repo-local2\",\"path\":{\"$match\":\"a*b*c/dd\"},\"name\":{\"$match\":\"*\"}}]},{\"$and\":[{\"repo\":\"repo-local2\",\"path\":{\"$match\":\"a*b*c/dd/*\"},\"name\":{\"$match\":\"*\"}}]}]}"},
                {"repo-local*/a*b*c/dd/", true, "{\"path\":{\"$ne\":\".\"},\"$or\":[{\"$and\":[{\"repo\":{\"$match\":\"repo-local*\"},\"path\":{\"$match\":\"*a*b*c/dd\"},\"name\":{\"$match\":\"*\"}}]},{\"$and\":[{\"repo\":{\"$match\":\"repo-local*\"},\"path\":{\"$match\":\"*a*b*c/dd/*\"},\"name\":{\"$match\":\"*\"}}]}]}"},
                {"repo-local", false, "{\"$or\":[{\"$and\":[{\"repo\":\"repo-local\",\"path\":\".\",\"name\":{\"$match\":\"*\"}}]}]}"},
                {"*repo-local", false, "{\"$or\":[{\"$and\":[{\"repo\":{\"$match\":\"*\"},\"path\":\".\",\"name\":{\"$match\":\"*repo-local\"}}]},{\"$and\":[{\"repo\":{\"$match\":\"*repo-local\"},\"path\":\".\",\"name\":{\"$match\":\"*\"}}]}]}"},
                {"repo-local2/a*b*c/dd/", false, "{\"path\":{\"$ne\":\".\"},\"$or\":[{\"$and\":[{\"repo\":\"repo-local2\",\"path\":{\"$match\":\"a*b*c/dd\"},\"name\":{\"$match\":\"*\"}}]}]}"},
                {"*/a*b*c/dd/", false, "{\"path\":{\"$ne\":\".\"},\"$or\":[{\"$and\":[{\"repo\":{\"$match\":\"*\"},\"path\":{\"$match\":\"*a*b*c/dd\"},\"name\":{\"$match\":\"*\"}}]}]}"}
        };
    }
}
