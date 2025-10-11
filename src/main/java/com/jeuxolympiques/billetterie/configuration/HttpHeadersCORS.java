package com.jeuxolympiques.billetterie.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;

/*
* Objet qui se charge d'écrire les en-têtes des réponses afin de ne pas avoir d'erreur CORS
*/
public class HttpHeadersCORS {
    @Value("${app.url-front}")
    private String frontUrl;

    public HttpHeaders headers() {
        HttpHeaders httpHeader = new HttpHeaders();
        httpHeader.set("Access-Control-Allow-Headers","Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token");
        httpHeader.set("Access-Control-Allow-Origin",frontUrl);
        httpHeader.set("Access-Control-Allow-Methods", "DELETE,GET,HEAD,OPTIONS,PUT,POST,PATCH");

        return httpHeader;
    }
}
