package org.n27.tado.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import org.n27.tado.R
import org.n27.tado.data.api.models.Mode
import org.n27.tado.data.room.AcConfig

typealias OnIconClicked = (id: Int, mode: Mode) -> Unit
typealias OnTemperatureClicked = (id: Int, OnTemperatureUpdated) -> Unit
typealias OnSwitchClicked = (id: Int, isEnabled: Boolean) -> Unit

typealias OnTemperatureUpdated = (String) -> Unit

class ACCardAdapter(
    private val acs: List<AcConfig>,
    private val onIconClicked: OnIconClicked,
    private val onTemperatureClicked: OnTemperatureClicked,
    private val onSwitchClicked: OnSwitchClicked
) : RecyclerView.Adapter<ACCardAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val card: CardView) : RecyclerView.ViewHolder(card)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // create a new view
        val card = LayoutInflater.from(parent.context).inflate(R.layout.ac_card, parent, false) as CardView

        return MyViewHolder(card)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val card = holder.card

        val ac = acs[position]
        var modePosition = ac.mode.ordinal

        card.findViewById<ImageView>(R.id.ac_mode_icon).apply {
            setImageResource(ac.mode.res)

            setOnClickListener {
                modePosition = if (modePosition == 2) 0 else modePosition + 1
                val newMode = Mode.values()[modePosition]

                onIconClicked(ac.id, newMode)

                setImageResource(newMode.res)
            }
        }

        card.findViewById<TextView>(R.id.ac_name).text = acs[position].name
        card.findViewById<TextView>(R.id.desired_temperature).apply {
            val tmp = "${ac.temperature} º"
            text = tmp
            val temperatureCallback: OnTemperatureUpdated = { text = it }

            setOnClickListener { onTemperatureClicked(ac.id, temperatureCallback) }
        }

        card.findViewById<SwitchCompat>(R.id.switch_button).apply {
            isChecked = ac.serviceEnabled

            setOnClickListener { onSwitchClicked(ac.id, (it as SwitchCompat).isChecked) }
        }

        if (position == 0) {
            card.updateLayoutParams<MarginLayoutParams> {
                topMargin = card.context.resources.getDimensionPixelSize(R.dimen.default_spacing)
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = acs.size
}

