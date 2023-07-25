package com.genoox.homeAss.util;

import com.genoox.homeAss.model.Variant;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import java.util.concurrent.TimeUnit;


import java.io.IOException;

public class ApiUtils {

    private static final OkHttpClient httpClient = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static String fetchGeneFromVariant(Variant variant) throws IOException {
        JSONObject json = new JSONObject();
        json.put("chr", variant.getChromosome());
        json.put("pos", String.valueOf(variant.getPosition()));
        json.put("ref", variant.getRef());
        json.put("alt", variant.getAlt());
        json.put("reference_version", "hg19");

        RequestBody body = RequestBody.create(JSON, json.toString());

        // Increasing the timeout
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url("https://test.genoox.com/api/fetch_variant_details")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            JSONObject jsonResponse = new JSONObject(response.body().string());
            return jsonResponse.getString("gene");
        }
    }
}
