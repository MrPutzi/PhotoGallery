package sk.kasv.fekete.opg.PhotoGallery.Controller;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sk.kasv.fekete.opg.PhotoGallery.Database.Database;
import sk.kasv.fekete.opg.PhotoGallery.Util.Token;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@org.springframework.web.bind.annotation.RestController
@CrossOrigin
public class ImagesController {

    @Autowired
    private GridFSBucket gridFSBucket;
    private Database database = new Database();


    // ...

    /**
     * @name: UPLOAD IMAGE
     * @description: This method is used to upload an image for a user.
     * @param token The user's authentication token.
     * @param file The image file to be uploaded.
     * @return ResponseEntity with success or error message.
     */
    @PostMapping(value = "/upload")
    public ResponseEntity<String> uploadImage(@RequestHeader("token") String token,
                                                @RequestParam("file") MultipartFile file) {
            String username = Token.getInstance().validateUsername(token);

            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token.");
            }
            // Upload the image using ImagesDatabase's uploadImage method
            boolean success = database.uploadImage(username, file);
            if (success) {
                return ResponseEntity.ok("Image uploaded successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image.");
            }
        }

    @GetMapping(value = "/getimage/{imageName}")
    public ResponseEntity<byte[]> getImage(@RequestHeader("token") String token,
                                           @PathVariable ("imageName") String imageName) {
        String username = Token.getInstance().validateUsername(token);

        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        // Retrieve the image using ImagesDatabase's getImage method
        byte[] imageBytes = database.getImage(username, imageName);

        if (imageBytes != null) {
           /* HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // Set the content type appropriately */
            /* return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);    */
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("image/jpeg")).body(imageBytes);

        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping(value = "/images")
    public ResponseEntity<List<byte[]>> getImagesForUser (@RequestHeader("token") String token){
        String username = Token.getInstance().validateUsername(token);

        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        // Retrieve the image using ImagesDatabase's getImage method
        List<byte[]> images = database.getImagesForUser(username);

        if (images != null) {
            /* return ResponseEntity.status(HttpStatus.OK).body(images);*/
           /* return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("image/jpeg")).body(images);*/
            return ResponseEntity.status(HttpStatus.OK).contentType((MediaType) List.of(MediaType.valueOf("image/jpeg"))).body(images);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }








}
