package com.datastax.dse;

import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.SimpleStatement;
import com.datastax.driver.core.Statement;
import com.datastax.driver.dse.geometry.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Inserts {
    //Get an instance of the Session class.
    private static Session iSession = Session.getInstance();
    private static List<String> firstNames = new ArrayList<String>();
    private static List<String> lastNames = new ArrayList<String>();
    private static List<Point> pointList = new ArrayList<Point>();

    //Create a simple statement for an insert. Use this methodology only for infrequent inserts, otherwise use prepared statements
    private static Statement newInsert = new SimpleStatement(
            "INSERT INTO java.test_table (customer_id, first_name, last_name, coords) " +
                    "VALUES (?, ?, ?, ?);", 4, "Bill", "Clinton", new Point(38.2693, -75.7920));

    //Utilizing the prepared statement in the Session class, insert several records.
    public static void insertStatements(){
        firstNames.addAll(Arrays.asList("Steve","William","James"));
        lastNames.addAll(Arrays.asList("Jobs","The Conqueror","Brown"));
        pointList.addAll(Arrays.asList(
                new Point(37.4349, -122.1406),
                new Point(49.6570, -1.4162),
                new Point(33.4206, -81.8312)));
        for (int i = 0; i <= 2; i++) {
            iSession.session.execute(iSession.getPrepared()
                            .bind(i+1,firstNames.get(i),lastNames.get(i),pointList.get(i)));
        }
    }

    //Demonstrating the use of a SimpleStatement with an explicit consistency level.
    public static void insertWithConsistency(){
        newInsert.setConsistencyLevel(ConsistencyLevel.LOCAL_ONE);
        iSession.session.execute(newInsert);
    }

    //Demonstrating the use of a Prepared Statement with an explicit consistency level.
    public static void preparedInsertWithConsistency(){
        iSession.session.execute(iSession.getPrepared().setConsistencyLevel(ConsistencyLevel.LOCAL_ONE)
        .bind(5,"CL","Test",new Point(29.7537, -95.3604)));
    }
}