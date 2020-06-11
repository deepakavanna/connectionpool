package com.sample.connectionpool;

import javax.sql.PooledConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class MyConnectionPoolManager {

    private static volatile Map<Connection, PooledConnection> map = new HashMap<>();


    public static void put(Connection connection, PooledConnection pooledConnection) {
        synchronized(MyConnectionPoolManager.class) {
            map.put(connection, pooledConnection);
        }
    }

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

    public static void close(Connection connection) throws SQLException {
        PooledConnection pc = get(connection);
        pc.close();
        remove(connection);
    }
}
