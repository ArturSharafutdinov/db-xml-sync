package ru.sharafutdinov.artur.dbXmlSynchronizer.services.department;

import ru.sharafutdinov.artur.dbXmlSynchronizer.models.Department;

import java.util.List;
import java.util.Set;

public interface DepartmentService {

    List<Department> getAll();

    void sync(Set<Department> departmentsForUpdate, Set<Department> departmentsForRemove);

}
