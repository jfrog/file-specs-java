package org.jfrog.filespecs.distribution;

import java.util.Objects;

public class PathMapping {
    private String input;
    private String output;

    public PathMapping(String input, String output) {
        this.input = input;
        this.output = output;
    }

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }

    @Override
    public String toString() {
        return "PathMapping{" +
                "input='" + input + '\'' +
                ", output='" + output + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathMapping that = (PathMapping) o;
        return input.equals(that.input) && output.equals(that.output);
    }

    @Override
    public int hashCode() {
        return Objects.hash(input, output);
    }
}
