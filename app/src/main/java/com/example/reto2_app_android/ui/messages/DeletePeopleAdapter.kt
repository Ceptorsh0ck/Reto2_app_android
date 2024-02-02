package com.example.reto2_app_android.ui.messages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reto2_app_android.data.AddPeople
import com.example.reto2_app_android.databinding.PopupListDeleteItemBinding


class DeletePeopleAdapter() :
    ListAdapter<AddPeople, DeletePeopleAdapter.DeletePeopleViewHolder>(DeletePeopleDiffCallback()) {
    private lateinit var recyclerView: RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeletePeopleViewHolder {
        val binding = PopupListDeleteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeletePeopleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeletePeopleViewHolder, position: Int) {
        val people = getItem(position)
        holder.bind(people)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    inner class DeletePeopleViewHolder(private val binding: PopupListDeleteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(people: AddPeople) {
            val email = people.email
            val atIndex = email.indexOf('@')
            val textToShow = if (atIndex != -1) email.substring(0, atIndex) else email

            binding.emailCheckBox.text = textToShow
            binding.idTextView.text = people.userId.toString()
        }
    }


    class DeletePeopleDiffCallback : DiffUtil.ItemCallback<AddPeople>() {

        override fun areItemsTheSame(oldItem: AddPeople, newItem: AddPeople): Boolean {
            // TODO
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: AddPeople,
            newItem: AddPeople
        ): Boolean {
            // TODO
            return oldItem == newItem
        }

    }
}