package edu.umsl.rtsreviews.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.umsl.rtsreviews.Movie
import edu.umsl.rtsreviews.Review
import edu.umsl.rtsreviews.ui.ExpandableReviewsSection
import edu.umsl.rtsreviews.ui.ReviewForm

@Composable
fun MovieDetailsScreen(movieId: String) {
    var movie by remember { mutableStateOf<Movie?>(null) }
    var reviews by remember { mutableStateOf(emptyList<Review>()) }
    var movieRating by remember { mutableStateOf(0.0) }
    var isLoading by remember { mutableStateOf(false) }

    // Get reviews when the screen loads
    LaunchedEffect(movieId) {
        isLoading = true

        calculateAverageRatingForMovie(movieId) { rating ->
            movieRating = rating
            isLoading = false
        }

        // Get movie details
        val movieRef = FirebaseDatabase.getInstance().getReference("movies").child(movieId)
        movieRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                movie = snapshot.getValue(Movie::class.java)
            }
            override fun onCancelled(error: DatabaseError) {
                // TODO Handle cancelled event
            }
        })

        // Get reviews for this movie
        val reviewsRef = FirebaseDatabase.getInstance().getReference("reviews")
        reviewsRef.orderByChild("movieId").equalTo(movieId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    reviews = snapshot.children.mapNotNull { it.getValue(Review::class.java) }
                    isLoading = false
                }
                override fun onCancelled(error: DatabaseError) {
                    // TODO Handle the error
                    isLoading = false
                }
            })
    }

    Column(
        modifier = Modifier.padding(18.dp)
    ) {

        // Display movie details
        movie?.let {
            Image(
                painter = rememberImagePainter(data = it.imageUrl),
                contentDescription = "Movie Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(bottom = 8.dp)
            )
            Text("${it.title}", Modifier.padding(bottom = 8.dp))
            Text("${it.description}", Modifier.padding(bottom = 8.dp))
            Text("Rated ${"%.2f".format(movieRating)}")
        }

        // Show a loading indicator if we're still loading the movie
        if (movie == null && isLoading) {
            CircularProgressIndicator()
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Show a loading indicator if we're still loading the reviews
        // Otherwise, show the reviews
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            ExpandableReviewsSection(reviews = reviews)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Show the review form!
        ReviewForm(movieId = movieId) { review ->
            isLoading = true
            val reviewsRef = FirebaseDatabase.getInstance().getReference("reviews")
            val newReviewRef = reviewsRef.push()
            newReviewRef.setValue(review)
                .addOnSuccessListener {
                    // TODO Reset the form and state
                    isLoading = false
                }
                .addOnFailureListener {
                    // TODO Reset the loading state, handle and display the error message
                    isLoading = false
                }
        }
    }
}

/**
 * Calculate the average rating for a movie
 */
fun calculateAverageRatingForMovie(movieId: String, onRatingCalculated: (Double) -> Unit) {
    val reviewsRef = FirebaseDatabase.getInstance().getReference("reviews")
    reviewsRef.orderByChild("movieId").equalTo(movieId)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val reviews = snapshot.children.mapNotNull { it.getValue(Review::class.java) }
                val averageRating = if (reviews.isNotEmpty()) reviews.map { it.rating }.average() else 0.0
                onRatingCalculated(averageRating)
            }
            override fun onCancelled(error: DatabaseError) {
                // TODO Handle error
            }
        })
}

