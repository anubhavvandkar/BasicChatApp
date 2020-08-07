package com.something.chatapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_list_receiver.view.*

class ChatAdapter(private val chatMessages: List<ChatMessage>, private val uid: String): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent)
    }
    override fun getItemCount(): Int {
        return chatMessages.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatMessage = chatMessages[position]
        if (chatMessage.user == uid) {
            holder.itemView.textViewSender.text = chatMessage.text
            holder.itemView.receiverCardView.visibility = View.GONE
        } else {
            holder.itemView.textViewReceiver.text = chatMessage.text
            holder.itemView.senderCardView.visibility = View.GONE
        }
    }
    class ViewHolder(inflater: LayoutInflater, parent: ViewGroup): RecyclerView.ViewHolder(
        inflater.inflate(R.layout.recycler_list_receiver, parent, false)
    ) {
        private var chatTextSent: TextView? = null
        private var chatTextReceived: TextView? = null
        init {
            chatTextSent = itemView.findViewById(R.id.textViewSender)
            chatTextReceived = itemView.findViewById(R.id.textViewReceiver)
        }
    }
}