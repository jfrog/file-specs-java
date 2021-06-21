[![Test](https://github.com/jfrog/file-specs-java/actions/workflows/test.yml/badge.svg)](https://github.com/jfrog/file-specs-java/actions/workflows/test.yml)

# file-specs-java

## Overview

[File specs](https://www.jfrog.com/confluence/display/JFROG/Using+File+Specs) can be used to define a list of files.
You can then perform operations on these files, such as upload or download.
File specs are specified in a JSON format.

This library provides methods for parsing file specs, validating them and generating AQL queries from them.

## Tests

To run the tests, execute the following command while at the root directory of the project:
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

Using the method `toAql()` you can turn a file spec into AQL queries. The file spec can include more than one file group - an AQL will be generated for each group.

Example:

```java
FileSpec fileSpec = new FileSpec();
FilesGroup group1 = new FilesGroup().setPattern("repo1/file.txt");
fileSpec.addFilesGroup(group1);
FilesGroup group2 = new FilesGroup().setPattern("repo1/dir/*");
fileSpec.addFilesGroup(group2);
List<String> aqls = fileSpec.toAql();
```
In the above example, the returned list contains two strings with AQL queries, one for each group.
