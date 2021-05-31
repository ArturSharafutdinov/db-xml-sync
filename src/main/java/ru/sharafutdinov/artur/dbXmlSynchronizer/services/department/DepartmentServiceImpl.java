package ru.sharafutdinov.artur.dbXmlSynchronizer.services.department;

import org.apache.log4j.Logger;
import ru.sharafutdinov.artur.dbXmlSynchronizer.common.ConnectionSource;
import ru.sharafutdinov.artur.dbXmlSynchronizer.models.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The implementation of DepartmentService interface for manipulating data in a database
 */
public class DepartmentServiceImpl implements DepartmentService {

    private static final String SQL_SELECT_ALL = "SELECT id, depCode as code, depJob as job, description from DEPARTMENT";
    private static final String SQL_INSERT = "INSERT INTO DEPARTMENT(depCode,depJob,description) VALUES (?,?,?)";
    private static final String SQL_UPDATE = "UPDATE DEPARTMENT SET description = ? WHERE ID = ?";
    private static final String SQL_DELETE = "DELETE FROM DEPARTMENT WHERE ID=?";

    private static final Logger logger = Logger.getLogger(DepartmentServiceImpl.class);

    /**
     * @return all Departments from the database
     */
    public List<Department> getAll() {

        List<Department> departments = new ArrayList<>();

        Connection connection = ConnectionSource.getInstance().openConnection();

        try (Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            try (ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL)) {
                while (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    String code = resultSet.getString("code");
                    String job = resultSet.getString("job");
                    String description = resultSet.getString("description");
                    departments.add(new Department(id, code, job, description));
                }
            }
        } catch (SQLException exception) {
            logger.error("Failed to get all departments from database" + exception);
        } finally {
            ConnectionSource.getInstance().closeConnection();
        }

        return departments;
    }


    /**
     * Method The method synchronizes the database from the xml file within one transaction
     *
     * @param departmentsForUpdate - Set with data for update or add, depending on id
     * @param departmentsForRemove - Set with data to be deleted from the database is stored
     */
    @Override
    public void sync(Set<Department> departmentsForUpdate, Set<Department> departmentsForRemove) {

        Connection connection = ConnectionSource.getInstance().openConnection();

        boolean isSuccess = false;

        try {

            connection.setAutoCommit(false);

            try (PreparedStatement updateStatement = connection.prepareStatement(SQL_UPDATE);
                 PreparedStatement insertStatement = connection.prepareStatement(SQL_INSERT);
                 PreparedStatement deleteStatement = connection.prepareStatement(SQL_DELETE)
            ) {
                for (Department department : departmentsForUpdate) {
                    if (department.getId() != null) {
                        updateStatement.setString(1, department.getDescription());
                        updateStatement.setLong(2, department.getId());
                        updateStatement.addBatch();
                    } else {
                        insertStatement.setString(1, department.getCode());
                        insertStatement.setString(2, department.getJob());
                        insertStatement.setString(3, department.getDescription());
                        insertStatement.addBatch();
                    }
                }
                for (Department department : departmentsForRemove) {
                    deleteStatement.setLong(1, department.getId());
                    deleteStatement.addBatch();
                }

                insertStatement.executeBatch();
                updateStatement.executeBatch();
                deleteStatement.executeBatch();
                isSuccess = true;

            }

        } catch (SQLException exception) {
            logger.error("Failed to execute statements" + exception);
        } finally {
            try {
                if (isSuccess) {
                    connection.commit();
                    logger.info("Database synchronized successfully");
                    System.out.println("Database synchronized successfully");
                } else {
                    connection.rollback();
                    logger.info("Database wasn't synchronized");
                    System.out.println("Database wasn't synchronized");
                }
            } catch (SQLException exception) {
                logger.error("Failed to synchronize database" + exception);
                System.out.println("Failed to synchronize database");
            } finally {
                ConnectionSource.getInstance().closeConnection();
            }
        }


    }


}
