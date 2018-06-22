package com.datastax.dse;

import com.datastax.driver.core.*;
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
        //This list of nodes should only contain nodes from the local data center
        //This list shouldn't include nodes from other data centers as the driver will treat `LOCAL` as the data center of the first node that it connects to
        //This list should include more than one node so that the client app can connect when any given node is down
            String[] clusterNodes = new String[]{"127.0.0.1","127.0.0.2"};
            String localDC = "local_dc";
            cluster = DseCluster.builder()
                    .addContactPoints(clusterNodes)
                    .withLoadBalancingPolicy(
                            new TokenAwarePolicy(
                                    DCAwareRoundRobinPolicy
                                        .builder()
                                        .withLocalDc(localDC)
                                        .build()))
                    .withPoolingOptions(
                            new PoolingOptions()
                            .setCoreConnectionsPerHost(HostDistance.LOCAL,1 )
                            .setMaxConnectionsPerHost(HostDistance.LOCAL,10 )
                            .setMaxRequestsPerConnection(HostDistance.LOCAL,32768 )
                            //by setting these next three values to zero, you're saying never connect to non-local nodes
                            .setCoreConnectionsPerHost(HostDistance.REMOTE,0 )
                            .setMaxConnectionsPerHost(HostDistance.REMOTE,0 )
                            .setMaxRequestsPerConnection(HostDistance.REMOTE,0 ))
                    .withQueryOptions(
                            new QueryOptions()
                            .setConsistencyLevel(ConsistencyLevel.LOCAL_ONE))
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