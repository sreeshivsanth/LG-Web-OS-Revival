package org.lgrevival.core
import java.net.*
import javax.net.ssl.*
import java.security.cert.CertificateException
object Diagnostics {
 fun connection(t:Throwable):String {
  val causes=generateSequence(t){it.cause}.take(10).toList()
  return when {
   causes.any {it is SSLPeerUnverifiedException}->"TLS identity check failed (certificate or hostname). No trust bypass; legacy mode requires explicit consent."
   causes.any {it is CertificateException || it is java.security.cert.CertPathValidatorException}->"TLS certificate is not trusted. Per-TV fingerprint enrollment is not implemented."
   causes.any {it is SSLHandshakeException}->"TLS handshake failed: certificate trust or protocol compatibility. No automatic fallback."
   causes.any {it is SSLException}->"Secure transport failed; TLS version/certificate needs investigation."
   causes.any {it is SocketTimeoutException}->"TV connection timed out. Check power, address and LAN isolation."
   causes.any {it is ConnectException || it is NoRouteToHostException}->"TCP connection failed. TV may be offline or blocked by LAN routing."
   causes.any {it is UnknownHostException}->"Address resolution failed. Check the TV address."
   else->"WebSocket connection failed. Check LAN reachability and TV service availability."
  }
 }
}
