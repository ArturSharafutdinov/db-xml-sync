package ru.sharafutdinov.artur.dbXmlSynchronizer.services;

public interface DatabaseXmlService {

    void saveToXml(String fileName);

    void sync(String fileName);
}
