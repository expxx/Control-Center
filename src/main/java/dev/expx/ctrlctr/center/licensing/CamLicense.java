package dev.expx.ctrlctr.center.licensing;

import com.google.gson.JsonObject;
import dev.expx.ctrlctr.center.http.JSONRequest;
import dev.expx.ctrlctr.center.licensing.impl.CamLicenseResp;
import dev.expx.ctrlctr.center.licensing.util.HWID;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.jetbrains.annotations.ApiStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.*;

/**
 * License Validation server
 * created by Cam M, unreleased.
 */
@ApiStatus.Experimental
public class CamLicense {

    private final String key;
    private final String product;

    /**
     * Create a new license validator.
     * @param key License key
     * @param product Product
     */
    public CamLicense(String key, String product) {
        this.key = key;
        this.product = product;
    }

    /**
     * Validate a license key.
     * @param version Version
     * @return License response
     */
    public CamLicenseResp validate(String version) {
        try {
            HashMap<String, String> params1 = new HashMap<>();
            JsonObject tokenData = JSONRequest.request(
                    "https://auth.expx.dev/api/remote/v1/sign?only200=true&license=" + key + "&product=" + product,
                    "POST",
                    params1,
                    false,
                    null
            );
            String jwtToken = tokenData.getAsJsonObject("data").get("token").getAsString();
            if (jwtToken == null)
                return new CamLicenseResp(false, null, null, false, null,"Invalid License");

            HashMap<String, String> params2 = new HashMap<>();
            params2.put("Authorization", "JWT " + jwtToken);
            JsonObject responseData = JSONRequest.request(
                    "https://auth.expx.dev/api/remote/v1/validate?hwid=" + HWID.read() + "&version=" + version + "&only200=true",
                    "GET",
                    params2,
                    false,
                    null
            ).getAsJsonObject("data");
            boolean valid = responseData.get("valid").getAsBoolean();
            String latest = responseData.get("latest").getAsString();
            if (!valid) {
                return new CamLicenseResp(false, null, responseData.get("customer").getAsString(), !Objects.equals(latest, version), latest, responseData.get("safeToShow").getAsString());
            }
            String local = responseData.get("localKey").getAsString();
            socket();
            return new CamLicenseResp(valid, local, responseData.get("customer").getAsString(), !Objects.equals(latest, version), latest,"LocalKey Valid");
        } catch (Exception e) {
            // not valid
            return new CamLicenseResp(false, null, null, false, null, "Invalid License");
        }
    }

    /**
     * Create a socket connection.
     */
    public void socket() {
        URI uri = URI.create("https://auth.expx.dev");
        Socket io = IO.socket(uri).connect();

        io.on("require", objects -> {
            if(objects[0].equals("auth") && objects[1] instanceof Ack ack) {
                try {
                    ack.call(new JSONObject()
                            .put("license", key)
                            .put("product", product)
                    );
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

        });
    }

}
