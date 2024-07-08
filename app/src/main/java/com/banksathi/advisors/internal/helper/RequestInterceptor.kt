import com.banksathi.advisors.internal.helper.BanksathiBase
import okhttp3.*
import java.security.MessageDigest
import java.util.*

class RequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val instance = BanksathiBase.getInstance()

        val now = Calendar.getInstance().timeInMillis.toString()
        val input = now + instance.PROD_CLIENT_SECRET + now + instance.advisorCode + now
        val md5Input = input.md5()
        val signatureMd = md5Input.md5() + md5Input
        val signature = signatureMd.md5() + signatureMd

        val newHeaders = originalRequest.headers().newBuilder()
            .add("Accept", "application/json")
            .add("content-type", "application/json")
            .add("ucode", instance.advisorCode)
            .add("signature", signature)
            .add("tcode", now)
            .add("agentMobile", instance.advisorMobile)
            .add("agentName", instance.advisorName)
            .add("agentEmail", instance.advisorEmail)
            .build()

        val newRequest = originalRequest.newBuilder()
            .headers(newHeaders)
            .build()

        // Proceed with the new request
        return chain.proceed(newRequest)
    }
}

fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(toByteArray())
    return digest.joinToString("") { "%02x".format(it) }
}