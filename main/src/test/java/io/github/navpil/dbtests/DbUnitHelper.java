package io.github.navpil.dbtests;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class DbUnitHelper {

    public static void handleSetUpOperation(String pathname, Connection connection) throws DatabaseUnitException, IOException, SQLException {
        final IDatabaseConnection conn = getConnection(connection);
        final IDataSet data = getDataSet(pathname);
        try{
            DatabaseOperation.CLEAN_INSERT.execute(conn, data);
        }finally{
            conn.close();
        }
    }

    private static IDatabaseConnection getConnection(Connection jdbcConnection) throws DatabaseUnitException {
        //need to specify schema, otherwise it will fail
        final DatabaseConnection connection = new DatabaseConnection(jdbcConnection, "dbo");
        //need to specify escape pattern, otherwise it will fail
        connection.getConfig().setProperty(DatabaseConfig.PROPERTY_ESCAPE_PATTERN , "[?]");
        connection.getConfig().setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS , "true");

        return connection;
    }

    private static IDataSet getDataSet(String pathname) throws IOException,
            DataSetException {
        return new FlatXmlDataSet(new File(pathname));
    }




}
