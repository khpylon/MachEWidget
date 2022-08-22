package com.example.khughes.machewidget

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.example.khughes.machewidget.databinding.ActivityVehicleBinding

private lateinit var binding: ActivityVehicleBinding
private lateinit var mVehicleViewModel: VehicleViewModel
private lateinit var context: Context

class VehicleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVehicleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = applicationContext

        val adapter = VehicleListAdapter(VehicleDiff())
        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(this)

        mVehicleViewModel = ViewModelProvider(this).get(VehicleViewModel::class.java)
        mVehicleViewModel.allVehicles.observe(this) { list: List<VehicleIds?>? ->
            adapter.submitList(
                list
            )
        }
    }

    class VehicleViewHolder (itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val VINItemView: TextView
        val nicknameItemView: TextView
        val enabledView: CheckBox
        val imageView: ImageView

        companion object {
            fun create(parent: ViewGroup): VehicleViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.vehicleview_item, parent, false)
                return VehicleViewHolder(view)
            }
        }

        init {
            VINItemView = itemView.findViewById(R.id.VIN)
            nicknameItemView = itemView.findViewById(R.id.nickname)
            enabledView = itemView.findViewById(R.id.checkBox)
            imageView = itemView.findViewById(R.id.image)
        }
    }

    class VehicleListAdapter(diffCallback: DiffUtil.ItemCallback<VehicleIds>) :
        ListAdapter<VehicleIds, VehicleViewHolder>(diffCallback) {
        private var changing = false
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
            val tmp = VehicleViewHolder.create(parent)
            tmp.enabledView.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                if (!changing) {
                    val position = tmp.adapterPosition
                    if(position >= 0) {
                        val vehicle = getItem(position)
                        vehicle?.let {
                            val VIN = vehicle.vin
                            VIN?.let {
                                mVehicleViewModel.setEnable(VIN, isChecked)
                                vehicle.isEnabled = isChecked
                                notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
            return tmp
        }

        override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
            val current = getItem(position)
            val VIN = current.vin

            // if VIN isn't recognized, denote with a strike-through
            val text = SpannableString(VIN)
            if (!Vehicle.isVINRecognized(VIN)) {
                text.setSpan(StrikethroughSpan(), 0, VIN.length, 0)
            }
            holder.VINItemView.text = text
            holder.nicknameItemView.text = current.nickname
            val bmp = Utils.getRandomVehicleImage(context, VIN)
            if (bmp != null) {
                holder.imageView.setImageBitmap(bmp)
                holder.imageView.visibility = VISIBLE
            } else {
                holder.imageView.visibility = GONE
            }
            val nightModeFlags =
                holder.itemView.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            holder.VINItemView.setTextColor(Color.parseColor(if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO) "#000000" else "#FFFFFF"))
            holder.nicknameItemView.setTextColor(Color.parseColor(if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO) "#000000" else "#FFFFFF"))
            if (position % 2 == 1) {
                holder.itemView.setBackgroundColor(Color.parseColor(if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO) "#FFFFFF" else "#000000"))
            } else {
                holder.itemView.setBackgroundColor(Color.parseColor(if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO) "#F0F0F0" else "#202020"))
            }
            changing = true
            holder.enabledView.isChecked = current.isEnabled
            holder.enabledView.isEnabled =
                !current.isEnabled || mVehicleViewModel.countEnabledVehicle() > 1
            changing = false
        }
    }

    class VehicleDiff : DiffUtil.ItemCallback<VehicleIds>() {
        override fun areItemsTheSame(oldItem: VehicleIds, newItem: VehicleIds): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: VehicleIds, newItem: VehicleIds): Boolean {
            return oldItem == newItem
        }
    }

}