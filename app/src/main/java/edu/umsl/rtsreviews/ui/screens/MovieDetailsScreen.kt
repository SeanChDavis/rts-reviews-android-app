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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.FirebaseDatabase
import edu.umsl.rtsreviews.MoviesViewModel
import edu.umsl.rtsreviews.ui.ExpandableReviewsSection
import edu.umsl.rtsreviews.ui.ReviewForm

/**
 * === SCREEN 2 - Single Movie
 * The individual movie screen, accessed from the default app screen movie list
 */
// TODO add navController to below
@Composable
fun MovieDetailsScreen(movieId: String, moviesViewModel: MoviesViewModel) {

    // Get the movie from the ViewModel
    val movie by moviesViewModel.selectedMovie.collectAsState()

    // Get the reviews from the ViewModel
    val reviews by moviesViewModel.reviews.collectAsState()

    // Get the average rating from the ViewModel
    val movieRating = moviesViewModel.getAverageRating(reviews)

    // Get the loading state from the ViewModel
    val isLoading by moviesViewModel.isLoading.collectAsState()

    // A sweet lambda for getting the movie and reviews.
    // Using a lambda because we call it when the screen is launched, and when a new review is added.
    // Lambdas are sweet - https://stackoverflow.com/questions/16501/what-is-a-lambda-function
    val fetchMovieAndReviews = {
        moviesViewModel.fetchMovieDetails(movieId)
        moviesViewModel.fetchMovieReviews(movieId)
    }

    // Now run that lambda when the screen is launched
    LaunchedEffect(movieId) {
        fetchMovieAndReviews()
    }

    /**
     * === The Movie Details
     * This is the movie details, displayed in a LazyColumn.
     * --- https://developer.android.com/jetpack/compose/lists
     * Just like the movie list, we use Row and Column to create layouts.
     * --- https://developer.android.com/jetpack/compose/layout
     * ~ Notes by Sean
     */
    LazyColumn(
        modifier = Modifier.padding(18.dp)
    ) {

        // Must use "item" in order to use the LazyColumn.
        // Even when there's only one item, like here, we still need to use "item".
        // --- https://developer.android.com/jetpack/compose/lists#lazylistscope
        // ~ Notes by Sean
        item {

            // Display movie details if the movie exists
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

                    Column {

                        Text(
                            text = it.title,
                            Modifier.padding(bottom = 8.dp),
                            style = MaterialTheme.typography.titleLarge,
                        )

                        // Only show the rating if it's greater than 0.0
                        if ( movieRating > 0.0 ) Text(
                            text = "Overall rating: ${"%.2f".format(movieRating)}"
                        )
                        else Text(
                            text = "Not yet rated."
                        )
                    }
                }

                Text(
                    text = it.description,
                    Modifier.padding(bottom = 12.dp)
                )
            }

            // Show a loading indicator if we're still loading the movie
            if (movie == null && isLoading) {
                CircularProgressIndicator()
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Show a loading indicator if we're still loading the reviews
            // Otherwise, show the reviews button toggle
            if (isLoading) {
                CircularProgressIndicator()
            } else {

                // Show the reviews button toggle that expands the reviews section
                // Moved to Components.kt for organization
                ExpandableReviewsSection(reviews = reviews)
            }

            Spacer(modifier = Modifier.height(20.dp))

            /**
             * === The Review Form
             * This is the review form, which uses the ReviewForm composable in Components.kt.
             * The ReviewForm composable takes a lambda, which is used to submit the review.
             * --- See Components.kt for more details.
             * ~ Notes by Sean
             */
            ReviewForm(movieId = movieId) { review ->
                moviesViewModel.setLoading(true)

                val reviewsRef = FirebaseDatabase.getInstance().getReference("reviews")
                val newReviewRef = reviewsRef.push()

                newReviewRef.setValue(review)
                    .addOnSuccessListener {

                        // Reset the loading state
                        moviesViewModel.setLoading(false)

                        // Now reuse that other sweet lambda to get the movie and reviews again
                        fetchMovieAndReviews()
                    }
                    .addOnFailureListener {

                        // Reset the loading state
                        moviesViewModel.setLoading(false)
                    }

                // Update the movie's rating in the ViewModel so it can be updated in the main list.
                // Without this, the main list won't update the movie's rating everywhere!
                // That is the exact reason why we must have a single source of truth: the ViewModel.
                // ~ Notes by Sean
                moviesViewModel.updateMovieRating(movieId)
            }
        }
    }
}

