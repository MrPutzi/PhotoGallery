package sk.kasv.fekete.opg.PhotoGallery.Database;

import com.mongodb.client.*;
import org.bson.Document;
import sk.kasv.fekete.opg.PhotoGallery.Util.Role;

import java.util.Date;
import java.util.Iterator;

import static com.mongodb.client.model.Filters.eq;

public class ImagesDatabase {

    public static Role checkUser(String username) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("ImagesDB");
        MongoCollection<Document> collection = database.getCollection("User");
        Document user = new Document().append("username", username);
        FindIterable<Document> iterable = collection.find(user);
        Iterator<Document> iterator = iterable.iterator();
        if (iterator.hasNext()) {
            Document document = (Document) iterator.next();
            String passwordFromDatabase = document.getString("password");
            String role = document.getString("role");
            if (role.equals("ADMIN")) {
                return Role.ADMIN;
            } else if (role.equals("USER")) {
                return Role.USER;
            }
        }
        return null;
    }

    public void insertLogWithToken(Date date, String username) {
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("ImagesDB");
            MongoCollection<Document> collection = database.getCollection("Log");

            // Create the document to be inserted
            Document document = new Document()
                    .append("date", date)
                    .append("username", username);

            // Insert the document into the collection
            collection.insertOne(document);
        } catch (Exception e) {
            // Handle exceptions appropriately
        }
    }

    public void deleteTokenFromLog() {
      /*  MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("Lectures");
        MongoCollection<Document> collection = database.getCollection("Log");
        collection.deleteMany(new Document());
       */
    }

    public void deleteUser(String userToDelete) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("ImagesDB");
        MongoCollection<Document> collection = database.getCollection("User");
        collection.deleteOne(eq("username", userToDelete));
    }

    public void changePassword(String username, String password, String newPassword) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("ImagesDB");
        MongoCollection<Document> collection = database.getCollection("User");
        Document user = new Document().append("username", username).append("password", password);
        FindIterable<Document> iterable = collection.find(user);
        Iterator<Document> iterator = iterable.iterator();
        if (iterator.hasNext()) {
            Document document = (Document) iterator.next();
            document.put("password", newPassword);
            collection.replaceOne(eq("username", username), document);
        }
    }



}
