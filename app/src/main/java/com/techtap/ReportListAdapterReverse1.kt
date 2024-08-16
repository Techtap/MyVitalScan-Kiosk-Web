package com.techtap

import android.graphics.Typeface
import android.text.Spannable
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.toSpanned
import androidx.recyclerview.widget.RecyclerView
import com.techtap.databinding.ItemReportListR1Binding
import com.techtap.utils.BindingViewHolder
import com.techtap.utils.CustomTypefaceSpan
import com.techtap.utils.Utils


internal class ReportListAdapterReverse1(items: ArrayList<Reading>) : RecyclerView.Adapter<BindingViewHolder<ItemReportListR1Binding>>() {

    private var items: ArrayList<Reading> = arrayListOf()
    internal var reportListener: ReportListener? = null

    init {
        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<ItemReportListR1Binding> {
        return BindingViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_report_list_r1, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BindingViewHolder<ItemReportListR1Binding>, position: Int) {
        val reading = items[position]
        holder.binding.reading = reading
        holder.binding.position = position
        holder.binding.reportListener = reportListener



        if (!reading.title.isNullOrEmpty()) {
            holder.binding.tvVitalName.text = reading.title
        } else {
            holder.binding.tvVitalName.text = "-"
        }

        if (!reading.level.isNullOrEmpty()) {
            holder.binding.tvVitalLevel.text = reading.level
        } else {
            holder.binding.tvVitalLevel.text = "-"
        }

        if (!reading.observedValue.isNullOrEmpty() && !reading.observedValue.equals("-1")) {
//            holder.binding.tvVitalReading.text = reading.observedValue
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
                holder.binding.ivVitalSign.setImageResource(R.drawable.ic_vs_heart_rate_white)
            } else if (reading.title.equals("Breathing Rate")) {
                holder.binding.ivVitalSign.setImageResource(R.drawable.ic_vs_breathing_rate_white)
            } else if (reading.title.equals("PRQ")) {
                holder.binding.ivVitalSign.setImageResource(R.drawable.ic_vs_breathing_rate_white)
            } else if (reading.title.equals("Oxygen Saturation")) {
                holder.binding.ivVitalSign.setImageResource(R.drawable.ic_vs_oxygen_saturation_white)
            } else if (reading.title.equals("Blood Pressure")) {
                holder.binding.ivVitalSign.setImageResource(R.drawable.ic_vs_blood_pressure_white)
            } else if (reading.title.equals("Stress Level")) {
                holder.binding.ivVitalSign.setImageResource(R.drawable.ic_stress_level_white)
            } else if (reading.title.equals("Recovery Ability")) {
                holder.binding.ivVitalSign.setImageResource(R.drawable.ic_vs_recovery_ability_white)
            } else if (reading.title.equals("Stress Response")) {
                holder.binding.ivVitalSign.setImageResource(R.drawable.ic_stress_response_white)
            } else if (reading.title.equals("HRV-SDNN")) {
                holder.binding.ivVitalSign.setImageResource(R.drawable.ic_vs_temperature_white)
            } else if (reading.title.equals("Hemoglobin")) {
                holder.binding.ivVitalSign.setImageResource(R.drawable.ic_vs_hemoglobin_white)
            } else if (reading.title.equals("Hemoglobin A1c")) {
                holder.binding.ivVitalSign.setImageResource(R.drawable.ic_vs_hba1c_white)
            }
        }

        var str1 = ""
        var str2 = ""
        var str3 = ""

        val finalResultStr = StringBuilder()
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

//        if (reading.level.equals("NA")) {
//            holder.binding.clHeader.setBackgroundColor(ContextCompat.getColor(holder.binding.root.context, R.color.purple))
//        } else {
//            holder.binding.clHeader.setBackgroundColor(ContextCompat.getColor(holder.binding.root.context, R.color.level_green_color))
//        }

        if (!reading.colorCode.isNullOrEmpty()) {
            holder.binding.clHeader.setBackgroundColor(android.graphics.Color.parseColor(reading.colorCode))
        } else {
            holder.binding.clHeader.setBackgroundColor(ContextCompat.getColor(holder.binding.root.context, R.color.purple))
        }
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface ReportListener {
        fun onReportItemClick(reading: Reading?, position: Int)
    }


}