package ru.sharafutdinov.artur.dbXmlSynchronizer;


import ru.sharafutdinov.artur.dbXmlSynchronizer.services.DatabaseXmlService;
import ru.sharafutdinov.artur.dbXmlSynchronizer.services.DatabaseXmlServiceImpl;

public class DepartmentLoader {

    public static void main(String[] args) {

        DatabaseXmlService databaseXmlService = new DatabaseXmlServiceImpl();

        if (args[0].equalsIgnoreCase("create")) {
            databaseXmlService.saveToXml(args[1]);
        } else if (args[0].equalsIgnoreCase("sync")) {
            databaseXmlService.sync(args[1]);
        } else {
            throw new RuntimeException("Invalid command Name");
        }
    }
}
