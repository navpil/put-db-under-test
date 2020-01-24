package io.github.navpil.dbtests;

import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.assertion.comparer.value.ValueComparer;
import org.dbunit.assertion.comparer.value.ValueComparerTemplateBase;
import org.dbunit.assertion.comparer.value.ValueComparers;
import org.dbunit.assertion.comparer.value.builder.ColumnValueComparerMapBuilder;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

@Ignore
public class ImportExportDataWithDbUnit {

    // http://jvalentino.blogspot.com/2011/08/using-dbunit-through-ant-with-large-ms.html
    // https://nofluffjuststuff.com/blog/paul_duvall/2006/07/dbunit_with_junit_4
    // https://stackoverflow.com/questions/26118271/dbunit-fails-to-clean-insert-foreign-key-constraint

    @Test
    public void testExportingData() throws SQLException, DatabaseUnitException, IOException {
        IDatabaseConnection connection = getConnection();

        // full database export
            IDataSet fullDataSet =  new FilteredDataSet(new DatabaseSequenceFilter(connection), connection.createDataSet());
        FlatXmlDataSet.write(fullDataSet, new FileOutputStream("myfile.xml"));
    }

    @Test
    public void cleanDb() throws DatabaseUnitException, SQLException {
        final IDatabaseConnection connection = getConnection();
        try{
            DatabaseOperation.DELETE_ALL.execute(connection, new FilteredDataSet(new DatabaseSequenceFilter(connection), connection.createDataSet()));
        }finally{
            connection.close();
        }
    }

    @Test
    public void setupData() throws Exception {
        handleSetUpOperation("full_carrental_main.xml");
    }

    @Test
    public void setupData3Cars() throws Exception {
        handleSetUpOperation("full_carrental_3_cars.xml");
    }

    @Test
    public void setupFasterData() throws Exception {
        handleSetUpOperation("full_carrental_faster.xml");
    }

    @Test
    public void testAssertions() throws DatabaseUnitException, SQLException, IOException {
        IDatabaseConnection connection = getConnection();
        IDataSet fullDataSet =  new FilteredDataSet(new DatabaseSequenceFilter(connection), connection.createDataSet());


        final IDataSet expectedDataSet = getDataSet("full_carrental_main.xml");

//        Assertion.assertEquals(expectedDataSet, fullDataSet);
        Assertion.assertEquals(expectedDataSet.getTable("car"), fullDataSet.getTable("car"));

    }
    @Test
    public void testValidate() throws DatabaseUnitException, SQLException, IOException {
        IDatabaseConnection connection = getConnection();
        IDataSet fullDataSet =  new FilteredDataSet(new DatabaseSequenceFilter(connection), connection.createDataSet());


        final IDataSet expectedDataSet = getDataSet("full_carrental_main.xml");

        final ITable expectedCars = expectedDataSet.getTable("car");
        final ITable actualCars = fullDataSet.getTable("car");

        ITable expectedTable = expectedCars;
        ITable actualTable = actualCars;
        ValueComparer defaultValueComparer = ValueComparers.isActualEqualToExpected;
        Map<String, ValueComparer> columnValueComparers =
                new ColumnValueComparerMapBuilder()
                        .add("max_speed", new IntApproximateComparer(5  ))
                        .build();

        Assertion.assertWithValueComparer(expectedTable, actualTable, defaultValueComparer, columnValueComparers);
    }

    private static IDatabaseConnection getConnection() throws DatabaseUnitException, SQLException {
        if (SQLConfig.getDefault() == SQLConfig.HSQLDB) {
            return getHsqlConnection();
        } else {
            return getSqlServerConnection();
        }
    }

    private static IDatabaseConnection getHsqlConnection() throws SQLException, DatabaseUnitException {
        final SQLConfig c = SQLConfig.HSQLDB;
        Connection jdbcConnection = DriverManager.getConnection(
                c.getUrl("file:D:/temp/carrental"), c.username, c.password);
        //need to specify schema, otherwise it will fail
        final DatabaseConnection connection = new DatabaseConnection(jdbcConnection, "public");
        connection.getConfig().setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS , "true");

        return connection;
    }

    private static IDatabaseConnection getSqlServerConnection() throws SQLException, DatabaseUnitException {
        Connection jdbcConnection = DriverManager.getConnection(
                "jdbc:sqlserver://localhost;databaseName=carrental;integratedSecurity=true;", "", "");
        //need to specify schema, otherwise it will fail
        final DatabaseConnection connection = new DatabaseConnection(jdbcConnection, "dbo");
        //need to specify escape pattern, otherwise it will fail
        connection.getConfig().setProperty(DatabaseConfig.PROPERTY_ESCAPE_PATTERN , "[?]");
        connection.getConfig().setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS , "true");

        return connection;
    }


    private static void handleSetUpOperation(String pathname) throws Exception{
        final IDatabaseConnection conn = getConnection();
        final IDataSet data = getDataSet(pathname);
        try{
            DatabaseOperation.CLEAN_INSERT.execute(conn, data);
        }finally{
            conn.close();
        }
    }

    private static IDataSet getDataSet(String pathname) throws IOException,
            DataSetException {
        return new FlatXmlDataSet(new File(pathname));
    }

    public static class IntApproximateComparer extends ValueComparerTemplateBase {

        private final int maxDeviation;

        public IntApproximateComparer(int maxDeviation) {
            this.maxDeviation = maxDeviation;
        }

        protected boolean isExpected(ITable iTable, ITable iTable1, int i, String s, DataType dataType, Object o, Object o1) throws DatabaseUnitException {
            return Math.abs(toInt(o) - toInt(o1)) <= maxDeviation;
        }

        protected String getFailPhrase() {
            return "very different to";
        }

        private Integer toInt(Object o) {
            if (o instanceof String) {
                return Integer.parseInt((String)o);
            }
            return (Integer)o;
        }
    }

}
