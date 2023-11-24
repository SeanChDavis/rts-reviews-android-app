package edu.umsl.rtsreviews.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Define the typography to be used throughout the app. Only using the defaults!
// https://developer.android.com/jetpack/compose/designsystems/material3#typography
val Typography = Typography(
    titleSmall = TextStyle(
        fontWeight = FontWeight.ExtraBold, // I changed this from the default weight
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.ExtraBold, // I changed this from the default weight
        fontSize = 20.sp, // I changed this from the default size
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.ExtraBold, // I changed this from the default weight
        fontSize = 26.sp, // I changed this from the default size
    ),
)