package edu.umsl.rtsreviews.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import edu.umsl.rtsreviews.Movie

// List all the movies!
@Composable
fun MovieListScreen(moviesList: List<Movie>, navController: NavController) {

    // The main app view
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.padding(18.dp)
        ) {
            items(moviesList) { movie ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Image(
                            painter = rememberImagePainter(data = movie.imageUrl),
                            contentDescription = "Movie Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .padding(bottom = 8.dp)
                        )
                        Text("${movie.title}", Modifier.padding(bottom = 8.dp))
                        Text("${movie.description}", Modifier.padding(bottom = 8.dp))
                        Text("Rated ${"%.2f".format(movie.averageRating)}")
                        Button(onClick = { navController.navigate("movieDetails/${movie.id}") }) {
                            Text("View Details & Rate")
                        }
                    }
                }
            }
        }
    }
}