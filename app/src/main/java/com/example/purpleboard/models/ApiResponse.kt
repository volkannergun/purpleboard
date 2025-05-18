import com.google.gson.annotations.SerializedName

// Hypothetical generic ApiResponse<T>
data class ApiResponse<T>(
    @SerializedName("status")
    val status: String,
    @SerializedName("data")
    val data: T?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("errorCode")
    val errorCode: Int?
)