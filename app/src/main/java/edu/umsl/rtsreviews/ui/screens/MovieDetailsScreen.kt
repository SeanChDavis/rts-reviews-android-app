package edu.umsl.rtsreviews.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.umsl.rtsreviews.ui.ReviewForm


@Composable
fun MovieDetailsScreen(movieId: String) {

    // The main app view
    Surface( modifier = Modifier.fillMaxSize() ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text("Movie Details for ID: $movieId")
            Spacer(modifier = Modifier.height(16.dp))
            Text("Submit Your Review")
            ReviewForm(movieId = movieId, onReviewSubmitted = { review ->

            })
        }
    }
}
