package edu.umsl.rtsreviews.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
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
    // TODO RM this is the spot that you get the path
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
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        LazyColumn(
            modifier = Modifier.padding(20.dp)
        ) {

            // Simple app introduction. Not an official app header, but it works.
            // Must use "item" in order to use the LazyColumn.
            // Even when there's only one item, like here, we still need to use "item".
            // --- https://developer.android.com/jetpack/compose/lists#lazylistscope
            // ~ Notes by Sean
            item {

                Column(
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {

                    Text(
                        text = "RTS Reviews",
                        modifier = Modifier.padding(top = 10.dp, bottom = 16.dp),
                        style = MaterialTheme.typography.titleLarge,
                    )

                    Column {

                        // buildAnnotatedString is a way to style text within text. Pretty cool!
                        // https://developer.android.com/jetpack/compose/text
                        Text(
                            buildAnnotatedString {

                                withStyle(
                                    style = SpanStyle(
                                        color = MaterialTheme.colorScheme.tertiary,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("Welcome! ")
                                }

                                append(
                                    "Use the list below to find movies worth watching. For" +
                                            " movies you've watched, leave an honest reviews" +
                                            " for others to see."
                                )
                            },
                            color = MaterialTheme.colorScheme.tertiary,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize
                        )
                    }
                }
            }

            // Loop through the movies and display them
            // Also, when there are multiple items, we use "items" instead of "item"
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
                            modifier = Modifier.clickable { clickToMovie(movie.id) },
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleMedium,
                        )

                        // Only show the rating if it's greater than 0.0
                        if (movie.averageRating > 0.0)
                            Text(
                                buildAnnotatedString {

                                    append("Rating: ")

                                    withStyle(
                                        style = SpanStyle(
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    ) {
                                        append("${"%.2f".format(movie.averageRating)}")
                                    }
                                },
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize
                            )
                        else Text(
                            text = "Not yet rated.",
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { clickToMovie(movie.id) },
                            modifier = Modifier.padding(top = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(start = 14.dp, end = 17.dp)
                        ) {

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                // Found some built in icons!
                                // https://developer.android.com/reference/kotlin/androidx/compose/material/icons/package-summary
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Star Ratings",
                                    tint = Color.Yellow,
                                    modifier = Modifier.size(16.dp)
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Text(
                                    text = "Reviews & Details".uppercase(),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold
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
