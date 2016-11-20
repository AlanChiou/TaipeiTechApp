package com.taipeitech.utility;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.CookieManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Connector {
    private static final int TIMEOUT = 10000;
    private static final int BUFFER_SIZE = 4096;
    private static TrustManager[] s_trustAllCerts = new TrustManager[] { new X509TrustManager() {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            // Not implemented
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            // Not implemented
        }
    } };

    static {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, s_trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDataByPost(String uri,
                                       Map<String, String> params, String charsetName) throws Exception {
        return getDataByPost(uri, params, charsetName, null);
    }

    public static String getDataByPost(String uri,
                                          Map<String, String> params, String charsetName, String referer) throws Exception {
        URL urL = new URL(uri);
        HttpURLConnection httpURLConnection = (HttpURLConnection) urL.openConnection();
        httpURLConnection.setReadTimeout(TIMEOUT);
        httpURLConnection.setConnectTimeout(TIMEOUT);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestProperty("Cookie", getCookieFromAppCookieManager(uri));
        httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.76 Mobile Safari/537.36");
        httpURLConnection.setRequestProperty("Upgrade-Insecure-Requests", "1");
        if (!TextUtils.isEmpty(referer)) {
            httpURLConnection.setRequestProperty("Referer", referer);
        }
        String paramsString = null;
        if(params != null) {
            paramsString = getQuery(params);
        }
        if (!TextUtils.isEmpty(paramsString)) {
            httpURLConnection.setFixedLengthStreamingMode(paramsString.getBytes().length);
            OutputStream os = httpURLConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, charsetName));
            writer.write(paramsString);
            writer.flush();
            writer.close();
            os.close();
        }

        int responseCode = httpURLConnection.getResponseCode();
        int redirectCount = 0;
        while ((responseCode == HttpURLConnection.HTTP_MOVED_TEMP
                || responseCode == HttpURLConnection.HTTP_MOVED_PERM
                || responseCode == HttpURLConnection.HTTP_SEE_OTHER) && redirectCount < 3) {
            // get redirect url from "location" header field
            String newUrl = httpURLConnection.getHeaderField("Location");
            // get the cookie if need, for login
            String cookies = httpURLConnection.getHeaderField("Set-Cookie");
            // open the new connnection again
            httpURLConnection = (HttpURLConnection) new URL(newUrl).openConnection();
            httpURLConnection.setRequestProperty("Cookie", cookies);
            httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.76 Mobile Safari/537.36");
            httpURLConnection.setReadTimeout(TIMEOUT);
            httpURLConnection.setConnectTimeout(TIMEOUT);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            responseCode = httpURLConnection.getResponseCode();
            redirectCount++;
        }

        if (responseCode == HttpURLConnection.HTTP_OK) {
            String dataString = convertStreamToString(httpURLConnection.getInputStream(), charsetName);
            return dataString;
        } else {
            throw new Exception();
        }
    }

    public static String getDataByGet(String uri, String charsetName)
            throws Exception {
        return convertStreamToString(getInputStreamByGet(uri, null),
                charsetName);
    }

    public static String getDataByGet(String uri, String charsetName, String referer)
            throws Exception {
        return convertStreamToString(getInputStreamByGet(uri,referer),
                charsetName);
    }

    public static InputStream getInputStreamByGet(String uri, String referer)
            throws Exception {
        URL urL = new URL(uri);
        HttpURLConnection httpURLConnection = (HttpURLConnection) urL.openConnection();
        httpURLConnection.setRequestProperty("Cookie", getCookieFromAppCookieManager(uri));
        httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.76 Mobile Safari/537.36");
        httpURLConnection.setRequestProperty("Upgrade-Insecure-Requests", "1");
        if (!TextUtils.isEmpty(referer)) {
            httpURLConnection.setRequestProperty("Referer", referer);
        }
        httpURLConnection.setReadTimeout(TIMEOUT);
        httpURLConnection.setConnectTimeout(TIMEOUT);
        httpURLConnection.setRequestMethod("GET");

        int responseCode = httpURLConnection.getResponseCode();
        int redirectCount = 0;
        while ((responseCode == HttpURLConnection.HTTP_MOVED_TEMP
                || responseCode == HttpURLConnection.HTTP_MOVED_PERM
                || responseCode == HttpURLConnection.HTTP_SEE_OTHER) && redirectCount < 3) {
            // get redirect url from "location" header field
            String newUrl = httpURLConnection.getHeaderField("Location");
            // get the cookie if need, for login
            String cookies = httpURLConnection.getHeaderField("Set-Cookie");
            // open the new connnection again
            httpURLConnection = (HttpURLConnection) new URL(newUrl).openConnection();
            httpURLConnection.setRequestProperty("Cookie", cookies);
            httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.76 Mobile Safari/537.36");
            httpURLConnection.setReadTimeout(TIMEOUT);
            httpURLConnection.setConnectTimeout(TIMEOUT);
            httpURLConnection.setRequestMethod("GET");
            responseCode = httpURLConnection.getResponseCode();
            redirectCount++;
        }

        if (responseCode == HttpsURLConnection.HTTP_OK) {
            return httpURLConnection.getInputStream();
        } else {
            httpURLConnection.disconnect();
            throw new Exception();
        }
    }

    private static String convertStreamToString(InputStream is,
                                                String charsetName) throws IOException {
        InputStreamReader isr;
        StringBuilder buffer = new StringBuilder();
        isr = new InputStreamReader(is, charsetName);
        Reader in = new BufferedReader(isr);
        int ch;
        while ((ch = in.read()) != -1) {
            buffer.append((char) ch);
        }
        isr.close();
        is.close();
        return buffer.toString();
    }

    public static boolean download(Context context, String file_url,
                                   String file_name) {
        try {
            URL urL = new URL(file_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) urL.openConnection();
            httpURLConnection.setRequestProperty("Cookie", getCookieFromAppCookieManager(file_url));
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.76 Mobile Safari/537.36");
            httpURLConnection.setReadTimeout(TIMEOUT);
            httpURLConnection.setConnectTimeout(TIMEOUT);
            httpURLConnection.setRequestMethod("GET");

            int responseCode = httpURLConnection.getResponseCode();
            int redirectCount = 0;
            while ((responseCode == HttpURLConnection.HTTP_MOVED_TEMP
                    || responseCode == HttpURLConnection.HTTP_MOVED_PERM
                    || responseCode == HttpURLConnection.HTTP_SEE_OTHER) && redirectCount < 3) {
                // get redirect url from "location" header field
                String newUrl = httpURLConnection.getHeaderField("Location");
                // get the cookie if need, for login
                String cookies = httpURLConnection.getHeaderField("Set-Cookie");
                // open the new connnection again
                httpURLConnection = (HttpURLConnection) new URL(newUrl).openConnection();
                httpURLConnection.setRequestProperty("Cookie", cookies);
                httpURLConnection.setReadTimeout(TIMEOUT);
                httpURLConnection.setConnectTimeout(TIMEOUT);
                httpURLConnection.setRequestMethod("GET");
                responseCode = httpURLConnection.getResponseCode();
                redirectCount++;
            }

            File root = context.getCacheDir();
            root.mkdirs();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                InputStream inputStream = httpURLConnection.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(root.getAbsolutePath() + "/"
                        + file_name);
                int bytesRead;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.close();
                inputStream.close();
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getRedirectUri(String uri) throws Exception {
        URLConnection con = new URL(uri).openConnection();
        con.connect();
        InputStream is = con.getInputStream();
        String redirectUri = con.getURL().toString();
        is.close();
        return redirectUri;
    }

    private static String getQuery(Map<String, String> params) {
        Uri.Builder builder = new Uri.Builder();
        Set<String> keys = params.keySet();
        for (String key : keys) {
            builder.appendQueryParameter(key, params.get(key));
        }
        return builder.build().getEncodedQuery();
    }

    public static String getDataByHTTPSPost(String uri,
                                       Map<String, String> params, String charsetName) throws Exception {
        URL urL = new URL(uri);
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) urL.openConnection();
        httpsURLConnection.setReadTimeout(TIMEOUT);
        httpsURLConnection.setConnectTimeout(TIMEOUT);
        httpsURLConnection.setRequestMethod("POST");
        httpsURLConnection.setDoInput(true);
        httpsURLConnection.setDoOutput(true);

        OutputStream os = httpsURLConnection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, charsetName));
        if (params != null) {
            writer.write(getQuery(params));
        }
        writer.flush();
        writer.close();
        os.close();

        int responseCode = httpsURLConnection.getResponseCode();
        int redirectCount = 0;
        while ((responseCode == HttpURLConnection.HTTP_MOVED_TEMP
                || responseCode == HttpURLConnection.HTTP_MOVED_PERM
                || responseCode == HttpURLConnection.HTTP_SEE_OTHER) && redirectCount < 3) {
            // get redirect url from "location" header field
            String newUrl = httpsURLConnection.getHeaderField("Location");
            // get the cookie if need, for login
            String cookies = httpsURLConnection.getHeaderField("Set-Cookie");
            // open the new connnection again
            httpsURLConnection = (HttpsURLConnection) new URL(newUrl).openConnection();
            httpsURLConnection.setRequestProperty("Cookie", cookies);
            httpsURLConnection.setReadTimeout(TIMEOUT);
            httpsURLConnection.setConnectTimeout(TIMEOUT);
            httpsURLConnection.setRequestMethod("POST");
            httpsURLConnection.setDoInput(true);
            httpsURLConnection.setDoOutput(true);
            responseCode = httpsURLConnection.getResponseCode();
            redirectCount++;
        }

        if (responseCode == HttpURLConnection.HTTP_OK) {
            String dataString = convertStreamToString(httpsURLConnection.getInputStream(), charsetName);
            return dataString;
        } else {
            throw new Exception();
        }
    }


    public static String getCookieFromAppCookieManager(String url) throws MalformedURLException {
        CookieManager cookieManager = CookieManager.getInstance();
        if (cookieManager == null)
            return null;
        cookieManager.setAcceptCookie(true);
        String rawCookieHeader = null;
        URL parsedURL = new URL(url);

        // Extract Set-Cookie header value from Android app CookieManager for this URL
        rawCookieHeader = cookieManager.getCookie(parsedURL.getHost());
        if (rawCookieHeader == null)
            return null;
        return rawCookieHeader;
    }
}
