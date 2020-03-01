package com.rodrigmatrix.sigaaufc.firebase

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics


class FirebaseEvents(
    private val firebaseAnalytics: FirebaseAnalytics
) {

    fun addEvent(eventKey: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, eventKey)
        firebaseAnalytics.logEvent(eventKey, bundle)
    }

}
