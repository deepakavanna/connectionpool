package com.sample.connectionpool;

import javax.sql.PooledConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Uses a Map to maintain mapping between connection object and pooledConnection object.
 */
public class MyConnectionPoolManager {


    private static volatile Map<Connection, PooledConnection> map = new HashMap<>();

    /**
     * Called from the DataSource
     * @param connection
     * @param pooledConnection
     */
    public static void put(Connection connection, PooledConnection pooledConnection) {
        synchronized(MyConnectionPoolManager.class) {
            map.put(connection, pooledConnection);
        }
    }

    /**
     *
     * @param connection
     * @return
     * @throws NoSuchElementException
     */
    public static PooledConnection get(Connection connection) throws NoSuchElementException {
        PooledConnection pooledConnection = null;
        synchronized(MyConnectionPoolManager.class) {
            if (map.containsKey(connection)) {
                pooledConnection = map.get(connection);
            }
            else {
                throw new NoSuchElementException();
            }

        }
        return pooledConnection;
    }

    /**
     *
     * @param connection
     */
    public static void remove(Connection connection) {

        synchronized(MyConnectionPoolManager.class) {
            if (map.containsKey(connection)) {
                map.remove(connection);
            }
            else {
                throw new NoSuchElementException();
            }
        }
    }

    // Called from the connection object to release the connection back to the connection pool
    public static void close(Connection connection) throws SQLException {
        PooledConnection pc = get(connection);
        pc.close();
        remove(connection);
    }
}
