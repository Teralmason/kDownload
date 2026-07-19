package com.otomatik.indirici

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import android.util.Log

class MyAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        Toast.makeText(this, "Otomatik Link Yakalayıcı Aktif!", Toast.LENGTH_LONG).show()
        Log.d("OtoIndirici", "Servis sisteme bağlandı.")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        // İndirme tetikleyicisi buraya gelecek!
        // Şimdilik ekrandaki hareketleri okumaya başladığını logluyoruz.
        Log.d("OtoIndirici", "Bir eylem algılandı: ${event.eventType}")
    }

    override fun onInterrupt() {
        Log.d("OtoIndirici", "Servis durduruldu.")
    }
}
