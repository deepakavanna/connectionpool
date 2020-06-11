package com.sample.connectionpool;

import javax.sql.PooledConnection;


/*
       This class simulates DriverManager and only required method is
       implemented here. As DriverManager was not required for the exercise,
       I am not implementing the DriverManager interface.
 */
public class MyDriverManager  {

    public PooledConnection getConnection() {
        MyConnection myConnection = new MyConnection();
        PooledConnection connection = new MyPooledConnection(myConnection);
        return connection;
    }


}
