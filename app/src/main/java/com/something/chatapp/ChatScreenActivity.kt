package com.something.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_chat_screen.*
import kotlinx.android.synthetic.main.activity_users.*

class ChatScreenActivity : AppCompatActivity() {

    private lateinit var bottomBar: BottomAppBar
    private var users: ArrayList<String> = ArrayList()
    private var emails: ArrayList<String> = ArrayList()
    private lateinit var fab: FloatingActionButton
    private val fireStore = FirebaseFirestore.getInstance()
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private var auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_screen)

        loadEmails()

        fab=findViewById(R.id.fab)
        bottomBar=findViewById(R.id.bottomAppBar)
        setSupportActionBar(bottomBar)

        recycler_chat.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = UserAdapter(emails, users)
        }
        recycler_chat.addOnItemTouchListener(object: OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                startChat(position)
            }
        })
    }
    interface OnItemClickListener {
        fun onItemClicked(position: Int, view: View)
    }
    private fun RecyclerView.addOnItemTouchListener(onItemClickListener: OnItemClickListener) {
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
    override fun onStart() {
        recycler_chat.adapter?.notifyDataSetChanged()
        super.onStart()
    }
    override fun onResume() {
        recycler_chat.adapter?.notifyDataSetChanged()
        super.onResume()
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.chat_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.bottom_log_out) {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onBackPressed() {
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
        super.onBackPressed()
    }
    fun startUsersActivity(view : View){
        val intent = Intent(this, UsersActivity::class.java)
        startActivity(intent)
    }
    private fun loadEmails(){
        fireStore.collection("users/"+auth.currentUser?.uid+"/chats")
            .get()
            .addOnSuccessListener { user ->
                for(name in user){
                    emails.add(name.get("email").toString())
                    users.add(name.get("username").toString())
                    recycler_chat.adapter?.notifyDataSetChanged()
                }
            }
    }
    private fun startChat(position : Int){
        val intent = Intent(this, ChatActivity::class.java)
        fireStore.collection("users/"+auth.currentUser?.uid+"/chats")
            .whereEqualTo("email", emails[position])
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    intent.putExtra("ROOM_ID", document.get("roomID").toString())
                    intent.putExtra("RECEIVER_USER", users[position])
                    startActivity(intent)
                }
            }
    }
}