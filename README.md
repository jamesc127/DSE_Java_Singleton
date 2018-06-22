# DSE_Java_Singleton
Utilize this program with the provided links from the DataStax DSE Java Driver manual to get an initial understanding of creating a program and setting up a DSE session.

## Background
In this example, the DSE Session (the object by which you will connect to DSE) has been set up as a singleton class. In our example program, the `Session` class is used to ensure there is only one `DseSession` in our program. 
**This is an imperitive step when utilizing any Spring framework for development**
This keeps everything pertaining to the DSE session across our program syncronized. The DSE Java driver utilizes logic under the hood that keeps track of hosts, data centers, in-flight requests, async operations, pooling options, and much more. We want to ensure that the driver has the clearest picture available to it in order to get the best results.
[DSE Driver Documents Page](https://docs.datastax.com/en/developer/java-driver-dse/1.6/)

## Setup
See the [DataStax Java driver quick start page](https://docs.datastax.com/en/developer/java-driver-dse/1.6/manual/) for information on setting up project dependencies.

### DSE Cluster Builder
Many of the settings used by the CQL driver code is configured in the connection to the cluster. In our example program, the `Session` class holds our cluster `ContactPoints`, or the IP addresses of the nodes in our local data center. In practice, this could also be read into the program from a configuration file. Here is some example code:
```
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
```

### Load balancing
The default load balancing policy is TokenAwarePolicy wrapping DCAwareRoundRobinPolicy.  You can configure the DC name used in the DCAwareRoundRobinPolicy which basically applies a filter to the nodes from which the driver chooses to load balance between.

See: https://docs.datastax.com/en/developer/java-driver-dse/1.6/manual/load_balancing/

### Connection pooling
Connection pooling settings can be configured for local nodes vs remote nodes.  The CQL driver creates connections to each node in the cluster.  It creates a relatively small number of connections and then multiplexes many queries via these channels.  You can control the number of connections it creates initially ("CoreConnections"), the max number of connections it will create ("MaxConnections"), and the number of queries it will push across these connections at a given time ("RequestsPerConnection").

You can also determine whether your client code will talk to non-local data centers (in the case when you have a multi-datacenter cluster).  You will want to decide whether client applications should automatically talk to non-local nodes when local nodes are not accessible.

See: https://docs.datastax.com/en/developer/java-driver-dse/1.6/manual/pooling/

### Contact points
When setting contact points, there are a few principles to follow:
* Supply only nodes from the local DC.  Don't mix a list of nodes from multiple data centers.
* Supply at least two nodes.  If you only supply one node and that node happens to be down when your client code starts, it will not be able to connect to the cluster.
* As long as the client code is able to connect to one node in the cluster, it will gossip with the cluster and discover all the other nodes.
* There is no advantage or disadvantage to specifying "seed nodes" as the contact points.

### Consistency Level
DataStax recommends using consistency levels of LOCAL_ONE or LOCAL_QUORUM, as opposed to ONE or QUORUM.  This is true even if the code is running against a single-DC cluster.  The reason for this is that if you later expand your cluster to be a multi-DC cluster, your code will already be in good shape to support this migration.
You can specify a default consistency level for the cluster, and you can also specify consistency levels on a statement-by-statement basis.
It is beneficial to know that Solr queries require a consistency level of `ONE` or `LOCAL_ONE`.
Also refer to the following blog post about [asynchronous queries with the Java Driver](https://www.datastax.com/dev/blog/java-driver-async-queries)

## Statements & Queries
### Simple Statements
Use SimpleStatement for queries that will be executed only once (or a few times) in your application. The `Insert` class in this demo program uses a SimpleStatement for an insert. See the below code, which also examplifies an explicitly stated `ConsistencyLevel` and [see this page](https://docs.datastax.com/en/developer/java-driver-dse/1.6/manual/statements/simple/) for more information.
```
//Create a simple statement for an insert. Use this methodology only for infrequent inserts, otherwise use prepared statements
private static Statement newInsert = new SimpleStatement(
    "INSERT INTO java.test_table (customer_id, first_name, last_name, coords) " +
            "VALUES (?, ?, ?, ?);", 4, "Bill", "Clinton", new Point(38.2693, -75.7920));
            
//Demonstrating the use of a SimpleStatement with an explicit consistency level.
public static void insertWithConsistency(){
newInsert.setConsistencyLevel(ConsistencyLevel.LOCAL_ONE);
iSession.session.execute(newInsert);
}
```   

## Intent
This sample program is intended to serve as a jumping off point for the DataStax Java Driver and to demonstrate some of the basic functionality that the driver has to offer. Please refer to the [DataStax Java driver manual](https://docs.datastax.com/en/developer/java-driver-dse/1.6/manual/) for additional and advanced features. 
