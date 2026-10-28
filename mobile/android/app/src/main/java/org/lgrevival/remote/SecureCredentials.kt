package org.lgrevival.remote
import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import org.lgrevival.core.Credentials
class SecureCredentials(context:Context):Credentials {
 private val prefs=context.getSharedPreferences("pairing",Context.MODE_PRIVATE)
 private fun encryptionKey():SecretKey {
  val store=KeyStore.getInstance("AndroidKeyStore").apply {load(null)}
  (store.getKey("revival-pairing",null) as? SecretKey)?.let {return it}
  return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore").apply {
   init(KeyGenParameterSpec.Builder("revival-pairing",KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build())
  }.generateKey()
 }
 override fun put(host:String,key:String) {
  val cipher=Cipher.getInstance("AES/GCM/NoPadding").apply {init(Cipher.ENCRYPT_MODE,encryptionKey());updateAAD(host.toByteArray())}
  val bytes=cipher.doFinal(key.toByteArray())
  check(prefs.edit().putString(host,Base64.encodeToString(cipher.iv+bytes,Base64.NO_WRAP)).commit())
 }
 override fun get(host:String):String? {
  val value=prefs.getString(host,null)?:return null
  val bytes=Base64.decode(value,Base64.NO_WRAP);require(bytes.size>28)
  val cipher=Cipher.getInstance("AES/GCM/NoPadding").apply {init(Cipher.DECRYPT_MODE,encryptionKey(),GCMParameterSpec(128,bytes.copyOfRange(0,12)));updateAAD(host.toByteArray())}
  return String(cipher.doFinal(bytes.copyOfRange(12,bytes.size)),Charsets.UTF_8)
 }
 override fun forget(host:String) {check(prefs.edit().remove(host).commit())}
}
