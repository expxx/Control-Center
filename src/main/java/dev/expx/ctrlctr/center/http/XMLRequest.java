package dev.expx.ctrlctr.center.http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.expx.ctrlctr.center.logger.errors.ArgumentSyntaxException;
import org.json.XML;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;

/**
 * JSONRequest is a utility class that is used
 * to send HTTP requests to a server and
 * receive a readable XML response.
 */
public class XMLRequest {

    private XMLRequest() {
        throw new UnsupportedOperationException();
    }

    /**
     * Sends an HTTP request to a server and returns an XML response.
     * @param url The URL to send the request to
     * @param requestMethod The HTTP request method to use
     * @param requestProperties The request properties to send
     * @param includeBody Whether to include a body in the request
     * @param body The body to send with the request
     * @return A {@link JsonObject} containing the response
     * @throws IOException If an error occurs while sending the request
     */
    public static JsonObject request(String url, String requestMethod, Map<String, String> requestProperties, Boolean includeBody, byte[] body) throws IOException {
        try {
            LoggerFactory.getLogger(XMLRequest.class).info("Sending HTTP request to {}", url);
            URL requestUrl = new URI(url).toURL();
            HttpURLConnection con = (HttpURLConnection) requestUrl.openConnection();
            con.setRequestMethod(requestMethod);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            requestProperties.forEach(con::setRequestProperty);
            con.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
            con.connect();
            if (Boolean.TRUE.equals(includeBody)) {
                try (OutputStream os = con.getOutputStream()) {
                    os.write(body, 0 , body.length);
                }
                InputStream inputStream = con.getInputStream();
                if(con.getResponseCode() <= 200 && con.getResponseCode() < 299) { inputStream = con.getErrorStream(); }
                return parseXML(inputStream);
            }
            StringBuilder response = new StringBuilder();
            Scanner conScanner = new Scanner(con.getInputStream());
            while (conScanner.hasNext()) {
                response.append(conScanner.nextLine());
            }

            return JsonParser.parseString(XML.toJSONObject(response.toString()).toString()).getAsJsonObject();
        } catch (ConnectException x) {
            return JsonParser.parseString("{\"success\": false}").getAsJsonObject();
        } catch (URISyntaxException e) {
            throw new ArgumentSyntaxException(e.toString());
        }
    }

    /**
     * Parses a JSON response from an {@link InputStream}.
     * @param inputStream The {@link InputStream} to parse
     * @return A {@link JsonObject} containing the response
     */
    protected static JsonObject parseXML(InputStream inputStream) {
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        ) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return JsonParser.parseString(XML.toJSONObject(response.toString()).toString()).getAsJsonObject();
        }  catch (Exception x) {
            return JsonParser.parseString("{\"success\": false}").getAsJsonObject();
        }
    }

}
