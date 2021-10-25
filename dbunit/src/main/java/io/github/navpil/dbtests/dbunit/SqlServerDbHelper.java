package io.github.navpil.dbtests.dbunit;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.database.search.TablesDependencyHelper;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.ext.mssql.MsSqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqlServerDbHelper implements AutoCloseable {

    private IDatabaseConnection connection;

    public SqlServerDbHelper(Connection connection, String schema) throws DatabaseUnitException {
        this.connection = getDbUnitConnection(connection, schema);
    }

    /**
     * Creates connections used by DBUnit for a given DB name
     * @return connection
     * @throws DatabaseUnitException when something goes wrong
     * @param jdbcConnection
     * @param schema
     */
    private static IDatabaseConnection getDbUnitConnection(Connection jdbcConnection, String schema) throws DatabaseUnitException {
        //need to specify schema, otherwise it will fail
        IDatabaseConnection connection = new DatabaseConnection(jdbcConnection, schema);
        //need to specify escape pattern, otherwise it will fail
        connection.getConfig().setProperty(DatabaseConfig.PROPERTY_ESCAPE_PATTERN , "[?]");
        connection.getConfig().setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS , "true");
        connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MsSqlDataTypeFactory());

        return connection;
    }

    /**
     * Sets up the DB with given data. Searches for a file in resources/dbdumps
     *
     * @param dataName name of a file
     * @throws DatabaseUnitException when something goes wrong
     * @throws SQLException when something goes wrong
     */
    public void setUp(String dataName) throws DatabaseUnitException, SQLException, FileNotFoundException {
        DatabaseOperation.DELETE_ALL.execute(connection, connection.createDataSet());

        final IDataSet data = getDataSet(dataName);
        DatabaseOperation.CLEAN_INSERT.execute(connection, data);
    }

    /**
     *
     * Sets up the DB with given data. Searches for a file in resources/dbdumps
     *
     * @param dataName file to read data from
     *
     * @throws DatabaseUnitException when something goes wrong
     * @throws SQLException when something goes wrong
     * @throws FileNotFoundException when something goes wrong
     */
    public void setUp(File dataName) throws DatabaseUnitException, SQLException, FileNotFoundException {
        setUp(dataName, true);
    }

    public void setUp(File dataName, boolean cleanAll) throws DatabaseUnitException, SQLException, FileNotFoundException {
        final FlatXmlProducer flatXmlProducer = new FlatXmlProducer(new InputSource(new FileReader(dataName)));
        flatXmlProducer.setColumnSensing(true);
        final IDataSet data = new FlatXmlDataSet(flatXmlProducer);
        final DatabaseOperation dbOperation = cleanAll ? DatabaseOperation.CLEAN_INSERT : DatabaseOperation.INSERT;
        dbOperation.execute(connection, data);
    }

    private static IDataSet getDataSet(String pathname) throws
            DataSetException, FileNotFoundException {
        final InputStream resource = new FileInputStream(pathname);
        final FlatXmlProducer flatXmlProducer = new FlatXmlProducer(new InputSource(resource));
        flatXmlProducer.setColumnSensing(true);
        return new FlatXmlDataSet(flatXmlProducer);
    }

    private static IDataSet msSqlTransform(IDataSet dataset) throws DataSetException {
        List<ITable> updatedTables = new ArrayList<>();
        final String[] tableNames = dataset.getTableNames();
        for (String tableName : tableNames) {
            ITable table = dataset.getTable(tableName);
            List<Column> excludedColumns = new ArrayList<Column>();
            Column[] columns = table.getTableMetaData().getColumns();
            for (Column column : columns) {
                //TIMESTAMP columns have to be ignored in MSSQL because they cannot have an explicit insert
                //Unfortunately DATETIME in MSSQL also shows here as a TIMESTAMP so the only way to
                //determine defaulted DATETIME or TIMESTAMP is by looking for defaults and the SQL Type name
                if ( (column.getDefaultValue() != null && column.getDataType() == DataType.TIMESTAMP) ||
                        column.getSqlTypeName().equals("timestamp")) {
                    excludedColumns.add(column);
                }
            }
            Column[] excluded =  excludedColumns.toArray(new Column[0]);
            //create a new ITable that excludes the filtered columns
            ITable filteredTable = DefaultColumnFilter.excludedColumnsTable(table,excluded);
            updatedTables.add(filteredTable);
        }
        //Take all of the modified table and create a new dataset
        ITable[] updateTableArray =  updatedTables.toArray(new ITable[0]);
        return new CompositeDataSet(updateTableArray);
    }

    /**
     * Exports data from a db
     *
     * @param fileName name of a file to write into
     * @param tables tables to read
     * @throws DatabaseUnitException  when something goes wrong
     * @throws IOException when something goes wrong
     * @throws SQLException when something goes wrong
     */
    public void export(String fileName, String... tables) throws DatabaseUnitException, IOException, SQLException {
        IDataSet dataSet;
        if (tables == null || tables.length == 0) {
            //full database export
            dataSet = connection.createDataSet();
        } else {
            // partial database export
            QueryDataSet partialDataSet = new QueryDataSet(connection);
            for (String table : tables) {
                partialDataSet.addTable(table);
            }
            dataSet = partialDataSet;
        }

        final FilteredDataSet filteredDataSet = new FilteredDataSet(new DatabaseSequenceFilter(connection), dataSet);
        FlatXmlDataSet.write(msSqlTransform(filteredDataSet), new FileOutputStream(fileName));
    }

    public void exportDependents(String fileName, String tableName) throws DatabaseUnitException, SQLException, IOException {
        String[] depTableNames =
                TablesDependencyHelper.getAllDependentTables(connection, tableName);
        IDataSet depDataSet = connection.createDataSet(depTableNames);

        FlatXmlDataSet.write(depDataSet, new FileOutputStream(fileName));
    }

    public void cleanDb() throws DatabaseUnitException, SQLException {
        DatabaseOperation.DELETE_ALL.execute(connection, new FilteredDataSet(new DatabaseSequenceFilter(connection), connection.createDataSet()));
    }

    @Override
    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
