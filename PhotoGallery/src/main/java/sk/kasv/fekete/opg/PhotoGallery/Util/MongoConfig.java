package sk.kasv.fekete.opg.PhotoGallery.Util;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "sk.kasv.fekete.opg.PhotoGallery.Database")
public class MongoConfig {
    @Bean
    public GridFSBucket gridFSBucket(MongoDatabaseFactory mongoDbFactory, MongoConverter converter) {
        return GridFSBuckets.create(mongoDbFactory.getMongoDatabase(), converter.toString());
    }
}
