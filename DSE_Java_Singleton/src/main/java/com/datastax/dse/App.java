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
        DseSession aSession = Session.getInstance().session;
        Row row = aSession.execute("SELECT release_version FROM system.local").one();
        System.out.println( "Hello World! I'm version " + row.getString("release_version"));

        ResultSet rs = anotherClass.getKeyspaces();
        System.out.println("These are the keyspaces in the system:");
        for (Row rsRow : rs){
            System.out.println(rsRow.getString("keyspace_name"));
        }

        String clusterName = aSession.getCluster().getClusterName();
        System.out.println("The cluster name for aSession in App is " + clusterName + " and the cluster name for cSession " +
                "in anotherClass is " + anotherClass.sessionSetting());

        anotherClass.asyncQuery();
        Futures.addCallback(anotherClass.listenForCallback(), new FutureCallback<String>() {
            public void onSuccess(String result) {
                System.out.println("The result of the async query is " + result);
            }

            public void onFailure(Throwable t) {
                System.out.println("The error with the async query is " + t.getMessage());
            }
        });

//        boolean complete = Inserts.createKeyspaceTable();
        boolean complete;
        boolean bKeyspace = aSession.execute("CREATE KEYSPACE IF NOT EXISTS java WITH REPLICATION = {'class' : 'SimpleStrategy', 'replication_factor' : '1'};").wasApplied();
        boolean bTable = aSession.execute("CREATE TABLE IF NOT EXISTS java.test_table ( customer_id int PRIMARY KEY, first_name text, last_name text );").wasApplied();
        if (bKeyspace && bTable) complete = true;
        else complete = false;
        if (complete) Inserts.insertStatements();
        System.exit(0);
    }
}