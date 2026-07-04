package com.example.data.local

import androidx.room.*
import com.example.data.model.Chat
import com.example.data.model.Contact
import com.example.data.model.Message
import com.example.data.model.UserStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAllContacts(): Flow<List<Contact>>

    @Query("SELECT * FROM contacts WHERE name LIKE :query OR phoneNumber LIKE :query ORDER BY name ASC")
    fun searchContacts(query: String): Flow<List<Contact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<Contact>)

    @Delete
    suspend fun deleteContact(contact: Contact)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats ORDER BY lastMessageTime DESC")
    fun getAllChats(): Flow<List<Chat>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: Chat)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChats(chats: List<Chat>)

    @Query("UPDATE chats SET lastMessageText = :text, lastMessageTime = :time, unreadCount = unreadCount + :unreadIncrement WHERE id = :chatId")
    suspend fun updateLastMessage(chatId: String, text: String, time: Long, unreadIncrement: Int)

    @Query("UPDATE chats SET unreadCount = 0 WHERE id = :chatId")
    suspend fun markChatAsRead(chatId: String)

    @Delete
    suspend fun deleteChat(chat: Chat)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun getMessagesForChat(chatId: String): Flow<List<Message>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)

    @Query("UPDATE messages SET translatedText = :translatedText WHERE id = :messageId")
    suspend fun updateTranslation(messageId: Int, translatedText: String)

    @Query("DELETE FROM messages WHERE chatId = :chatId")
    suspend fun clearChatMessages(chatId: String)
}

@Dao
interface UserStatusDao {
    @Query("SELECT * FROM user_statuses ORDER BY timestamp DESC")
    fun getAllStatuses(): Flow<List<UserStatus>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatus(status: UserStatus)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatuses(statuses: List<UserStatus>)

    @Query("DELETE FROM user_statuses WHERE isMe = 1")
    suspend fun deleteMyStatuses()
}
