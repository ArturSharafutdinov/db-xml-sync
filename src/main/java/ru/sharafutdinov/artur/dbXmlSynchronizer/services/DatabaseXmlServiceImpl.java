package ru.sharafutdinov.artur.dbXmlSynchronizer.services;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.sharafutdinov.artur.dbXmlSynchronizer.exceptions.DuplicateException;
import ru.sharafutdinov.artur.dbXmlSynchronizer.models.Department;
import ru.sharafutdinov.artur.dbXmlSynchronizer.models.DepartmentKey;
import ru.sharafutdinov.artur.dbXmlSynchronizer.services.department.DepartmentService;
import ru.sharafutdinov.artur.dbXmlSynchronizer.services.department.DepartmentServiceImpl;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The implementation of DatabaseXmlService interface for combine operations for working with database and xml files
 */
public class DatabaseXmlServiceImpl implements DatabaseXmlService {

    private static final String XML_FILES_DIRECTORY_PATH = "C:\\xml\\";

    private final DepartmentService departmentService;

    private static final Logger logger = Logger.getLogger(DatabaseXmlServiceImpl.class);

    public DatabaseXmlServiceImpl() {
        this.departmentService = new DepartmentServiceImpl();
    }

    /**
     * @param fileName - The name of the xml file to be created
     */
    @Override
    public void saveToXml(String fileName) {

        File newXmlFile = new File(XML_FILES_DIRECTORY_PATH + fileName);

        try {
            if (!newXmlFile.createNewFile()) {
                logger.error("File with name " + fileName + " already exists");
                System.out.println("File wasn't created");
                return;
            } else {
                logger.info("File with name " + fileName + " created successfully");
            }
        } catch (IOException exception) {
            logger.error("File with name " + fileName + " cannot be created " + exception);
            System.out.println("File wasn't created");
        }

        Document document;
        try {
            document = createDocument();
            document.setXmlStandalone(true);
            DOMSource domSource = new DOMSource(document);
            Transformer transformer;
            transformer = getTransformer();
            StreamResult streamResult = new StreamResult(newXmlFile);
            transformer.transform(domSource, streamResult);
            logger.info("File with name " + fileName + " was successfully populated with data");
            System.out.println("File with name " + fileName + " was successfully populated with data");
        } catch (ParserConfigurationException | TransformerException e) {
            logger.error("File filling error");
            System.out.println("File wasn't created");
        }

    }

    /**
     * @param fileName - The name of the xml file to be parsed
     */
    @Override
    public void sync(String fileName) {

        try {
            final Map<DepartmentKey, Department> departmentsFromXml = new HashMap<>();

            for (Department department : getAllDepartmentsFromXmlFile(fileName)) {
                DepartmentKey departmentKey = new DepartmentKey(department.getCode(), department.getJob());
                departmentsFromXml.put(departmentKey, department);
            }

            final Map<DepartmentKey, Department> departmentsFromDatabase = new HashMap<>();

            for (Department department : departmentService.getAll()) {
                DepartmentKey departmentKey = new DepartmentKey(department.getCode(), department.getJob());
                departmentsFromDatabase.put(departmentKey, department);
            }

            Set<Department> departmentsForDelete = new HashSet<>();
            Set<Department> departmentsForUpdate = new HashSet<>();

            Set<Map.Entry<DepartmentKey, Department>> departmentsFromDatabaseEntrySet = departmentsFromDatabase.entrySet();
            for (Map.Entry<DepartmentKey, Department> currentDBDepartment : departmentsFromDatabaseEntrySet) {
                if (!departmentsFromXml.containsKey(currentDBDepartment.getKey())) {
                    departmentsForDelete.add(currentDBDepartment.getValue());
                } else {
                    Department department = departmentsFromXml.get(currentDBDepartment.getKey());
                    if (!currentDBDepartment.getValue().getDescription().equals(department.getDescription())) {
                        currentDBDepartment.getValue().setDescription(department.getDescription());
                        departmentsForUpdate.add(currentDBDepartment.getValue());
                    }
                }
                departmentsFromXml.remove(currentDBDepartment.getKey());
            }
            Set<Department> departmentsForInsert = new HashSet<>(departmentsFromXml.values());


            departmentService.sync(Stream.concat(departmentsForInsert.stream(), departmentsForUpdate.stream()).collect(Collectors.toSet()), departmentsForDelete);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

    }

    private Transformer getTransformer() throws TransformerConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        setTransformerOutputProperties(transformer);
        return transformer;
    }

    private void setTransformerOutputProperties(Transformer transformer) {
        transformer.setOutputProperty("http://www.oracle.com/xml/is-standalone", "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
    }

    private Document getDocumentFromDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

        return documentBuilder.newDocument();
    }

    private Element createDepartmentElement(Document document, Department department) {
        Element departmentElem = document.createElement("department");
        departmentElem.setAttribute("depCode", department.getCode());
        departmentElem.setAttribute("depJob", department.getJob());
        departmentElem.setAttribute("description", department.getDescription());
        return departmentElem;
    }

    private void addAllDepartments(Document document, Element root) {
        this.departmentService.getAll().forEach(department -> root.appendChild(createDepartmentElement(document, department)));
    }

    private Document createDocument() throws ParserConfigurationException {

        Document document = getDocumentFromDocumentBuilder();

        // root element
        Element root = document.createElement("database");
        document.appendChild(root);

        addAllDepartments(document, root);

        return document;
    }

    private Set<Department> getAllDepartmentsFromXmlFile(String fileName) throws ParserConfigurationException, IOException, SAXException {
        File xmlFile = new File(XML_FILES_DIRECTORY_PATH + fileName);
        if (!xmlFile.exists()) {
            throw new IllegalArgumentException("File with name " + fileName + " doesn't exist");
        }
        Set<Department> departments = new HashSet<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFile);
        NodeList rows = document.getDocumentElement().getElementsByTagName("department");
        for (int i = 0; i < rows.getLength(); i++) {
            NamedNodeMap attributes = rows.item(i).getAttributes();
            String code = attributes.getNamedItem("depCode").getNodeValue();
            String job = attributes.getNamedItem("depJob").getNodeValue();
            String description = attributes.getNamedItem("description").getNodeValue();
            if (!departments.add(new Department(code, job, description))) {
                logger.error("The xml file " + xmlFile.getName() + " has duplicates!");
                throw new DuplicateException("The xml file " + xmlFile.getName() + " has duplicates!");
            }
        }
        return departments;
    }

}
