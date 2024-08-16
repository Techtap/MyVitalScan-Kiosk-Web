package com.techtap

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techtap.databinding.ItemQuestionOptionBinding

class QuestionOptionAdapter(mContext: Context, items: ArrayList<QuestionOption>, questionListener1: QuestionAdapter.QuestionListener) :
    RecyclerView.Adapter<QuestionOptionAdapter.WinesByPairingChildViewHolder>() {


    private var items: ArrayList<QuestionOption> = arrayListOf()
    private var context: Context? = null
    private var questionListener: QuestionAdapter.QuestionListener = questionListener1

    init {
        this.items = items
        this.context = mContext
    }

    inner class WinesByPairingChildViewHolder(val binding: ItemQuestionOptionBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
//            val layoutManager = FlexboxLayoutManager(context)
//            layoutManager.flexDirection = FlexDirection.ROW
//            layoutManager.justifyContent = JustifyContent.FLEX_START
//            binding.recyclerViewChild.layoutManager = layoutManager
//            adapter = WinesByPairingChildAdapter(listItems)
//            binding.recyclerViewChild.adapter = adapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WinesByPairingChildViewHolder {
        return WinesByPairingChildViewHolder(ItemQuestionOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: WinesByPairingChildViewHolder, position: Int) {
        val questionOption = items[position]
        holder.binding.tvOption.text = questionOption.option
        holder.binding.clOption.setOnClickListener {
            questionListener.onQuestionOptionClick(questionOption, position)
        }

        if (questionOption.selected) {
            holder.binding.ivSelected.setImageResource(R.drawable.rb_selected_small)
//            holder.binding.tvWinesByPairingChild.setTextColor(ContextCompat.getColor(holder.binding.root.context, R.color.type_color))
        } else {
            holder.binding.ivSelected.setImageResource(R.drawable.rb_default_small)
//            holder.binding.clWinesByPairingChild.background = ContextCompat.getDrawable(holder.binding.root.context, R.drawable.et_search_bg)
//            holder.binding.tvWinesByPairingChild.setTextColor(ContextCompat.getColor(holder.binding.root.context, R.color.hint_color))
        }

        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun notifyChildAdapter(pos: Int) {
        notifyItemChanged(pos)
    }


}