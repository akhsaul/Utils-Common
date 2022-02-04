package me.akhsaul.common.custom

import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * a Class for ignore all SSL issue
 * like SSL-Self-Signed.
 * Use it with CAUTION,
 * because it can be danger
 * for Man in The Middle Attack (Hacker)
 * */
internal class MySSL {
    /**
     * Create a Trust Manager that does not validate certificate chains.
     * Use it with CAUTION
     * */
    private val trustAllCerts: Array<TrustManager> = arrayOf(
        object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }
    )

    fun getSSLSocket(): SSLSocketFactory {
        return SSLContext.getInstance("SSL").apply {
            // Install all Cert with TrustManager
            // Install the all-trusting trust manager
            init(null, trustAllCerts, SecureRandom())
        }.socketFactory // Create a ssl socket factory with our all-trusting manager
    }

    fun getTrustManager(): X509TrustManager {
        return trustAllCerts[0] as X509TrustManager
    }
}