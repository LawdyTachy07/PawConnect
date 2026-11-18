package com.example.pawconnect.business.listings.data

object PetLocationData {
    // Preset data for Malaysia States & Cities
    val malaysiaStates = listOf(
        "Selangor", "Kuala Lumpur", "Penang", "Johor", "Perak",
        "Kedah", "Malacca", "Negeri Sembilan", "Pahang", "Sabah",
        "Sarawak", "Kelantan", "Terengganu", "Perlis", "Putrajaya", "Labuan"
    )

    val malaysiaCitiesMap = mapOf(
        "Selangor" to listOf("Petaling Jaya", "Subang Jaya", "Shah Alam", "Puchong", "Klang", "Cyberjaya", "Rawang", "Kajang"),
        "Kuala Lumpur" to listOf("Bangsar", "Mont Kiara", "Cheras", "Kepong", "Bukit Bintang", "Setapak", "Ampang"),
        "Penang" to listOf("George Town", "Tanjong Bungah", "Bayan Lepas", "Butterworth", "Bukit Mertajam"),
        "Johor" to listOf("Johor Bahru", "Batu Pahat", "Muar", "Skudai", "Kluang", "Iskandar Puteri"),
        "Perak" to listOf("Ipoh", "Taiping", "Teluk Intan", "Kampar", "Sitiawan"),
        "Kedah" to listOf("Alor Setar", "Sungai Petani", "Kulim", "Langkawi"),
        "Malacca" to listOf("Melaka City", "Ayer Keroh", "Alor Gajah", "Jasin"),
        "Negeri Sembilan" to listOf("Seremban", "Port Dickson", "Nilai"),
        "Pahang" to listOf("Kuantan", "Temerloh", "Bentong", "Cameron Highlands"),
        "Sabah" to listOf("Kota Kinabalu", "Sandakan", "Tawau"),
        "Sarawak" to listOf("Kuching", "Miri", "Sibu", "Bintulu"),
        "Kelantan" to listOf("Kota Bharu", "Tanah Merah"),
        "Terengganu" to listOf("Kuala Terengganu", "Dungun"),
        "Perlis" to listOf("Kangar", "Arau"),
        "Putrajaya" to listOf("Precinct 1", "Precinct 8", "Precinct 11", "Precinct 15"),
        "Labuan" to listOf("Victoria")
    )

    val breedPresets = listOf(
        "Dog - Mixed Breed",
        "Cat - Domestic Shorthair",
        "Rabbit",
        "Hamster",
        "Bird",
        "Turtle / Tortoise",
        "Other"
    )
}