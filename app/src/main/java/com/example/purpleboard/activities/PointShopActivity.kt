package com.example.purpleboard.activities // Adjust package name

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.purpleboard.R
import com.example.purpleboard.databinding.ActivityPointShopBinding
// import com.example.purpleboard.models.ShopItem // Keep if you expand to fetch items dynamically
import com.example.purpleboard.network.RetrofitClient
import com.example.purpleboard.utils.SharedPreferencesHelper
import com.example.purpleboard.utils.hide // Assuming these are your extension functions
import com.example.purpleboard.utils.show // Assuming these are your extension functions
import com.example.purpleboard.utils.showToast
import kotlinx.coroutines.launch

class PointShopActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPointShopBinding
    private lateinit var prefsHelper: SharedPreferencesHelper
    private var currentStudentId: Int = -1
    private var currentUserPoints: Int = 0

    // For simplicity, hardcoding item ID for now.
    // In a real app, fetch items from server.
    private val chatGptItemId = 1 // Assume this is the ID for "30 Days ChatGPT Pro Code" in your ShopItems table
    private val chatGptItemCode = "N074-R34L-C0D3" // The code to reveal

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
                // Assuming the cost of chatGptItemId (ID=1) is 5 points.
                // In a real app, this cost would come from the server along with the item details.
                purchaseItem(chatGptItemId, 5)
            } else {
                showToast("Item already purchased.")
            }
        }
    }

    private fun loadInitialData() {
        showLoading(true)
        binding.textViewCurrentUserPoints.text = "Your Points: ..." // Placeholder while loading

        lifecycleScope.launch {
            var userPointsFetched = false
            var purchaseStatusChecked = false

            // Fetch current user's points
            try {
                // Assuming getStudentProfile also returns the userRole if needed elsewhere,
                // and current points.
                val userResponse = RetrofitClient.apiService.getStudentProfile(currentStudentId)
                if (userResponse.isSuccessful && userResponse.body() != null) {
                    currentUserPoints = userResponse.body()!!.pointsCurrent
                    binding.textViewCurrentUserPoints.text = "Your Points: $currentUserPoints"
                    userPointsFetched = true
                } else {
                    showToast("Could not load your points.")
                    Log.w("PointShop", "Failed to fetch user profile/points: ${userResponse.code()} - ${userResponse.message()}")
                }
            } catch (e: Exception) {
                Log.e("PointShop", "Error fetching user points", e)
                showToast("Error loading points.")
            }

            // Check if item is already purchased
            if (currentStudentId != -1) { // Ensure we have a valid student ID
                try {
                    // >>> MODIFICATION HERE: Pass currentStudentId for both path and header <<<
                    val purchasesResponse = RetrofitClient.apiService.getStudentPurchases(
                        currentStudentId, // studentIdInPath (for the URL)
                        currentStudentId  // authenticatedStudentId (for the X-Student-ID header)
                    )
                    if (purchasesResponse.isSuccessful && purchasesResponse.body() != null) {
                        if (purchasesResponse.body()!!.contains(chatGptItemId)) {
                            isChatGptItemPurchased = true
                            updateUiForItem1Purchased()
                        } else {
                            isChatGptItemPurchased = false // Explicitly set if not purchased
                            updateUiForItem1NotPurchased() // Ensure UI is in the correct state
                        }
                    } else {
                        Log.w("PointShop", "Could not fetch student purchases: ${purchasesResponse.code()} - ${purchasesResponse.message()}")
                        // If fetching purchases fails, assume not purchased for safety, or handle error more explicitly
                        isChatGptItemPurchased = false
                        updateUiForItem1NotPurchased()
                    }
                } catch (e: Exception) {
                    Log.e("PointShop", "Error fetching purchases", e)
                    isChatGptItemPurchased = false // Assume not purchased if check fails
                    updateUiForItem1NotPurchased()
                }
            }
            purchaseStatusChecked = true

            // Only hide loading after both attempts (points and purchase status)
            if (userPointsFetched || purchaseStatusChecked) { // Or simply after both calls regardless of success
                showLoading(false)
            }
        }
    }


    private fun purchaseItem(itemId: Int, itemCost: Int) {
        if (currentUserPoints < itemCost) {
            showToast("Not enough points to purchase this item.")
            return
        }

        showLoading(true) // Disable button while processing
        lifecycleScope.launch {
            try {
                // Ensure currentStudentId (the authenticated user) is passed for the header
                val response = RetrofitClient.apiService.purchaseShopItem(itemId, currentStudentId)
                if (response.isSuccessful) {
                    showToast(response.body()?.message ?: "Purchase successful!")
                    currentUserPoints -= itemCost
                    binding.textViewCurrentUserPoints.text = "Your Points: $currentUserPoints"

                    if (itemId == chatGptItemId) {
                        isChatGptItemPurchased = true
                        updateUiForItem1Purchased()
                    }
                    // Notify ProfileActivity to refresh points if it's in the back stack and resumed
                    // This could be done with a BroadcastReceiver, SharedViewModel, or simply by ProfileActivity reloading onResume.
                    // For simplicity, ProfileActivity.onResume already calls loadUserProfile().
                } else {
                    val errorMsg = response.errorBody()?.string() ?: response.message() ?: "Purchase failed."
                    showToast("Purchase failed: $errorMsg")
                    Log.e("PointShop", "Purchase API Error ${response.code()}: $errorMsg")
                }
            } catch (e: Exception) {
                showToast("Error during purchase: ${e.message}")
                Log.e("PointShop", "Purchase exception", e)
            } finally {
                showLoading(false) // Re-enable button
            }
        }
    }

    private fun updateUiForItem1Purchased() {
        binding.textViewShopItem1Name.text = getString(R.string.chatgpt_pro_code_button_text_default) // Ensure original name
        binding.textViewShopItem1Cost.visibility = View.GONE
        binding.textViewShopItem1RevealedCode.text = chatGptItemCode
        binding.textViewShopItem1RevealedCode.show() // Use your extension function
        binding.buttonPurchaseItem1.text = "Purchased"
        binding.buttonPurchaseItem1.isEnabled = false
        binding.buttonPurchaseItem1.alpha = 0.7f
    }

    // Added this method to reset UI if item is not purchased (e.g., after failed purchase check)
    private fun updateUiForItem1NotPurchased() {
        binding.textViewShopItem1Name.text = getString(R.string.chatgpt_pro_code_button_text_default)
        binding.textViewShopItem1Cost.visibility = View.VISIBLE // Show cost
        binding.textViewShopItem1Cost.text = getString(R.string.chatgpt_pro_code_button_points_default) // Set cost text
        binding.textViewShopItem1RevealedCode.hide() // Hide revealed code
        binding.buttonPurchaseItem1.text = "Purchase"
        binding.buttonPurchaseItem1.isEnabled = true
        binding.buttonPurchaseItem1.alpha = 1.0f
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBarPointShop.visibility = if (isLoading) View.VISIBLE else View.GONE
        // Only disable purchase button if not already purchased
        binding.buttonPurchaseItem1.isEnabled = !isLoading && !isChatGptItemPurchased
    }

    override fun onSupportNavigateUp(): Boolean {
        setResult(Activity.RESULT_OK) // Signal ProfileActivity that points might have changed
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        setResult(Activity.RESULT_OK) // Also signal on physical back press
        super.onBackPressed()
    }
}