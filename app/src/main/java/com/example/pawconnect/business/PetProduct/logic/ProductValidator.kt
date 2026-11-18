package com.example.pawconnect.business.PetProduct.logic

/**
 * Input validation rules for Module 5 (Product Listing Management).
 * Every function returns an error message, or null when the input is valid.
 */
object ProductValidator {

    const val MIN_NAME_LENGTH = 3
    const val MAX_NAME_LENGTH = 50
    const val MAX_DESCRIPTION_LENGTH = 150
    const val MAX_PRICE = 10_000.0
    const val MAX_STOCK = 9_999

    /** Set to false if you want to allow adding a product without a photo. */
    const val REQUIRE_PHOTO_ON_ADD = true

    private val priceFormat = Regex("""^\d+(\.\d{1,2})?$""")

    fun validatePhoto(hasPhoto: Boolean, isEditing: Boolean): String? =
        if (REQUIRE_PHOTO_ON_ADD && !isEditing && !hasPhoto) "Please upload a product photo." else null

    fun validateName(name: String, otherProductNames: List<String> = emptyList()): String? {
        val trimmed = name.trim()
        return when {
            trimmed.isEmpty() -> "Product name is required."
            trimmed.length < MIN_NAME_LENGTH -> "Product name must be at least $MIN_NAME_LENGTH characters."
            trimmed.length > MAX_NAME_LENGTH -> "Product name cannot exceed $MAX_NAME_LENGTH characters."
            otherProductNames.any { it.trim().equals(trimmed, ignoreCase = true) } ->
                "You already have a product called \"$trimmed\"."
            else -> null
        }
    }

    fun validateCategory(category: String): String? =
        if (category.isBlank()) "Please select a product category." else null

    fun validateDescription(description: String): String? {
        val trimmed = description.trim()
        return when {
            trimmed.isEmpty() -> "Product description is required."
            trimmed.length > MAX_DESCRIPTION_LENGTH ->
                "Description cannot exceed $MAX_DESCRIPTION_LENGTH characters."
            else -> null
        }
    }

    fun validatePrice(price: String): String? {
        val trimmed = price.trim()
        val value = trimmed.toDoubleOrNull()
        return when {
            trimmed.isEmpty() -> "Price is required."
            value == null -> "Price must be a number, e.g. 25.90."
            value <= 0.0 -> "Price must be more than RM 0.00."
            !priceFormat.matches(trimmed) -> "Price can only have up to 2 decimal places."
            value > MAX_PRICE -> "Price cannot be more than RM 10,000.00."
            else -> null
        }
    }

    fun validateStock(stock: String): String? = validateWholeNumber(
        input = stock,
        fieldName = "Stock quantity",
        min = 0
    )

    fun validateMinimumStock(minimumStock: String): String? = validateWholeNumber(
        input = minimumStock,
        fieldName = "Minimum stock level",
        min = 1
    )

    fun validateRestock(amount: String, currentStock: Int): String? {
        validateWholeNumber(amount, "Quantity", min = 1)?.let { return it }
        val added = amount.trim().toInt()
        return if (currentStock + added > MAX_STOCK) {
            "Total stock cannot exceed $MAX_STOCK units (you have $currentStock now)."
        } else null
    }

    private fun validateWholeNumber(input: String, fieldName: String, min: Int): String? {
        val trimmed = input.trim()
        val value = trimmed.toIntOrNull()
        return when {
            trimmed.isEmpty() -> "$fieldName is required."
            trimmed.contains('.') || trimmed.contains(',') -> "$fieldName must be a whole number."
            value == null -> if (trimmed.all { it.isDigit() }) "$fieldName cannot exceed $MAX_STOCK."
                             else "$fieldName must be a number."
            value < min -> if (min == 0) "$fieldName cannot be negative." else "$fieldName must be at least $min."
            value > MAX_STOCK -> "$fieldName cannot exceed $MAX_STOCK."
            else -> null
        }
    }
}
