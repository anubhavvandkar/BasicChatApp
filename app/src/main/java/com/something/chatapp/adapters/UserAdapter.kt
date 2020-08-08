package com.something.chatapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.something.chatapp.R

class UserAdapter(private val emails: ArrayList<String>, private val users: ArrayList<String>): RecyclerView.Adapter<ChatsViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ChatsViewHolder(inflater, parent)
    }
    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        holder.bind(emails[position], users[position])
    }
    override fun getItemCount(): Int = emails.size
}

class ChatsViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.recycler_user, parent, false)) {
    private var Username: TextView? = null
    private var Email: TextView? = null

    init {
        Username = itemView.findViewById(R.id.mainText)
        Email = itemView.findViewById(R.id.subText)
    }
    fun bind(email: String, user: String) {
        Username?.text = user
        Email?.text = email
    }
}