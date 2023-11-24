package edu.umsl.rtsreviews.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.umsl.rtsreviews.Movie

@Composable
fun RTSReviewsNavigation(moviesList: List<Movie>) {

    // Create a NavController
    val navController = rememberNavController()

    NavHost(navController, startDestination = "movieList") {

        /**
         * === SCREEN - Movie List
         * The default app screen, listing all movies
         */
        composable("movieList") {
            MovieListScreen(moviesList = moviesList, navController = navController)
        }

        /**
         * === SCREEN - Single Movie
         * The individual movie screen, accessed from the default app screen movie list
         */
        composable("movieDetails/{movieId}") { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId") ?: ""
            MovieDetailsScreen(movieId)
        }
    }
}