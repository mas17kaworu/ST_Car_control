package com.longkai.stcarcontrol.st_exp.compose.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
  primary = ColorPrimaryLight,
  primaryVariant = ColorPrimaryDark,
  secondary = ColorAccent,
  background = backgroundDark,
//  onBackground =
)

private val LightColorPalette = lightColors(
  primary = ColorPrimary,
  primaryVariant = ColorPrimaryDark,
  secondary = ColorAccent,
  background = backgroundLight,

  /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun STCarTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
  val colors = if (true/*darkTheme*/) {
    DarkColorPalette
  } else {
    LightColorPalette
  }

  MaterialTheme(
    colors = colors,
    typography = Typography,
    shapes = Shapes,
    content = content
  )
}