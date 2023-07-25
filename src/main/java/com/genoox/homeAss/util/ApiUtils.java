// Itai Alcalai 206071110
package com.genoox.homeAss.util;
// Variant class
import com.genoox.homeAss.model.Variant;
// API
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
// Java
import java.util.concurrent.TimeUnit;
import java.io.IOException;

// Class for utility methods related to API operations
public class ApiUtils {

    // Cache to store API responses to avoid repeated requests
    public static Cache apiResponseCache = new Cache();

    // HTTP client for making API requests
    private static final OkHttpClient httpClient = new OkHttpClient();

    // JSON media type for API request bodies
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    // Method to fetch gene data for a variant from an API
    public static String fetchGeneFromVariant(Variant variant) throws IOException {
        // Construct a unique key for the variant
        String variantKey = variant.getChromosome() + variant.getPosition() + variant.getRef() + variant.getAlt();

        // Check if the gene data for this variant is already in the cache
        String cachedValue = apiResponseCache.get(variantKey);
        if (cachedValue != null) {
            // If the data is in the cache, return it
            return cachedValue;
        }

        // If the data is not in the cache, prepare a JSON object for the API request
        JSONObject json = new JSONObject();
        json.put("chr", variant.getChromosome());
        json.put("pos", String.valueOf(variant.getPosition()));
        json.put("ref", variant.getRef());
        json.put("alt", variant.getAlt());
        json.put("reference_version", "hg19");

        // Convert the JSON object to a request body
        RequestBody body = RequestBody.create(JSON, json.toString());

        // Build a new HTTP client with increased timeouts
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        // Build the API request
        Request request = new Request.Builder()
                .url("https://test.genoox.com/api/fetch_variant_details")
                .post(body)
                .build();

        // Execute the API request and handle the response
        try (Response response = httpClient.newCall(request).execute()) {
            // If the response is not successful, throw an exception
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Parse the response body as a JSON object and return the gene data
            JSONObject jsonResponse = new JSONObject(response.body().string());
            return jsonResponse.getString("gene");
        }
    }
}
