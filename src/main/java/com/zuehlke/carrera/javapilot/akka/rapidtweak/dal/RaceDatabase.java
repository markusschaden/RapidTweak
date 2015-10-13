package com.zuehlke.carrera.javapilot.akka.rapidtweak.dal;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.android.Serializator;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.track.Race;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by Markus on 13.10.2015.
 */
public class RaceDatabase {

    private final Logger LOGGER = LoggerFactory.getLogger(RaceDatabase.class);
    private MongoDatabase db = null;
    private Serializator<Race> serializator = new Serializator<>();

    public RaceDatabase() {
        try {
            MongoClient mongoClient = new MongoClient();
            db = mongoClient.getDatabase("raceDB");
        } catch (Exception e) {
            LOGGER.error("Can't connect to mongo db");
            e.printStackTrace();
        }
    }

    public void insertRace(Race race) {
        if(db != null) {
            LOGGER.info("Inserting Race " + race.getTrackId());
            MongoCollection<Document> coll = db.getCollection("races");
            String json = serializator.serialize(race);

            coll.insertOne(Document.parse(json));
        }
    }


}
