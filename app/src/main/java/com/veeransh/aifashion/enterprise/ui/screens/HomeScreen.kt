package com.veeransh.aifashion.enterprise.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.veeransh.aifashion.enterprise.data.local.entity.ProductEntity
import com.veeransh.aifashion.enterprise.ui.components.*
import com.veeransh.aifashion.enterprise.ui.viewmodel.HomeViewModel
import com.veeransh.aifashion.enterprise.ui.viewmodel.PlacementWithProduct
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onProductClick: (ProductEntity) -> Unit,
    onAdminAccess: () -> Unit,
    onToolClick: (String) -> Unit,
    onCartClick: () -> Unit,
    isAdmin: Boolean = false,
    isDealer: Boolean = false,
    onAddSareeClick: () -> Unit = {}
) {
    val products by viewModel.filteredProducts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val activePlacements by viewModel.activePlacements.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.addSampleIfEmpty()
    }

    HomeScreenContent(
        products = products,
        activePlacements = activePlacements,
        searchQuery = searchQuery,
        cartCount = cartItems.sumOf { it.qty },
        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
        onProductClick = onProductClick,
        onAdminAccess = onAdminAccess,
        onToolClick = onToolClick,
        onCartClick = onCartClick,
        isAdmin = isAdmin,
        isDealer = isDealer,
        onAddSareeClick = onAddSareeClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    products: List<ProductEntity>,
    activePlacements: List<PlacementWithProduct>,
    searchQuery: String = "",
    cartCount: Int = 0,
    onSearchQueryChange: (String) -> Unit = {},
    onProductClick: (ProductEntity) -> Unit,
    onAdminAccess: () -> Unit,
    onToolClick: (String) -> Unit,
    onCartClick: () -> Unit = {},
    isAdmin: Boolean = false,
    isDealer: Boolean = false,
    onAddSareeClick: () -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredProducts = remember(products, selectedCategory) {
        products.filter { product ->
            val matchesCategory = (selectedCategory == "All" || product.category == selectedCategory)
            val isVisible = product.stock > 0 || isAdmin || isDealer // Show all to admin/dealer, hide out of stock from public gallery
            
            matchesCategory && isVisible
        }
    }

    val heroPlacements = remember(activePlacements) {
        activePlacements.filter { it.placement.placementType == "homeHero" }
    }

    val bannerPlacements = remember(activePlacements) {
        activePlacements.filter { it.placement.placementType == "homeBanner" }
    }

    val gridPlacements = remember(activePlacements) {
        activePlacements.filter { it.placement.placementType == "productGrid" }
    }

    val featuredPlacements = remember(activePlacements) {
        activePlacements.filter { it.placement.placementType == "featured" }
    }

    val newArrivalPlacements = remember(activePlacements) {
        activePlacements.filter { it.placement.placementType == "newArrivals" }
    }

    val bestSellerPlacements = remember(activePlacements) {
        activePlacements.filter { it.placement.placementType == "bestSellers" }
    }

    val adPlacements = remember(activePlacements) {
        activePlacements.filter { it.placement.placementType == "adBanner" }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    VeeranshLogo(
                        modifier = Modifier.padding(vertical = 8.dp),
                        onAdminAccess = onAdminAccess
                    )
                },
                actions = {
                    BadgedBox(
                        badge = { if (cartCount > 0) Badge { Text(cartCount.toString()) } },
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        IconButton(onClick = onCartClick) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0A5C36)
                )
            )
        },
        floatingActionButton = {
            if (isAdmin || isDealer) {
                ExtendedFloatingActionButton(
                    onClick = onAddSareeClick,
                    containerColor = Color(0xFF5B4CFF),
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Add Saree") }
                )
            }
        },
        containerColor = Color(0xFFF5F1E8)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                VeeranshSearchBar(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                CategoryChips(
                    categories = listOf("Silk", "Cotton", "Banarasi", "Daily Wear", "Georgette"),
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
            }

            // Hero Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                if (heroPlacements.isNotEmpty()) {
                    BannerSlider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        placements = heroPlacements,
                        onProductClick = onProductClick
                    )
                } else {
                    BannerSlider(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "ERP MANAGEMENT TOOLS",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0A5C36)
                )
                ERPToolsGrid(onToolClick = onToolClick)
            }

            // Featured Section
            if (featuredPlacements.isNotEmpty()) {
                item {
                    HorizontalPlacementSection(
                        title = "FEATURED SELECTION",
                        placements = featuredPlacements,
                        onProductClick = onProductClick
                    )
                }
            }

            // New Arrivals Section
            if (newArrivalPlacements.isNotEmpty()) {
                item {
                    HorizontalPlacementSection(
                        title = "NEW ARRIVALS",
                        placements = newArrivalPlacements,
                        onProductClick = onProductClick
                    )
                }
            }

            // Ad Banners (Phase 2.5)
            if (adPlacements.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    BannerSlider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        placements = adPlacements,
                        onProductClick = onProductClick
                    )
                }
            }

            // Best Sellers Section
            if (bestSellerPlacements.isNotEmpty()) {
                item {
                    HorizontalPlacementSection(
                        title = "BEST SELLERS",
                        placements = bestSellerPlacements,
                        onProductClick = onProductClick
                    )
                }
            }

            // homeBanner Section (Additional Banners)
            if (bannerPlacements.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "FEATURED DEALS",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0A5C36)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    BannerSlider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        placements = bannerPlacements,
                        onProductClick = onProductClick
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DEALER B2B CATALOGUE",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0A5C36)
                    )
                    TextButton(onClick = {}) {
                        Text("View All", color = Color(0xFF0A5C36))
                    }
                }
            }

            // Product Grid
            item {
                if (gridPlacements.isNotEmpty() && searchQuery.isEmpty() && selectedCategory == "All") {
                    // Driven by placements
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .heightIn(max = 2000.dp)
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        userScrollEnabled = false
                    ) {
                        items(gridPlacements) { pw ->
                            PlacementGridItem(pw, onProductClick)
                        }
                    }
                } else if (filteredProducts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No Products Matching Filters",
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // Legacy/Filtered Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .heightIn(max = 2000.dp)
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        userScrollEnabled = false
                    ) {
                        items(filteredProducts) { product ->
                            SareeGridItem(product = product, onClick = onProductClick)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HorizontalPlacementSection(
    title: String,
    placements: List<PlacementWithProduct>,
    onProductClick: (ProductEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF0A5C36)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(placements) { pw ->
                Box(modifier = Modifier.width(180.dp)) {
                    PlacementGridItem(pw, onProductClick)
                }
            }
        }
    }
}

@Composable
fun PlacementGridItem(
    pw: PlacementWithProduct,
    onClick: (ProductEntity) -> Unit
) {
    val placement = pw.placement
    val product = pw.product
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(
                when (placement.aspectRatio) {
                    "16:9" -> 16f / 9f
                    "1:1" -> 1f
                    "4:5" -> 4f / 5f
                    "3:4" -> 3f / 4f
                    "9:16" -> 9f / 16f
                    else -> 0.8f // Default approx 4:5
                }
            )
            .clickable { onClick(product) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = if (placement.imageUri.isNotBlank()) placement.imageUri else product.image,
                contentDescription = product.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = when (placement.cropMode) {
                    "CenterCrop" -> ContentScale.Crop
                    "Fit" -> ContentScale.Fit
                    "FillBounds" -> ContentScale.FillBounds
                    else -> ContentScale.Crop
                }
            )
            
            // Info Overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(8.dp)
            ) {
                Text(
                    text = if (placement.title.isNotBlank()) placement.title else product.name,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "₹${product.retailPrice}",
                    color = Color(0xFFE9C46A),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
