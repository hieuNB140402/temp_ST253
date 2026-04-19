package com.conchoback.haingon.ui.choose_clothes_after.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.conchoback.haingon.core.extension.loadAccessory2DURL
import com.conchoback.haingon.core.extension.loadImage
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.data.model.clothes.AccessorySelectedModel
import com.conchoback.haingon.databinding.ItemCategoryAccessoryBinding

class CategoryAccessoryAdapter :
    ListAdapter<AccessorySelectedModel, CategoryAccessoryAdapter.CategoryAccessoryViewHolder>(CategoryAccessoryDiffCallback()) {

    var onItemClick: ((position: Int) -> Unit) = { _ -> }
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CategoryAccessoryViewHolder {
        return CategoryAccessoryViewHolder(ItemCategoryAccessoryBinding.inflate(LayoutInflater.from(p0.context), p0, false))
    }

    override fun onBindViewHolder(p0: CategoryAccessoryViewHolder, p1: Int) {
        p0.bind(getItem(p1))
    }

    inner class CategoryAccessoryViewHolder(val binding: ItemCategoryAccessoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AccessorySelectedModel) {
            binding.apply {
                vFocus.isVisible = item.isSelected

                val pathURL = loadAccessory2DURL(item.thumbnail)

                loadImage(pathURL, imvImage)

                root.tap { onItemClick.invoke(bindingAdapterPosition) }
            }
        }
    }

    class CategoryAccessoryDiffCallback : DiffUtil.ItemCallback<AccessorySelectedModel>() {
        override fun areItemsTheSame(p0: AccessorySelectedModel, p1: AccessorySelectedModel): Boolean {
            return p0.id == p1.id
        }

        override fun areContentsTheSame(p0: AccessorySelectedModel, p1: AccessorySelectedModel): Boolean {
            return p0 == p1
        }

    }
}