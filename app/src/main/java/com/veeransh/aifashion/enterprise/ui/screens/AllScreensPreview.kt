package com.veeransh.aifashion.enterprise.ui.preview

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.veeransh.aifashion.enterprise.data.local.entity.ProductEntity
import com.veeransh.aifashion.enterprise.ui.theme.VeeranshTheme
import com.veeransh.aifashion.enterprise.ui.screens.*
import com.veeransh.aifashion.enterprise.ui.admin.CODSettings
import com.veeransh.aifashion.enterprise.ui.shop.StylePartnerDashboardContent
import com.veeransh.aifashion.enterprise.data.local.entity.DealerWalletEntity
import com.veeransh.aifashion.enterprise.data.local.entity.WalletTransactionEntity
import com.veeransh.aifashion.enterprise.data.local.entity.OrderMasterEntity
import com.veeransh.aifashion.enterprise.ui.admin.AdminManagedUser

class ProductPreviewProvider : PreviewParameterProvider<List<ProductEntity>> {
    override val values = sequenceOf(
        listOf(
            ProductEntity(
                id = "1",
                name = "Banarasi Royal Silk",
                sku = "BRS-101",
                barcode = "",
                category = "Silk",
                brand = "Veeransh",
                fabric = "Silk",
                colour = "Royal Blue",
                size = "Free",
                hsn = "5007",
                gst = 5.0,
                purchasePrice = 3000.0,
                wholesalePrice = 4500.0,
                retailPrice = 5999.0,
                mrp = 7999.0,
                discount = 0.0,
                stock = 15,
                image = "",
                createdAt = "2026-08-28"
            ),
            ProductEntity(
                id = "2",
                name = "Kanjivaram Gold",
                sku = "KGS-202",
                barcode = "",
                category = "Silk",
                brand = "Veeransh",
                fabric = "Silk",
                colour = "Gold",
                size = "Free",
                hsn = "5007",
                gst = 5.0,
                purchasePrice = 4500.0,
                wholesalePrice = 6500.0,
                retailPrice = 8500.0,
                mrp = 10000.0,
                discount = 0.0,
                stock = 8,
                image = "",
                createdAt = "2026-08-28"
            )
        )
    )
}

fun sampleCartItems() = listOf(
    CartCheckoutItem(
        product = ProductEntity(
            id = "1", name = "Banarasi Katan Rose Blush", sku = "BAN-001",
            retailPrice = 1000.0, category = "Silk", brand = "Veeransh",
            barcode = "", colour = "Rose", createdAt = "", discount = 0.0,
            fabric = "Silk", gst = 5.0, hsn = "", image = "",
            mrp = 1500.0, purchasePrice = 500.0, size = "Free", stock = 10, wholesalePrice = 800.0
        ),
        qty = 1,
        appliedCoupon = UserPDPCouponItem("COUP5", "5% OFF", "PERCENT", 5.0)
    )
)

@Preview(name = "Home Screen Light", showBackground = true, showSystemUi = true)
@Composable
fun FullHomeScreenPreview(
    @PreviewParameter(ProductPreviewProvider::class) products: List<ProductEntity>
) {
    CompositionLocalProvider(LocalInspectionMode provides true) {
        VeeranshTheme {
            HomeScreenContent(
                products = products,
                onProductClick = { _ -> },
                onAdminAccess = {},
                onToolClick = {}
            )
        }
    }
}

@Preview(name = "Admin Dashboard Preview", showBackground = true, showSystemUi = true, device = "spec:width=1280dp,height=800dp")
@Composable
fun AdminDashboardPreview() {
    CompositionLocalProvider(LocalInspectionMode provides true) {
        VeeranshTheme {
            AdminDashboardScreenContent(onLogout = {}, isLive = false)
        }
    }
}

@Preview(name = "All Screens - Move To Display Page Preview", device = "spec:width=1280dp,height=800dp", showBackground = true)
@Composable
fun AllScreensMoveToDisplayPreview() {
    CompositionLocalProvider(LocalInspectionMode provides true) {
        VeeranshTheme {
            MoveToDisplayContent(
                onSaveDraft = {},
                onPublish = { _ -> },
                onBack = {}
            )
        }
    }
}

@Preview(name = "All Screens - User PDP Preview", device = "spec:width=1280dp,height=800dp", showBackground = true)
@Composable
fun AllScreensUserPDPPreview() {
    CompositionLocalProvider(LocalInspectionMode provides true) {
        VeeranshTheme {
            UserPDPDetailContent(
                product = ProductEntity(
                    id = "VEER-P-2026-0001",
                    name = "Banarasi Katan Silk - Rose Blush",
                    sku = "VEER-SKU-BAN-0001",
                    barcode = "8908001123456",
                    category = "Banarasi",
                    brand = "Veeransh",
                    fabric = "Pure Katan Silk",
                    colour = "Rose Blush",
                    size = "Free Size",
                    hsn = "5007",
                    gst = 5.0,
                    purchasePrice = 5000.0,
                    wholesalePrice = 7500.0,
                    retailPrice = 9999.0,
                    mrp = 12500.0,
                    discount = 0.0,
                    stock = 15,
                    image = "",
                    location = "productGrid|homeBanner",
                    tags = "coupons:COUP5|COUP10",
                    createdAt = "2026-08-28"
                ),
                onBack = {},
                onAddToCart = { _, _ -> }
            )
        }
    }
}

@Preview(name = "All Screens - EWallet Preview", device = "spec:width=1280dp,height=800dp", showBackground = true)
@Composable
fun AllScreensEWalletPreview() {
    CompositionLocalProvider(LocalInspectionMode provides true) {
        VeeranshTheme {
            EWalletScreenContent(
                wallet = DealerWalletEntity(
                    dealerId = "DLR-001",
                    dealerCode = "VEER-DLR-001",
                    balanceAvailable = 12450.50,
                    balancePending = 2350.0
                ),
                transactions = emptyList()
            )
        }
    }
}

@Preview(name = "All Screens - Admin Control Preview", device = "spec:width=1280dp,height=800dp", showBackground = true)
@Composable
fun AllScreensAdminControlPreview() {
    CompositionLocalProvider(LocalInspectionMode provides true) {
        VeeranshTheme {
            AdminControlScreen(onBack = {})
        }
    }
}

@Preview(name = "Style Partner Dashboard Preview", device = "spec:width=1280dp,height=800dp", showBackground = true)
@Composable
fun StylePartnerDashboardPreview() {
    CompositionLocalProvider(LocalInspectionMode provides true) {
        VeeranshTheme {
            StylePartnerDashboardContent(
                wallet = DealerWalletEntity(
                    dealerId = "DLR-001",
                    dealerCode = "VEER-DLR-001",
                    balanceAvailable = 12450.50,
                    balancePending = 2350.0
                ),
                transactions = emptyList()
            )
        }
    }
}

@Preview(name = "Orders Screen Preview", device = "spec:width=1280dp,height=800dp", showBackground = true)
@Composable
fun OrdersScreenPreview() {
    CompositionLocalProvider(LocalInspectionMode provides true) {
        VeeranshTheme {
            OrdersScreenContent(
                orders = listOf(
                    OrderMasterEntity(
                        orderId = 1L,
                        orderNumber = "ORD-2026-001",
                        dealerId = "DLR-001",
                        dealerName = "Mock Dealer",
                        mobile = "9876543210",
                        whatsapp = "9876543210",
                        orderDate = "2026-08-30",
                        totalItems = 2,
                        totalQty = 2,
                        totalAmount = 10000.0,
                        gstAmount = 500.0,
                        netAmount = 10500.0,
                        status = "PENDING",
                        createdDate = "2026-08-30",
                        updatedDate = "2026-08-30"
                    )
                ),
                onOrderClick = {}
            )
        }
    }
}

@Preview(name = "COD Settings Preview", device = "spec:width=1280dp,height=800dp", showBackground = true)
@Composable
fun CODSettingsPreview() {
    CompositionLocalProvider(LocalInspectionMode provides true) {
        VeeranshTheme {
            CODSettings()
        }
    }
}

@Preview(name = "Cart & Checkout Preview", device = "spec:width=1280dp,height=800dp", showBackground = true)
@Composable
fun CartCheckoutPreview() {
    CompositionLocalProvider(LocalInspectionMode provides true) {
        VeeranshTheme {
            CartCheckoutContent(
                cartItems = sampleCartItems(),
                onNavigateBack = {},
                onOrderPlaced = {}
            )
        }
    }
}
