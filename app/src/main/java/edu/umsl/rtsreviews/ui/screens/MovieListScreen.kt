package edu.umsl.rtsreviews.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
                        .padding(8.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Button(onClick = { navController.navigate("movieDetails/${movie.id}") }) {
                            Text(movie.title)
                        }
                        Text("ID: ${movie.id}")
                        Text("Description: ${movie.description}")
                        Text("Rating: ${movie.averageRating}")
                    }
                }
            }
        }
    }
}