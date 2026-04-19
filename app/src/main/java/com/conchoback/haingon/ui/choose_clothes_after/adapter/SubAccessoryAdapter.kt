package com.conchoback.haingon.ui.choose_clothes_after.adapter

import android.R.attr.path
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.conchoback.haingon.R
import com.conchoback.haingon.core.extension.dLog
import com.conchoback.haingon.core.extension.loadAccessory2DURL
import com.conchoback.haingon.core.extension.loadImage
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.data.model.clothes.AccessoryModel
import com.conchoback.haingon.data.model.clothes.SubAccessoryModel
import com.conchoback.haingon.databinding.ItemChooseClothesAccessoryBinding

class SubAccessoryAdapter : ListAdapter<SubAccessoryModel, SubAccessoryAdapter.SubAccessoryViewHolder>(SubAccessoryDiffCallback()){
    var onItemClick: ((model: AccessoryModel, position: Int) -> Unit) = { _,_ -> }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SubAccessoryViewHolder {
        return SubAccessoryViewHolder(ItemChooseClothesAccessoryBinding.inflate(LayoutInflater.from(p0.context), p0, false))
    }

    override fun onBindViewHolder(p0: SubAccessoryViewHolder, p1: Int) {
        p0.bind(getItem(p1))
    }

    inner class SubAccessoryViewHolder(val binding: ItemChooseClothesAccessoryBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: SubAccessoryModel){
            binding.apply {
                vFocus.isVisible = item.isSelected

                if (bindingAdapterPosition != 0) {

                    // https://lvtglobal.tech/public/app/ST253_ClothesSkinsMakerforRBX_v2/2D/abcd.png
                    val pathURL = loadAccessory2DURL(item.accessory.value)

                    dLog("pathURL[$bindingAdapterPosition]: $pathURL")

                    loadImage(pathURL, imvImage)
                } else {
                    loadImage(R.drawable.ic_none_accessory, imvImage)
                }

                root.tap { onItemClick.invoke(item.accessory, bindingAdapterPosition) }
            }
        }
    }

    class SubAccessoryDiffCallback : DiffUtil.ItemCallback<SubAccessoryModel>(){
        override fun areItemsTheSame(p0: SubAccessoryModel, p1: SubAccessoryModel): Boolean {
            return p0.id == p1.id
        }

        override fun areContentsTheSame(p0: SubAccessoryModel, p1: SubAccessoryModel): Boolean {
            return p0 == p1
        }
    }
}