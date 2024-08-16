package com.techtap

import android.graphics.Typeface
import android.text.Spannable
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.toSpanned
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.techtap.databinding.ItemReportListBinding
import com.techtap.databinding.ItemReportListDescBinding
import com.techtap.utils.BindingViewHolder
import com.techtap.utils.CustomTypefaceSpan
import com.techtap.utils.Utils

internal class ReportListAdapter(items: ArrayList<Any>, isExternal: Int) : RecyclerView.Adapter<BindingViewHolder<ViewDataBinding>>() {

    private var items: ArrayList<Any> = arrayListOf()
    internal var reportListener: ReportListener? = null
    private var isExternal: Int = 0

    private val VITAL = 1
    private val DESC = 2

    init {
        this.items = items
        this.isExternal = isExternal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<ViewDataBinding> {
//        return BindingViewHolder(
//            LayoutInflater.from(parent.context).inflate(R.layout.item_report_list, parent, false)
//        )

        return when (viewType) {
            VITAL -> BindingViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_report_list, parent, false)
            )

            DESC -> BindingViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_report_list_desc, parent, false)
            )

            else -> {
                BindingViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_report_list, parent, false)
                )
            }
        }

    }

    override fun onBindViewHolder(holder: BindingViewHolder<ViewDataBinding>, position: Int) {
        when (holder.binding) {
            is ItemReportListBinding -> {
                val reading = items[position] as Reading
                holder.binding.reading = reading
                holder.binding.position = position
                holder.binding.reportListener = reportListener

                if (!reading.title.isNullOrEmpty()) {
                    holder.binding.tvVitalName.text = reading.title
                } else {
                    holder.binding.tvVitalName.text = "-"
                }

                if (!reading.level.isNullOrEmpty()) {
                    holder.binding.tvVitalLevel.text = reading.level!!.replace("_", " ")
                } else {
                    holder.binding.tvVitalLevel.text = "-"
                }


                if (!reading.observedValue.isNullOrEmpty() && !reading.observedValue.equals("-1")) {
//                    holder.binding.tvVitalReading.text = reading.observedValue


//                    val str: Spanned
//                    val myStr = "This is test"
//                    str = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        Html.fromHtml("X<sup><small>123</small></sup>$myStr", Html.FROM_HTML_MODE_LEGACY)
//                    } else {
//                        Html.fromHtml("X<sup><small>123</small></sup>$myStr")
//                    }
//
//                    txt.setText(str)
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
//            ic_heart_rate_variability_theme
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

                val finalResultStr = StringBuilder()

                var str1 = ""
                var str2 = ""
                var str3 = ""

                if (!reading.description.isNullOrEmpty()) {
//            finalResultStr.append(reading.description + "\n\n")
                    str1 = reading.description + "\n\n"
                }

                if (!reading.status.isNullOrEmpty()) {
//            finalResultStr.append(reading.status + "\n\n")
                    str2 = reading.status + "\n\n"
                }

                if (!reading.shortDesc.isNullOrEmpty()) {
//            finalResultStr.append(reading.shortDesc)
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


//        holder.binding.tvVitalDetails.text = finalResultStr.toString()

                if (reading.isSelected) {

                    holder.binding.tvVitalDetails.visibility = View.VISIBLE
                    holder.binding.tvVitalName.setTextColor(ContextCompat.getColor(holder.binding.root.context, R.color.white))
                    holder.binding.tvVitalLevel.setTextColor(ContextCompat.getColor(holder.binding.root.context, R.color.white))
                    holder.binding.tvVitalReading.setTextColor(ContextCompat.getColor(holder.binding.root.context, R.color.white))

                    holder.binding.ivArrow.setImageResource(R.drawable.ic_arrow_up_white)

//                    if (reading.level.equals("NA")) {
//                        holder.binding.clHeader.setBackgroundColor(ContextCompat.getColor(holder.binding.root.context, R.color.purple))
//                    } else {
//                        holder.binding.clHeader.setBackgroundColor(ContextCompat.getColor(holder.binding.root.context, R.color.level_green_color))
//                    }

                    if (!reading.colorCode.isNullOrEmpty()) {
                        holder.binding.clHeader.setBackgroundColor(android.graphics.Color.parseColor(reading.colorCode!!.trim()))
                    } else {
                        holder.binding.clHeader.setBackgroundColor(ContextCompat.getColor(holder.binding.root.context, R.color.purple))
                    }
                } else {
                    holder.binding.tvVitalDetails.visibility = View.GONE
                    holder.binding.tvVitalName.setTextColor(ContextCompat.getColor(holder.binding.root.context, R.color.primary_text_color))
                    holder.binding.tvVitalLevel.setTextColor(ContextCompat.getColor(holder.binding.root.context, R.color.primary_text_color))
                    holder.binding.tvVitalReading.setTextColor(ContextCompat.getColor(holder.binding.root.context, R.color.primary_text_color))
                    holder.binding.ivArrow.setImageResource(R.drawable.ic_arrow_down)

                    holder.binding.clHeader.setBackgroundColor(ContextCompat.getColor(holder.binding.root.context, R.color.white))
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


                holder.binding.executePendingBindings()
            }

            is ItemReportListDescBinding -> {
                if (isExternal == 1) {
                    holder.binding.lblThisIsLimitedScan.visibility = View.VISIBLE
                } else {
                    holder.binding.lblThisIsLimitedScan.visibility = View.GONE
                }
                holder.binding.executePendingBindings()
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        if (items[position] is Reading) {
            return VITAL
        } else if (items[position] is ReadingDesc) {
            return DESC
        }
        return -1
    }

    interface ReportListener {
        fun onReportItemClick(reading: Reading?, position: Int)
    }

}