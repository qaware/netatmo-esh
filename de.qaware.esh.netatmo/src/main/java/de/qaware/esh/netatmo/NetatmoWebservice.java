package de.qaware.esh.netatmo;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import de.qaware.esh.netatmo.model.StationData;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class NetatmoWebservice {
    public static final NetatmoWebservice INSTANCE = new NetatmoWebservice();

    private static final String CLIENT_ID = "";
    private static final String CLIENT_SECRET = "";
    private static final String USERNAME = "";
    private static final String PASSWORD = "";
    private static final String SCOPES = "read_station read_thermostat write_thermostat";
    private static final String REQUEST_TOKEN_URL = "https://api.netatmo.net/oauth2/token";
    private static final String STATION_DATA_URL = "https://api.netatmo.net/api/getstationsdata";

    private final Logger logger = LoggerFactory.getLogger(NetatmoWebservice.class);
    private final CloseableHttpClient client;
    private final Gson gson;

    private OAuthToken token;

    private NetatmoWebservice() {
        this.client = createHttpClient();
        this.gson = new Gson();
    }

    private CloseableHttpClient createHttpClient() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManager trustAll = new X509TrustManager() {

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }
            };

            sslContext.init(null, new TrustManager[]{trustAll}, null);

            SSLSocketFactory sslSocketFactory = new SSLSocketFactory(sslContext,
                    SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager cm = new PoolingClientConnectionManager();
            cm.getSchemeRegistry().register(new Scheme("https", 443, sslSocketFactory));

            return new DefaultHttpClient(cm);
        } catch (Exception e) {
            logger.error("Exception while creating http client", e);
            return null;
        }
    }

    public void authenticate() throws IOException {
        HttpPost request = new HttpPost(REQUEST_TOKEN_URL);
        List<NameValuePair> formData = new ArrayList<>();
        formData.add(new BasicNameValuePair("grant_type", "password"));
        formData.add(new BasicNameValuePair("client_id", CLIENT_ID));
        formData.add(new BasicNameValuePair("client_secret", CLIENT_SECRET));
        formData.add(new BasicNameValuePair("username", USERNAME));
        formData.add(new BasicNameValuePair("password", PASSWORD));
        formData.add(new BasicNameValuePair("scope", SCOPES));

        UrlEncodedFormEntity body = new UrlEncodedFormEntity(formData);
        request.setEntity(body);

        try (CloseableHttpResponse response = client.execute(request)) {
            checkStatus(response);

            String json = EntityUtils.toString(response.getEntity());
            token = gson.fromJson(json, OAuthToken.class);
        }
    }

    private void checkStatus(HttpResponse response) {
        int status = response.getStatusLine().getStatusCode();
        if (status / 100 != 2) {
            throw new IllegalStateException("Expected status code 2xx, got " + status);
        }
    }

    public StationData fetchStationData() throws IOException {
        HttpGet request = new HttpGet(STATION_DATA_URL);
        request.setHeader("Authorization", "Bearer " + token.getAccessToken());

        try (CloseableHttpResponse response = client.execute(request)) {
            checkStatus(response);
            String json = EntityUtils.toString(response.getEntity());

            return gson.fromJson(json, StationData.class);
        }
    }

    public static class OAuthToken {
        @SerializedName("access_token")
        private String accessToken;

        public String getAccessToken() {
            return accessToken;
        }
    }
}
