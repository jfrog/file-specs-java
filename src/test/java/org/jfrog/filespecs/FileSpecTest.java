package org.jfrog.filespecs;

import org.jfrog.filespecs.FileSpec;
import org.jfrog.filespecs.entities.InvalidFileSpecException;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileSpecTest {
    @Test(dataProvider = "toSearchAqlProvider")
    public void testToSearchAql(String spec, List<String> expectedAqls) throws InvalidFileSpecException {
        List<String> actualAqls = FileSpec.fromString(spec).toAql();
        Assert.assertEquals(actualAqls, expectedAqls);
    }

    @DataProvider
    private static Object[][] toSearchAqlProvider() {
        return new Object[][]{{
                "{\"files\": [{\"pattern\": \"repo-local\"}]}",
                Collections.singletonList("items.find({\"$or\":[{\"$and\":[{\"repo\":\"repo-local\",\"path\":{\"$match\":\"*\"},\"name\":{\"$match\":\"*\"}}]}]}).include(\"name\",\"repo\",\"path\",\"actual_md5\",\"actual_sha1\",\"size\",\"type\",\"modified\",\"created\",\"property\")")
        }, {
                "{\"files\": [{\"pattern\": \"repo-w*ldcard\"}]}",
                Collections.singletonList("items.find({\"$or\":[{\"$and\":[{\"repo\":{\"$match\":\"repo-w*\"},\"path\":{\"$match\":\"*\"},\"name\":{\"$match\":\"*ldcard\"}}]},{\"$and\":[{\"repo\":{\"$match\":\"repo-w*ldcard\"},\"path\":{\"$match\":\"*\"},\"name\":{\"$match\":\"*\"}}]}]}).include(\"name\",\"repo\",\"path\",\"actual_md5\",\"actual_sha1\",\"size\",\"type\",\"modified\",\"created\",\"property\")")
        }, {
                "{\"files\": [{\"pattern\": \"repo-local2/a*b*c/dd/\"}]}",
                Collections.singletonList("items.find({\"path\":{\"$ne\":\".\"},\"$or\":[{\"$and\":[{\"repo\":\"repo-local2\",\"path\":{\"$match\":\"a*b*c/dd\"},\"name\":{\"$match\":\"*\"}}]},{\"$and\":[{\"repo\":\"repo-local2\",\"path\":{\"$match\":\"a*b*c/dd/*\"},\"name\":{\"$match\":\"*\"}}]}]}).include(\"name\",\"repo\",\"path\",\"actual_md5\",\"actual_sha1\",\"size\",\"type\",\"modified\",\"created\",\"property\")")
        }, {
                "{\"files\": [{\"pattern\": \"repo-local*/a*b*c/dd/\"}]}",
                Collections.singletonList("items.find({\"path\":{\"$ne\":\".\"},\"$or\":[{\"$and\":[{\"repo\":{\"$match\":\"repo-local*\"},\"path\":{\"$match\":\"*a*b*c/dd\"},\"name\":{\"$match\":\"*\"}}]},{\"$and\":[{\"repo\":{\"$match\":\"repo-local*\"},\"path\":{\"$match\":\"*a*b*c/dd/*\"},\"name\":{\"$match\":\"*\"}}]}]}).include(\"name\",\"repo\",\"path\",\"actual_md5\",\"actual_sha1\",\"size\",\"type\",\"modified\",\"created\",\"property\")")
        }, {
                "{\"files\": [{\"pattern\": \"repo-local\", \"recursive\": \"false\"}]}",
                Collections.singletonList("items.find({\"$or\":[{\"$and\":[{\"repo\":\"repo-local\",\"path\":\".\",\"name\":{\"$match\":\"*\"}}]}]}).include(\"name\",\"repo\",\"path\",\"actual_md5\",\"actual_sha1\",\"size\",\"type\",\"modified\",\"created\",\"property\")")
        }, {
                "{\"files\": [{\"pattern\": \"*repo-local\", \"recursive\": \"false\"}]}",
                Collections.singletonList("items.find({\"$or\":[{\"$and\":[{\"repo\":{\"$match\":\"*\"},\"path\":\".\",\"name\":{\"$match\":\"*repo-local\"}}]},{\"$and\":[{\"repo\":{\"$match\":\"*repo-local\"},\"path\":\".\",\"name\":{\"$match\":\"*\"}}]}]}).include(\"name\",\"repo\",\"path\",\"actual_md5\",\"actual_sha1\",\"size\",\"type\",\"modified\",\"created\",\"property\")")
        }, {
                "{\"files\": [{\"pattern\": \"repo-local2/a*b*c/dd/\", \"recursive\": \"false\"}, {\"pattern\": \"*/a*b*c/dd/\", \"recursive\": \"false\"}]}",
                Arrays.asList(
                        "items.find({\"path\":{\"$ne\":\".\"},\"$or\":[{\"$and\":[{\"repo\":\"repo-local2\",\"path\":{\"$match\":\"a*b*c/dd\"},\"name\":{\"$match\":\"*\"}}]}]}).include(\"name\",\"repo\",\"path\",\"actual_md5\",\"actual_sha1\",\"size\",\"type\",\"modified\",\"created\",\"property\")",
                        "items.find({\"path\":{\"$ne\":\".\"},\"$or\":[{\"$and\":[{\"repo\":{\"$match\":\"*\"},\"path\":{\"$match\":\"*a*b*c/dd\"},\"name\":{\"$match\":\"*\"}}]}]}).include(\"name\",\"repo\",\"path\",\"actual_md5\",\"actual_sha1\",\"size\",\"type\",\"modified\",\"created\",\"property\")"
                )
        }, {
                "{\"files\": [{\"aql\": {\"items.find\": {\"repo\": \"a\", \"path\": \"b\", \"name\": \"c\"}}}]}",
                Collections.singletonList("items.find({\"repo\":\"a\",\"path\":\"b\",\"name\":\"c\"}).include(\"name\",\"repo\",\"path\",\"actual_md5\",\"actual_sha1\",\"size\",\"type\",\"modified\",\"created\",\"property\")")
        }
        };
    }
}
