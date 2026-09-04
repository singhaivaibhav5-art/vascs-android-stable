package com.veeransh.aifashion.enterprise.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.veeransh.aifashion.enterprise.data.local.entity.ProductEntity
import com.veeransh.aifashion.enterprise.ui.components.*
import com.veeransh.aifashion.enterprise.ui.viewmodel.HomeViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onProductClick: (ProductEntity) -> Unit,
    onAdminAccess: () -> Unit,
    onToolClick: (String) -> Unit,
    isAdmin: Boolean = false,
    isDealer: Boolean = false,
    onAddSareeClick: () -> Unit = {}
) {
    val products by viewModel.filteredProducts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.addSampleIfEmpty()
    }

    HomeScreenContent(
        products = products,
        searchQuery = searchQuery,
        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
        onProductClick = onProductClick,
        onAdminAccess = onAdminAccess,
        onToolClick = onToolClick,
        isAdmin = isAdmin,
        isDealer = isDealer,
        onAddSareeClick = onAddSareeClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    products: List<ProductEntity>,
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    onProductClick: (ProductEntity) -> Unit,
    onAdminAccess: () -> Unit,
    onToolClick: (String) -> Unit,
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
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Wishlist", tint = Color.White)
                    }
                    BadgedBox(
                        badge = { Badge { Text("3") } },
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        IconButton(onClick = {}) {
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

            item {
                Spacer(modifier = Modifier.height(16.dp))
                BannerSlider(
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
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
                if (filteredProducts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No Products - Add from Draping",
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
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
