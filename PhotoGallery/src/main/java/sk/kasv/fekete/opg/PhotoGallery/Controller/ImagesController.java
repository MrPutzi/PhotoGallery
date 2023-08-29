package sk.kasv.fekete.opg.PhotoGallery.Controller;

import com.cloudinary.utils.ObjectUtils;
import com.mongodb.client.gridfs.GridFSBucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sk.kasv.fekete.opg.PhotoGallery.Database.Database;
import sk.kasv.fekete.opg.PhotoGallery.Util.Token;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static sk.kasv.fekete.opg.PhotoGallery.Database.Database.getUserImages;
import com.cloudinary.*;

@org.springframework.web.bind.annotation.RestController
@CrossOrigin
public class ImagesController {

    Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "dylky1pnk",
            "api_key", "233675543165788",
            "api_secret", "arDheGtgfMsBpT4eSDnyIGu1DXY"));
    @Autowired
    private GridFSBucket gridFSBucket;
    private Database database = new Database();
    RestController restController = new RestController();




    /**
     * @name: UPLOAD IMAGE
     * @description: This method is used to upload an image for a user.
     * @param token The user's authentication token.
     * @param file The image file to be uploaded.
     * @return ResponseEntity with success or error message.
     */
    @CrossOrigin
    @PostMapping(value = "/upload")
    @ResponseBody
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
    @ResponseBody
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

    @CrossOrigin
    @GetMapping(value = "/images")
    @ResponseBody
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

/*    @GetMapping(value = "/allimages")
    public ResponseEntity<byte[]> getAllImages (@RequestHeader("token") String token){
        String username = Token.getInstance().validateUsername(token);

        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        for (byte[] image : database.getImagesForUser(username)

        ) {
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("image/jpeg")).body(image);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

    }

 */
@CrossOrigin
@GetMapping(value = "/getimages")
@ResponseBody
public ResponseEntity<byte[][]> getImages(@RequestHeader("token") String token) {
    String username = Token.getInstance().validateUsername(token);

    if (username == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }


// Get the list of image names from the getUserImages method
    List<String> imageNames = getUserImages(database.DbConnection("ImagesDB.Image"), username);

// Initialize a list to store the image bytes
    List<byte[]> imageBytes = new ArrayList<>();

// Iterate over the list of image names and get the image bytes for each image
    for (String imageName : imageNames) {
        byte[] imageBytesTemp = database.getImage(username, imageName);
        imageBytes.add(imageBytesTemp);
    }

// Convert the list of ArrayList objects to a list of byte arrays
    byte[][] imageBytesArray = convertToByteArray(imageBytes);

// Return the list of image bytes
    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("image/jpeg")).body(imageBytesArray);
}

    private byte[][] convertToByteArray(List<byte[]> imageBytes) {
        byte[] imageBytesArray []= new byte[imageBytes.size()][];
        for (int i = 0; i < imageBytes.size(); i++) {
            imageBytesArray[i] = imageBytes.get(i);
        }
        return imageBytesArray;
    }
    @CrossOrigin
@GetMapping (value ="/savedImages")
    @ResponseBody
    public ResponseEntity<List<String>> getSavedImages(@RequestHeader("token") String token){
        String username = Token.getInstance().validateUsername(token);

        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        List<String> images = getUserImages(database.DbConnection("ImagesDB.Image"), username);
        if (!images.isEmpty()) {
            return ResponseEntity.ok(images);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}
