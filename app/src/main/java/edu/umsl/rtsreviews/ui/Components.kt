package edu.umsl.rtsreviews.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import edu.umsl.rtsreviews.Review

/**
 * This composable displays a form for submitting a review.
 */
@Composable
fun ReviewForm(movieId: String, onReviewSubmitted: (Review) -> Unit) {
    var reviewText by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0.0) }
    var isSubmitting by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = reviewText,
            onValueChange = { reviewText = it },
            label = { Text("Your Review") },
            modifier = Modifier.fillMaxWidth()
        )
        Slider(
            value = rating.toFloat(),
            onValueChange = { rating = it.toDouble() },
            valueRange = 0f..5f
        )
        Button(
            onClick = {
                isSubmitting = true
                val review = Review(movieId, reviewText, rating)
                onReviewSubmitted(review)
                isSubmitting = false
            },
            enabled = !isSubmitting
        ) {
            if (isSubmitting) {
                CircularProgressIndicator()
            } else {
                Text("Submit Review")
            }
        }
    }
}

/**
 * This composable displays a list of reviews, and allows the user to expand/collapse the list.
 */
@Composable
fun ExpandableReviewsSection(reviews: List<Review>) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Button(
            onClick = { expanded = !expanded }
        ) {
            Text(if (expanded) "Hide Reviews" else "Show Reviews")
        }

        // Conditionally display the reviews based on 'expanded' state
        if (expanded) {
            reviews.forEach { review ->
                Text("Rating: ${review.rating}")
                Text("Review: ${review.text}")
                Divider()
            }
        }
    }
}
