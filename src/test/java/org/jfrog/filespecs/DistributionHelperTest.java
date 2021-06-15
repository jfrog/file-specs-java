package org.jfrog.filespecs;

import org.jfrog.filespecs.distribution.DistributionSpecComponent;
import org.jfrog.filespecs.distribution.PathMapping;
import org.jfrog.filespecs.entities.FileSpec;
import org.jfrog.filespecs.entities.InvalidFileSpecException;
import org.jfrog.filespecs.properties.Property;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;

public class DistributionHelperTest {
    @Test(dataProvider = "toSpecComponentsProvider")
    public void testToSpecComponents(String spec, List<DistributionSpecComponent> expectedComponents) throws InvalidFileSpecException {
        List<DistributionSpecComponent> actualAqls = DistributionHelper.toSpecComponents(FileSpec.fromString(spec));
        Assert.assertEquals(actualAqls, expectedComponents);
    }

    @DataProvider
    private static Object[][] toSpecComponentsProvider() {
        return new Object[][]{{
                "{\"files\": [{\"pattern\": \"repo-local/file.sfx\"}]}",
                Collections.singletonList(
                        new DistributionSpecComponent(
                                "items.find({\"$or\":[{\"$and\":[{\"repo\":\"repo-local\",\"path\":\".\",\"name\":\"file.sfx\"}]}]}).include(\"name\",\"repo\",\"path\",\"actual_md5\",\"actual_sha1\",\"size\",\"type\",\"property\")",
                                new ArrayList<>(),
                                new ArrayList<>())
                )
        }, {
                "{\"files\": [{\"pattern\": \"a/b/c\", \"target\": \"a/b/x\", \"targetProps\": \"k1=v11,v12;k2=v2;k1=v13,v11\"}]}",
                Collections.singletonList(
                        new DistributionSpecComponent(
                                "items.find({\"path\":{\"$ne\":\".\"},\"$or\":[{\"$and\":[{\"repo\":\"a\",\"path\":\"b\",\"name\":\"c\"}]}]}).include(\"name\",\"repo\",\"path\",\"actual_md5\",\"actual_sha1\",\"size\",\"type\",\"property\")",
                                Collections.singletonList(new PathMapping("^a/b/c$", "a/b/x")),
                                Arrays.asList(new Property("k1", asSet("v11", "v12", "v13")), new Property("k2", asSet("v2"))))
                )
        }, {
                "{\"files\": [{\"pattern\": \"a/*/c/*\", \"target\": \"a/b/c/{1}-{2}\"}, {\"pattern\": \"a/b/c\", \"target\": \"a/b/x\"}]}",
                Arrays.asList(
                        new DistributionSpecComponent(
                                "items.find({\"path\":{\"$ne\":\".\"},\"$or\":[{\"$and\":[{\"repo\":\"a\",\"path\":{\"$match\":\"*/c\"},\"name\":{\"$match\":\"*\"}}]},{\"$and\":[{\"repo\":\"a\",\"path\":{\"$match\":\"*/c/*\"},\"name\":{\"$match\":\"*\"}}]}]}).include(\"name\",\"repo\",\"path\",\"actual_md5\",\"actual_sha1\",\"size\",\"type\",\"property\")",
                                Collections.singletonList(new PathMapping("^a/.*/c/.*$", "a/b/c/$1-$2")),
                                new ArrayList<>()),
                        new DistributionSpecComponent(
                                "items.find({\"path\":{\"$ne\":\".\"},\"$or\":[{\"$and\":[{\"repo\":\"a\",\"path\":\"b\",\"name\":\"c\"}]}]}).include(\"name\",\"repo\",\"path\",\"actual_md5\",\"actual_sha1\",\"size\",\"type\",\"property\")",
                                Collections.singletonList(new PathMapping("^a/b/c$", "a/b/x")),
                                new ArrayList<>())
                )
        }, {
                "{\"files\": [{\"aql\": {\"items.find\": {\"repo\": \"a\", \"path\": \"b\", \"name\": \"c\"}}, \"target\": \"a/b/x\"}]}",
                Collections.singletonList(
                        new DistributionSpecComponent(
                                "items.find({\"repo\":\"a\",\"path\":\"b\",\"name\":\"c\"}).include(\"name\",\"repo\",\"path\",\"actual_md5\",\"actual_sha1\",\"size\",\"type\",\"property\")",
                                Collections.singletonList(new PathMapping("^$", "a/b/x")),
                                new ArrayList<>())
                )
        }
        };
    }

    private static Set<String> asSet(String... elements) {
        return new HashSet<>(Arrays.asList(elements));
    }
}
