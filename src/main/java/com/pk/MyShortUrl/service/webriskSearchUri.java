package com.pk.MyShortUrl.service;

import com.google.cloud.webrisk.v1.WebRiskServiceClient;
import com.google.webrisk.v1.SearchUrisRequest;
import com.google.webrisk.v1.SearchUrisResponse;
import com.google.webrisk.v1.ThreatType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class webriskSearchUri {
    public static boolean searchUri(String uri) {
        try {
            List<ThreatType> threatTypes = Arrays.asList(ThreatType.MALWARE, ThreatType.SOCIAL_ENGINEERING, ThreatType.UNWANTED_SOFTWARE);
            try (WebRiskServiceClient webRiskServiceClient = WebRiskServiceClient.create()) {
                SearchUrisRequest searchUrisRequest = SearchUrisRequest.newBuilder()
                        .addAllThreatTypes(threatTypes)
                        .setUri(uri)
                        .build();

                SearchUrisResponse searchUrisResponse = webRiskServiceClient.searchUris(searchUrisRequest);

                return searchUrisResponse.getThreat().getThreatTypesList().isEmpty();
            }
        } catch (IOException e) {
            System.err.println("IOException occurred: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}