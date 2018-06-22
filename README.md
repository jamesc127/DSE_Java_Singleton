# DSE_Java_Singleton
Utilize this program with the provided links from the DataStax DSE Java Driver manual to get an initial understanding of creating a program and setting up a DSE session.
## Background
In this example, the DSE Session (the object by which you will connect to DSE) has been set up as a singleton class. In our example program, the `Session` class is used to ensure there is only one `DseSession` in our program. This keeps everything pertaining to the DSE session across our program syncronized. The DSE Java driver utilizes logic under the hood that keeps track of hosts, data centers, in-flight requests, async operations, pooling options, and much more. We want to ensure that the driver has the clearest picture available to it in order to get the best results.
[DSE Driver Documents Page](https://docs.datastax.com/en/developer/java-driver-dse/1.6/)
## Setup
In our example program, the `Session` class holds our cluster `ContactPoints`, or the IP addresses of the nodes in our local data center. In practice, this could also be read into the program from a configuration file. The cluster configuration is done in a `private` method:
```
private Session() {
            cluster = DseCluster.builder()
                    .addContactPoint("127.0.0.1")
                    .withLoadBalancingPolicy(new TokenAwarePolicy(DCAwareRoundRobinPolicy.builder().build()))
                    .withRetryPolicy(new MyRetryPolicy())
                    .build();
            session = cluster.connect();
    }
```
See the [DataStax Java driver quick start page](https://docs.datastax.com/en/developer/java-driver-dse/1.6/manual/) for more information and additional options.
Also refer to the following blog post about [asynchronous queries with the Java Driver](https://www.datastax.com/dev/blog/java-driver-async-queries)
## Intent
This sample program is intended to serve as a jumping off point for the DataStax Java Driver and to demonstrate some of the basic functionality that the driver has to offer. Please refer to the [DataStax Java driver manual](https://docs.datastax.com/en/developer/java-driver-dse/1.6/manual/) for additional and advanced features. 
