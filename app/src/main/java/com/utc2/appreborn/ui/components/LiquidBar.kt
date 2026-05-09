package com.utc2.appreborn.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utc2.appreborn.R

data class NavItem(
    val id: Int,
    val icon: Int,
    val labelRes: Int = R.string.placeholder_none
)

private data class NavDimensions(
    val barHeight: Dp,
    val containerHeight: Dp,
    val ballSize: Dp,
    val ballRadius: Dp,
    val iconActiveSize: Dp,
    val iconNormalSize: Dp,
    val labelSizeSp: Float,
    val dipDepth: Dp,
    val scaleFactor: Float
)

@Composable
private fun rememberNavDimensions(maxWidth: Dp): NavDimensions {
    // Tự động điều chỉnh kích thước để tương thích nhiều màn hình
    return remember(maxWidth) {
        val scaleFactor = (maxWidth.value / 360f).coerceIn(0.8f, 1.4f)
        val barH = (56 * scaleFactor).dp

        NavDimensions(
            barHeight = barH,
            containerHeight = (85 * scaleFactor).dp,
            ballSize = (48 * scaleFactor).dp,
            ballRadius = ((48 * scaleFactor) / 2).dp,
            iconActiveSize = (22 * scaleFactor).dp,
            iconNormalSize = (18 * scaleFactor).dp,
            labelSizeSp = (8 * scaleFactor),
            dipDepth = (barH.value * 0.55f).dp,
            scaleFactor = scaleFactor
        )
    }
}

fun getLiquidShape(offset: Float, radius: Float, dipDepth: Float) = GenericShape { size, _ ->
    // Tạo hình dạng thanh điều hướng có vết lõm mềm mại
    val topY = 0f
    val curveWidth = radius * 2.2f

    moveTo(0f, topY)
    lineTo(offset - curveWidth, topY)
    // cubicTo dùng để vẽ các đường cong mượt tại vết lõm
    cubicTo(
        x1 = offset - curveWidth * 0.5f, y1 = topY,
        x2 = offset - radius * 1.2f, y2 = dipDepth,
        x3 = offset, y3 = dipDepth
    )
    cubicTo(
        x1 = offset + radius * 1.2f, y1 = dipDepth,
        x2 = offset + curveWidth * 0.5f, y2 = topY,
        x3 = offset + curveWidth, y3 = topY
    )
    lineTo(size.width, topY)
    lineTo(size.width, size.height)
    lineTo(0f, size.height)
    close()
}

fun setupLiquidBottomBar(composeView: ComposeView, onItemSelected: (Int) -> Unit) {
    // Thiết lập môi trường Compose để nhúng vào giao diện XML
    composeView.apply {
        setBackgroundColor(android.graphics.Color.TRANSPARENT)
        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
            ) {
                LiquidBottomNavigation(onItemSelected)
            }
        }
    }
}

@Composable
fun LiquidBottomNavigation(
    onItemSelected: (Int) -> Unit
) {
    // Khai báo danh sách các mục hiển thị trên thanh điều hướng
    val items = remember {
        listOf(
            NavItem(R.id.nav_home, R.drawable.ic_house, R.string.nav_label_home),
            NavItem(R.id.nav_register, R.drawable.ic_book_open, R.string.nav_label_register),
            NavItem(R.id.nav_schedule, R.drawable.ic_calendar, R.string.nav_label_schedule),
            NavItem(R.id.nav_result, R.drawable.ic_graduation_cap, R.string.nav_label_result),
            NavItem(R.id.nav_profile, R.drawable.ic_user, R.string.nav_label_profile)
        )
    }

    var selectedIndex by remember { mutableIntStateOf(0) }
    val navBgColor = colorResource(R.color.black)
    val iconActiveColor = colorResource(R.color.indicator_location)
    val iconNormalColor = colorResource(R.color.white)
    val labelNormalColor = colorResource(R.color.text_muted_light)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val dims = rememberNavDimensions(maxWidth = this.maxWidth)
        val density = LocalDensity.current
        // Tính toán tọa độ pixel để vẽ hình dạng chính xác nhất
        val itemWidthPx = with(density) { (this@BoxWithConstraints.maxWidth / items.size).toPx() }
        val dipDepthPx = with(density) { dims.dipDepth.toPx() }
        val radiusPx = with(density) { dims.ballSize.toPx() / 2f * 1.1f }

        // Tạo hiệu ứng di chuyển mượt khi chuyển đổi mục chọn
        val animX by animateFloatAsState(
            targetValue = selectedIndex * itemWidthPx + itemWidthPx / 2,
            animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
            label = "ball_x"
        )
        val animXDp = with(density) { animX.toDp() }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dims.containerHeight)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dims.barHeight)
                    .align(Alignment.BottomCenter)
                    .background(
                        color = navBgColor,
                        shape = getLiquidShape(animX, radiusPx, dipDepthPx)
                    )
            )

            Box(
                modifier = Modifier
                    .size(dims.ballSize)
                    .offset(
                        x = animXDp - dims.ballRadius,
                        // coerceAtLeast đảm bảo bóng không văng ra ngoài khung hình
                        y = (dims.containerHeight - dims.barHeight - dims.ballSize * 0.55f).coerceAtLeast(
                            0.dp
                        )
                    )
                    .background(navBgColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Tự động khởi chạy lại hiệu ứng mỗi khi thay đổi tab
                var trigger by remember { mutableStateOf(false) }
                LaunchedEffect(selectedIndex) { trigger = false; trigger = true }

                val transition = updateTransition(targetState = trigger, label = "icon_anim")
                val iconScale by transition.animateFloat(
                    transitionSpec = { tween(400) }, label = "scale"
                ) { if (it) 1.2f else 0.6f }

                val iconColor by transition.animateColor(
                    transitionSpec = { tween(400) }, label = "color"
                ) { if (it) iconActiveColor else iconNormalColor }

                Icon(
                    painter = painterResource(id = items[selectedIndex].icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(dims.iconActiveSize)
                        // Biến đổi kích thước icon để tạo cảm giác sinh động
                        .graphicsLayer { scaleX = iconScale; scaleY = iconScale },
                    tint = iconColor
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dims.barHeight)
                    .align(Alignment.BottomCenter)
            ) {
                items.forEachIndexed { index, item ->
                    val isSelected = selectedIndex == index
                    val label = stringResource(id = item.labelRes)
                    val showLabel = label != stringResource(R.string.placeholder_none)

                    val iconAlpha by animateFloatAsState(
                        targetValue = if (isSelected) 0f else 1f,
                        label = "icon_alpha"
                    )
                    val iconOffsetY by animateDpAsState(
                        targetValue = if (isSelected) (-14 * dims.scaleFactor).dp else 0.dp,
                        label = "icon_offset"
                    )
                    val labelAlpha by animateFloatAsState(
                        targetValue = if (isSelected) 0f else 1f,
                        label = "label_alpha"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            // Loại bỏ hiệu ứng gợn nước mặc định khi nhấn tab
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                selectedIndex = index
                                onItemSelected(item.id)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (!isSelected) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.offset(y = iconOffsetY)
                            ) {
                                Icon(
                                    painter = painterResource(id = item.icon),
                                    contentDescription = label,
                                    modifier = Modifier.size(dims.iconNormalSize),
                                    tint = iconNormalColor.copy(alpha = iconAlpha)
                                )
                                if (showLabel) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = label,
                                        color = labelNormalColor.copy(alpha = labelAlpha),
                                        fontSize = dims.labelSizeSp.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}