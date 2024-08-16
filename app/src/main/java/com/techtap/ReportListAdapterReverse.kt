package com.techtap

import android.graphics.Typeface
import android.text.Spannable
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.text.toSpanned
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.techtap.databinding.ItemReportListLimitedBinding
import com.techtap.databinding.ItemReportListRBinding
import com.techtap.utils.BindingViewHolder
import com.techtap.utils.CustomTypefaceSpan
import com.techtap.utils.Logger
import com.techtap.utils.Utils


internal class ReportListAdapterReverse(items: ArrayList<Any>) : RecyclerView.Adapter<BindingViewHolder<ViewDataBinding>>() {

    private var items: ArrayList<Any> = arrayListOf()
    internal var reportListener: ReportListener? = null

    private val VITAL = 1
    private val LIMITED = 2

    init {
        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<ViewDataBinding> {
//        return BindingViewHolder(
//            LayoutInflater.from(parent.context).inflate(R.layout.item_report_list_r, parent, false)
//        )

        return when (viewType) {
            VITAL -> BindingViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_report_list_r, parent, false)
            )

            LIMITED -> BindingViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_report_list_limited, parent, false)
            )

            else -> {
                BindingViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_report_list_r, parent, false)
                )
            }
        }

    }

    override fun onBindViewHolder(holder: BindingViewHolder<ViewDataBinding>, position: Int) {

        when (holder.binding) {
            is ItemReportListRBinding -> {
                val reading = items[position]
                holder.binding.reading = reading as Reading
                holder.binding.position = position
                holder.binding.reportListener = reportListener
                if (!reading.title.isNullOrEmpty()) {
                    holder.binding.tvVitalName.text = reading.title
                } else {
                    holder.binding.tvVitalName.text = "-"
                }

                if (!reading.level.isNullOrEmpty()) {
                    val levelStr = reading.level!!.replace("_", "\n")
                    holder.binding.tvVitalLevel.text = levelStr

                } else {
                    holder.binding.tvVitalLevel.text = "-"
                }

                if (!reading.observedValue.isNullOrEmpty() && !reading.observedValue.equals("-1")) {
                    val observedValueStr: Spanned?
                    if (!reading.unit.isNullOrEmpty()) {
                        observedValueStr = Utils.getSuperText(reading.unit, reading.observedValue)
                    } else {
                        observedValueStr = reading.observedValue!!.toSpanned()
                    }
                    holder.binding.tvVitalReading.text = observedValueStr
                } else {
                    holder.binding.tvVitalReading.text = "-"
                }

                if (!reading.title.isNullOrEmpty()) {
                    if (reading.title.equals("Heart Rate")) {
                        if (reading.isSelected) {
                            holder.binding.ivVitalSign.setImageResource(R.drawable.ic_vs_heart_rate_white)
                        } else {
                            holder.binding.ivVitalSign.setImageResource(R.drawable.ic_vs_heart_rate_theme)
                        }
                    } else if (reading.title.equals("Breathing Rate")) {
                        if (reading.isSelected) {
                            holder.binding.ivVitalSign.setImageResource(R.drawable.ic_vs_breathing_rate_white)
                        } else {
                            holder.binding.ivVitalSign.setImageResource(R.drawable.ic_vs_breathing_rate_theme)
                        }
                    } else if (reading.title.equals("PRQ")) {
                        if (reading.isSelected) {
                            holder.binding.ivVitalSign.setImageResource(R.drawable.ic_vs_breathing_rate_white)
                        } else {
                            holder.binding.ivVitalSign.setImageResource(R.drawable.ic_vs_breathing_rate_theme)
                        }
                    } else if (reading.title.equals("Oxygen Saturation")) {
                        if (reading.isSelected) {
                            holder.binding.ivVitalSign.setImageResource(R.drawable.ic_vs_oxygen_saturation_white)
                        } else {
                            holder.binding.ivVitalSign.setImageResource(R.drawable.ic_vs_oxygen_saturation_theme)
                        }
                    } else if (reading.title.equals("Blood Pressure")) {
                        if (reading.isSelected) {
                            holder.binding.ivVitalSign.setImageResource(R.drawable.ic_vs_blood_pressure_white)
                        } else {
                            holder.binding.ivVitalSign.setImageResource(R.drawable.ic_vs_blood_pressure_theme)
                        }
                    } else if (reading.title.equals("Stress Level")) {
                        if (reading.isSelected) {
                            holder.binding.ivVitalSign.setImageResource(R.drawable.ic_stress_level_white)
                        } else {
                            holder.binding.ivVitalSign.setImageResource(R.drawable.ic_stress_level_theme)
                        }
                    } else if (reading.title.equals("Recovery Ability")) {
                        if (reading.isSelected) {
                            holder.binding.ivVitalSign.setImageResource(R.drawable.ic_vs_recovery_ability_white)
                        } else {
                            holder.binding.ivVitalSign.setImageResource(R.drawable.ic_vs_recovery_ability_theme)
                        }
                    } else if (reading.title.equals("Stress Response")) {
                        if (reading.isSelected) {
                            holder.binding.ivVitalSign.setImageResource(R.drawable.ic_stress_response_white)
                        } else {
                            holder.binding.ivVitalSign.setImageResource(R.drawable.ic_stress_response_theme)
                        }
                    } else if (reading.title.equals("HRV-SDNN")) {
                        if (reading.isSelected) {
                            holder.binding.ivVitalSign.setImageResource(R.drawable.ic_vs_hrv_sdnn_white)
                        } else {
                            holder.binding.ivVitalSign.setImageResource(R.drawable.ic_vs_hrv_sdnn_theme)
                        }
                    } else if (reading.title.equals("Hemoglobin")) {
                        if (reading.isSelected) {
                            holder.binding.ivVitalSign.setImageResource(R.drawable.ic_vs_hemoglobin_white)
                        } else {
                            holder.binding.ivVitalSign.setImageResource(R.drawable.ic_vs_hemoglobin_theme)
                        }
                    } else if (reading.title.equals("Hemoglobin A1c")) {
                        if (reading.isSelected) {
                            holder.binding.ivVitalSign.setImageResource(R.drawable.ic_vs_hba1c_white)
                        } else {
                            holder.binding.ivVitalSign.setImageResource(R.drawable.ic_vs_hba1c_theme)
                        }
                    } else if (reading.title.equals("Temperature")) {
                        if (reading.isSelected) {
                            holder.binding.ivVitalSign.setImageResource(R.drawable.ic_vs_temperature_white)
                        } else {
                            holder.binding.ivVitalSign.setImageResource(R.drawable.ic_vs_temperature_theme)
                        }
                    }
                }

                var str1 = ""
                var str2 = ""
                var str3 = ""

                val finalResultStr = StringBuilder()
                if (!reading.description.isNullOrEmpty()) {
                    str1 = reading.description + "\n\n"
                }

                if (!reading.status.isNullOrEmpty()) {
                    str2 = reading.status + "\n\n"
                }

                if (!reading.shortDesc.isNullOrEmpty()) {
                    str3 = reading.shortDesc!!
                }

                finalResultStr.append(str1)
                finalResultStr.append(str2)
                finalResultStr.append(str3)

                val bold = Typeface.createFromAsset(holder.binding.root.context.assets, "fonts/inter_bold.ttf")
                val boldSpan: CustomTypefaceSpan = CustomTypefaceSpan(bold)

                val detailSpan = Spannable.Factory.getInstance().newSpannable(finalResultStr.toString())
                detailSpan.setSpan(boldSpan, str1.length, (str1.length + str2.length), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                holder.binding.tvVitalDetails.text = detailSpan
                holder.binding.tvVitalDetails.movementMethod = LinkMovementMethod.getInstance()

                if (!reading.colorCode.isNullOrEmpty()) {
                    holder.binding.clHeader.setBackgroundColor(android.graphics.Color.parseColor(reading.colorCode))
                } else {
                    holder.binding.clHeader.setBackgroundColor(ContextCompat.getColor(holder.binding.root.context, R.color.purple))
                }


                val llParentWidth = holder.binding.llParent.width
                val displayMetrics: DisplayMetrics = holder.binding.root.context.resources.displayMetrics
//        val parentWidth = `1020`
                val parentWidth = displayMetrics.widthPixels - 40
                val headerWidth = parentWidth * 0.33
                val scrollWidth = parentWidth * 0.67

                val layoutParams = (holder.binding.llParent.layoutParams as? ViewGroup.MarginLayoutParams)
                layoutParams?.setMargins(if (position == 0) 20 else 0, 0, 20, 0)
                holder.binding.llParent.layoutParams = layoutParams

                holder.binding.ivThumbVertical.visibility = View.VISIBLE

                if (reading.isSelected) {
//            holder.binding.llParent.layoutParams = LinearLayoutCompat.LayoutParams(headerWidth.toInt(), LayoutParams.MATCH_PARENT)
                    holder.binding.clHeader.layoutParams = LinearLayoutCompat.LayoutParams(headerWidth.toInt(), LayoutParams.MATCH_PARENT)
                    holder.binding.scrollView.visibility = View.VISIBLE
                    holder.binding.scrollView.layoutParams = LinearLayoutCompat.LayoutParams(scrollWidth.toInt(), LayoutParams.MATCH_PARENT)
                    if (!reading.colorCode.isNullOrEmpty()) {
                        holder.binding.clHeader.setBackgroundColor(android.graphics.Color.parseColor(reading.colorCode!!.trim()))
                    } else {
                        holder.binding.clHeader.setBackgroundColor(ContextCompat.getColor(holder.binding.root.context, R.color.purple))
                    }

                    holder.binding.tvVitalDetails.visibility = View.VISIBLE
                    holder.binding.tvVitalName.setTextColor(ContextCompat.getColor(holder.binding.root.context, R.color.white))
                    holder.binding.tvVitalLevel.setTextColor(ContextCompat.getColor(holder.binding.root.context, R.color.white))
                    holder.binding.tvVitalReading.setTextColor(ContextCompat.getColor(holder.binding.root.context, R.color.white))
                    holder.binding.ivArrow.setImageResource(R.drawable.ic_arrow_left_white)

                    val parentViewHeight = holder.binding.llParent.height
                    val vitalDetailViewHeight: Int = holder.binding.nsv.getChildAt(0).height
                    Logger.e("parentViewHeight = $parentViewHeight ")

                    val viewTreeObserver: ViewTreeObserver = holder.binding.nsv.viewTreeObserver

                    viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            holder.binding.nsv.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            val childHeight = holder.binding.tvVitalDetails.height
                            Logger.e(" childHeight = $childHeight")

                            if (parentViewHeight <= childHeight) {
                                holder.binding.ivThumbVertical.visibility = View.VISIBLE
                            } else {
                                holder.binding.ivThumbVertical.visibility = View.INVISIBLE
                            }
//                    val isScrollable: Boolean = scrollView.getHeight() < childHeight + scrollView.getPaddingTop() + scrollView.getPaddingBottom()
//                    if (isScrollable) {
//                        //Urrah! is scrollable
//                    }
                        }
                    })
                } else {
//            holder.binding.llParent.layoutParams = LinearLayoutCompat.LayoutParams((headerWidth.toInt() + scrollWidth.toInt()), LayoutParams.MATCH_PARENT)
                    holder.binding.clHeader.layoutParams = LinearLayoutCompat.LayoutParams(headerWidth.toInt(), LayoutParams.MATCH_PARENT)
                    holder.binding.scrollView.visibility = View.GONE
                    holder.binding.clHeader.setBackgroundColor(ContextCompat.getColor(holder.binding.root.context, R.color.white))

                    holder.binding.tvVitalDetails.visibility = View.GONE
                    holder.binding.tvVitalName.setTextColor(ContextCompat.getColor(holder.binding.root.context, R.color.primary_text_color))
                    holder.binding.tvVitalLevel.setTextColor(ContextCompat.getColor(holder.binding.root.context, R.color.primary_text_color))
                    holder.binding.tvVitalReading.setTextColor(ContextCompat.getColor(holder.binding.root.context, R.color.primary_text_color))
                    holder.binding.ivArrow.setImageResource(R.drawable.ic_arrow_right_theme)
                }


                if (reading.hasConfidenceLevel == 1 && !reading.confidenceLevel.isNullOrEmpty()) {
                    holder.binding.ivConfidence.visibility = View.VISIBLE
                    when (reading.confidenceLevel) {
                        "3" -> {
                            if (reading.isSelected) {
                                holder.binding.ivConfidence.setImageResource(R.drawable.ic_conf_level_high_white)
                            } else {
                                holder.binding.ivConfidence.setImageResource(R.drawable.ic_conf_level_high)
                            }
                        }

                        "2" -> {
                            if (reading.isSelected) {
                                holder.binding.ivConfidence.setImageResource(R.drawable.ic_conf_level_medium_white)
                            } else {
                                holder.binding.ivConfidence.setImageResource(R.drawable.ic_conf_level_medium)
                            }
                        }

                        "1" -> {
                            if (reading.isSelected) {
                                holder.binding.ivConfidence.setImageResource(R.drawable.ic_conf_level_low_white)
                            } else {
                                holder.binding.ivConfidence.setImageResource(R.drawable.ic_conf_level_low)
                            }
                        }

                        else -> {
                            holder.binding.ivConfidence.visibility = View.GONE
                        }
                    }

                } else {
                    holder.binding.ivConfidence.visibility = View.GONE
                }
            }

            is ItemReportListLimitedBinding -> {

                val displayMetrics: DisplayMetrics = holder.binding.root.context.resources.displayMetrics
                val parentWidth = displayMetrics.widthPixels - 40
                val headerWidth = parentWidth * 0.33
                val scrollWidth = parentWidth * 0.67

                val layoutParams = (holder.binding.llLimited.layoutParams as? ViewGroup.MarginLayoutParams)
                layoutParams?.setMargins(if (position == 0) 20 else 0, 0, 20, 0)
                holder.binding.llLimited.layoutParams = layoutParams
                holder.binding.llLimited.layoutParams = LinearLayoutCompat.LayoutParams(headerWidth.toInt(), LayoutParams.MATCH_PARENT)

            }
        }

        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        if (items[position] is Reading) {
            return VITAL
        } else if (items[position] is String) {
            return LIMITED
        }
        return -1
    }

    interface ReportListener {
        fun onReportItemClick(reading: Reading?, position: Int)
    }


}