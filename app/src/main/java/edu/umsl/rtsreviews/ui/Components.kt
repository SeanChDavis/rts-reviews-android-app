package edu.umsl.rtsreviews.ui

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.umsl.rtsreviews.Review

/**
 * This composable displays a list of reviews, and allows the user to expand/collapse the list.
 */
@Composable
fun ExpandableReviewsSection(reviews: List<Review>) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Button(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            Text(if (expanded) "Hide Reviews" else "Read All Reviews")
        }

        // Conditionally display the reviews based on 'expanded' state
        if (expanded) {
            reviews.forEach { review ->
                Column {
                    Text(
                        "\"${review.text}\"",
                        modifier = Modifier
                            .padding(bottom = 8.dp),
                        color = Color(0xFF333333),
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    )
                    Text(
                        "Rating: ${review.rating}",
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

/**
 * This composable displays a form for submitting a review.
 */
@Composable
fun ReviewForm(movieId: String, onReviewSubmitted: (Review) -> Unit) {
    var reviewText by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(4.0) }
    var isSubmitting by remember { mutableStateOf(false) }

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

        Button(
            onClick = {
                isSubmitting = true
                val review = Review(movieId, reviewText, rating)
                onReviewSubmitted(review)
                isSubmitting = false
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
