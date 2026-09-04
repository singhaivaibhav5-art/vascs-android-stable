package com.veeransh.aifashion.enterprise.constants

import com.veeransh.aifashion.enterprise.types.*

object SeedData {
    val INITIAL_PRODUCTS = listOf(
        Product("1", "BAN-001", "P-101", "Royal Banarasi Silk", "Banarasi", "Silk", "Zari", 10, "Free", "4:5", "Regular", listOf("Home"), listOf("COUP5"), listOf("https://images.unsplash.com/photo-1610030469915-9a88edc1c29a"), "QR101", false),
        Product("2", "BAN-002", "P-102", "Classic Katan Silk", "Banarasi", "Katan Silk", "Zari", 5, "Free", "4:5", "Regular", listOf("Home"), listOf("COUP10"), listOf("https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b"), "QR102", false),
        Product("3", "BAN-003", "P-103", "Gold Tissue Banarasi", "Banarasi", "Tissue Silk", "Heavy Zari", 8, "Free", "4:5", "Regular", listOf("Home"), listOf("COUP5"), listOf(""), "QR103", false),
        Product("4", "BAN-004", "P-104", "Silver Zari Organza", "Banarasi", "Organza", "Silver Zari", 12, "Free", "4:5", "Regular", listOf("Home"), listOf("COUP5"), listOf(""), "QR104", false),
        Product("5", "BAN-005", "P-105", "Butidar Silk Saree", "Banarasi", "Silk", "Buti Work", 15, "Free", "4:5", "Regular", listOf("Home"), listOf("COUP10"), listOf(""), "QR105", false),
        Product("6", "BAN-006", "P-106", "Tanchoi Silk Masterpiece", "Banarasi", "Tanchoi Silk", "Thread Work", 7, "Free", "4:5", "Regular", listOf("Home"), listOf("COUP5"), listOf(""), "QR106", false),
        Product("7", "BAN-007", "P-107", "Antique Zari Banarasi", "Banarasi", "Silk", "Antique Zari", 4, "Free", "4:5", "Regular", listOf("Home"), listOf("COUP10"), listOf(""), "QR107", false),
        Product("8", "BAN-008", "P-108", "Meenakari Banarasi", "Banarasi", "Silk", "Meenakari", 9, "Free", "4:5", "Regular", listOf("Home"), listOf("COUP5"), listOf(""), "QR108", false),
        Product("9", "BAN-009", "P-109", "Dupion Silk Banarasi", "Banarasi", "Dupion Silk", "Zari", 20, "Free", "4:5", "Regular", listOf("Home"), listOf("COUP5"), listOf(""), "QR109", false),
        Product("10", "BAN-010", "P-110", "Jamdani Banarasi", "Banarasi", "Silk", "Jamdani", 6, "Free", "4:5", "Regular", listOf("Home"), listOf("COUP10"), listOf(""), "QR110", false)
    )

    val CATEGORIES = listOf("Banarasi", "Kanjivaram", "Chanderi", "Cotton Silk", "Georgette", "Chiffon", "Net", "Organza", "Satin", "Paithani")

    val BANNERS = listOf(
        Banner("B1", "GRAND SAREE FESTIVAL", "Up to 70% OFF", "", "FESTIVAL"),
        Banner("B2", "SILK SPECIAL", "Exclusive Banarasi", "", "SILK"),
        Banner("B3", "NEW ARRIVALS", "Festive Collection 2026", "", "NEW"),
        Banner("B4", "WEDDING COLLECTION", "Be the Bride", "", "WEDDING"),
        Banner("B5", "DAILY WEAR", "Comfort Meets Style", "", "DAILY"),
        Banner("B6", "PARTY WEAR", "Shine Everywhere", "", "PARTY"),
        Banner("B7", "DESIGNER PICKS", "Handpicked for You", "", "DESIGNER"),
        Banner("B8", "CLEARANCE SALE", "Last Chance", "", "SALE"),
        Banner("B9", "BOUTIQUE SPECIAL", "Unique Designs", "", "BOUTIQUE"),
        Banner("B10", "ACCESSORIES", "Complete Your Look", "", "ACCESSORIES")
    )

    val DEALER_CATEGORIES = listOf("Silver", "Gold", "Platinum", "Diamond")
}
