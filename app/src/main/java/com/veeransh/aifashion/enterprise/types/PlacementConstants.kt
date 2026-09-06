package com.veeransh.aifashion.enterprise.types

data class PlacementType(
    val machineValue: String,
    val displayName: String
)

object PlacementConstants {
    val TYPES = listOf(
        PlacementType("homeHero", "Home Hero"),
        PlacementType("homeBanner", "Home Banner"),
        PlacementType("productGrid", "Product Grid"),
        PlacementType("category", "Category Page"),
        PlacementType("featured", "Featured"),
        PlacementType("newArrivals", "New Arrivals"),
        PlacementType("bestSellers", "Best Sellers"),
        PlacementType("adBanner", "Ad Banner")
    )

    val ASPECT_RATIOS = listOf("Original", "16:9", "1:1", "4:5", "3:4", "9:16")

    val CROP_MODES = listOf("CenterCrop", "Fit", "FillBounds")
}

data class PlacementTemplate(
    val placementType: String,
    val aspectRatio: String,
    val targetWidthPx: Int,
    val targetHeightPx: Int,
    val cropMode: String
)

object DefaultTemplates {
    val DEFAULTS = listOf(
        PlacementTemplate("homeHero", "16:9", 1920, 1080, "CenterCrop"),
        PlacementTemplate("homeBanner", "16:9", 1600, 900, "CenterCrop"),
        PlacementTemplate("productGrid", "4:5", 800, 1000, "CenterCrop"),
        PlacementTemplate("category", "4:5", 800, 1000, "CenterCrop"),
        PlacementTemplate("featured", "1:1", 1000, 1000, "CenterCrop"),
        PlacementTemplate("newArrivals", "4:5", 800, 1000, "CenterCrop"),
        PlacementTemplate("bestSellers", "4:5", 800, 1000, "CenterCrop"),
        PlacementTemplate("adBanner", "16:9", 1600, 900, "CenterCrop")
    )

    fun getSerializedDefaults(): String {
        val sb = StringBuilder("[")
        DEFAULTS.forEachIndexed { index, t ->
            sb.append("{")
            sb.append("\"placementType\":\"${t.placementType}\",")
            sb.append("\"aspectRatio\":\"${t.aspectRatio}\",")
            sb.append("\"targetWidthPx\":${t.targetWidthPx},")
            sb.append("\"targetHeightPx\":${t.targetHeightPx},")
            sb.append("\"cropMode\":\"${t.cropMode}\"")
            sb.append("}")
            if (index < DEFAULTS.size - 1) sb.append(",")
        }
        sb.append("]")
        return sb.toString()
    }
}
