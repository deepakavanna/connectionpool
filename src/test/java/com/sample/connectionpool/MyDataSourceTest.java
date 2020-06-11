package com.sample.connectionpool;

import junit.framework.TestCase;

import javax.sql.PooledConnection;
import java.sql.Connection;
import java.util.NoSuchElementException;

public class MyDataSourceTest extends TestCase {
    public void testGetConnection() throws Exception {
        MyDriverManager myDriverManager = new MyDriverManager();

        MyDataSource myDataSource = new MyDataSource(myDriverManager);

        Connection connection = myDataSource.getConnection();

        assertNotNull(connection);
        assertEquals(1,myDataSource.getActiveConnections());
        assertNotNull(MyConnectionPoolManager.get(connection));

        connection.close();
        assertEquals(0,myDataSource.getActiveConnections());
        try {
            MyConnectionPoolManager.get(connection);

            fail();
        } catch (Exception ex) {
            assertTrue(ex instanceof NoSuchElementException);
        }


    }

}