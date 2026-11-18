package com.example.pawconnect.user.order.logic

object CheckoutValidator {

    private val nameFormat = Regex("""^[A-Za-z][A-Za-z\s.'@/-]*$""")

    // Malaysian mobile: 01X-XXXXXXX(X) or +601X-XXXXXXX(X); spaces and dashes are ignored
    private val malaysianMobile = Regex("""^(\+?60|0)1\d{8,9}$""")

    fun validateName(name: String): String? {
        val trimmed = name.trim()
        return when {
            trimmed.isEmpty() -> "Recipient name is required."
            trimmed.length < 3 -> "Name must be at least 3 characters."
            trimmed.length > 60 -> "Name cannot exceed 60 characters."
            !nameFormat.matches(trimmed) -> "Name can only contain letters and spaces."
            else -> null
        }
    }

    fun validatePhone(phone: String): String? {
        val digits = phone.trim().replace(" ", "").replace("-", "")
        return when {
            digits.isEmpty() -> "Phone number is required."
            !malaysianMobile.matches(digits) -> "Enter a valid Malaysian mobile number, e.g. 012-3456789."
            else -> null
        }
    }

    fun validateAddress(address: String): String? {
        val trimmed = address.trim()
        return when {
            trimmed.isEmpty() -> "Delivery address is required."
            trimmed.length < 10 -> "Please enter your full address (street, postcode, city)."
            trimmed.length > 200 -> "Address cannot exceed 200 characters."
            else -> null
        }
    }
}
