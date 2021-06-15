package org.jfrog.filespecs.entities;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;

/**
 * Created by romang on 4/20/16.
 */
public class FilesGroup {
    protected Aql aql;
    protected String pattern;
    protected String target;
    protected String props;
    protected String targetProps;
    protected String recursive;
    protected String flat;
    protected String regexp;
    protected String explode;
    protected String[] exclusions;
    protected String[] sortBy;
    protected String sortOrder;
    protected String limit;
    protected String offset;

    public enum SpecType {
        PATTERN,
        AQL
    }

    public String getAql() {
        if (aql != null) {
            return aql.getFind();
        }
        return null;
    }

    public String getPattern() {
        return pattern;
    }

    public String getTarget() {
        return target;
    }

    public FilesGroup setAql(Aql aql) {
        this.aql = aql;
        return this;
    }

    public FilesGroup setPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    public FilesGroup setTarget(String target) {
        this.target = target;
        return this;
    }

    public String getProps() {
        return props;
    }

    public FilesGroup setProps(String props) {
        this.props = props;
        return this;
    }

    public String getTargetProps() {
        return targetProps;
    }

    public FilesGroup setTargetProps(String targetProps) {
        this.targetProps = targetProps;
        return this;
    }

    public String getRecursive() {
        return recursive;
    }

    public FilesGroup setRecursive(String recursive) {
        this.recursive = recursive;
        return this;
    }

    public String getRegexp() {
        return regexp;
    }

    public FilesGroup setRegexp(String regexp) {
        this.regexp = regexp;
        return this;
    }

    public String getFlat() {
        return flat;
    }

    public FilesGroup setFlat(String flat) {
        this.flat = flat;
        return this;
    }

    public String getExplode() {
        return explode;
    }

    public FilesGroup setExplode(String explode) {
        this.explode = explode;
        return this;
    }

    public String[] getSortBy() {
        if (sortBy != null) {
            return sortBy;
        }
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }

    public String[] getExclusions() {
        return exclusions;
    }

    public FilesGroup setExclusions(String[] exclusions) {
        this.exclusions = exclusions;
        return this;
    }

    public String getExclusion(int index) {
        return exclusions[index];
    }

    public FilesGroup setSortBy(String[] sortBy) {
        this.sortBy = sortBy;
        return this;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public FilesGroup setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    public String getOffset() {
        return offset;
    }

    public FilesGroup setOffset(String offset) {
        this.offset = offset;
        return this;
    }

    public String getLimit() {
        return limit;
    }

    public FilesGroup setLimit(String resultLimit) {
        this.limit = resultLimit;
        return this;
    }

    @Override
    public String toString() {
        return "FilesGroup{" +
                "aql=" + aql +
                ", pattern='" + pattern + '\'' +
                ", target='" + target + '\'' +
                ", props='" + props + '\'' +
                ", targetProps='" + targetProps + '\'' +
                ", recursive='" + recursive + '\'' +
                ", flat='" + flat + '\'' +
                ", regexp='" + regexp + '\'' +
                ", explode='" + explode + '\'' +
                ", exclusions='" + Arrays.toString(exclusions) + '\'' +
                ", sortBy='" + Arrays.toString(sortBy) + '\'' +
                ", sortOrder='" + sortOrder + '\'' +
                ", offset='" + offset + '\'' +
                ", limit='" + limit + '\'' +
                '}';
    }

    /**
     * Calculates the type of the files group. If the files group doesn't match any type - null is returned.
     * @return the type of the files group
     */
    public SpecType getSpecType() {
        if (StringUtils.isNotEmpty(this.pattern)) {
            return SpecType.PATTERN;
        } else if (StringUtils.isNotEmpty(getAql())) {
            return SpecType.AQL;
        }
        return null;
    }
}
