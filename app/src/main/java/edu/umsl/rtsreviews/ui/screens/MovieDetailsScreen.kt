package edu.umsl.rtsreviews.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.umsl.rtsreviews.Movie
import edu.umsl.rtsreviews.Review
import edu.umsl.rtsreviews.ui.ExpandableReviewsSection
import edu.umsl.rtsreviews.ui.ReviewForm
import edu.umsl.rtsreviews.ui.calculateAverageRatingForMovie

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

    LazyColumn(
        modifier = Modifier.padding(18.dp)
    ) {

        // Must use "item" in order to use the LazyColumn
        item {

            // Display movie details
            movie?.let {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = it.imageUrl),
                        contentDescription = "Movie Image",
                        modifier = Modifier
                            .width(100.dp)
                            .aspectRatio(0.7f)
                            .clip(RoundedCornerShape(6.dp))
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Use a column to stack the title and rating vertically
                    Column {
                        Text(
                            text = "${it.title}",
                            Modifier.padding(bottom = 8.dp),
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Text(
                            text = "Community rating: ${"%.2f".format(movieRating)}"
                        )
                    }
                }
                Text(
                    text = "${it.description}",
                    Modifier.padding(bottom = 12.dp)
                )
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
}

