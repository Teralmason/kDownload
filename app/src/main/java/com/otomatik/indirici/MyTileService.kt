package com.otomatik.indirici

import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class MyTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        qsTile.icon = Icon.createWithResource(this, R.drawable.ic_tile)
        qsTile.state = Tile.STATE_ACTIVE
        qsTile.updateTile()
    }

    override fun onClick() {
        super.onClick()

        val intent = Intent(this, TriggerActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            startActivityAndCollapse(pendingIntent)
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }

    override fun onTileAdded() {
        super.onTileAdded()
        qsTile.label = "Videoyu İndir"
        qsTile.updateTile()
    }
}
