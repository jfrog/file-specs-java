package org.jfrog.filespecs.aql;

import java.util.Objects;

class RepoPathFile {
    private String repo;
    private String path;
    private String file;

    RepoPathFile(String repo, String path, String file) {
        this.repo = repo;
        this.path = path;
        this.file = file;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RepoPathFile)) return false;
        RepoPathFile that = (RepoPathFile) o;
        return Objects.equals(repo, that.repo) &&
                Objects.equals(path, that.path) &&
                Objects.equals(file, that.file);
    }

    @Override
    public String toString() {
        return "RepoPathFile{" +
                "repo='" + repo + '\'' +
                ", path='" + path + '\'' +
                ", file='" + file + '\'' +
                '}';
    }
}
