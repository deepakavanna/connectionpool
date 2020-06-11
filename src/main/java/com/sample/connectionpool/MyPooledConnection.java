package com.sample.connectionpool;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;
import javax.sql.StatementEventListener;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;


public class MyPooledConnection implements PooledConnection {

    List<ConnectionEventListener> list = new ArrayList<>();
    MyConnection myConnection = null;

    public List<ConnectionEventListener> getList() {
        return list;
    }

    public void setList(List<ConnectionEventListener> list) {
        this.list = list;
    }

    public MyConnection getMyConnection() {
        return myConnection;
    }

    public void setMyConnection(MyConnection myConnection) {
        this.myConnection = myConnection;
    }

// Prefer to use Lombok libraries.

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (! (o instanceof MyPooledConnection)) {
            return false;
        }

        MyPooledConnection myPooledConnection = (MyPooledConnection) o;

        if (this.myConnection != null && !this.myConnection.equals(o)) {
            return false;
        }

        if (this.list != null && !this.list.equals(myPooledConnection.getList())) {
            return false;
        }

        if (myPooledConnection.getList() != null && this.list == null) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result = 17;
        result = (result * 31) + (myConnection != null ? 0 : myConnection.hashCode());
        result = (result * 31) + (list != null ? 0 : list.hashCode());
        return result;
    }

    public MyPooledConnection(MyConnection myConnection) {
        this.myConnection =  myConnection;
    }

    public Connection getConnection() throws SQLException {
        return myConnection.getConnection();
    }

    // release the connection back to DataSource
    public void close() throws SQLException {
        for (ConnectionEventListener connectionEventListener : list) {
            ConnectionEvent event = new ConnectionEvent(this);
            connectionEventListener.connectionClosed(event);
        }
    }

    // add the connectionEventListener so that it can be called when we close the connection
    public void addConnectionEventListener(ConnectionEventListener listener) {
        list.add(listener);
    }

    public void addStatementEventListener(StatementEventListener listener) {
        throw new UnsupportedOperationException();
    }

    public void removeConnectionEventListener(ConnectionEventListener listener) {
        throw new UnsupportedOperationException();
    }


    public void removeStatementEventListener(StatementEventListener listener){
        throw new UnsupportedOperationException();
    }



}
