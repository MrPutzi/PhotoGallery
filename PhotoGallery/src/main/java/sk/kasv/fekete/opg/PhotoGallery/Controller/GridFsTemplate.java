package sk.kasv.fekete.opg.PhotoGallery.Controller;

import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsObject;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;

import java.io.InputStream;

public class GridFsTemplate
        extends Object
        implements GridFsOperations, ResourcePatternResolver
{
    public GridFsTemplate() {
    }


    @Override
    public ObjectId store(InputStream content, String filename) {
        return GridFsOperations.super.store(content, filename);
    }

    @Override
    public ObjectId store(InputStream content, Object metadata) {
        return GridFsOperations.super.store(content, metadata);
    }

    @Override
    public ObjectId store(InputStream content, Document metadata) {
        return GridFsOperations.super.store(content, metadata);
    }

    @Override
    public ObjectId store(InputStream content, String filename, String contentType) {
        return GridFsOperations.super.store(content, filename, contentType);
    }

    @Override
    public ObjectId store(InputStream content, String filename, Object metadata) {
        return GridFsOperations.super.store(content, filename, metadata);
    }

    @Override
    public ObjectId store(InputStream content, String filename, String contentType, Object metadata) {
        return null;
    }

    @Override
    public ObjectId store(InputStream content, String filename, Document metadata) {
        return GridFsOperations.super.store(content, filename, metadata);
    }

    @Override
    public ObjectId store(InputStream content, String filename, String contentType, Document metadata) {
        return GridFsOperations.super.store(content, filename, contentType, metadata);
    }

    @Override
    public <T> T store(GridFsObject<T, InputStream> upload) {
        return null;
    }

    @Override
    public GridFSFindIterable find(Query query) {
        return null;
    }

    @Override
    public GridFSFile findOne(Query query) {
        return null;
    }

    @Override
    public void delete(Query query) {

    }

    @Override
    public GridFsResource getResource(String filename) {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }

    @Override
    public GridFsResource getResource(GridFSFile file) {
        return null;
    }

    @Override
    public GridFsResource[] getResources(String filenamePattern) {
        return new GridFsResource[0];
    }
}
