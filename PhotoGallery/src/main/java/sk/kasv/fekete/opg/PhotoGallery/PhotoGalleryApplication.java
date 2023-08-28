package sk.kasv.fekete.opg.PhotoGallery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import sk.kasv.fekete.opg.PhotoGallery.Util.CorsFilter;
import sk.kasv.fekete.opg.PhotoGallery.Util.MongoConfig;


@SpringBootApplication
public class PhotoGalleryApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhotoGalleryApplication.class, args);
	}

	@Bean
	public CorsFilter corsFilter() {
		CorsFilter filter = new CorsFilter();
		return filter;
	}

}
