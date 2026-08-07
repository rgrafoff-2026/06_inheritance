data class Post(
    val id: Int,
    val ownerId: Int,
    val fromId: Int,
    val createdBy: Int,
    val date: Int,
    val text: String,

    val replyOwnerId: Int?,
    val replyPostId: Int?,

    val friendsOnly: Boolean,

    val copyright: String?,

    val postType: String,

    val signerId: Int?,

    val canPin: Boolean,
    val canDelete: Boolean,
    val canEdit: Boolean,
    val isPinned: Boolean,
    val markedAsAds: Boolean,
    val isFavorite: Boolean,

    val postponedId: Int?
)
object WallService {
    private var posts = emptyArray<Post>()

    fun add(post: Post): Post {
        val newId = maxOf(1, (posts.maxOfOrNull { it.id } ?: 0) + 1)

        val newPost = post.copy(id = newId)

        posts += newPost

        return newPost
    }

    fun update(post: Post): Boolean {
        val index = posts.indexOfFirst { it.id == post.id }

        if (index == -1) {
            return false
        }

        posts[index] = post

        return true
    }
}