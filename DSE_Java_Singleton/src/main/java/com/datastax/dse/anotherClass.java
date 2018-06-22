package com.datastax.dse;

import com.datastax.driver.core.*;
import com.google.common.base.Function;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public class anotherClass {
    //Return a ResultSet of all keyspaces in the Cluster.
    public static ResultSet getKeyspaces(){
        Session bSession = Session.getInstance();
        ResultSet resultSet = bSession.session.execute("SELECT keyspace_name FROM system_schema.keyspaces");
        return resultSet;
    }

    //Return the Cluster name.
    public static String sessionSetting(){
        Session cSession = Session.getInstance();
        String clusterName = cSession.session.getCluster().getClusterName();
        return clusterName;
    }

    //Create an async query.
    private static ListenableFuture<ResultSet> resultSet = null;
    public static void asyncQuery(){
        Session dSession = Session.getInstance();
        ListenableFuture<com.datastax.driver.core.Session> sessionListen = dSession.session.initAsync();
        resultSet = Futures.transform(sessionListen,
                new AsyncFunction<com.datastax.driver.core.Session, ResultSet>() {
                    public ListenableFuture<ResultSet> apply(com.datastax.driver.core.Session sessionListen) throws Exception {
                        return sessionListen.executeAsync("SELECT release_version FROM system.local");
                    }
                });
    }
    public static ListenableFuture<String> listenForCallback() {
        ListenableFuture<String> version = Futures.transform(resultSet, new Function<ResultSet, String>() {
            public String apply(ResultSet rs) {
                return rs.one().getString("release_version");
            }
        });
        return version;
    }
}
