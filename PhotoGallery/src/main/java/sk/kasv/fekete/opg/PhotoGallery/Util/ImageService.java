package sk.kasv.fekete.opg.PhotoGallery.Util;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sk.kasv.fekete.opg.PhotoGallery.Database.Database;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class ImageService {

    public boolean uploadImage(String username, MultipartFile file) {
        GridFSBucket gridFSBucket = GridFSBuckets.create(Database.DbConnection("ImagesDB.Image"));

        try (InputStream inputStream = file.getInputStream()) {
            String originalFilename = file.getOriginalFilename();
            String uniqueFilename = generateUniqueFilename(username, originalFilename);

            ObjectId fileId = gridFSBucket.uploadFromStream(uniqueFilename, inputStream);

            // You might want to store the fileId or other relevant information in your database.
            // Return true or false based on the success of the upload.
            return fileId != null;
        } catch (IOException e) {
            // Handle exceptions appropriately.
            e.printStackTrace();
            return false;
        }
    }

    private String generateUniqueFilename(String username, String originalFilename) {
        // You can customize the format of the unique filename as needed.
        return username + "_" + UUID.randomUUID() + "_" + originalFilename;
    }
}