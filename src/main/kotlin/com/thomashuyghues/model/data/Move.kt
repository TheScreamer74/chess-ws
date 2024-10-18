package com.thomashuyghues.model.data

import kotlinx.serialization.Serializable

@Serializable
data class Move(val toX: Int, val toY: Int, val fromX: Int, val fromY: Int)
