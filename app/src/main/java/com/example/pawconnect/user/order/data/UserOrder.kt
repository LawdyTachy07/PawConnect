package com.example.pawconnect.user.order.data

enum class OrderStatus(val label: String) {
    PROCESSING("Processing"),
    SHIPPED("Shipped"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}

data class OrderItem(
    val productId: String,
    val productName: String,
    val price: Double,
    val quantity: Int
)

data class UserOrder(
    val orderId: String,
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val items: List<OrderItem>,
    val subtotal: Double,
    val commissionRate: Double = 0.05, // 5% platform commission
    val orderDate: String = "17 Nov 2026",
    val status: OrderStatus = OrderStatus.PROCESSING
) {
    val commissionAmount: Double get() = subtotal * commissionRate
    val partnerPayout: Double get() = subtotal - commissionAmount
}