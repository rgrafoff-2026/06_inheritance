data class Comments(
    val count: Int = 0,
    val canPost: Boolean = true,
    val groupsCanPost: Boolean = false,
    val canClose: Boolean = false,
    val canOpen: Boolean = false
)

data class Likes(
    val count: Int = 0,
    val userLikes: Boolean = false,
    val canLike: Boolean = true,
    val canPublish: Boolean = true
)

data class Copyright(
    val id: Int = 0,
    val name: String = "",
    val link: String = ""
)

data class Photo(
    val id: Int,
    val ownerId: Int,
    val photo130: String = "",
    val photo604: String = ""
)

data class Video(
    val id: Int,
    val ownerId: Int,
    val title: String = "",
    val duration: Int = 0
)

data class Audio(
    val id: Int,
    val ownerId: Int,
    val artist: String = "",
    val title: String = "",
    val duration: Int = 0
)

data class Doc(
    val id: Int,
    val ownerId: Int,
    val title: String = "",
    val size: Int = 0,
    val ext: String = ""
)

data class Link(
    val url: String,
    val title: String = "",
    val caption: String = "",
    val description: String = ""
)

sealed class Attachment(val type: String)

data class PhotoAttachment(val photo: Photo) : Attachment("photo")
data class VideoAttachment(val video: Video) : Attachment("video")
data class AudioAttachment(val audio: Audio) : Attachment("audio")
data class DocAttachment(val doc: Doc) : Attachment("doc")
data class LinkAttachment(val link: Link) : Attachment("link")

data class Post(
    val id: Int = 0,
    val ownerId: Int = 0,
    val fromId: Int = 0,
    val createdBy: Int = 0,
    val date: Int = 0,
    val text: String = "",
    val replyOwnerId: Int? = null,
    val replyPostId: Int? = null,
    val copyright: Copyright? = null,
    val signerId: Int? = null,
    val postponedId: Int? = null,
    val accessKey: String? = null,
    val friendsOnly: Boolean = false,
    val postType: String = "post",
    val canPin: Boolean = false,
    val canDelete: Boolean = true,
    val canEdit: Boolean = true,
    val isPinned: Boolean = false,
    val markedAsAds: Boolean = false,
    val isFavorite: Boolean = false,
    val comments: Comments = Comments(),
    val likes: Likes = Likes(),
    val hash: String = "",
    val isArchived: Boolean = false,
    val isExplicit: Boolean = false,
    val viewsCount: Int = 0,
    val repostsCount: Int = 0,
    val toId: Int = 0,
    val shortTextRate: Int = 0,
    val donutIsPaid: Boolean = false,
    val isPaid: Boolean = false,
    val attachments: Array<Attachment> = emptyArray()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Post) return false

        if (id != other.id) return false
        if (text != other.text) return false
        if (replyOwnerId != other.replyOwnerId) return false
        if (!attachments.contentEquals(other.attachments)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + text.hashCode()
        result = 31 * result + (replyOwnerId ?: 0)
        result = 31 * result + attachments.contentHashCode()
        return result
    }
}

object WallService {
    private var posts = emptyArray<Post>()
    private var nextId = 1

    fun add(post: Post): Post {
        val newPost = post.copy(id = nextId)
        nextId++
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

    fun clear() {
        posts = emptyArray()
        nextId = 1
    }
}

fun testAdd() {
    WallService.clear()
    val post = Post(text = "Тестовая запись на стене")
    val addedPost = WallService.add(post)

    check(addedPost.id != 0) { "ID поста должен быть больше 0" }
    println("✅ testAdd() пройден")
}

fun testUpdateExisting() {
    WallService.clear()
    val originalPost = WallService.add(Post(text = "Оригинальный текст"))
    val updatePost = originalPost.copy(text = "Обновленный текст")

    val result = WallService.update(updatePost)

    check(result) { "Обновление существующего поста должно возвращать true" }
    println("✅ testUpdateExisting() пройден")
}

fun testUpdateNonExisting() {
    WallService.clear()
    val nonExistingPost = Post(id = 9999, text = "Пост, которого нет")

    val result = WallService.update(nonExistingPost)

    check(!result) { "Обновление несуществующего поста должно возвращать false" }
    println("✅ testUpdateNonExisting() пройден")
}

fun testAddWithAttachments() {
    WallService.clear()

    val photo = Photo(id = 1, ownerId = 1, photo130 = "url1", photo604 = "url2")
    val video = Video(id = 2, ownerId = 1, title = "Video", duration = 30)

    val postWithMedia = Post(
        text = "Пост с медиа",
        replyOwnerId = 123,
        attachments = arrayOf(
            PhotoAttachment(photo),
            VideoAttachment(video)
        )
    )

    val addedPost = WallService.add(postWithMedia)

    check(addedPost.id != 0) { "ID поста должен быть больше 0" }
    check(addedPost.attachments.size == 2) { "Должно быть добавлено 2 вложения" }
    check(addedPost.attachments[0].type == "photo") { "Первое вложение должно быть фото" }
    check(addedPost.attachments[1].type == "video") { "Второе вложение должно быть видео" }
    check(addedPost.replyOwnerId == 123) { "Nullable поле должно сохраниться" }

    println("✅ testAddWithAttachments() пройден")
}

fun main() {
    println("Запуск автотестов...")

    testAdd()
    testUpdateExisting()
    testUpdateNonExisting()
    testAddWithAttachments()

    println("\n🎉 Все тесты пройдены успешно!")
}