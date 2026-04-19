package com.conchoback.haingon.ui.choose_clothes_after.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.conchoback.haingon.core.extension.domain
import com.conchoback.haingon.core.extension.loadImage
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.core.utils.DataLocal
import com.conchoback.haingon.core.utils.key.DomainKey
import com.conchoback.haingon.data.model.SelectedModel
import com.conchoback.haingon.databinding.ItemChooseClothesAccessoryBinding

class ClothesAdapter : ListAdapter<SelectedModel, ClothesAdapter.ClothesViewHolder>(ClothesDiffCallback()) {
    var onItemClick: ((path: String, position: Int) -> Unit) = { _, _ -> }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ClothesViewHolder {
        return ClothesViewHolder(ItemChooseClothesAccessoryBinding.inflate(LayoutInflater.from(p0.context), p0, false))
    }

    override fun onBindViewHolder(p0: ClothesViewHolder, p1: Int) {
        p0.bind(getItem(p1))
    }

    inner class ClothesViewHolder(val binding: ItemChooseClothesAccessoryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SelectedModel) {
            binding.apply {
                vFocus.isVisible = item.isSelected

                // https://lvtglobal.tech/public/app/ST253_ClothesSkinsMakerforRBX_v2/special/1.png
                val pathURL = domain("${DomainKey.BASE_PATH}/${item.path}")

                loadImage(pathURL, imvImage)

                root.tap { onItemClick.invoke(item.path, bindingAdapterPosition) }
            }
        }
    }


    class ClothesDiffCallback : DiffUtil.ItemCallback<SelectedModel>() {
        override fun areItemsTheSame(p0: SelectedModel, p1: SelectedModel): Boolean {
            return p0.id == p1.id
        }

        override fun areContentsTheSame(p0: SelectedModel, p1: SelectedModel): Boolean {
            return p0 == p1
        }

    }
}