package com.srggrch.composeworkshop.ui.compoments

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val TrackWidth = 56.dp
private val TrackStrokeWidth = 32.dp
private val ThumbDiameter = 24.dp

private val ThumbRippleRadius = 24.dp

private val HorizontalPadding = 4.dp

private val DefaultSwitchPadding = 2.dp
private val SwitchWidth = TrackWidth
private val SwitchHeight = TrackStrokeWidth
private val ThumbPathLength = TrackWidth - ThumbDiameter - HorizontalPadding

private val AnimationSpec = TweenSpec<Float>(durationMillis = 100)

private val ThumbDefaultElevation = 1.dp
private val ThumbPressedElevation = 6.dp
private const val SwitchPositionalThreshold = 0.7f

@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }

    val colors = SwitchDefaults.colors()
    val haptic = LocalHapticFeedback.current

    var isFirstLaunch by remember { mutableStateOf(true) }

    val minBound = with(LocalDensity.current) { HorizontalPadding.toPx() }
    val maxBound = with(LocalDensity.current) { ThumbPathLength.toPx() }
    // If we reach a bound and settle, we invoke onCheckedChange with the new value. If the user
    // does not update `checked`, we would now be in an invalid state. We keep track of the
    // the animation state through this, animating back to the previous value if we don't receive
    // a new checked value.
    var forceAnimationCheck by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val decay = rememberSplineBasedDecay<Float>()

    // TODO заменить на AnchoredDraggableState
    val transitionX = remember(maxBound) {
        Animatable(if (checked) maxBound else minBound).apply {
            updateBounds(minBound, maxBound)
        }
    }

    var currentTranslationState by remember { mutableStateOf(checked) }

    val draggableState = rememberDraggableState { dragAmount ->
        coroutineScope.launch {
            transitionX.snapTo(transitionX.value + dragAmount)
        }
    }

    val currentOnCheckedChange by rememberUpdatedState(onCheckedChange)
    val currentChecked by rememberUpdatedState(checked)

    // Анимация при смене состояния checked по нажатию
    LaunchedEffect(checked, forceAnimationCheck) {
        if (checked == currentTranslationState) return@LaunchedEffect

        transitionX.animateTo(
            if (checked) maxBound else minBound,
            animationSpec = TweenSpec<Float>(
                durationMillis = 100,
                easing = LinearEasing
            )
        )
    }

    // Обработка дрега свича
    LaunchedEffect(transitionX.value) {
        if (isFirstLaunch) {
            isFirstLaunch = false
            return@LaunchedEffect
        }

        if (transitionX.value == minBound) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)

            currentTranslationState = false
        } else if (transitionX.value == maxBound) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)

            currentTranslationState = true
        }
    }

    // Оповещение листенера при изменении состояния по дрегу
    LaunchedEffect(currentTranslationState) {
        snapshotFlow { currentTranslationState }
            .collectLatest { newValue ->
                if (currentChecked != newValue) {
                    currentOnCheckedChange?.invoke(newValue)
                    forceAnimationCheck = !forceAnimationCheck
                }
            }
    }

    val toggleableModifier =
        if (onCheckedChange != null) {
            Modifier.toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                enabled = enabled,
                role = Role.Switch,
                interactionSource = interactionSource,
                indication = null
            )
        } else {
            Modifier
        }

    Box(
        modifier
            .then(toggleableModifier)
            .draggable(
                state = draggableState,
                orientation = Orientation.Horizontal,
                enabled = enabled && onCheckedChange != null,
                interactionSource = interactionSource,
                startDragImmediately = false,
                onDragStopped = { velocity ->
                    val decayX = decay.calculateTargetValue(transitionX.value, velocity)
                    coroutineScope.launch {
                        val targetX =
                            if (decayX > maxBound * SwitchPositionalThreshold) maxBound else minBound

                        val canReachTarget = (decayX > targetX && targetX == maxBound)
                                || (decayX < targetX && targetX == minBound)

                        if (canReachTarget) {
                            transitionX.animateDecay(
                                initialVelocity = velocity,
                                animationSpec = decay
                            )
                        } else {
                            transitionX.animateTo(
                                targetValue = targetX,
                                initialVelocity = velocity,
                                animationSpec = AnimationSpec
                            )
                        }
                    }
                }
            )
            .wrapContentSize(Alignment.Center)
            .padding(DefaultSwitchPadding)
            .requiredSize(SwitchWidth, SwitchHeight)

    ) {
        SwitchImpl(
            checked = checked,
            enabled = enabled,
            colors = colors,
            thumbValue = { transitionX.value },
            interactionSource = interactionSource
        )
    }
}


@Composable
private fun BoxScope.SwitchImpl(
    checked: Boolean,
    enabled: Boolean,
    colors: SwitchColors,
    thumbValue: () -> Float,
    interactionSource: InteractionSource
) {
    val interactions = remember { mutableStateListOf<Interaction>() }
    var isPressed by remember { mutableStateOf(false) }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    interactions.add(interaction)
                    isPressed = true
                }

                is PressInteraction.Release -> {
                    interactions.remove(interaction.press)
                    isPressed = false
                }

                is PressInteraction.Cancel -> {
                    interactions.remove(interaction.press)
                    isPressed = false
                }

                is DragInteraction.Start -> {
                    interactions.add(interaction)
                    isPressed = true
                }

                is DragInteraction.Stop -> {
                    interactions.remove(interaction.start)
                    isPressed = false
                }

                is DragInteraction.Cancel -> {
                    interactions.remove(interaction.start)
                    isPressed = false
                }
            }
        }
    }

    val hasInteraction = interactions.isNotEmpty()
    val elevation = if (hasInteraction) {
        ThumbPressedElevation
    } else {
        ThumbDefaultElevation
    }

    val trackColor by colors.trackColor(enabled, checked, hasInteraction)
    val resolvedTrackColor by animateColorAsState(trackColor)

    Canvas(
        Modifier
            .align(Alignment.Center)
            .fillMaxSize()
    ) {
        drawTrack(resolvedTrackColor, TrackWidth.toPx(), TrackStrokeWidth.toPx())
    }
    val thumbColor = colors.thumbColor

    Spacer(
        Modifier
            .align(Alignment.CenterStart)
            .offset { IntOffset(thumbValue().roundToInt(), 0) }
            .indication(
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = false, radius = ThumbRippleRadius)
            )
            .requiredSize(ThumbDiameter)
            .shadow(elevation, CircleShape, clip = false)
            .background(thumbColor, CircleShape)
    )
}

private fun DrawScope.drawTrack(trackColor: Color, trackWidth: Float, strokeWidth: Float) {
    val strokeRadius = strokeWidth / 2
    drawLine(
        color = trackColor,
        start = Offset(strokeRadius, center.y),
        end = Offset(trackWidth - strokeRadius, center.y),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )
}


/**
 * Contains the default values used by [Switch]
 */
private object SwitchDefaults {
    @Composable
    fun colors(
        thumbColor: Color = Color(0xffffffff),
        checkedTrackColor: Color = Color(0xff00a739),
        checkedPressedTrackColor: Color = Color(0xff00822c),
        uncheckedTrackColor: Color = Color(0xffc2c9d2),
        uncheckedPressedTrackColor: Color = Color(0xff8593a5),
        disabledCheckedTrackColor: Color = Color(0xffbbedcc),
        disabledUncheckedTrackColor: Color = Color(0xffd5d8dc)
    ): SwitchColors = SwitchColors(
        thumbColor,
        checkedTrackColor,
        checkedPressedTrackColor,
        uncheckedTrackColor,
        uncheckedPressedTrackColor,
        disabledCheckedTrackColor,
        disabledUncheckedTrackColor
    )
}


@Immutable
private data class SwitchColors(
    private val _thumbColor: Color,
    private val checkedTrackColor: Color,
    private val checkedPressedTrackColor: Color,
    private val uncheckedTrackColor: Color,
    private val uncheckedPressedTrackColor: Color,
    private val disabledCheckedTrackColor: Color,
    private val disabledUncheckedTrackColor: Color,
) {
    val thumbColor: Color
        @Composable
        get() = _thumbColor

    @Composable
    fun trackColor(enabled: Boolean, checked: Boolean, pressed: Boolean): State<Color> {
        return rememberUpdatedState(
            if (enabled) {
                if (pressed) {
                    if (checked) checkedPressedTrackColor else uncheckedPressedTrackColor
                } else {
                    if (checked) checkedTrackColor else uncheckedTrackColor
                }
            } else {
                if (checked) disabledCheckedTrackColor else disabledUncheckedTrackColor
            }
        )
    }
}


@Preview
@Composable
private fun SwitchPreview(
    @PreviewParameter(SwtichParamsProvider::class) params: SwtichParamsProvider.Params
) {
    Switch(params.checked, {}, enabled = params.enabled)
}

private class SwtichParamsProvider : PreviewParameterProvider<SwtichParamsProvider.Params> {
    override val values: Sequence<Params>
        get() = sequenceOf(
            Params(checked = true, enabled = true),
            Params(checked = false, enabled = true),
            Params(checked = true, enabled = false),
            Params(checked = false, enabled = false)
        )

    data class Params(
        val checked: Boolean,
        val enabled: Boolean
    )
}