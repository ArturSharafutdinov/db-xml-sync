package ru.sharafutdinov.artur.dbXmlSynchronizer.common;


import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Class using for opening and closing connection to database
 */
public class ConnectionSource {

    private static Connection connection = null;

    private static ConnectionSource instance;

    private static final Logger logger = Logger.getLogger(ConnectionSource.class);

    private ConnectionSource() {
    }

    public static ConnectionSource getInstance() {
        if (instance == null) {
            instance = new ConnectionSource();
        }
        return instance;
    }

    public Connection openConnection() {
        try {
            InputStream input = ConnectionSource.class.getClassLoader().getResourceAsStream("application.properties");
            Properties properties = new Properties();
            properties.load(input);

            Class.forName(properties.getProperty("db.driverClassName"));

            connection = DriverManager
                    .getConnection(
                            properties.getProperty("db.url"),
                            properties.getProperty("db.username"),
                            properties.getProperty("db.password"));
        } catch (SQLException | IOException | ClassNotFoundException exception) {
            logger.error("Could not establish a connection to the database" + exception);
        }

        return connection;
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException exception) {
            logger.error("Could not close a connection to the database" + exception);
        }
    }

}