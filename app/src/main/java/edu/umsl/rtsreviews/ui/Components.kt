package edu.umsl.rtsreviews.ui

//import androidx.compose.foundation.gestures.ModifierLocalScrollableContainerProvider.value
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.umsl.rtsreviews.Review
import kotlinx.coroutines.delay

/**
 * This composable displays a list of reviews, and allows the user to expand/collapse the list.
 * Used in MovieDetailsScreen.kt, but lives here for convenience.
 */
@Composable
fun ExpandableReviewsSection(reviews: List<Review>) {
    var expanded by remember { mutableStateOf(false) }

    // Only display the review-related UI if there are reviews
    if (reviews.isNotEmpty()) {

        Column {

            // Toggle button for expanding/collapsing the reviews
            Button(
                onClick = { expanded = !expanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                Text(if (expanded) "Hide Reviews" else "Read All Reviews")
            }

            // Conditionally display the reviews based on "expanded" state
            if (expanded) {
                reviews.forEach { review ->

                    Column {

                        Text(
                            text = "\"${review.text}\"",
                            modifier = Modifier
                                .padding(bottom = 8.dp),
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        )

                        Text(
                            text = "Rating: ${"%.2f".format(review.rating)}",
                            modifier = Modifier.padding(bottom = 4.dp),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                        )

                        Divider(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth(),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                        )
                    }
                }
            }
        }
    }

    // If there are no reviews, display a message
    // NOTE: Leaving a review will automatically refresh the screen and display the review
    else {
        Text(
            text = "This movie currently has no reviews.",
            modifier = Modifier.padding(bottom = 20.dp),
            fontStyle = Italic,
        )
    }
}

/**
 * This composable displays a form for submitting a review.
 * Used in MovieDetailsScreen.kt, but lives here for convenience.
 */
@Composable
fun ReviewForm(movieId: String, onReviewSubmitted: (Review) -> Unit) {
    var reviewText by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(4.0) }
    var isSubmitting by remember { mutableStateOf(false) }


            // RM
    // RM set variable to track the original value for the slider
    var originalRating by remember { mutableStateOf(rating)}
    // RM added to track Snackbar visibility
    var snackbarVisibleState by remember { mutableStateOf(false) }
    // RM added to store the Snackbar message
    var snackbarMessage by remember { mutableStateOf("") }



    // RM added to control the Snackbar visibility
    LaunchedEffect(snackbarVisibleState) {
        if (snackbarVisibleState) {
            // Display the Snackbar for a short duration
            delay(6000)
            // Reset the state to hide the Snackbar
            snackbarVisibleState = false
        }
    }

    // RM added to create the Snackbar
    if (snackbarVisibleState) {
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                // Optionally, you can add an action to the Snackbar
                // onClick = { /* Handle action click */ }
            }
        ) {
            Text(text = snackbarMessage)
        }
    }




    Column {

        Text(
            text = "Submit your own review:",
            Modifier.padding(bottom = 4.dp),
            style = MaterialTheme.typography.titleMedium,
        )

        OutlinedTextField(
            value = reviewText,
            onValueChange = { reviewText = it },
            label = { Text("How was the movie?") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(30.dp)
        )

        Text(
            text = "Rate from 0 to 5:",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
        )

        Slider(
            value = rating.toFloat(),
            onValueChange = { rating = it.toDouble() },
            steps = 49,
            valueRange = 0f..5f
        )

        Text(
            text = "%.2f".format(rating)
        )

        Spacer(
            modifier = Modifier.height(30.dp)
        )

        // When the user submits the review, create a Review object and pass it to the callback.
        // The callback is defined in MovieDetailsScreen.kt as a lambda function.
        Button(
            onClick = {
                isSubmitting = true
                val review = Review(movieId, reviewText, rating)
                onReviewSubmitted(review)
                isSubmitting = false

                // RM below
                rating = originalRating // RM This is working and resets the slider

                //TODO RM Still need to get the review to clear when submitted
                reviewText = ""

                // RM added to show the Snackbar
                snackbarVisibleState = true

                // RM This is the Snackbar message
                snackbarMessage = "Review submitted successfully!"

            },
            enabled = reviewText.isNotEmpty() && !isSubmitting
        ) {
            if (isSubmitting) {
                CircularProgressIndicator()
            } else {
                Text("Submit your review!")
            }
        }
    }
}