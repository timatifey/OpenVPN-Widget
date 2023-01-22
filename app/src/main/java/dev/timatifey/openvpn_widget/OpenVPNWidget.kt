package dev.timatifey.openvpn_widget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.preference.PreferenceManager.getDefaultSharedPreferences

class OpenVPNWidget : AppWidgetProvider() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.M)
@SuppressLint("RemoteViewLayout")
internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val preferences = getDefaultSharedPreferences(context)
    val profileName = preferences.getString("profile_name", null)
    val views = RemoteViews(context.packageName, R.layout.open_vpn_widget).apply {
        setOnClickPendingIntent(
            R.id.appwidget_settings,
            context.buildOpenSettingsActivityIntent()
        )
        if (profileName == null) {
            setTextViewText(R.id.appwidget_profile, "Need pick profile")
            setViewVisibility(R.id.appwidget_btn_connect, View.GONE)
            setViewVisibility(R.id.appwidget_btn_disconnect, View.GONE)
        } else {
            setTextViewText(R.id.appwidget_profile, profileName)
            setOnClickPendingIntent(
                R.id.appwidget_btn_connect,
                context.buildConnectByProfileIntent(profileName)
            )
            setOnClickPendingIntent(
                R.id.appwidget_btn_disconnect,
                context.buildDisconnectIntent()
            )
        }
    }
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

@RequiresApi(Build.VERSION_CODES.M)
internal fun Context.buildOpenSettingsActivityIntent() = PendingIntent.getActivity(
    this,
    0,
    Intent(this, SettingsActivity::class.java),
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
)

@RequiresApi(Build.VERSION_CODES.M)
internal fun Context.buildConnectByProfileIntent(profileName: String) = PendingIntent.getActivity(
    this,
    0,
    Intent().apply {
        action = "net.openvpn.openvpn.CONNECT"
        component = ComponentName(
            "net.openvpn.openvpn",
            "net.openvpn.unified.MainActivity"
        )
        putExtra("net.openvpn.openvpn.AUTOCONNECT", true)
        putExtra("net.openvpn.openvpn.AUTOSTART_PROFILE_NAME", "PC $profileName")
    },
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
)

@RequiresApi(Build.VERSION_CODES.M)
internal fun Context.buildDisconnectIntent() = PendingIntent.getActivity(
    this,
    0,
    Intent().apply {
        action = "net.openvpn.openvpn.DISCONNECT"
        component = ComponentName(
            "net.openvpn.openvpn",
            "net.openvpn.unified.MainActivity"
        )
        putExtra("net.openvpn.openvpn.STOP", true)
    },
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
)