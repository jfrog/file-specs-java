# file-specs-java

## Overview

[File specs](https://www.jfrog.com/confluence/display/JFROG/Using+File+Specs) are a way to locate and place files in Artifactory.
They are JSON-formatted and can also include extra options to be used in operations that use Artifactory.

This library provides methods for parsing file specs, validating them and generating AQL queries from them to be used as-is in Artifactory.

## Tests

To run the tests, execute the following command at the root directory of the project:
```
./gradlew clean test
```

## APIs

### Parsing a File Spec From a String

Using the static method `FileSpec.fromString()` you can turn a string that represents a file spec into a FileSpec object.

Example:

```java
FileSpec fileSpec = FileSpec.fromString("{\"files\": [{\"pattern\": \"repo1/file.txt\"}]}");
```

### Generating AQL Queries From a File Spec

Using the method `toAql()` you can turn a file spec into AQL queries. The file spec can contain more than file group - an AQL will be generated for each group.

Example:

```java
FileSpec fileSpec = new FileSpec();
FilesGroup group1 = new FilesGroup().setPattern("repo1/file.txt");
fileSpec.addFilesGroup(group1);
FilesGroup group2 = new FilesGroup().setPattern("repo1/dir/*");
fileSpec.addFilesGroup(group2);
List<String> aqls = fileSpec.toAql();
```
After that, the list `aqls` contains two strings with AQL queries (one for each files group).

### Using File Specs with JFrog Distribution

Using the method `DistributionHelper.toSpecComponents()`, you can use file specs to generate AQL queries and more parameters needed by the JFrog Distribution API.

Example:

```java
FileSpec fileSpec = new FileSpec();
FilesGroup group = new FilesGroup().setPattern("repo1/dir/*").setTarget("repo2/{1}").setTargetProps("k1=v11,v12;k2=v2");
fileSpec.addFilesGroup(group);
List<DistributionSpecComponent> components = DistributionHelper.toSpecComponents(fileSpec);
```
Now, the list `components` contains one DistributionSpecComponent object (because the file spec contains only one files group) with:
* `aql` - a string of the AQL query
* `mappings` - list of mappings, based on the fields 'pattern' and 'target' of the files group
* `addedProps` - list of properties, based on the field 'targetProps' of the files group

Read more about [JFrog Distribution REST API](https://www.jfrog.com/confluence/display/JFROG/Distribution+REST+API).
