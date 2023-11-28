package edu.umsl.rtsreviews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.umsl.rtsreviews.ui.screens.RTSReviewsNavigation
import edu.umsl.rtsreviews.ui.theme.RTSReviewsTheme

/**
 * The single Review object
 */
data class Review(
    val movieId: String = "",
    val text: String = "",
    val rating: Double = 0.0
)

/**
 * The single Movie object
 */
data class Movie(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    var averageRating: Double = 0.0 // Just a calculation, not a data field
)

/**
 * === Main Activity
 * This is the app's "Main Activity", which is where we see and interact with all saved movies.
 * --- https://data-flair.training/blogs/android-activity/
 * ComponentActivity extends Android's Activity class.
 * --- https://developer.android.com/reference/kotlin/androidx/activity/ComponentActivity
 * ~ Notes by Sean
 */
class MainActivity : ComponentActivity() {

    /**
     * This override is called immediately when the Main Activity is created. It runs the moment
     * the app is opened.
     * --- https://data-flair.training/blogs/android-activity/
     * ~ Notes by Sean
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            // Let's theme the app, RTS style.
            // https://developer.android.com/jetpack/compose/documentation
            RTSReviewsTheme {
                RTSReviewsApp()
            }
        }
    }

    /**
     * === The main app
     * This is the main app, which is called from the setContent block above.
     * It calls the RTSReviewsNavigation function, which is the app's navigation.
     * --- See Navigation.kt for more details.
     * More importantly, it calls the MoviesViewModel, which is the app's single source of truth.
     * --- See MoviesViewModel.kt for more details.
     * ~ Notes by Sean
     */
    @Composable
    fun RTSReviewsApp() {

        // A single instance of the MoviesViewModel is used throughout the app.
        // If I had hair, I would have pulled it out trying to figure this out!
        val moviesViewModel: MoviesViewModel = viewModel()

        // Call the navigation function, passing in the MoviesViewModel.
        RTSReviewsNavigation(moviesViewModel)
    }
}
