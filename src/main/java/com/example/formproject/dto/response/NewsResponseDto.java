package com.example.formproject.dto.response;

import com.example.formproject.FinalValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class NewsResponseDto implements Serializable {
    private String time;
    private String title;
    private String descript;
    private String link;
    private String imageUrl;
    private String pubDate;
    private String article;

    public NewsResponseDto(JSONObject object) throws IOException {
        this.title = object.get("title").toString().replaceAll("&quot;","");
        this.descript = object.get("description").toString().replaceAll("<b>","").replaceAll("</b>","");
        this.link = object.get("link").toString();
        this.pubDate = object.get("pubDate").toString();
        try {
            Connection conn = Jsoup.connect(this.link);
            Document document = conn.get();
            Element head = document.getElementsByTag("head").get(0);
            try {
                this.imageUrl = head.getElementsByAttributeValue("property", "og:image").get(0).attr("content");
            } catch (IndexOutOfBoundsException e) {
                this.imageUrl = "";
            }
            try {
                this.article = head.getElementsByAttributeValue("property", "og:article:author").get(0).attr("content");
            } catch (IndexOutOfBoundsException e) {
                try {
                    head.getElementsByAttributeValue("property", "Copyright").get(0).attr("content");
                } catch (IndexOutOfBoundsException f) {
                    this.article = "";
                }
            }
        }catch (Exception e){
            this.imageUrl="";
            this.article="";
        }
    }
    public void setTime() throws ParseException {
        this.time = convertTimeData(this.pubDate);
    }
    private String convertTimeData(String pubDate) throws ParseException {
        Date date = FinalValue.PUBDATE_PARSSER.parse(pubDate);
        LocalDateTime time = convertDateToLocal(date);

        Duration duration = Duration.between(time,LocalDateTime.now());
        if(duration.toDays() > 0){
            return duration.toDaysPart()+"일 전";
        }else if(duration.toHoursPart() > 0){
            return duration.toHoursPart()+"시간 전";
        }else if(duration.toMinutesPart() > 0){
            return duration.toMinutesPart() +"분 전";
        }else{
            return "방금 전";
        }

    }
    private LocalDateTime convertDateToLocal(Date date){
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private class CustomSSLSocketFactory extends SSLSocketFactory {
        private SSLSocketFactory defaultFactory;
        public CustomSSLSocketFactory() throws IOException {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }};

            try {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init((KeyManager[])null, trustAllCerts, new SecureRandom());
                defaultFactory = sslContext.getSocketFactory();
            } catch (KeyManagementException | NoSuchAlgorithmException var3) {
                throw new IOException("Can't create unsecure trust manager");
            }
        }
        @Override
        public String[] getDefaultCipherSuites() {
            return defaultFactory.getDefaultCipherSuites();
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return defaultFactory.getSupportedCipherSuites();
        }

        @Override
        public Socket createSocket(Socket socket, String s, int i, boolean b) throws IOException {
            //magic happens here, we send null as hostname
            return defaultFactory.createSocket(socket, null, i, b);
        }

        @Override
        public Socket createSocket(String s, int i) throws IOException, UnknownHostException {
            return defaultFactory.createSocket(s,i);
        }

        @Override
        public Socket createSocket(String s, int i, InetAddress inetAddress, int i1) throws IOException, UnknownHostException {
            return defaultFactory.createSocket(s,i,inetAddress,i1);
        }

        @Override
        public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
            return defaultFactory.createSocket(inetAddress, i);
        }

        @Override
        public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress1, int i1) throws IOException {
            return defaultFactory.createSocket(inetAddress,i, inetAddress1, i1);
        }
    }
}
