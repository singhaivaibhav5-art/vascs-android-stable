package com.veeransh.aifashion.enterprise.util

import com.veeransh.aifashion.enterprise.types.CartItem
import com.veeransh.aifashion.enterprise.types.UserPDPCouponItem
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object FinancialCalculator {
    
    data class CalculationResult(
        val subtotal: Double, // Original retail price * qty
        val productDiscount: Double, // Sum of product-level discounts
        val grossSubtotal: Double, // subtotal - productDiscount
        val cartDiscount: Double,
        val taxableAmount: Double, // grossSubtotal - cartDiscount (min 0)
        val gstAmount: Double,
        val netAmount: Double
    )

    fun calculate(items: List<CartItem>, cartCoupon: UserPDPCouponItem?): CalculationResult {
        var totalSubtotal = 0.0
        var totalProductDiscount = 0.0
        
        items.forEach { item ->
            val lineSubtotal = item.product.retailPrice * item.qty
            totalSubtotal += lineSubtotal
            
            val rawItemDiscount = when (item.appliedCoupon?.type) {
                "PERCENT" -> (item.product.retailPrice * (item.appliedCoupon.value / 100.0)).roundToInt().toDouble() * item.qty
                "FIXED" -> item.appliedCoupon.value * item.qty
                else -> 0.0
            }
            // EDGE CASE FIX: Cap product discount to line subtotal
            val effectiveItemDiscount = min(rawItemDiscount, max(0.0, lineSubtotal))
            totalProductDiscount += effectiveItemDiscount
        }
        
        val grossSubtotal = totalSubtotal - totalProductDiscount
        
        val rawCartDiscount = when (cartCoupon?.type) {
            "PERCENT" -> (grossSubtotal * (cartCoupon.value / 100.0)).roundToInt().toDouble()
            "FIXED" -> cartCoupon.value
            else -> 0.0
        }
        
        // EDGE CASE FIX: Cap cart discount to gross subtotal
        val effectiveCartDiscount = min(rawCartDiscount, max(0.0, grossSubtotal))
        
        val taxableAmount = grossSubtotal - effectiveCartDiscount
        val gstAmount = (taxableAmount * 0.05).roundToInt().toDouble()
        val netAmount = taxableAmount + gstAmount
        
        return CalculationResult(
            subtotal = totalSubtotal,
            productDiscount = totalProductDiscount,
            grossSubtotal = grossSubtotal,
            cartDiscount = effectiveCartDiscount,
            taxableAmount = taxableAmount,
            gstAmount = gstAmount,
            netAmount = netAmount
        )
    }
}
