package ru.sharafutdinov.artur.dbXmlSynchronizer.models;

import java.util.Objects;

public class DepartmentKey {

    private String depCode;

    private String depJob;

    public String getDepCode() {
        return depCode;
    }

    public void setDepCode(String depCode) {
        this.depCode = depCode;
    }

    public String getDepJob() {
        return depJob;
    }

    public void setDepJob(String depJob) {
        this.depJob = depJob;
    }

    public DepartmentKey(String depCode, String depJob) {
        this.depCode = depCode;
        this.depJob = depJob;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DepartmentKey that = (DepartmentKey) o;
        return this.depCode.equals(that.depCode) &&
                this.depJob.equals(that.depJob);
    }

    @Override
    public int hashCode() {
        return Objects.hash(depCode, depJob);
    }
}
