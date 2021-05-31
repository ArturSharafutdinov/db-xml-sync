package ru.sharafutdinov.artur.dbXmlSynchronizer.models;

import java.util.Objects;

public class Department {

    private Long id;

    private String code;

    private String job;

    private String description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Department(String code, String job, String description) {
        this.code = code;
        this.job = job;
        this.description = description;
    }

    public Department(Long id, String code, String job, String description) {
        this.id = id;
        this.code = code;
        this.job = job;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", job='" + job + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, job);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Department department = (Department) obj;
        return department.code.equals(this.code) && department.job.equals(this.job);
    }


}
