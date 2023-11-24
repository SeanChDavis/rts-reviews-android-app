package edu.umsl.rtsreviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import android.util.Log

/**
 * === Responsible for getting the data from Firebase and watching for changes.
 * Separated out into a ViewModel so that it can be used by multiple screens.
 */
class MoviesViewModel : ViewModel() {

    // Use StateFlow to hold the list of movies
    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    init {
        fetchMovies()
    }

    private fun fetchMovies() {
        viewModelScope.launch {

            // Get movies from Firebase
            val database = FirebaseDatabase.getInstance()
            val moviesRef = database.getReference("movies")

            // Listen for changes in Firebase and update the _movies StateFlow
            moviesRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val movies = mutableListOf<Movie>()

                    // Log the snapshot for debugging
                    // Log.d("MoviesViewModel", "Snapshot: $snapshot")

                    snapshot.children.forEach { movieSnapshot ->
                        val movie = movieSnapshot.getValue(Movie::class.java)
                        movie?.let {
                            val updatedMovie = it.copy(id = movieSnapshot.key ?: "")
                            movies.add(updatedMovie)
                            fetchAndCalculateAverageRating(updatedMovie)
                        }
                    }

                    // Parse snapshot to movies
                    _movies.value = movies
                }

                override fun onCancelled(error: DatabaseError) {
                    // TODO Handle cancelled event
                }
            })
        }
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
        val index = _movies.value.indexOfFirst { it.id == updatedMovie.id }
        if (index != -1) {

            // If the movie is found, update the movie at that index.
            val updatedList = _movies.value.toMutableList().apply {
                this[index] = updatedMovie
            }

            // Update the state to reflect the new list. This is important because Compose reacts
            // to state changes. It "recomposes" elements when states change. Hence, "update".
            _movies.value = updatedList
        }
    }
}