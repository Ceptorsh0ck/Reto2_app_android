package com.example.reto2_app_android.ui.messages

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.reto2_app_android.R
import com.example.reto2_app_android.data.AddPeople
import com.example.reto2_app_android.databinding.PopupListItemBinding


class AddPeopleAdapter() :
    ListAdapter<AddPeople, AddPeopleAdapter.AddPeopleViewHolder>(AddPeopleDiffCallback()) {
    private lateinit var recyclerView: RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddPeopleViewHolder {
        val binding = PopupListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddPeopleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddPeopleViewHolder, position: Int) {
        val people = getItem(position)
        holder.bind(people)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    inner class AddPeopleViewHolder(private val binding: PopupListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(people: AddPeople) {
            binding.emailCheckBox.text = people.mail
            binding.adminCheckBox.text = "Admin"
        }
    }


    class AddPeopleDiffCallback : DiffUtil.ItemCallback<AddPeople>() {

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