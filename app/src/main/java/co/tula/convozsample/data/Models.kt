package co.tula.convozsample.data

import com.google.gson.annotations.SerializedName

data class GifObject(
    val id: String,
    val url: String,
    val images: Images
)

data class Images(
    @SerializedName("fixed_height") val fixedHeight: FixedHeightImage
)

data class FixedHeightImage(
    val url: String,
    val width: Int,
    val height: Int
)

data class GiphyResponse<T>(
    val data: T
)