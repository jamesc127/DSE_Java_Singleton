package com.datastax.dse;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.dse.DseSession;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

public class App 
{
    public static void main( String[] args )
    {
        //Explicitly get the instance of DseSession from the Session class.
        DseSession aSession = Session.getInstance().session;

        //Execute a simple query and return a Row object.
        Row row = aSession.execute("SELECT release_version FROM system.local").one();
        //Print the results.
        System.out.println( "Hello World! I'm version " + row.getString("release_version"));

        //Using a helper method, return a ResultSet of all the keyspaces in my Cluster.
        ResultSet rs = anotherClass.getKeyspaces();
        System.out.println("These are the keyspaces in the system:");

        //for each Row in my ResultSet, print the keyspace name.
        for (Row rsRow : rs){
            System.out.println(rsRow.getString("keyspace_name"));
        }

        //Simple indication that the cluster name (and all other metadata) is consistent, as we are using the same Session in all classes.
        String clusterName = aSession.getCluster().getClusterName();
        System.out.println("The cluster name for aSession in App is " + clusterName + " and the cluster name for cSession " +
                "in anotherClass is " + anotherClass.sessionSetting());

        //Send an async query in another class, register for the callback here.
        anotherClass.asyncQuery();
        Futures.addCallback(anotherClass.listenForCallback(), new FutureCallback<String>() {
            public void onSuccess(String result) {
                System.out.println("The result of the async query is " + result);
            }
            public void onFailure(Throwable t) {
                System.out.println("The error with the async query is " + t.getMessage());
            }
        });

        //Create a sample keyspace and table, and insert values using helper methods in other classes
        boolean complete = false;
        boolean bKeyspace = aSession.execute("CREATE KEYSPACE IF NOT EXISTS java WITH REPLICATION = " +
                "{'class' : 'SimpleStrategy', 'replication_factor' : '1'};").wasApplied();
        boolean bTable = aSession.execute("CREATE TABLE IF NOT EXISTS java.test_table " +
                "( customer_id int PRIMARY KEY, first_name text, last_name text, coords 'PointType' );").wasApplied();
        boolean bIndex = aSession.execute("CREATE SEARCH INDEX IF NOT EXISTS ON java.test_table " +
                "WITH COLUMNS first_name {indexed:true}, coords {indexed:false} AND OPTIONS {lenient:true};").wasApplied();
        if (bKeyspace && bTable && bIndex) complete = true;
        if (complete) Inserts.insertStatements();
        if (complete) Inserts.insertWithConsistency();
        if (complete) Inserts.preparedInsertWithConsistency();
        if (complete) anotherClass.solrQuery();

        aSession.close();
        System.exit(0);
    }
}