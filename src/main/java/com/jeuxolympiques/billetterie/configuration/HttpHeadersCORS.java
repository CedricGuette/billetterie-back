package com.jeuxolympiques.billetterie.configuration;

import org.springframework.http.HttpHeaders;

public class HttpHeadersCORS {
    public HttpHeaders headers() {
        HttpHeaders httpHeader = new HttpHeaders();
        httpHeader.set("Access-Control-Allow-Headers","Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token");
        httpHeader.set("Access-Control-Allow-Origin","http://localhost:3000");
        httpHeader.set("Access-Control-Allow-Methods", "DELETE,GET,HEAD,OPTIONS,PUT,POST,PATCH");

        return httpHeader;
    }
}
