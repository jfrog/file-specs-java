package org.jfrog.filespecs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jfrog.filespecs.aql.AqlConverter;
import org.jfrog.filespecs.entities.FilesGroup;
import org.jfrog.filespecs.entities.InvalidFileSpecException;
import org.jfrog.filespecs.validation.SearchBasedSpecValidator;
import org.jfrog.filespecs.validation.SpecsValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by romang on 4/20/16.
 */
public class FileSpec {
    private List<FilesGroup> files;

    public FileSpec() {
        this.files = new ArrayList<>();
    }

    public List<FilesGroup> getFiles() {
        return files;
    }

    public void addFilesGroup(FilesGroup filesGroup) {
        this.files.add(filesGroup);
    }

    /**
     * Validates the file spec and creates an AQL query out of each files group in it.
     * This method is for search-based file specs only (including download).
     * @return a list of AQL queries matching the files groups in the file spec.
     * @throws InvalidFileSpecException if the given file spec is invalid or not search-based.
     */
    public List<String> toAql() throws InvalidFileSpecException {
        SpecsValidator specsValidator = new SearchBasedSpecValidator();
        specsValidator.validate(this);
        List<String> aqls = new ArrayList<>();

        for (FilesGroup file : this.getFiles()) {
            aqls.add(AqlConverter.convertFilesGroupToAql(file));
        }

        return aqls;
    }

    /**
     * Converts string to a FileSpec object
     *
     * @param specStr the string to convert
     * @return a FileSpec object that represents the string
     * @throws InvalidFileSpecException in case of parsing problem
     */
    public static FileSpec fromString(String specStr) throws InvalidFileSpecException {
        ObjectMapper mapper = new ObjectMapper();
        // When mapping the file spec from String to FileSpec, one backslash is being removed, multiplying the backslashes solves this.
        specStr = specStr.replace("\\", "\\\\");
        FileSpec fileSpec;
        try {
            fileSpec = mapper.readValue(specStr, FileSpec.class);
        } catch (JsonProcessingException e) {
            throw new InvalidFileSpecException(String.format("Parsing of file spec failed:\n%s", specStr), e);
        }
        FileSpecsParsingUtils.pathToUnixFormat(fileSpec);

        return fileSpec;
    }
}
