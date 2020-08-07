package com.something.chatapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*
import kotlin.collections.ArrayList

data class ChatMessage(
    var text : String,
    var user : String,
    var timestamp: Timestamp
)

class ChatActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser
    private val fireStore = FirebaseFirestore.getInstance()
    private val chatMessages = ArrayList<ChatMessage>()
    private var chatRegistration: ListenerRegistration? = null
    private var roomId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        //checkUser()
        initList()
        setViewListeners()

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.title = intent.getStringExtra("RECEIVER_USER")
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        setSupportActionBar(toolbar)
    }
    private fun setViewListeners() {
        fabSend.setOnClickListener {
            sendChatMessage()
        }
    }
    private fun initList() {
        if (user == null)
            return
        messagesReyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ChatAdapter(chatMessages, user.uid)
        messagesReyclerView.adapter = adapter
        listenForChatMessages()
    }
    private fun listenForChatMessages() {
        roomId = intent.getStringExtra("ROOM_ID")
        if (roomId == null) {
            finish()
            return
        }
        chatRegistration = fireStore.collection("rooms")
            .document(roomId!!)
            .collection("messages")
            .addSnapshotListener { messageSnapshot, exception ->
                if (messageSnapshot == null || messageSnapshot.isEmpty)
                    return@addSnapshotListener
                chatMessages.clear()
                for (messageDocument in messageSnapshot.documents) {
                    chatMessages.add(
                        ChatMessage(
                            messageDocument["text"] as String,
                            messageDocument["user"] as String,
                            messageDocument["timestamp"] as Timestamp
                        ))
                }
                chatMessages.sortBy { it.timestamp }
                messagesReyclerView.adapter?.notifyDataSetChanged()
            }
    }

    override fun onBackPressed() {
        finish()
    }
    private fun sendChatMessage() {
        val message = chatEditText.text.toString()
        chatEditText.setText("")
        fireStore.collection("rooms").document(roomId!!).collection("messages")
            .add(mapOf(
                Pair("text", message),
                Pair("user", user?.uid),
                Pair("timestamp", Timestamp.now())
            ))
    }
    override fun onDestroy() {
        chatRegistration?.remove()
        super.onDestroy()
    }
}