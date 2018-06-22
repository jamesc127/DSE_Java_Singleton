package com.datastax.dse;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import com.datastax.driver.dse.DseSession;
import com.datastax.driver.dse.DseCluster;

//Create a Singleton Class to ensure there is only one Session object for the connection to DSE.
public class Session {
    private static Session thisInstance = null;
    private static PreparedStatement insertCustomer = null;

    public static DseSession session;
    private DseCluster cluster = null;

    private Session() {
            cluster = DseCluster.builder()
                    .addContactPoint("127.0.0.1")
                    //Explicitly declare a load balancing policy. Using the default here as an example.
                    .withLoadBalancingPolicy(new TokenAwarePolicy(DCAwareRoundRobinPolicy.builder().build()))
                    //.withRetryPolicy(MyRetryPolicy()) //Create a retry policy here if desired
                    .build();
            session = cluster.connect();
    }

    //Return a new session if it has not been instantiated yet, otherwise return the current Session.
    public static Session getInstance() {
        if (thisInstance == null)
            thisInstance = new Session();
        return thisInstance;
    }

    //Using the Singleton methodology to ensure statement is prepared only once.
    public static PreparedStatement getPrepared() {
        if (insertCustomer == null)
            insertCustomer = session.prepare("INSERT INTO java.test_table " +
                    "(customer_id, first_name, last_name, coords) VALUES (?, ?, ?, ?);");
        return insertCustomer;
    }
}