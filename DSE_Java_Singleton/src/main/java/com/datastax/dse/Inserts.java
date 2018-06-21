package com.datastax.dse;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.dse.DseSession;

import java.util.ArrayList;
import java.util.List;

public class Inserts {
    private static Session iSession = Session.getInstance();
    private static List<String> firstNames = new ArrayList<String>();
    private static List<String> lastNames = new ArrayList<String>();

    public static void insertStatements(){
        firstNames.add(0,"Steve" );
        firstNames.add(1,"William" );
        firstNames.add(2,"James" );
        lastNames.add(0,"Jobs" );
        lastNames.add(1,"The Conqueror" );
        lastNames.add(2,"Brown" );
        for (int i = 0; i <= 2; i++) {
            iSession.session.execute(iSession.getPrepared().bind(i+1,firstNames.get(i),lastNames.get(i)));
        }
    }
}