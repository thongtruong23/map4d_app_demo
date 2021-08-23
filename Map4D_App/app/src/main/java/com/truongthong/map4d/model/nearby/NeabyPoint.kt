package com.truongthong.map4d.model.nearby

data class NeabyPoint(
    val code: String,
    val result: MutableList<ResultNearby>
)