package com.veeransh.aifashion.enterprise.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.veeransh.aifashion.enterprise.R
import com.veeransh.aifashion.enterprise.data.local.entity.ProductEntity
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VeeranshLogo(
    modifier: Modifier = Modifier,
    showSubtitle: Boolean = true,
    onAdminAccess: () -> Unit = {}
) {
    var tapCount by remember { mutableIntStateOf(0) }
    
    Row(
        modifier = modifier
            .combinedClickable(
                onClick = { 
                    tapCount++
                    if (tapCount >= 5) {
                        onAdminAccess()
                        tapCount = 0
                    }
                },
                onLongClick = {
                    onAdminAccess()
                },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.brand_name),
            contentDescription = "Veeransh Logo",
            modifier = Modifier.height(32.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = "VEERANSH",
                color = Color(0xFF2A1A1D),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Serif
            )
            if (showSubtitle) {
                Text(
                    text = "AI FASHION • V4",
                    color = Color(0xFF0A5C36),
                    fontSize = 11.sp,
                    letterSpacing = 2.sp, // 0.18em approx 2sp at 11sp
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun VeeranshSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
            Spacer(modifier = Modifier.width(12.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text("Search by Saree Name, SKU, Barcode...", color = Color.Gray, fontSize = 14.sp)
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            Icon(Icons.Default.Mic, contentDescription = "Voice Search", tint = Color(0xFF0A5C36))
        }
    }
}

@Composable
fun CategoryChips(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(listOf("All") + categories) { category ->
            val isSelected = category == selectedCategory
            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                label = { Text(category) },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFE9C46A),
                    selectedLabelColor = Color.Black,
                    containerColor = Color.White,
                    labelColor = Color.Gray
                ),
                border = null
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BannerSlider(
    modifier: Modifier = Modifier
) {
    val banners = listOf(
        BannerData("GRAND SAREE FESTIVAL", "Up to 70% OFF", Color(0xFF0A5C36)),
        BannerData("SILK SPECIAL", "Exclusive Banarasi", Color(0xFF5C4900)),
        BannerData("NEW ARRIVALS", "Festive Collection 2026", Color(0xFFB53C6E))
    )
    val pagerState = rememberPagerState(pageCount = { banners.size })

    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            val nextPage = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(20.dp))
        ) { page ->
            val banner = banners[page]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(banner.bgColor)
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = banner.title,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = banner.subtitle,
                            color = Color(0xFFE9C46A),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text("Shop Now", color = banner.bgColor, fontWeight = FontWeight.Bold)
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.Checkroom,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color.White.copy(alpha = 0.3f)
                    )
                }
            }
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(banners.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color(0xFF0A5C36) else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
    }
}

data class BannerData(val title: String, val subtitle: String, val bgColor: Color)

@Composable
fun SareeGridItem(
    product: ProductEntity,
    onClick: (ProductEntity) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f) // 4:5 ratio
            .clickable { onClick(product) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                AsyncImage(
                    model = product.image,
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "₹${product.retailPrice}",
                            color = Color(0xFF0A5C36),
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFE9C46A))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(product.sku.takeLast(4), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            
            // MOQ Badge for B2B
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF0A5C36))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                    .align(Alignment.TopStart)
            ) {
                Text("MOQ: 5", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun OrderSuccessDialog(orderId: String, total: Double, onDismiss: () -> Unit) {
    val date = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), color = Color.White) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Order Placed Successfully", fontWeight = FontWeight.Black, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(24.dp))
                
                Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp)).padding(16.dp)) {
                    Text("Order ID: $orderId", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Date: $date", fontSize = 12.sp, color = Color.Gray)
                    Text("Total Amount: ₹${total.toInt()}", fontWeight = FontWeight.Black, color = Color(0xFF0D5C36), fontSize = 16.sp)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Close")
                    }
                    Button(
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D5C36))
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share")
                    }
                }
            }
        }
    }
}
