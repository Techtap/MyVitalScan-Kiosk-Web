package com.techtap

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.techtap.databinding.ItemQuestionBinding
import com.techtap.utils.CustomTypefaceSpan


class QuestionAdapter(mContext: Context, items: ArrayList<Question>) : RecyclerView.Adapter<QuestionAdapter.WinesByPairingParentViewHolder>() {

    private var items: ArrayList<Question> = arrayListOf()
    private var context: Context? = null
    private var questionListener: QuestionListener? = null
    private var layoutManager: LinearLayoutManager? = null

    init {
        this.items = items
        this.context = mContext


    }

    inner class WinesByPairingParentViewHolder(val binding: ItemQuestionBinding) : RecyclerView.ViewHolder(binding.root) {
        val listItemsChild: ArrayList<QuestionOption> = arrayListOf()
        var childAdapter: QuestionOptionAdapter? = null

        init {
//            layoutManager = FlexboxLayoutManager(context)
//            layoutManager?.flexDirection = FlexDirection.ROW
//            layoutManager?.justifyContent = JustifyContent.FLEX_START
//            binding.rvOptions.layoutManager = layoutManager

//            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            val layoutManager: LinearLayoutManager = object : LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
            binding.rvOptions.layoutManager = layoutManager
            childAdapter = QuestionOptionAdapter(context!!, listItemsChild, questionListener!!)
            binding.rvOptions.adapter = childAdapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WinesByPairingParentViewHolder {
        return WinesByPairingParentViewHolder(ItemQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: WinesByPairingParentViewHolder, position: Int) {
        val question = items[position]
        holder.binding.tvQuestion.text = question.questions

        val bold = Typeface.createFromAsset(holder.binding.root.context.assets, "fonts/inter_bold.ttf")

        val impactStr = holder.binding.root.context.getString(R.string.impact)

        val keepingYourBodyStr = question.impact
        val keepingYourBodySpan = Spannable.Factory.getInstance().newSpannable("$impactStr $keepingYourBodyStr")
        val impactSpan: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        keepingYourBodySpan.setSpan(impactSpan, 0, impactStr.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        keepingYourBodySpan.setSpan(StyleSpan(Typeface.ITALIC), 0, impactStr.length, 0);
        holder.binding.tvQuestionDesc.text = keepingYourBodySpan
        holder.binding.tvQuestionDesc.movementMethod = LinkMovementMethod.getInstance()


        holder.listItemsChild.clear()
        holder.listItemsChild.addAll(question.options!!)
        holder.childAdapter!!.notifyDataSetChanged()
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setItemClickListener(questionListener: QuestionListener) {
        this.questionListener = questionListener
    }

    interface QuestionListener {
        fun onQuestionOptionClick(questionOption: QuestionOption?, childPosition: Int)
    }

    fun notifyChildAdapter(parentPosition: Int, childPosition: Int) {
        notifyItemChanged(parentPosition)
    }

}