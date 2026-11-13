package com.example.agricultureoffline.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agricultureoffline.R
import com.example.agricultureoffline.ui.theme.AgriColors

@Composable
fun FieldBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_agri_photo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x66041711),
                            Color(0xB3061C12),
                            Color(0xF005160F)
                        )
                    )
                )
        )
        BackgroundGlow(
            size = 240.dp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 12.dp, top = 48.dp)
        )
        BackgroundGlow(
            size = 200.dp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
        )
        BackgroundGlow(
            size = 260.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 96.dp)
        )
        content()
    }
}

@Composable
private fun BackgroundGlow(
    size: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0x30B8F5BF),
                        Color(0x1087C98F),
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(24.dp)
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed && onClick != null) 0.98f else 1f, label = "glassPress")

    Box(
        modifier = modifier
            .scale(scale)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xD9133525),
                        Color(0xC40C2418),
                        Color(0xB50A1E15)
                    )
                ),
                shape
            )
            .border(1.dp, AgriColors.GlassStroke, shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(interactionSource = interaction, indication = null, onClick = onClick)
                } else {
                    Modifier
                }
            )
            .padding(18.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), content = content)
    }
}

enum class HomeTab { Home, Scan, History, Profile }

@Composable
fun AgriBottomBar(
    selected: HomeTab,
    onSelect: (HomeTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(28.dp)
    Row(
        modifier = modifier
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .fillMaxWidth()
            .height(72.dp)
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xE40A2117),
                        Color(0xD70D2A1B),
                        Color(0xE40A2117)
                    )
                ),
                shape
            )
            .border(1.dp, AgriColors.GlassStroke, shape)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomItem(
            selected = selected == HomeTab.Home,
            filled = Icons.Filled.Home,
            outlined = Icons.Outlined.Home,
            label = stringResource(R.string.nav_home),
            onClick = { onSelect(HomeTab.Home) },
            modifier = Modifier.weight(1f)
        )
        BottomItem(
            selected = selected == HomeTab.Scan,
            filled = Icons.Filled.Eco,
            outlined = Icons.Outlined.Eco,
            label = stringResource(R.string.nav_scan),
            onClick = { onSelect(HomeTab.Scan) },
            modifier = Modifier.weight(1f)
        )
        BottomItem(
            selected = selected == HomeTab.History,
            filled = Icons.Filled.History,
            outlined = Icons.Outlined.History,
            label = stringResource(R.string.nav_history),
            onClick = { onSelect(HomeTab.History) },
            modifier = Modifier.weight(1f)
        )
        BottomItem(
            selected = selected == HomeTab.Profile,
            filled = Icons.Filled.GridView,
            outlined = Icons.Outlined.GridView,
            label = stringResource(R.string.nav_profile),
            onClick = { onSelect(HomeTab.Profile) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BottomItem(
    selected: Boolean,
    filled: ImageVector,
    outlined: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeShape = RoundedCornerShape(18.dp)
    Column(
        modifier = modifier
            .clip(activeShape)
            .background(
                if (selected) Color(0x26D9FFE0) else Color.Transparent,
                activeShape
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (selected) filled else outlined,
            contentDescription = label,
            tint = if (selected) AgriColors.Mint else AgriColors.TextMuted,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (selected) AgriColors.Mint else AgriColors.TextMuted,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
fun AnalysisLoadingOverlay() {
    val pulse = rememberInfiniteTransition(label = "analyzePulse")
    val scale by pulse.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "leafScale"
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xBF04150E)),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(modifier = Modifier.padding(32.dp)) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.Eco,
                    contentDescription = null,
                    tint = AgriColors.Mint,
                    modifier = Modifier
                        .size(56.dp)
                        .graphicsLayer { scaleX = scale; scaleY = scale }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.analyzing_title),
                    color = AgriColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.analyzing_subtitle),
                    color = AgriColors.TextSecondary,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun LanguageChips(
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth()) {
        listOf(
            "en" to stringResource(R.string.lang_english),
            "hi" to stringResource(R.string.lang_hindi),
            "pa" to stringResource(R.string.lang_punjabi)
        ).forEach { (code, label) ->
            val active = selected == code
            Text(
                text = label,
                color = if (active) AgriColors.ForestDeep else AgriColors.TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (active) {
                            Brush.horizontalGradient(
                                colors = listOf(AgriColors.Mint, Color(0xFFD8F6D8))
                            )
                        } else {
                            Brush.horizontalGradient(
                                colors = listOf(Color(0x26FFFFFF), Color(0x1AFFFFFF))
                            )
                        }
                    )
                    .border(1.dp, if (active) Color.Transparent else AgriColors.GlassStroke, CircleShape)
                    .clickable { onSelect(code) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}
