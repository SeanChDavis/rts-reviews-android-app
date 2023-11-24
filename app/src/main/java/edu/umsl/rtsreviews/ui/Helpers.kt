package edu.umsl.rtsreviews.ui

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.umsl.rtsreviews.Review

/**
 * Calculate the average rating for a movie
 */
fun calculateAverageRatingForMovie(movieId: String, onRatingCalculated: (Double) -> Unit) {
    val reviewsRef = FirebaseDatabase.getInstance().getReference("reviews")
    reviewsRef.orderByChild("movieId").equalTo(movieId)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val reviews = snapshot.children.mapNotNull { it.getValue(Review::class.java) }
                val averageRating = if (reviews.isNotEmpty()) reviews.map { it.rating }.average() else 0.0
                onRatingCalculated(averageRating)
            }
            override fun onCancelled(error: DatabaseError) {
                // TODO Handle error
            }
        })
}