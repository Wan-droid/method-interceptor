package com.peach.privacy.plugin

class DisallowApi {
    private val methods = mutableListOf<Method>()

    companion object {

        private val privacy = DisallowApi()

        init {
            initMethods()
        }

        fun get() = privacy
        private fun initMethods() {
            privacy.run {
                add("android/accounts/AccountManager") {
                    dot("getAccounts", "()[Landroid/accounts/Account;")
                    dot("getAccountsByType", "(Ljava/lang/String;)[Landroid/accounts/Account;")
                }

                add("android/bluetooth/le/BluetoothLeScanner") {
                    dot("startScan", "(Landroid/bluetooth/le/ScanCallback;)V")
                }

                add("android/telephony/TelephonyManager") {
                    dot("getDeviceId")
                    dot("getImei")
                    dot("getSubscriberId")
                    dot("getSimSerialNumber")
                    dot("getLine1Number")
                    dot("getNetworkOperator")
                    dot("getSimCountryIso")
                    dot("getAllCellInfo", "()Ljava/util/List;")
                    dot("getCellLocation", "()Landroid/telephony/CellLocation;")
                }
                add("android/location/LocationManager") {
                    dot("requestLocationUpdates")
                }
                add("android/content/pm/PackageManager") {
                    dot("getInstalledApplications", "(I)Ljava/util/List;")
                    dot("getInstalledPackages", "(I)Ljava/util/List;")
                }
                add("android/net/wifi/WifiInfo") {
                    dot("getMacAddress", "()Ljava/lang/String;")
                    dot("getSSID")
                    dot("getBSSID")
                    dot("getScanResults")
                    dot("getConnectionInfo")
                }

                add("com/bun/miitmdid/interfaces/IdSupplier") {
                    dot("getOAID", "()Ljava/lang/String;")
                }
                add("android/hardware/SensorManager") {
                    dot("registerListener")
                }
                add("android/provider/Settings\$Secure") {
                    dot(
                        "getString",
                        "(Landroid/content/ContentResolver;Ljava/lang/String;)Ljava/lang/String;"
                    )
                }
            }
        }
    }

    fun add(className: String, content: Method.() -> Unit) {
        val method = Method(className)
        content.invoke(method)
        methods.add(method)
    }

    fun findByOwnerClass(ownerClassName: String?): Method? {
        if (ownerClassName.isNullOrBlank()) return null
        val filter = methods.filter { it.ownerClassName == ownerClassName }
        return when (filter.size) {
            0 -> {
                null
            }
            1 -> {
                filter.first()
            }
            else -> {
                throw IllegalStateException("PrivacyApi contain more than 2 same ownerClassName.")
            }
        }
    }
}

