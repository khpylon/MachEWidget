package com.example.khughes.machewidget

import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.Editable
import android.text.InputFilter
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
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
import java.lang.ref.WeakReference
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

private lateinit var mVehicleViewModel: VehicleViewModel
private lateinit var activity: AppCompatActivity
private lateinit var userId: String
private lateinit var userInfo: UserInfo

class VehicleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVehicleBinding
    private lateinit var context: Context
    private lateinit var newVINWidget: TextInputLayout

    private fun getStatus(context: Context, VIN: String, nickname: String) {
        val statusHandler: Handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                val bundle = msg.data
                val action = bundle.getString("action")
                if (action == Constants.STATE_HAVE_TOKEN_AND_VIN) {
                    Toast.makeText(context, "Vehicle status successfully retrieved.", Toast.LENGTH_LONG)
                        .show()
                    NetworkCalls.getVehicleImage(context, VIN, userInfo.country!!)
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
                if (action == Constants.STATE_HAVE_TOKEN_AND_VIN) {
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
            NetworkCalls.refreshAccessToken(refreshHandler, context, userId, userInfo.refreshToken!!)
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
            // Create the alert dialog
            val layout = layoutInflater.inflate(R.layout.newvehicle, null)
            newVINWidget = layout.findViewById<TextInputLayout>(R.id.new_vehicle_vin)
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

                val nickname = newNicknameWidget.editText?.text.toString()

                mVehicleViewModel.allVehicles.value?.let {
                    for (tmp in it) {
                        if (tmp.vin == VIN) {
                            Toast.makeText(context, "This VIN already exists.", Toast.LENGTH_LONG)
                                .show()
                            return@OnClickListener
                        } else if (nickname != "" && tmp.nickname == nickname) {
                            Toast.makeText(context, "This nickname is already in use.", Toast.LENGTH_LONG)
                                .show()
                            return@OnClickListener
                        }
                    }
                }
                getStatus(context, VIN, nickname)
                dialog.dismiss()
            })
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

            // Force text to uppercase
            newVINWidget.editText?.let{ it.filters += InputFilter.AllCaps() }

            // Process VIN text as it's typed
            newVINWidget.editText?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val VIN = s.toString()

                    // When we recognize something in the VIN, display a specific hint
                    var message: String
                    if (Vehicle.isVINRecognized(VIN)) {
                        message = Vehicle.getVehicle(VIN).name
                        if (message != "") {
                            message = "VIN appears to be for " +
                                    (if ("AEIOU".contains(message.subSequence(0,1))) "an " else "a ") + message
                        }
                        newVINWidget.hintTextColor = ContextCompat.getColorStateList(context,R.color.light_blue_200)
                    } else {
                        message = context.resources.getString(R.string.vehicles_vin)
                        newVINWidget.hintTextColor = ContextCompat.getColorStateList(context,R.color.light_blue_600)
                    }
                    newVINWidget.hint = message

                    // Display helper text if VIN string is too short; otherwise, enable the OK button
                    val size = s.toString().length
                    newVINWidget.helperText = if (size == 17) "" else context.resources.getString(R.string.correct_VIN_length)
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = size == 17
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
        }

        val adapter = VehicleListAdapter(VehicleDiff(), context)
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
            if(userInfo.userId == null) {
                binding.addVehicle.hide()
                AlertDialog.Builder(
                    ContextThemeWrapper(activity, R.style.AlertDialogCustom))
                    .setTitle("Error")
                    .setMessage("Vehicles can't be added without a valid user; please log in first.")
                    .setPositiveButton(
                        android.R.string.ok
                    ) { _: DialogInterface?, _: Int -> finish() }
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            }
        }
    }

    private suspend fun getUserInfo(context: Context, userId: String): UserInfo =
        coroutineScope {
            withContext(Dispatchers.IO) {
                UserInfoDatabase.getInstance(context).userInfoDao().findUserInfo(userId) ?: UserInfo()
//                if (user == null) {
//                    val tmp = UserInfo()
//                    tmp.userId = ""
//                    tmp
//                } else {
//                    user
//                }
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

    class VehicleListAdapter(diffCallback: DiffUtil.ItemCallback<VehicleIds>, context: Context) :
        ListAdapter<VehicleIds, VehicleViewHolder>(diffCallback) {
        private val mContext: WeakReference<Context> = WeakReference(context)
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
                                vehicle.enabled = isChecked
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
            val VIN = current.vin as String

            // if VIN isn't recognized, denote with a strike-through
            val text = SpannableString(VIN)
            if (!Vehicle.isVINRecognized(VIN)) {
                text.setSpan(StrikethroughSpan(), 0, VIN.length, 0)
            }
            holder.VINItemView.text = text
            holder.nicknameItemView.text = current.nickname
            val bmp = VehicleImages.getRandomImage(mContext.get()!!, VIN)
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
            holder.enabledView.isChecked = current.enabled
            holder.enabledView.isEnabled =
                !current.enabled || mVehicleViewModel.countEnabledVehicle() > 1
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
