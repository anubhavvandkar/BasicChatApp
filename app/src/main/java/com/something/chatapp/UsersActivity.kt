package com.something.chatapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.something.chatapp.adapters.UserAdapter
import kotlinx.android.synthetic.main.activity_users.*
import java.util.*
import kotlin.collections.ArrayList

class UsersActivity : AppCompatActivity() {

    private lateinit var usersRecyclerView: RecyclerView
    private var users: ArrayList<String> = ArrayList()
    private var emails: ArrayList<String> = ArrayList()
    private var username: String = ""
    private var currEmail: String = ""
    private val auth = FirebaseAuth.getInstance()
    private val fireStore = FirebaseFirestore.getInstance()
    private val fireStore1 = FirebaseFirestore.getInstance()
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        startProcess()
        context = this

        recycler_users.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = UserAdapter(emails, users)
        }
        recycler_users.addOnItemTouchListener(object: OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                startChat(position)
            }
        })
    }
    interface OnItemClickListener {
        fun onItemClicked(position: Int, view: View)
    }
    private fun RecyclerView.addOnItemTouchListener(onItemClickListener: UsersActivity.OnItemClickListener) {
        this.addOnChildAttachStateChangeListener(object: RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewDetachedFromWindow(view: View) {
                view.setOnClickListener(null)
            }
            override fun onChildViewAttachedToWindow(view: View) {
                view.setOnClickListener {
                    val holder = getChildViewHolder(view)
                    onItemClickListener.onItemClicked(holder.adapterPosition, view)
                }
            }
        })
    }
    private fun startProcess(){
        loadEmails()
    }
    private fun loadEmails(){
        fireStore.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                for(user in documents){
                    if(user.get("email").toString() != auth.currentUser?.email) {
                        users.add(user.get("username").toString())
                        emails.add(user.get("email").toString())
                        recycler_users.adapter?.notifyDataSetChanged()
                    }
                    else{
                        username = user.get("username").toString()
                        currEmail = user.get("email").toString()
                    }
                }
            }.addOnFailureListener { exception ->
                Log.i("Error getting documents: ", exception.toString())
            }
    }
    private fun startChat(position: Int){
        val intent = Intent(this, ChatActivity::class.java)
        var roomId: String

        fireStore.collection("users/"+auth.currentUser?.uid+"/chats")
            .whereEqualTo("email", emails[position])
            .get()
            .addOnSuccessListener {documents ->
                if(documents.size()>0){
                    for(document in documents){
                        intent.putExtra("ROOM_ID",document.get("roomID").toString())
                        intent.putExtra("RECEIVER_USER", users[position])
                        intent.putExtra("RECEIVER_EMAIL", emails[position])
                        finish()
                        startActivity(intent)
                    }
                }
                else{
                    roomId = UUID.randomUUID().toString()
                    val data = mapOf(
                        "email" to emails[position],
                        "username" to users[position],
                        "roomID" to roomId
                    )
                    storeRoomID(emails[position], roomId)
                    fireStore1.collection("users/"+auth.currentUser?.uid+"/chats")
                        .add(data)
                        .addOnSuccessListener {
                            intent.putExtra("ROOM_ID", data["roomID"])
                            intent.putExtra("RECEIVER_USER", users[position])
                            intent.putExtra("RECEIVER_EMAIL", emails[position])
                            finish()
                            startActivity(intent)
                            return@addOnSuccessListener
                        }
                        .addOnFailureListener{
                            Log.i("location", "Failure failure listener")
                            return@addOnFailureListener
                        }
                }
            }
    }
    private fun storeRoomID(email : String, roomId: String){
        val fireStore2 = FirebaseFirestore.getInstance()
        fireStore2.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { users ->
                val data = mapOf(
                    "email" to currEmail,
                    "username" to username,
                    "roomID" to roomId
                )
                for(user in users){
                    user.reference.collection("chats")
                        .add(data)
                }
            }
    }
}