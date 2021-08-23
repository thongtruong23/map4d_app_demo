package com.truongthong.map4d.model.nearby

data class ResultNearby(
    val address: String,
    val id: String,
    val location: Location,
    val name: String,
    val types: List<String>
)