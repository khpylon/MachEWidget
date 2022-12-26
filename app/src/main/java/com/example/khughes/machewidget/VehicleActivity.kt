package com.example.khughes.machewidget

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.GONE
import androidx.recyclerview.widget.RecyclerView.VISIBLE
import com.example.khughes.machewidget.databinding.ActivityVehicleBinding
import com.example.khughes.machewidget.db.UserInfoDatabase
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

private lateinit var binding: ActivityVehicleBinding
private lateinit var mVehicleViewModel: VehicleViewModel
private lateinit var context: Context
private lateinit var activity: AppCompatActivity
private lateinit var userId: String
private lateinit var userInfo: UserInfo

class VehicleActivity : AppCompatActivity() {

    private fun getStatus(context: Context, VIN: String?, nickname: String?) {
        val statusHandler: Handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                val bundle = msg.data
                val action = bundle.getString("action")
                if (action == Constants.STATE_HAVE_TOKEN_AND_VIN) {
                    Toast.makeText(
                        context,
                        "Vehicle status successfully retrieved.",
                        Toast.LENGTH_LONG
                    ).show()
                    NetworkCalls.getVehicleImage(context, userInfo.accessToken, VIN, userInfo.country)
                } else {
                    Toast.makeText(context, "Unable to retrieve vehicle status.", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

        val refreshHandler: Handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                val bundle = msg.data
                val action = bundle.getString("action")
                if (action == Constants.STATE_HAVE_TOKEN) {
                    NetworkCalls.getStatus(statusHandler, context, userInfo, VIN, nickname)
                } else {
                    Toast.makeText(context, "Unable to refresh access token.", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

        val timeout = userInfo.expiresIn
        val time = LocalDateTime.now(ZoneId.systemDefault())
        val nowtime = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        if (timeout < nowtime) {
            NetworkCalls.refreshAccessToken(refreshHandler, context, userId, userInfo.refreshToken)
        } else {
            NetworkCalls.getStatus(statusHandler, context, userInfo, VIN, nickname)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVehicleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = applicationContext
        activity = this

        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        userId =
            prefs.getString(context.resources.getString(R.string.userId_key), "") as String

        binding.addVehicle.setOnClickListener {
            val layout = layoutInflater.inflate(R.layout.newvehicle, null)
            val newVINWidget = layout.findViewById<TextInputLayout>(R.id.new_vehicle_vin)
            val newNicknameWidget = layout.findViewById<TextInputLayout>(R.id.new_vehicle_nickname)

            val dialog = AlertDialog.Builder(
                ContextThemeWrapper(
                    activity,
                    R.style.AlertDialogCustom
                )
            )
                .setMessage("Enter new vehicle information:")
                .setView(layout)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create()
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(View.OnClickListener {

                val VIN = newVINWidget.editText?.text.toString().uppercase(Locale.getDefault())

                if (VIN.length != 17 || !VIN.matches("^[A-Z0-9]*$".toRegex())) {
                    Toast.makeText(context, "The VIN is not valid.", Toast.LENGTH_LONG).show()
                    return@OnClickListener
                }

                val nickname = newNicknameWidget.editText?.text.toString()

                mVehicleViewModel.allVehicles.value?.let {
                    for (tmp in it) {
                        if (tmp.vin == VIN) {
                            Toast.makeText(context, "This VIN already exists.", Toast.LENGTH_LONG)
                                .show()
                            return@OnClickListener
                        } else if (tmp.nickname == nickname) {
                            Toast.makeText(
                                context,
                                "This nickname is already in use.",
                                Toast.LENGTH_LONG
                            ).show()
                            return@OnClickListener
                        }
                    }
                }
                getStatus(context, VIN, nickname)
                dialog.dismiss()
            })
        }

        val adapter = VehicleListAdapter(VehicleDiff())
        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(this)

        mVehicleViewModel = ViewModelProvider(this).get(VehicleViewModel::class.java)
        mVehicleViewModel.allVehicles.observe(this) { list: List<VehicleIds?>? ->
            adapter.submitList(
                list
            )
        }
        CoroutineScope(Dispatchers.Main).launch {
            userInfo = getUserInfo(context, userId)
        }
    }

    private suspend fun getUserInfo(context: Context, userId: String): UserInfo =
        coroutineScope {
            withContext(Dispatchers.IO) {
                val userInfo =
                    UserInfoDatabase.getInstance(context).userInfoDao().findUserInfo(userId)
                userInfo
            }
        }

    class VehicleViewHolder(itemView: View) :
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
                    if (position >= 0) {
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
            val bmp = VehicleImages.getRandomImage(context, VIN)
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