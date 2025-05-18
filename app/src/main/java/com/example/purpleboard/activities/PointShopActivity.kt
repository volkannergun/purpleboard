package com.example.purpleboard.activities // Adjust package name

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.purpleboard.R
import com.example.purpleboard.databinding.ActivityPointShopBinding
import com.example.purpleboard.models.ShopItem // Assuming you have a full ShopItem model
import com.example.purpleboard.network.RetrofitClient
import com.example.purpleboard.utils.SharedPreferencesHelper
import com.example.purpleboard.utils.hide
import com.example.purpleboard.utils.show
import com.example.purpleboard.utils.showToast
import kotlinx.coroutines.launch

class PointShopActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPointShopBinding
    private lateinit var prefsHelper: SharedPreferencesHelper
    private var currentStudentId: Int = -1
    private var currentUserPoints: Int = 0

    // For simplicity, hardcoding item ID for now.
    // In a real app, fetch items from server.
    private val chatGptItemId = 1 // Assume this is the ID in your ShopItems table

    private var isChatGptItemPurchased = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPointShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsHelper = SharedPreferencesHelper(this)
        currentStudentId = prefsHelper.getUserId()

        supportActionBar?.title = getString(R.string.point_shop_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (currentStudentId == -1) {
            showToast("User not logged in.")
            finish()
            return
        }

        loadInitialData() // Load user points and purchase status

        binding.buttonPurchaseItem1.setOnClickListener {
            if (!isChatGptItemPurchased) {
                purchaseItem(chatGptItemId, 5) // Item ID 1, Cost 5
            } else {
                showToast("Item already purchased.")
            }
        }
    }

    private fun loadInitialData() {
        showLoading(true)
        lifecycleScope.launch {
            // Fetch current user's points
            try {
                val userResponse = RetrofitClient.apiService.getStudentProfile(currentStudentId)
                if (userResponse.isSuccessful && userResponse.body() != null) {
                    currentUserPoints = userResponse.body()!!.pointsCurrent
                    binding.textViewCurrentUserPoints.text = "Your Points: $currentUserPoints"
                } else {
                    showToast("Could not load your points.")
                }
            } catch (e: Exception) {
                Log.e("PointShop", "Error fetching user points", e)
                showToast("Error loading points.")
            }

            // Check if item is already purchased
            try {
                val purchasesResponse = RetrofitClient.apiService.getStudentPurchases(currentStudentId)
                if (purchasesResponse.isSuccessful && purchasesResponse.body() != null) {
                    if (purchasesResponse.body()!!.contains(chatGptItemId)) {
                        isChatGptItemPurchased = true
                        updateUiForItem1Purchased()
                    }
                }
            } catch (e: Exception) {
                Log.e("PointShop", "Error fetching purchases", e)
                // Assume not purchased if check fails
            }
            showLoading(false)
        }
    }


    private fun purchaseItem(itemId: Int, itemCost: Int) {
        if (currentUserPoints < itemCost) {
            showToast("Not enough points to purchase this item.")
            return
        }

        showLoading(true)
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.purchaseShopItem(itemId, currentStudentId)
                if (response.isSuccessful) {
                    showToast(response.body()?.message ?: "Purchase successful!")
                    currentUserPoints -= itemCost // Deduct points locally for immediate UI update
                    binding.textViewCurrentUserPoints.text = "Your Points: $currentUserPoints"

                    if (itemId == chatGptItemId) {
                        isChatGptItemPurchased = true
                        updateUiForItem1Purchased()
                    }
                    // TODO: Invalidate user points in SharedPreferences or ProfileActivity if it caches them
                    // Or ProfileActivity can reload onResume
                } else {
                    showToast(response.errorBody()?.string() ?: response.message() ?: "Purchase failed.")
                }
            } catch (e: Exception) {
                showToast("Error during purchase: ${e.message}")
                Log.e("PointShop", "Purchase exception", e)
            } finally {
                showLoading(false)
            }
        }
    }

    private fun updateUiForItem1Purchased() {
        binding.textViewShopItem1Name.text = getString(R.string.chatgpt_pro_code_revealed)
        binding.textViewShopItem1Cost.visibility = View.GONE // Hide cost
        binding.buttonPurchaseItem1.text = "Purchased"
        binding.buttonPurchaseItem1.isEnabled = false
        binding.buttonPurchaseItem1.alpha = 0.7f
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBarPointShop.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonPurchaseItem1.isEnabled = !isLoading && !isChatGptItemPurchased
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}