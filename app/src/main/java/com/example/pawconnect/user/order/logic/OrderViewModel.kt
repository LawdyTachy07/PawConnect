package com.example.pawconnect.user.order.logic

import androidx.lifecycle.ViewModel
import com.example.pawconnect.user.order.data.OrderItem
import com.example.pawconnect.user.order.data.OrderStatus
import com.example.pawconnect.user.order.data.UserOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OrderViewModel : ViewModel() {
    private val _orders = MutableStateFlow(
        listOf(
            UserOrder(
                orderId = "PC-2381",
                customerName = "Aisha Rahman",
                customerPhone = "+6012-345 6789",
                deliveryAddress = "123, Jalan Tanjung Bungah, 11200 Penang",
                items = listOf(
                    OrderItem("food-1", "Daily Balance Kibble (2kg)", 48.00, 1),
                    OrderItem("toy-1", "Play Ball Duo", 18.00, 2)
                ),
                subtotal = 84.00,
                orderDate = "17 Nov 2026",
                status = OrderStatus.PROCESSING
            ),
            UserOrder(
                orderId = "PC-2340",
                customerName = "Marcus Lim",
                customerPhone = "+6016-889 1022",
                deliveryAddress = "88, Gurney Drive, 10250 George Town, Penang",
                items = listOf(
                    OrderItem("acc-2", "Orthopedic Pet Bed (M)", 95.00, 1)
                ),
                subtotal = 95.00,
                orderDate = "15 Nov 2026",
                status = OrderStatus.COMPLETED
            )
        )
    )
    val orders: StateFlow<List<UserOrder>> = _orders.asStateFlow()

    fun placeOrder(
        customerName: String,
        customerPhone: String,
        deliveryAddress: String,
        items: List<OrderItem>,
        onOrderCreated: (String) -> Unit
    ) {
        if (items.isEmpty()) return

        val subtotal = items.sumOf { it.price * it.quantity }
        val newOrderId = "PC-${(1000..9999).random()}"

        val newOrder = UserOrder(
            orderId = newOrderId,
            customerName = customerName,
            customerPhone = customerPhone,
            deliveryAddress = deliveryAddress,
            items = items,
            subtotal = subtotal,
            orderDate = "17 Nov 2026",
            status = OrderStatus.PROCESSING
        )

        _orders.update { listOf(newOrder) + it }
        onOrderCreated(newOrderId)
    }

    fun cancelOrder(orderId: String) {
        _orders.update { list ->
            list.map { order ->
                if (order.orderId == orderId && order.status == OrderStatus.PROCESSING) {
                    order.copy(status = OrderStatus.CANCELLED)
                } else {
                    order
                }
            }
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        _orders.update { list ->
            list.map { order ->
                if (order.orderId == orderId) order.copy(status = newStatus) else order
            }
        }
    }
}