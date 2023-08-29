package sk.kasv.fekete.opg.PhotoGallery;

import org.apache.catalina.filters.CorsFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
/*	import sk.kasv.fekete.opg.PhotoGallery.Util.CorsFilter; 	*/


@SpringBootApplication
public class PhotoGalleryApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhotoGalleryApplication.class, args);
	}

@Bean
	public void corsFilter() {
	CorsFilter filter = new CorsFilter();
}



}
