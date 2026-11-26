package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.OliBeakGold
import com.example.ui.theme.OliCheekBlush
import com.example.ui.theme.OliCreamBelly
import com.example.ui.theme.OliEyeDark
import com.example.ui.theme.OliMintFace
import com.example.ui.theme.OliTurquoiseBody
import com.example.ui.theme.OliTurquoiseShade
import kotlinx.coroutines.delay

enum class OwlExpression {
    HAPPY,
    THINKING,
    SURPRISED,
    CELEBRATING,
    ENCOURAGING,
}

@Composable
fun OwlMascot(
    expression: OwlExpression,
    modifier: Modifier = Modifier,
    size: Dp = 128.dp,
    avatarVariant: Int = 0,
    contentDescriptionText: String = "Oli the Owl",
) {
    val infiniteTransition = rememberInfiniteTransition(label = "owl_idle")
    val bobOffset by infiniteTransition.animateFloat(
        initialValue = -0.02f,
        targetValue = 0.025f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "owl_bob",
    )

    val celebrationBounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -0.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 520, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "owl_celebrate",
    )

    var isBlinking by remember { mutableStateOf(false) }
    LaunchedEffect(expression) {
        while (true) {
            delay(3200L)
            isBlinking = true
            delay(130L)
            isBlinking = false
        }
    }

    val headTilt by animateFloatAsState(
        targetValue = when (expression) {
            OwlExpression.THINKING -> -7f
            OwlExpression.ENCOURAGING -> 4f
            OwlExpression.SURPRISED -> -3f
            else -> 0f
        },
        animationSpec = spring(stiffness = 260f),
        label = "owl_tilt",
    )

    val wingLift by animateFloatAsState(
        targetValue = when (expression) {
            OwlExpression.CELEBRATING -> 1f
            OwlExpression.ENCOURAGING -> 0.55f
            OwlExpression.SURPRISED -> 0.45f
            OwlExpression.THINKING -> 0.3f
            OwlExpression.HAPPY -> 0.1f
        },
        animationSpec = spring(stiffness = 220f),
        label = "wing_lift",
    )

    val bodyPrimary = when (avatarVariant) {
        1 -> Color(0xFF0D9488)
        2 -> Color(0xFF06B6D4)
        3 -> Color(0xFF14B8A6)
        else -> OliTurquoiseBody
    }
    val bodyShade = when (avatarVariant) {
        1 -> Color(0xFF115E59)
        2 -> Color(0xFF0E7490)
        else -> OliTurquoiseShade
    }

    Canvas(
        modifier = modifier
            .size(size)
            .semantics { contentDescription = contentDescriptionText }
    ) {
        val w = this.size.width
        val h = this.size.height
        val verticalShift = h * (if (expression == OwlExpression.CELEBRATING) celebrationBounce else bobOffset)

        // Soft ground shadow
        drawOval(
            color = Color.Black.copy(alpha = 0.10f),
            topLeft = Offset(w * 0.24f, h * 0.89f),
            size = Size(w * 0.52f, h * 0.08f),
        )

        rotate(degrees = headTilt, pivot = Offset(w * 0.5f, h * 0.55f)) {
            val topY = verticalShift

            // Left & Right Wings
            val leftWingAngle = 14f + wingLift * 46f
            rotate(degrees = leftWingAngle, pivot = Offset(w * 0.24f, h * 0.54f + topY)) {
                drawOval(
                    color = bodyShade,
                    topLeft = Offset(w * 0.09f, h * 0.48f + topY),
                    size = Size(w * 0.20f, h * 0.30f),
                )
            }
            val rightWingAngle = -(14f + wingLift * 46f)
            rotate(degrees = rightWingAngle, pivot = Offset(w * 0.76f, h * 0.54f + topY)) {
                drawOval(
                    color = bodyShade,
                    topLeft = Offset(w * 0.71f, h * 0.48f + topY),
                    size = Size(w * 0.20f, h * 0.30f),
                )
            }

            // Feet
            val footY = h * 0.84f + topY
            drawRoundRect(
                color = OliBeakGold,
                topLeft = Offset(w * 0.33f, footY),
                size = Size(w * 0.12f, h * 0.06f),
                cornerRadius = CornerRadius(w * 0.04f, w * 0.04f),
            )
            drawRoundRect(
                color = OliBeakGold,
                topLeft = Offset(w * 0.55f, footY),
                size = Size(w * 0.12f, h * 0.06f),
                cornerRadius = CornerRadius(w * 0.04f, w * 0.04f),
            )

            // Main Body Silhouette
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(bodyPrimary, bodyShade),
                    startY = h * 0.18f + topY,
                    endY = h * 0.88f + topY,
                ),
                topLeft = Offset(w * 0.18f, h * 0.19f + topY),
                size = Size(w * 0.64f, h * 0.68f),
                cornerRadius = CornerRadius(w * 0.32f, w * 0.34f),
            )

            // Signature Ear Tufts
            val leftTuft = Path().apply {
                moveTo(w * 0.22f, h * 0.28f + topY)
                quadraticTo(w * 0.14f, h * 0.12f + topY, w * 0.26f, h * 0.10f + topY)
                quadraticTo(w * 0.34f, h * 0.15f + topY, w * 0.38f, h * 0.24f + topY)
                close()
            }
            val rightTuft = Path().apply {
                moveTo(w * 0.78f, h * 0.28f + topY)
                quadraticTo(w * 0.86f, h * 0.12f + topY, w * 0.74f, h * 0.10f + topY)
                quadraticTo(w * 0.66f, h * 0.15f + topY, w * 0.62f, h * 0.24f + topY)
                close()
            }
            drawPath(leftTuft, color = OliMintFace)
            drawPath(rightTuft, color = OliMintFace)

            // Cream Belly Patch + Feather Chevrons
            drawOval(
                color = OliCreamBelly,
                topLeft = Offset(w * 0.29f, h * 0.54f + topY),
                size = Size(w * 0.42f, h * 0.30f),
            )
            val chevronStroke = Stroke(width = w * 0.022f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            for (row in 0..1) {
                val cy = h * (0.63f + row * 0.085f) + topY
                val chevron = Path().apply {
                    moveTo(w * 0.38f, cy)
                    lineTo(w * 0.44f, cy + h * 0.035f)
                    lineTo(w * 0.50f, cy)
                    lineTo(w * 0.56f, cy + h * 0.035f)
                    lineTo(w * 0.62f, cy)
                }
                drawPath(chevron, color = bodyShade.copy(alpha = 0.45f), style = chevronStroke)
            }

            // Variant 3: Cozy Coral Scarf
            if (avatarVariant == 3) {
                drawRoundRect(
                    color = Color(0xFFF97316),
                    topLeft = Offset(w * 0.24f, h * 0.50f + topY),
                    size = Size(w * 0.52f, h * 0.075f),
                    cornerRadius = CornerRadius(w * 0.04f, w * 0.04f),
                )
            }

            // Facial Disc (Two Overlapping Mint Circles)
            drawCircle(
                color = OliMintFace,
                radius = w * 0.175f,
                center = Offset(w * 0.37f, h * 0.37f + topY),
            )
            drawCircle(
                color = OliMintFace,
                radius = w * 0.175f,
                center = Offset(w * 0.63f, h * 0.37f + topY),
            )

            // Cheek Blush
            drawOval(
                color = OliCheekBlush.copy(alpha = 0.35f),
                topLeft = Offset(w * 0.22f, h * 0.44f + topY),
                size = Size(w * 0.09f, h * 0.05f),
            )
            drawOval(
                color = OliCheekBlush.copy(alpha = 0.35f),
                topLeft = Offset(w * 0.69f, h * 0.44f + topY),
                size = Size(w * 0.09f, h * 0.05f),
            )

            // Eyes
            val leftEyeCenter = Offset(w * 0.37f, h * 0.37f + topY)
            val rightEyeCenter = Offset(w * 0.63f, h * 0.37f + topY)
            val eyeRadius = w * 0.11f

            drawCircle(color = Color.White, radius = eyeRadius, center = leftEyeCenter)
            drawCircle(color = Color.White, radius = eyeRadius, center = rightEyeCenter)

            if (isBlinking) {
                val blinkStroke = Stroke(width = w * 0.028f, cap = StrokeCap.Round)
                drawLine(
                    color = OliEyeDark,
                    start = Offset(leftEyeCenter.x - eyeRadius * 0.6f, leftEyeCenter.y),
                    end = Offset(leftEyeCenter.x + eyeRadius * 0.6f, leftEyeCenter.y),
                    strokeWidth = blinkStroke.width,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = OliEyeDark,
                    start = Offset(rightEyeCenter.x - eyeRadius * 0.6f, rightEyeCenter.y),
                    end = Offset(rightEyeCenter.x + eyeRadius * 0.6f, rightEyeCenter.y),
                    strokeWidth = blinkStroke.width,
                    cap = StrokeCap.Round,
                )
            } else if (expression == OwlExpression.CELEBRATING) {
                // Joyful upward curved happy eyes
                val happyStroke = Stroke(width = w * 0.03f, cap = StrokeCap.Round)
                listOf(leftEyeCenter, rightEyeCenter).forEach { center ->
                    val arcPath = Path().apply {
                        moveTo(center.x - eyeRadius * 0.55f, center.y + eyeRadius * 0.15f)
                        quadraticTo(
                            center.x,
                            center.y - eyeRadius * 0.6f,
                            center.x + eyeRadius * 0.55f,
                            center.y + eyeRadius * 0.15f,
                        )
                    }
                    drawPath(arcPath, color = OliEyeDark, style = happyStroke)
                }
            } else {
                val pupilRadius = when (expression) {
                    OwlExpression.SURPRISED -> eyeRadius * 0.45f
                    else -> eyeRadius * 0.62f
                }
                val pupilOffset = when (expression) {
                    OwlExpression.THINKING -> Offset(eyeRadius * 0.18f, -eyeRadius * 0.18f)
                    else -> Offset.Zero
                }
                listOf(leftEyeCenter, rightEyeCenter).forEach { center ->
                    val pCenter = center + pupilOffset
                    drawCircle(color = OliEyeDark, radius = pupilRadius, center = pCenter)
                    drawCircle(
                        color = Color.White,
                        radius = pupilRadius * 0.34f,
                        center = Offset(pCenter.x + pupilRadius * 0.32f, pCenter.y - pupilRadius * 0.32f),
                    )
                }
            }

            // Variant 1: Scholar Glasses
            if (avatarVariant == 1) {
                val frameStroke = Stroke(width = w * 0.02f)
                drawCircle(color = OliEyeDark, radius = eyeRadius * 1.15f, center = leftEyeCenter, style = frameStroke)
                drawCircle(color = OliEyeDark, radius = eyeRadius * 1.15f, center = rightEyeCenter, style = frameStroke)
                drawLine(
                    color = OliEyeDark,
                    start = Offset(leftEyeCenter.x + eyeRadius * 1.15f, leftEyeCenter.y),
                    end = Offset(rightEyeCenter.x - eyeRadius * 1.15f, rightEyeCenter.y),
                    strokeWidth = w * 0.02f,
                )
            }

            // Variant 2: Tiny Golden Crown
            if (avatarVariant == 2) {
                val crown = Path().apply {
                    moveTo(w * 0.40f, h * 0.19f + topY)
                    lineTo(w * 0.42f, h * 0.09f + topY)
                    lineTo(w * 0.47f, h * 0.14f + topY)
                    lineTo(w * 0.50f, h * 0.07f + topY)
                    lineTo(w * 0.53f, h * 0.14f + topY)
                    lineTo(w * 0.58f, h * 0.09f + topY)
                    lineTo(w * 0.60f, h * 0.19f + topY)
                    close()
                }
                drawPath(crown, color = OliBeakGold)
            }

            // Golden Diamond Beak
            if (expression == OwlExpression.SURPRISED) {
                drawOval(
                    color = OliBeakGold,
                    topLeft = Offset(w * 0.455f, h * 0.415f + topY),
                    size = Size(w * 0.09f, h * 0.095f),
                )
                drawOval(
                    color = Color(0xFF78350F),
                    topLeft = Offset(w * 0.472f, h * 0.44f + topY),
                    size = Size(w * 0.056f, h * 0.05f),
                )
            } else {
                val beak = Path().apply {
                    moveTo(w * 0.50f, h * 0.39f + topY)
                    lineTo(w * 0.56f, h * 0.445f + topY)
                    lineTo(w * 0.50f, h * 0.515f + topY)
                    lineTo(w * 0.44f, h * 0.445f + topY)
                    close()
                }
                drawPath(beak, color = OliBeakGold)
            }
        }

        // Expression Overlays: Sparkles (Celebrating) or Thought Dots (Thinking)
        if (expression == OwlExpression.CELEBRATING) {
            val sparkleColor = OliBeakGold
            listOf(
                Offset(w * 0.14f, h * 0.20f) to w * 0.045f,
                Offset(w * 0.86f, h * 0.18f) to w * 0.05f,
                Offset(w * 0.89f, h * 0.40f) to w * 0.032f,
            ).forEach { (pt, r) ->
                val star = Path().apply {
                    moveTo(pt.x, pt.y - r)
                    lineTo(pt.x + r * 0.35f, pt.y - r * 0.35f)
                    lineTo(pt.x + r, pt.y)
                    lineTo(pt.x + r * 0.35f, pt.y + r * 0.35f)
                    lineTo(pt.x, pt.y + r)
                    lineTo(pt.x - r * 0.35f, pt.y + r * 0.35f)
                    lineTo(pt.x - r, pt.y)
                    lineTo(pt.x - r * 0.35f, pt.y - r * 0.35f)
                    close()
                }
                drawPath(star, color = sparkleColor)
            }
        } else if (expression == OwlExpression.THINKING) {
            drawCircle(color = OliMintFace, radius = w * 0.025f, center = Offset(w * 0.80f, h * 0.25f))
            drawCircle(color = OliMintFace, radius = w * 0.038f, center = Offset(w * 0.86f, h * 0.18f))
            drawCircle(color = OliMintFace, radius = w * 0.052f, center = Offset(w * 0.91f, h * 0.09f))
        }
    }
}
