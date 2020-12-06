package com.rodrigmatrix.sigaaufc.internal

import android.app.Activity
import com.android.billingclient.api.*
import kotlinx.coroutines.*

class PremiumPurchaseService(
    private val activity: Activity
): CoroutineScope {

    private lateinit var skuProduct: SkuDetails
    private val job = SupervisorJob()
    override val coroutineContext = Dispatchers.Main + job
    private var purchaseUpdatedListener: PurchasesUpdatedListener? = null
    private lateinit var billingClient: BillingClient
    private var skuList = listOf("premium")

    init {
        setupBillingClient()
    }

    private fun setupBillingClient() {
        billingClient = BillingClient
            .newBuilder(activity)
            .enablePendingPurchases()
            .setListener(purchaseUpdatedListener)
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                    getProducts()
                }
            }
            override fun onBillingServiceDisconnected() {
            }
        })
    }

    private fun getProducts() = launch {
        if (billingClient.isReady) {
            val params = SkuDetailsParams
                .newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.INAPP)
                .build()
            val skuDetailsResult = withContext(Dispatchers.IO) {
                billingClient.querySkuDetails(params)
            }
            skuDetailsResult.skuDetailsList?.let {
                skuProduct = it.first()
            }

        }
    }



    fun purchaseProduct(){
        try {
            val billingFlowParams = BillingFlowParams
                .newBuilder()
                .setSkuDetails(skuProduct)
                .build()
            billingClient.launchBillingFlow(activity, billingFlowParams)
        }
        catch(e: Exception){
            e.printStackTrace()
        }
    }


}