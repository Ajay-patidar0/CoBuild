package com.example.cobuild.ui.project.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val PrimaryColor = Color(0xFF4F46E5)
private val UnselectedBg = Color(0xFFE2E8F0)
private val TextSelected = Color.White
private val TextUnselected = Color(0xFF64748B)

@Composable
fun ProjectTabSwitcher(
    selectedTab: Int,
    onTabChange: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        color = UnselectedBg.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabItem(
                text = "Joined Projects",
                isSelected = selectedTab == 0,
                modifier = Modifier.weight(1f),
                onClick = { onTabChange(0) }
            )
            TabItem(
                text = "Posted Projects",
                isSelected = selectedTab == 1,
                modifier = Modifier.weight(1f),
                onClick = { onTabChange(1) }
            )
        }
    }
}

@Composable
private fun TabItem(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) PrimaryColor else Color.Transparent,
        animationSpec = tween(300),
        label = "BgAnim"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) TextSelected else TextUnselected,
        animationSpec = tween(300),
        label = "TextAnim"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}