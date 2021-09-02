package org.jfrog.filespecs.entities;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;

/**
 * Created by romang on 4/20/16.
 */
public class FilesGroup {
    private Aql aql;
    private String pattern;
    private String target;
    private String props;
    private String targetProps;
    private String recursive;
    private String flat;
    private String regexp;
    private String build;
    private String explode;
    private String[] exclusions;
    private String[] sortBy;
    private String sortOrder;
    private String limit;
    private String offset;

    /**
     * @deprecated Use {@link FilesGroup#exclusions} instead.
     */
    @Deprecated
    protected String[] excludePatterns;

    public enum SpecType {
        BUILD,
        PATTERN,
        AQL
    }

    public String getAql() {
        if (aql != null) {
            return aql.getFind();
        }
        return null;
    }

    public FilesGroup setAql(Aql aql) {
        this.aql = aql;
        return this;
    }

    public String getPattern() {
        return pattern;
    }

    public FilesGroup setPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    public String getTarget() {
        return target;
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

    public String getFlat() {
        return flat;
    }

    public FilesGroup setFlat(String flat) {
        this.flat = flat;
        return this;
    }

    public String getRegexp() {
        return regexp;
    }

    public FilesGroup setRegexp(String regexp) {
        this.regexp = regexp;
        return this;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getExplode() {
        return explode;
    }

    public FilesGroup setExplode(String explode) {
        this.explode = explode;
        return this;
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

    public String[] getSortBy() {
        if (sortBy != null) {
            return sortBy;
        }
        return ArrayUtils.EMPTY_STRING_ARRAY;
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

    public String getLimit() {
        return limit;
    }

    public FilesGroup setLimit(String resultLimit) {
        this.limit = resultLimit;
        return this;
    }

    public String getOffset() {
        return offset;
    }

    public FilesGroup setOffset(String offset) {
        this.offset = offset;
        return this;
    }

    /**
     * @deprecated Use {@link FilesGroup#getExclusions()} instead.
     */
    @Deprecated
    public String[] getExcludePatterns() {
        return excludePatterns;
    }

    /**
     * @deprecated Use {@link FilesGroup#setExclusions(String[] exclusions)} instead.
     */
    @Deprecated
    public void setExcludePatterns(String[] excludePatterns) {
        this.excludePatterns = excludePatterns;
    }

    @Deprecated
    public String getExcludePattern(int index) {
        return excludePatterns[index];
    }

    @Deprecated
    public void setExcludePattern(String excludePattern, int index) {
        this.excludePatterns[index] = excludePattern;
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
                ", build='" + build + '\'' +
                ", explode='" + explode + '\'' +
                ", exclusions=" + Arrays.toString(exclusions) +
                ", excludePatterns=" + Arrays.toString(excludePatterns) +
                ", sortBy=" + Arrays.toString(sortBy) +
                ", sortOrder='" + sortOrder + '\'' +
                ", limit='" + limit + '\'' +
                ", offset='" + offset + '\'' +
                '}';
    }

    /**
     * Returns the type of the files group. If the files group doesn't match any type - null is returned.
     * @return the type of the files group
     */
    public SpecType getSpecType() {
        if (StringUtils.isNotEmpty(this.build) && StringUtils.isEmpty(getAql()) && (StringUtils.isEmpty(this.pattern) || this.pattern.equals("*"))) {
            return SpecType.BUILD;
        } else if (StringUtils.isNotEmpty(this.pattern)) {
            return SpecType.PATTERN;
        } else if (StringUtils.isNotEmpty(getAql())) {
            return SpecType.AQL;
        }
        return null;
    }
}
