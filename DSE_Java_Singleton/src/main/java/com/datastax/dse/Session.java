package com.datastax.dse;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.dse.DseSession;
import com.datastax.driver.dse.DseCluster;

public class Session {
    private static Session thisInstance = null;
    private static PreparedStatement insertCustomer = null;

    public static DseSession session;
    private DseCluster cluster = null;

    private Session() {
            cluster = DseCluster.builder()
                    .addContactPoint("127.0.0.1")
                    .build();
            session = cluster.connect();
    }

    public static Session getInstance() {
        if (thisInstance == null)
            thisInstance = new Session();
        return thisInstance;
    }

    public static PreparedStatement getPrepared() {
        if (insertCustomer == null)
            insertCustomer = session.prepare("INSERT INTO java.test_table (customer_id, first_name, last_name) VALUES (?, ?, ?);");
        return insertCustomer;
    }
}