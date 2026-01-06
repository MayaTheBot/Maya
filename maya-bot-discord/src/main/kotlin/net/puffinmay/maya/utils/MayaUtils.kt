package net.puffinmay.maya.utils

import net.puffinmay.maya.MayaInstance
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class MayaUtils(val instance: MayaInstance) {
    fun generateHmac(data: String): String {
        val hmacSecret = instance.config.internal.hmacSecret
        val mac = Mac.getInstance("HmacSHA256")
        val keySpec = SecretKeySpec(hmacSecret.toByteArray(Charsets.UTF_8), "HmacSHA256")
        mac.init(keySpec)
        val hmacBytes = mac.doFinal(data.toByteArray(Charsets.UTF_8))
        return hmacBytes.joinToString("") { "%02x".format(it) }
    }
}