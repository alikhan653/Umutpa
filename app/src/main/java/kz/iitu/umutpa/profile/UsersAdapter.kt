package kz.iitu.umutpa.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kz.iitu.umutpa.databinding.AdapterUserBinding

class UsersAdapter : RecyclerView.Adapter<UsersAdapter.InfoPageViewHolder>() {


    private var rcOnItemClick: RcOnItemClick? = null
    private val diffCallback =
        object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(
                oldItem: User,
                newItem: User
            ): Boolean {
                return oldItem.uid == newItem.uid
            }

            override fun areContentsTheSame(
                oldItem: User,
                newItem: User
            ): Boolean {
                return oldItem == newItem
            }

        }

    fun setRcOnItemClick(rcOnItemClick: RcOnItemClick) {
        this.rcOnItemClick = rcOnItemClick
    }
    inner class InfoPageViewHolder(private val binding: AdapterUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(info: User) {
            Glide.with(binding.root)
                .load(info.imageUrl)
                .into(binding.tvImage)
            binding.tvName.text = info.name
            binding.tvEmail.text = info.email
            binding.root.setOnClickListener {
                rcOnItemClick?.onItemClick(info.uid)
            }
        }
    }
    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<User>) {
        differ.submitList(list)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoPageViewHolder {
        return InfoPageViewHolder(
            AdapterUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: InfoPageViewHolder, position: Int) {
        holder.bindItem(differ.currentList[position])
    }

}