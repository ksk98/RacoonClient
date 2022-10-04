package com.bots.RaccoonClient.Communication;

import com.bots.RaccoonClient.Config;
import com.bots.RaccoonClient.Exceptions.SocketFactoryFailureException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

public abstract class SSLUtil {
    public static SSLSocketFactory getSocketFactory() throws SocketFactoryFailureException {
        SSLSocketFactory ssf;
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            KeyManagerFactory  kmf = KeyManagerFactory.getInstance("SunX509");
            KeyStore ks = KeyStore.getInstance("JKS");
            char[] passphrase = Config.localKeystorePassword.toCharArray();

            File keystoreFile = new File(Config.localKeystorePath);
            if (keystoreFile.exists())
                ks.load(new FileInputStream(Config.localKeystorePath), passphrase);
            else
                addCertfileToKeystore(ks);

            kmf.init(ks, passphrase);
            ctx.init(kmf.getKeyManagers(), null, null);

            ssf = ctx.getSocketFactory();
            return ssf;
        } catch (Exception e) {
            throw new SocketFactoryFailureException(e.toString());
        }
    }

    public static void addCertfileToKeystore(KeyStore keyStore) throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException {
        keyStore.load(null, null);
        FileInputStream fis;

        try { fis = new FileInputStream(Config.certfilePath); }
        catch (FileNotFoundException ignored) {
            throw new CertificateException("Could not find " + Config.certfilePath);
        }

        BufferedInputStream bis = new BufferedInputStream(fis);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate cert = null;
        while (bis.available() > 0)
        {
            cert = cf.generateCertificate(bis);
            keyStore.setCertificateEntry("SGCert", cert);
        }
        keyStore.setCertificateEntry("SGCert", cert);
        keyStore.store(new FileOutputStream(Config.localKeystorePath), Config.localKeystorePassword.toCharArray());

        return;
    }
}
