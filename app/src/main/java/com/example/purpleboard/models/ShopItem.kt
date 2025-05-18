package com.example.purpleboard.models // Adjust package name

import com.google.gson.annotations.SerializedName

data class ShopItem(
    @SerializedName("item_id")
    val itemId: Int,

    @SerializedName("item_name")
    val itemName: String, // e.g., "30 Days ChatGPT Pro Code"

    @SerializedName("item_cost")
    val itemCost: Int, // e.g., 5

    @SerializedName("item_code_revealed")
    val itemCodeRevealed: String, // e.g., "N074-R34L-C0D3"

    @SerializedName("description") // The text to show on the button initially
    val description: String,

    // Client-side flag to manage UI state after purchase
    @Transient
    var isPurchasedByCurrentUser: Boolean = false,
    @Transient
    var revealedCodeText: String? = null // To store "N074-R34L-C0D3" after purchase for this item
)