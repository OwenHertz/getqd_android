package com.app.upincode.getqd.utils;

import java.io.InputStream;
import java.security.KeyStore;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;

import android.content.Context;

import com.app.upincode.getqd.R;

public class GQHttpsClient extends DefaultHttpClient {

    /*  Information from Herbert Rush about how the keystore got built.  We executed these three commands after getting the Certificate
    for RSA using the MMC Dos Command.  Stored in herbrush.cer
    keytool -importcert -v -trustcacerts -file "herbrush.cer" -alias getqd  -keystore C:\codeproject\codeprojectssl.keystore  -provider org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath C:\codeproject\bcprov-jdk15on-146.jar -storetype BKS -storepass maniac
    keytool -list -keystore C:\codeproject\codeprojectssl.keystore  -provider org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath C:\codeproject\bcprov-jdk15on-146.jar -storetype BKS -storepass maniac
    copy codeprojectssl.keystore C:\Users\herbert\Documents\GetQD-Android\GetQD\app\src\main\res\raw

     This link gave the best description on dealing with setting https for android.
     http://transoceanic.blogspot.com/2011/11/android-import-ssl-certificate-and-use.html

     */
    final Context context;

    public GQHttpsClient(Context context) {
        this.context = context;
    }

    @Override
    protected ClientConnectionManager createClientConnectionManager() {
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        // Register for port 443 our SSLSocketFactory with our keystore
        // to the ConnectionManager
        registry.register(new Scheme("https", newSslSocketFactory(), 443));
        return new SingleClientConnManager(getParams(), registry);
    }

    private SSLSocketFactory newSslSocketFactory() {
        try {
            // Get an instance of the Bouncy Castle KeyStore format
            KeyStore trusted = KeyStore.getInstance("BKS");
            // Get the raw resource, which contains the keystore with
            // your trusted certificates (root and any intermediate certs)
            InputStream in = context.getResources().openRawResource(R.raw.codeprojectssl);
            try {
                // Initialize the keystore with the provided trusted certificates
                // Also provide the password of the keystore
                trusted.load(in, "maniac".toCharArray());
            } finally {
                in.close();
            }
            // Pass the keystore to the SSLSocketFactory. The factory is responsible
            // for the verification of the server certificate.
            SSLSocketFactory sf = new SSLSocketFactory(trusted);
            // Hostname verification from certificate
            // http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html#d4e506
            sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
            return sf;
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}
