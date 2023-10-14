package posts.data.dto

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
): Parcelable
