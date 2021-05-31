package ru.sharafutdinov.artur.dbXmlSynchronizer.exceptions;

/**
 * @see ru.sharafutdinov.artur.dbXmlSynchronizer.services.DatabaseXmlServiceImpl getAllDepartmentsFromXmlFile method
 */
public class DuplicateException extends RuntimeException {
    public DuplicateException(String message) {
        super(message);
    }
}
