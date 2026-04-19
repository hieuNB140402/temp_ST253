package com.conchoback.haingon.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.conchoback.haingon.R
import com.conchoback.haingon.core.extension.loadAccessory2DURL
import com.conchoback.haingon.core.extension.loadImage
import com.conchoback.haingon.core.helper.LoadClothesHelper
import com.conchoback.haingon.core.utils.DataLocal
import com.conchoback.haingon.core.utils.key.AssetsKey
import com.conchoback.haingon.core.utils.key.DomainKey
import com.conchoback.haingon.core.utils.key.ValueKey
import com.conchoback.haingon.data.model.DownloadModel
import com.conchoback.haingon.data.model.MyCreationModel
import com.conchoback.haingon.databinding.ItemMyCreationBinding
import com.conchoback.haingon.ui.home.MyCreationPayload
import kotlinx.serialization.Contextual
import java.io.File

class MyCreationAdapter(val context: Context) :
    ListAdapter<MyCreationModel, MyCreationAdapter.MyCreationViewHolder>(MyCreationDiffCallback()) {
    var onItemClick: (MyCreationModel) -> Unit = {}
    var onItemLongClick: (Int) -> Unit = {}
    var onItemSelectClick: (Int) -> Unit = {}

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyCreationViewHolder {
        return MyCreationViewHolder(ItemMyCreationBinding.inflate(LayoutInflater.from(p0.context), p0, false))
    }

    override fun onBindViewHolder(p0: MyCreationViewHolder, p1: Int) {
        p0.bind(getItem(p1))
    }

    override fun onBindViewHolder(holder: MyCreationViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {

            val combinedPayloads = payloads
                .filterIsInstance<List<MyCreationPayload>>()
                .flatten()

            val item = getItem(position)

            combinedPayloads.forEach { payload ->
                when (payload) {

                    is MyCreationPayload.SelectedChanged -> {
                        holder.updateSelected(payload.isSelected)
                    }

                    is MyCreationPayload.ShowSelectChanged -> {
                        holder.updateShowSelection(payload.isShowSelection)
                    }
                }
            }

            return
        }

        super.onBindViewHolder(holder, position, payloads)
    }

    inner class MyCreationViewHolder(val binding: ItemMyCreationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MyCreationModel) {

            when (item.clothes.typeClothes) {
                ValueKey.SHIRT -> loadImage(LoadClothesHelper.fullDomainImage(context, item.clothes.thumbnail), binding.imvImage)
                ValueKey.PANT -> loadImage(LoadClothesHelper.fullDomainImage(context, item.clothes.thumbnail), binding.imvImage)
                else -> loadImage(loadAccessory2DURL(item.clothes.thumbnail), binding.imvImage)
            }

            updateSelected(item.isSelected)
            updateShowSelection(item.isShowSelection)

            binding.root.setOnClickListener { onItemClick(item) }

            binding.btnSelect.setOnClickListener { onItemSelectClick(bindingAdapterPosition) }

            binding.root.setOnLongClickListener {
                onItemLongClick(bindingAdapterPosition)
                true
            }
        }

        fun updateSelected(isSelected: Boolean) {
            val res = if (isSelected) R.drawable.ic_selected else R.drawable.ic_not_select
            binding.btnSelect.setImageResource(res)
        }

        fun updateShowSelection(isShow: Boolean) {
            binding.btnSelect.isVisible = isShow
        }
    }

    class MyCreationDiffCallback : DiffUtil.ItemCallback<MyCreationModel>() {

        override fun areItemsTheSame(oldItem: MyCreationModel, newItem: MyCreationModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MyCreationModel, newItem: MyCreationModel): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: MyCreationModel, newItem: MyCreationModel): Any? {
            val payloads = mutableListOf<MyCreationPayload>()

            if (oldItem.isSelected != newItem.isSelected) {
                payloads.add(MyCreationPayload.SelectedChanged(newItem.isSelected))
            }

            if (oldItem.isShowSelection != newItem.isShowSelection) {
                payloads.add(MyCreationPayload.ShowSelectChanged(newItem.isShowSelection))
            }

            return if (payloads.isEmpty()) null else payloads
        }
    }
    
}