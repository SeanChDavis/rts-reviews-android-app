package edu.umsl.rtsreviews

// App dependencies
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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

    // Global MutableState for the list of movies, meaning this list can (and will) be changed.
    // See https://www.javatpoint.com/mutable-and-immutable-in-java
    private val moviesList = mutableStateOf(listOf<Movie>())

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

    @Composable
    fun RTSReviewsApp() {

        // Use the movies state from the ViewModel
        val moviesViewModel: MoviesViewModel = viewModel()
        val movies by moviesViewModel.movies.collectAsState()

        RTSReviewsNavigation(moviesList = movies)
    }
}
