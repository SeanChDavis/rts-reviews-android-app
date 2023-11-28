package edu.umsl.rtsreviews.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.umsl.rtsreviews.MoviesViewModel

/**
 * === The navigation of the app. There are two screens.
 * --- https://developer.android.com/jetpack/compose/navigation
 * ~ Notes by Sean
 */
@Composable
fun RTSReviewsNavigation(moviesViewModel: MoviesViewModel) {

    // Create a NavHost, passing in the Nav Controller
    val navController = rememberNavController()
    NavHost(navController, startDestination = "movieList") {

        /**
         * === SCREEN 1 (default) - Movie List
         * The default app screen, listing all movies
         */
        composable("movieList") {

            // Pass the MoviesViewModel instance to the MovieListScreen
            MovieListScreen(moviesViewModel, navController)
        }

        /**
         * === SCREEN 2 - Single Movie
         * The individual movie screen, accessed from the default app screen movie list
         */
        composable("movieDetails/{movieId}") { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId") ?: ""

            // Pass the MoviesViewModel instance to the MovieDetailsScreen
            MovieDetailsScreen(movieId, moviesViewModel)
        }
    }
}