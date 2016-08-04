package com.example.johnny.ssltest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends Activity {

    private static final String TAG = "ssltest";
    private static final String TEST_URL = "https://maps.googleapis.com/maps/api/place/textsearch/xml?query=New+York&sensor=true&key=AIzaSyD616RKopoEBR-5cfEW4yvEoDoQO13HFkg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonGet = (Button) findViewById(R.id.button);
        buttonGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        //HttpClient HttpClient = new DefaultHttpClient();
//                        HttpClient httpClient = getHttpClient();

                        //String urlString = "https://192.168.1.101/login";
                        String urlString = TEST_URL;

                        try {
                            URL url = null;
                            try {
                                url = new URL(urlString);
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }

//                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                            trustAllHosts();

                            HttpsURLConnection httpsURLConnection = null;
                            try {
                                httpsURLConnection = (HttpsURLConnection) url.openConnection();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                                @Override
                                public boolean verify(String s, SSLSession sslSession) {
                                    return true;
                                }
                            });

                            HttpURLConnection connection = httpsURLConnection;

                           // connection.setRequestMethod("POST");
                             connection.setRequestMethod("GET");
                            connection.setDoInput(true);
                            connection.setDoOutput(true);


//                            List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(2);
//                            nameValuePairs.add(new BasicNameValuePair("userId", "saltfactory"));
//                            nameValuePairs.add(new BasicNameValuePair("password", "password"));



                            OutputStream outputStream = connection.getOutputStream();
                            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                           // bufferedWriter.write(getURLQuery(nameValuePairs));
                           // bufferedWriter.write(TEST_URL);
                            bufferedWriter.flush();
                            bufferedWriter.close();
                            outputStream.close();

                            connection.connect();


                            StringBuilder responseStringBuilder = new StringBuilder();
                            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                                for (; ; ) {
                                    String stringLine = bufferedReader.readLine();
                                    if (stringLine == null) break;
                                    responseStringBuilder.append(stringLine + '\n');
                                }
                                bufferedReader.close();
                            }

                            connection.disconnect();

                            Log.d(TAG, responseStringBuilder.toString());


                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                };

                thread.start();
            }
        });

    }

    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType)
                    throws java.security.cert.CertificateException {
                // TODO Auto-generated method stub

            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType)
                    throws java.security.cert.CertificateException {
                // TODO Auto-generated method stub

            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getURLQuery(List<BasicNameValuePair> params){
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;

        for (BasicNameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                stringBuilder.append("&");

            try {
                stringBuilder.append(URLEncoder.encode(pair.getName(), "UTF-8"));
                stringBuilder.append("=");
                stringBuilder.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return stringBuilder.toString();
    }


}
