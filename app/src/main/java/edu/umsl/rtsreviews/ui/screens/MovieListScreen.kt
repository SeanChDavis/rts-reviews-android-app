package edu.umsl.rtsreviews.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import edu.umsl.rtsreviews.MoviesViewModel

/**
 * === SCREEN 1 (default) - Movie List
 * The default app screen, listing all movies!
 */
@Composable
fun MovieListScreen(moviesViewModel: MoviesViewModel, navController: NavController) {

    // Get the movies from the MoviesViewModel
    val movies by moviesViewModel.movies.collectAsState()

    // Navigate to the movie details screen (just for convenience)
    val clickToMovie = { movieId: String ->
        navController.navigate("movieDetails/$movieId")
    }

    /**
     * === The Movie List
     * This is the list of movies, displayed in a LazyColumn.
     * --- https://developer.android.com/jetpack/compose/lists
     * Notice how things like Row and Column are used to create layouts.
     * --- https://developer.android.com/jetpack/compose/layout
     * ~ Notes by Sean
     */
    Surface(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier.padding(18.dp)
        ) {

            items(movies) { movie ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Image(
                        painter = rememberAsyncImagePainter(model = movie.imageUrl),
                        contentDescription = "Movie Image",
                        modifier = Modifier
                            .width(100.dp)
                            .aspectRatio(0.7f)
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { clickToMovie(movie.id) }
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) {

                        Text(
                            text = movie.title,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.clickable { clickToMovie(movie.id) }
                        )

                        // Only show the rating if it's greater than 0.0
                        if ( movie.averageRating > 0.0 ) Text(
                            text = "Rated ${"%.2f".format(movie.averageRating)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        else Text(
                            text = "Not yet rated."
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        FilledTonalButton(
                            onClick = { clickToMovie(movie.id) },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                // Found some built in icons!
                                // https://developer.android.com/reference/kotlin/androidx/compose/material/icons/package-summary
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Star Ratings",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    "Reviews & Details",
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(0.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
