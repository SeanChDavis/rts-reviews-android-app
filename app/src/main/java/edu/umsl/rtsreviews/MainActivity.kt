package edu.umsl.rtsreviews

// App dependencies
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

        // Here, we grab an instance of the database to interact with.
        val database = FirebaseDatabase.getInstance()

        // Now that we have a database instance, we "refer" to the exact path of the "movies" node.
        val moviesRef = database.getReference("movies")

        /**
         * === Jetpack Compose - The UI!
         * This is where the UI of the app gets built. All of it. Nested functions are @Composable.
         * --- https://developer.android.com/jetpack/compose/tutorial
         * ~ Notes by Sean
         */
        setContent {

            // Let's theme the app, RTS style.
            // https://developer.android.com/jetpack/compose/documentation
            RTSReviewsTheme {
                RTSReviewsApp(moviesList = moviesList.value)
            }

            /**
             * === Responsible for "watching" Firebase for changes.
             * This is a "side-effect" and only runs once (because of "Unit") when the Activity
             * is first created.
             */
            LaunchedEffect(Unit) {

                // This is an event listener!
                moviesRef.addValueEventListener(object : ValueEventListener {

                    // It listens for data changes in Firebase using the DataSnapshot dependency.
                    // DataSnapshot is a snapshot of the data in Firebase.
                    override fun onDataChange(snapshot: DataSnapshot) {

                        val movies = mutableListOf<Movie>()

                        snapshot.children.forEach { movieSnapshot ->
                            val movie = movieSnapshot.getValue(Movie::class.java)
                            movie?.let { movies.add(it.copy(id = movieSnapshot.key ?: "")) }
                        }

                        // Boom. We have an updated list of movies and their data.
                        moviesList.value = movies

                        // After getting movies, immediately get reviews and calculate average rating.
                        movies.forEach { movie ->
                            fetchAndCalculateAverageRating(movie)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle cancelled event
                    }
                })
            }
        }
    }

    @Composable
    fun RTSReviewsApp(moviesList: List<Movie>) {
        RTSReviewsNavigation(moviesList = moviesList)
    }

    /**
     * Get information about updated movie ratings
     */
    private fun fetchAndCalculateAverageRating(movie: Movie) {

        // Grab an instance of the database pointing at the "reviews' node.
        val reviewsRef = FirebaseDatabase.getInstance().getReference("reviews")

        // Order the reviews by the movie they're attached to and listen for review changes.
        reviewsRef.orderByChild("movieId").equalTo(movie.id)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                // When there's a change...
                override fun onDataChange(snapshot: DataSnapshot) {

                    // ...get the new Review(s) data...
                    val reviews = snapshot.children.mapNotNull { it.getValue(Review::class.java) }

                    // ...and calculate the average of ALL review ratings for the movie by single
                    // movie (because fetchAndCalculateAverageRating() is in a forEach).
                    val averageRating =
                        if (reviews.isNotEmpty()) reviews.map { it.rating }.average() else 0.0

                    // Now update the movie that has a new rating!
                    updateMovieInList(movie.copy(averageRating = averageRating))
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle cancelled event
                }
            })
    }

    /**
     * Update the movie that has been changed
     */
    private fun updateMovieInList(updatedMovie: Movie) {

        // Find the index of the movie that needs to be updated
        val index = moviesList.value.indexOfFirst { it.id == updatedMovie.id }
        if (index != -1) {

            // If the movie is found, update the movie at that index.
            val updatedList = moviesList.value.toMutableList().apply {
                this[index] = updatedMovie
            }

            // Update the state to reflect the new list. This is important because Compose reacts
            // to state changes. It "recomposes" elements when states change. Hence, "update".
            moviesList.value = updatedList
        }
    }
}
