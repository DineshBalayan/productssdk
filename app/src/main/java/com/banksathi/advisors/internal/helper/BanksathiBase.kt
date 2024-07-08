package com.banksathi.advisors.internal.helper

class BanksathiBase private constructor() {
    val TEST_BASE_URL = "https://tryagentsdkapi.banksathi.com/api/"
    val TEST_CLIENT_SECRET = "DEV4uguysSDK4Agent?m=maRch724"
    var advisorCode = ""
    var advisorMobile = ""
    var advisorName = ""
    var advisorEmail = ""

    companion object {
        @Volatile
        private var instance: BanksathiBase? = null

        fun getInstance(): BanksathiBase {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = BanksathiBase()
                    }
                }
            }
            return instance!!
        }
    }

    fun writeUserData(name:String,email:String,mobile:String,code:String) {
        advisorCode = code
        advisorName = name
        advisorEmail = email
        advisorMobile = mobile
    }
}