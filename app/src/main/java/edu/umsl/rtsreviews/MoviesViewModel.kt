package edu.umsl.rtsreviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * === Responsible for getting the data from Firebase and watching for changes.
 * Separated out into a ViewModel so that it can be used by multiple screens.
 * This is the "single source of truth" for the app!
 * --- https://developer.android.com/topic/libraries/architecture/viewmodel
 * ~ Notes by Sean
 */
class MoviesViewModel : ViewModel() {

    // Expose a flow for all movies
    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    // Expose a flow for a single movie
    private val _selectedMovie = MutableStateFlow<Movie?>(null)
    val selectedMovie: StateFlow<Movie?> = _selectedMovie.asStateFlow()

    // Expose a flow for reviews
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    // Get the average rating of a movie
    fun getAverageRating(reviews: List<Review>): Double {
        return if (reviews.isNotEmpty()) reviews.map { it.rating }.average() else 0.0
    }

    // Expose a flow for loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Allow setting the loading state from outside the ViewModel
    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }

    // Initialize the ViewModel
    init {

        // I used this to see how many times the ViewModel was being initialized with fresh data
        // because the two different screens kept showing different review data. What a pain!!!
        // Log.d("MoviesViewModel", "ViewModel initialized: ${System.identityHashCode(this)}")

        // Fetch the movies from Firebase.
        fetchMovies()
    }

    /**
     * Get all movies from Firebase
     * This is the "single source of truth" for the app!
     */
    private fun fetchMovies() {
        viewModelScope.launch {

            // Get movies from Firebase
            val database = FirebaseDatabase.getInstance()
            val moviesRef = database.getReference("movies")

            // Listen for changes in Firebase and update the _movies StateFlow
            moviesRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val movies = mutableListOf<Movie>()

                    // Parse snapshot to movies
                    snapshot.children.forEach { movieSnapshot ->

                        // Get the movie data
                        val movie = movieSnapshot.getValue(Movie::class.java)

                        movie?.let {

                            // Update the movie ID with the Firebase key
                            val updatedMovie = it.copy(id = movieSnapshot.key ?: "")

                            // Add the movie to the list
                            movies.add(updatedMovie)

                            // Update the movie's average rating
                            fetchAndCalculateAverageRating(updatedMovie)
                        }
                    }

                    // Update the movies StateFlow
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
                    // movie (because fetchAndCalculateAverageRating() is used in a forEach).
                    val averageRating =
                        if (reviews.isNotEmpty()) reviews.map { it.rating }.average() else 0.0

                    // Now update the movie that has a new rating!
                    updateMovieInList(movie.copy(averageRating = averageRating))
                }

                override fun onCancelled(error: DatabaseError) {
                    // TODO Handle cancelled event
                }
            })
    }

    /**
     * Update the movie that has been changed
     */
    private fun updateMovieInList(updatedMovie: Movie) {

        // Find the index of the movie that needs to be updated
        val index = _movies.value.indexOfFirst { it.id == updatedMovie.id }

        // If the movie is found...
        if (index != -1) {

            // Update the movie at that index
            val updatedList = _movies.value.toMutableList().apply {

                // We're literally updating the movie in the single source of truth!
                this[index] = updatedMovie
            }

            // Update the state to reflect the new list. This is important because Compose reacts
            // to state changes. It "recomposes" elements when states change. Hence, "update".
            // So when this happens, the movie list will update with the new average rating.
            _movies.value = updatedList
        }
    }

    /**
     * Get a specific movie's details
     */
    fun fetchMovieDetails(movieId: String) {
        viewModelScope.launch {

            // Set loading to true
            _isLoading.value = true

            // Get specific movie (by ID) from Firebase
            val movieRef = FirebaseDatabase.getInstance().getReference("movies").child(movieId)

            // Listen for changes in Firebase and update the _selectedMovie StateFlow
            movieRef.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    // Parse snapshot to movie and update the StateFlow
                    val movie = snapshot.getValue(Movie::class.java)
                    _selectedMovie.value = movie

                    // Set loading to false
                    _isLoading.value = false
                }

                override fun onCancelled(error: DatabaseError) {

                    // TODO Handle the error and set loading to false
                    _isLoading.value = false
                }
            })
        }
    }

    /**
     * Get a specific movie's reviews
     */
    fun fetchMovieReviews(movieId: String) {
        viewModelScope.launch {

            // Set loading to true
            _isLoading.value = true

            // Grab an instance of the database pointing at the "reviews' node.
            val reviewsRef = FirebaseDatabase.getInstance().getReference("reviews")

            // Listen for changes in Firebase and update the _reviews StateFlow
            reviewsRef.orderByChild("movieId").equalTo(movieId)
                .addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {

                        // Parse snapshot to reviews and update the StateFlow
                        val reviews = snapshot.children.mapNotNull { it.getValue(Review::class.java) }
                        _reviews.value = reviews

                        // Set loading to false
                        _isLoading.value = false
                    }

                    override fun onCancelled(error: DatabaseError) {

                        // TODO Handle the error and set loading to false
                        _isLoading.value = false
                    }
                })
        }
    }

    /**
     * Update a movie's rating globally when a review is added or updated
     */
    fun updateMovieRating(movieId: String) {

        // Grab an instance of the database pointing at the "reviews' node.
        val reviewsRef = FirebaseDatabase.getInstance().getReference("reviews")

        // Order the reviews by the movie they're attached to and listen for review changes.
        reviewsRef.orderByChild("movieId").equalTo(movieId)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    // Get the new Review(s) data
                    val reviews = snapshot.children.mapNotNull { it.getValue(Review::class.java) }

                    // Calculate the average of ALL review ratings for the movie
                    val averageRating = if (reviews.isNotEmpty()) reviews.map { it.rating }.average() else 0.0

                    // Find the movie and update its rating
                    val updatedMovies = _movies.value.map { movie ->
                        if (movie.id == movieId) movie.copy(averageRating = averageRating) else movie
                    }
                    _movies.value = updatedMovies
                }

                override fun onCancelled(error: DatabaseError) {
                    // TODO Handle cancelled event
                }
            })
    }
}