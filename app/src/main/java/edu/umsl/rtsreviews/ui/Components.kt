package edu.umsl.rtsreviews.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
                    .padding(bottom = 20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(8.dp),
            ) {

                Text(
                    text = if (expanded) "Hide All Reviews".uppercase() else "See All Reviews".uppercase(),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            // Conditionally display the reviews based on "expanded" state
            if (expanded) {
                reviews.forEach { review ->

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFF2EFFC),
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {

                        Column(
                            modifier = Modifier.padding(18.dp)
                        ) {

                            Text(
                                text = "\"${review.text}\"",
                                modifier = Modifier
                                    .padding(bottom = 8.dp),
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            )

                            Text(
                                text = "Rating: ${"%.2f".format(review.rating)}",
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // If there are no reviews, display a message
    // NOTE: Leaving a review will automatically refresh the screen and display the review
    else {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFECECEC),
                    shape = RoundedCornerShape(8.dp)
                )
        ) {

            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                Text(
                    text = "This movie currently has no reviews.",
                    color = Color(0xFF919191),
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontStyle = Italic,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

/**
 * This composable displays a form for submitting a review.
 * Used in MovieDetailsScreen.kt, but lives here for convenience.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewForm(movieId: String, navController: NavController, onReviewSubmitted: (Review) -> Unit) {
    var reviewText by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(4.0) }
    var isSubmitting by remember { mutableStateOf(false) }

    // RM set variable to track the original value for the slider
    var originalRating by remember { mutableStateOf(rating) }
    // RM added to track Snackbar visibility
    var snackbarVisibleState by remember { mutableStateOf(false) }
    // RM added to store the Snackbar message
    var snackbarMessage by remember { mutableStateOf("") }

    // RM added to control the Snackbar visibility
    LaunchedEffect(snackbarVisibleState) {
        if (snackbarVisibleState) {
            // Display the Snackbar for a short duration
            delay(600000)
            // Reset the state to hide the Snackbar
            snackbarVisibleState = false
        }
    }

    Column {

        Text(
            text = "Submit your own review:",
            Modifier.padding(bottom = 4.dp),
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.titleSmall,
        )

        OutlinedTextField(
            value = reviewText,
            onValueChange = { reviewText = it },
            label = { Text("How was the movie?") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3,
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color(0xFFD0CDDA),
            ),
        )

        Spacer(
            modifier = Modifier.height(30.dp)
        )

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Rate from 0 to 5:",
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall,
            )

            Text(
                text = "%.2f".format(rating),
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall,
            )
        }

        Slider(
            value = rating.toFloat(),
            onValueChange = { rating = it.toDouble() },
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp)
                .background(
                    color = Color(0xFFF2EFFC),
                    shape = RoundedCornerShape(8.dp)
                ),
            steps = 49,
            valueRange = 0f..5f,
            colors = SliderDefaults.colors(
                activeTickColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTickColor = MaterialTheme.colorScheme.tertiary,
                inactiveTrackColor = MaterialTheme.colorScheme.tertiary,
                thumbColor = MaterialTheme.colorScheme.primary,
            ),
        )

        Spacer(
            modifier = Modifier.height(30.dp)
        )

        // RM added to create the Snackbar
        if (snackbarVisibleState) {
            Snackbar(
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                action = {
                    Row {

                        Text(
                            text = "Return to Movies",
                            modifier = Modifier.clickable { navController.navigate("movieList") },
                            color = MaterialTheme.colorScheme.onTertiary,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                        )

                        Text(
                            text = " | ",
                            color = MaterialTheme.colorScheme.onTertiary,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "Dismiss ",
                            modifier = Modifier.clickable { snackbarVisibleState = false },
                            color = MaterialTheme.colorScheme.onTertiary,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                },
                shape = MaterialTheme.shapes.medium,
                containerColor = MaterialTheme.colorScheme.secondary,
            ) {
                Text(text = snackbarMessage)
            }
        }

        // When the user submits the review, create a Review object and pass it to the callback.
        // The callback is defined in MovieDetailsScreen.kt as a lambda function.
        Button(
            onClick = {
                isSubmitting = true
                val review = Review(movieId, reviewText, rating)
                onReviewSubmitted(review)
                isSubmitting = false

                // RM added to reset the slider value
                rating = originalRating

                // RM added to clear the review text
                reviewText = ""

                // RM added to show the Snackbar
                snackbarVisibleState = true

                // RM This is the Snackbar message
                snackbarMessage = "Review submitted!"
            },
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                ),
            enabled = reviewText.isNotEmpty() && !isSubmitting
        ) {
            if (isSubmitting) {
                CircularProgressIndicator()
            } else {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Star Icon",
                        tint = Color(0xFFF8B728),
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Submit your review!".uppercase(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}