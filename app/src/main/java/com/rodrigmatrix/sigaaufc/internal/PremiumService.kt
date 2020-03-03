package com.rodrigmatrix.sigaaufc.internal

import android.content.Context
import com.android.billingclient.api.*
import kotlinx.coroutines.*

class PremiumService(
    private val context: Context,
    private val listener: PurchasesUpdatedListener
): CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext = Dispatchers.Main + job
    private lateinit var billingClient: BillingClient
    private var purchaseUpdatedListener: PurchasesUpdatedListener = listener

    init {
        setupBillingClient()
    }

    private fun setupBillingClient() {
        billingClient = BillingClient
            .newBuilder(context)
            .enablePendingPurchases()
            .setListener(purchaseUpdatedListener)
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                }
            }
            override fun onBillingServiceDisconnected() {
            }
        })
    }

    fun checkIsPremium(): Boolean = runBlocking {
        val purchaseList = withContext(Dispatchers.IO) {
            billingClient.queryPurchases(BillingClient.SkuType.INAPP)
        }
        if(purchaseList.purchasesList.isEmpty()){
            return@runBlocking false
        }
        purchaseList.purchasesList.first().let {
            if (it.purchaseState == Purchase.PurchaseState.PURCHASED) {
                return@runBlocking true
            }
        }
        return@runBlocking false
    }




}