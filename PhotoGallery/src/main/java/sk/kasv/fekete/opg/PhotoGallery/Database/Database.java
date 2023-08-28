package sk.kasv.fekete.opg.PhotoGallery.Database;

import com.mongodb.client.*;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSDownloadOptions;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.io.BasicOutputBuffer;
import org.bson.types.ObjectId;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.multipart.MultipartFile;
import sk.kasv.fekete.opg.PhotoGallery.Util.Role;
import sk.kasv.fekete.opg.PhotoGallery.Util.Token;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;

@Component
public class Database {



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

    public static MongoDatabase DbConnection(String collectionName) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("ImagesDB");
        MongoCollection<Document> collection = database.getCollection(collectionName);


        return database;
    }

    public boolean uploadImage(String username, MultipartFile file) {
        GridFSBucket gridFSBucket = GridFSBuckets.create(Database.DbConnection("ImagesDB.Image"));

        try (InputStream inputStream = file.getInputStream()) {
            //  String fileName = file.getOriginalFilename();
            String fileName = username + "_" + file.getOriginalFilename();

            ObjectId fileId = gridFSBucket.uploadFromStream(fileName, inputStream);

            // You might want to store the fileId or other relevant information in your database.
            // Return true or false based on the success of the upload.
            return fileId != null;
        } catch (IOException e) {
            // Handle exceptions appropriately.
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteImage(String username, String imageName) {
        GridFSBucket gridFSBucket = GridFSBuckets.create(Database.DbConnection("ImagesDB.Image"));
        gridFSBucket.delete((BsonValue) eq("filename", imageName));
        return true;
    }

    public ResponseEntity<byte[]> getAllImages() throws IOException {
        List<String> imageNames = Arrays.asList(new String[]{"1.jpg","2.jpg"});
        List<String> contentTypes = Arrays.asList(new String[]{MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_JPEG_VALUE});
        List<byte[]> imagesData = getImagesForUser("username");  // Replace "username" with the actual username
        byte[] htmlData = generateHtmlResponse(imageNames, contentTypes, imagesData);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        return new ResponseEntity<>(htmlData, headers, HttpStatus.OK);
    }

    private byte[] generateHtmlResponse(List<String> imageNames, List<String> contentTypes, List<byte[]> imagesData) {
        StringBuilder htmlContent = new StringBuilder("<!DOCTYPE html><html><head><title>Images</title></head><body>");

        for (int i = 0; i < imageNames.size(); i++) {
            String imageName = imageNames.get(i);
            String contentType = contentTypes.get(i);
            byte[] imageData = imagesData.get(i);

            String base64ImageData = Base64.getEncoder().encodeToString(imageData);
            htmlContent.append("<br/><br/><b>").append(imageName).append("</b><br/><br/><img src='data:")
                    .append(contentType).append(";base64, ").append(base64ImageData).append("'/>");
        }

        htmlContent.append("</body></html>");
        return htmlContent.toString().getBytes(StandardCharsets.UTF_8);
    }

    public List<byte[]> getImagesForUser(String username) {
        GridFSBucket gridFSBucket = GridFSBuckets.create(Database.DbConnection("ImagesDB.Image"));
        List<byte[]> images = new ArrayList<>();
        try (MongoCursor<GridFSFile> cursor = gridFSBucket.find().iterator()) {
            while (cursor.hasNext()) {
                GridFSFile file = cursor.next();
                if (file.getFilename().startsWith(username)) {
                    GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(file.getObjectId());
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = downloadStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    images.add(outputStream.toByteArray());
                }
            }
        }
        return images;
    }





    public byte[] GridFSDownloadStream(String username, MultipartFile filename) {
        GridFSBucket gridFSBucket = GridFSBuckets.create(Database.DbConnection("ImagesDB.Image"));
        GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream((BsonValue) filename);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = downloadStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        return outputStream.toByteArray();
    }


    public byte[] getImage(String username, String imageName) {
        GridFSBucket gridFSBucket = GridFSBuckets.create(Database.DbConnection("ImagesDB.Image"));
        ByteArrayOutputStream outputStream;
        try (GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(imageName)) {
            outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = downloadStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return outputStream.toByteArray();
    }

}

