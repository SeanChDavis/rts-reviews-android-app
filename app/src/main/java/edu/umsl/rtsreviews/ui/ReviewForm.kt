package edu.umsl.rtsreviews.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import edu.umsl.rtsreviews.Review

@Composable
fun ReviewForm(movieId: String, onReviewSubmitted: (Review) -> Unit) {
    var reviewText by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0.0) }
    var isSubmitting by remember { mutableStateOf(false) }
    var submitError by remember { mutableStateOf<String?>(null) }

    Column {
        TextField(
            value = reviewText,
            onValueChange = { reviewText = it },
            label = { Text("Review") }
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
                try {
                    onReviewSubmitted(review)
                    reviewText = ""
                    rating = 0.0
                } catch (e: Exception) {
                    submitError = e.localizedMessage
                }
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
        submitError?.let { Text("Error: $it", color = Color.Red) }
    }
}