package com.sample.connectionpool;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.DataSource;
import javax.sql.PooledConnection;
import javax.sql.StatementEventListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MyDataSource implements DataSource, ConnectionEventListener {

    private static final int MAX_CONNECTIONS = 10;
    private static final int TIMEOUT_SECS = 30;
    private int activeConnections = 0;
    private List<PooledConnection> freeConnectionList = new ArrayList<>();
    private List<PooledConnection> usedConnectionList = new ArrayList<>();

    private MyDriverManager driverManager = null;


    public MyDataSource(MyDriverManager driverManager) {
        this.driverManager = driverManager;

        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            freeConnectionList.add(driverManager.getConnection());
        }
    }

    public Connection getConnection() throws SQLException {
        long max = System.currentTimeMillis() + TIMEOUT_SECS * 1000;
        do {
            synchronized (this) {
                if (activeConnections < MAX_CONNECTIONS) {
                    PooledConnection pooledConnection = freeConnectionList.get(MAX_CONNECTIONS - 1 - activeConnections);
                    freeConnectionList.remove(MAX_CONNECTIONS - 1 - activeConnections);
                    usedConnectionList.add(pooledConnection);
                    activeConnections++;
                    // Adding the ConnectionEventListener
                    pooledConnection.addConnectionEventListener(this);
                    MyConnection myConnection = (MyConnection) pooledConnection.getConnection();
                    MyConnectionPoolManager.put(myConnection, pooledConnection);

                    return myConnection;
                }
                try {
                    wait(TIMEOUT_SECS * 1000);
                } catch (InterruptedException e) {
                    // if thread is interrupted, ignore it as we go back in while
                    // loop and check for connection availability.
                }
            }
        } while (System.currentTimeMillis() <= max);
        throw new SQLException("Connection timeout");
    }

    public synchronized long getActiveConnections() {
        return activeConnections;
    }


    public void connectionClosed(ConnectionEvent event) {
        synchronized (this) {
            PooledConnection pooledConnection = (PooledConnection) event.getSource();
            freeConnectionList.add(pooledConnection);
            usedConnectionList.remove(pooledConnection);
            activeConnections--;
        }

    }

    /*
        Following methods are not used
     */
    public Connection getConnection(String username, String password)
            throws SQLException {
        throw new UnsupportedOperationException();
    }


    public <T> T unwrap(java.lang.Class<T> iface) throws java.sql.SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isWrapperFor(java.lang.Class<?> iface) throws java.sql.SQLException {
        throw new UnsupportedOperationException();
    }

    public void removeStatementEventListener(StatementEventListener listener) {
        throw new UnsupportedOperationException();
    }


    public void connectionErrorOccurred(ConnectionEvent event) {
        throw new UnsupportedOperationException();
    }

    public java.io.PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void setLogWriter(java.io.PrintWriter out) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void setLoginTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException();
    }
}
