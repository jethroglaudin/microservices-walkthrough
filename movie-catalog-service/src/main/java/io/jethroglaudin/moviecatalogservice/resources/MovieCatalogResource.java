package io.jethroglaudin.moviecatalogservice.resources;

import io.jethroglaudin.moviecatalogservice.models.CatalogItem;
import io.jethroglaudin.moviecatalogservice.models.Movie;
import io.jethroglaudin.moviecatalogservice.models.Rating;
import io.jethroglaudin.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {


        UserRating ratings = restTemplate.getForObject("http://localhost:8083/ratingsdata/users/" + userId,
                UserRating.class);

        return ratings.getUserRating().stream().map(rating -> {
            // For each movie ID, call movie info service and get details
            Movie movie = restTemplate.getForObject("http://localhost:8082/movies/" + rating.getMovieId(), Movie.class);
            // Put them all together
            return new CatalogItem(movie.getName(), "desc", rating.getRating());
        }).collect(Collectors.toList());
    }
}

//            Movie movie = webClientBuilder.build()
//                    .get()
//                    .uri("http://localhost:8082/movies/" + rating.getMovieId())
//                    .retrieve()
//                    .bodyToMono(Movie.class)// Asynchronous convert body to Movie class. Getting back asynchronous object.
//                    .block(); // blocking execution until that bodyToMono is complete
