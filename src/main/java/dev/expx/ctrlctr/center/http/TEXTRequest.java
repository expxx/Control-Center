package dev.expx.ctrlctr.center.http;

import dev.expx.ctrlctr.center.Ctrlctr;
import dev.expx.ctrlctr.center.logger.errors.ArgumentSyntaxException;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.Scanner;

/**
 * JSONRequest is a utility class that is used
 * to send HTTP requests to a server and
 * receive a JSON response.
 */
@SuppressWarnings("unused")
public class TEXTRequest {

    private TEXTRequest() {
        throw new UnsupportedOperationException();
    }

    /**
     * Sends an HTTP request to a server and returns a PlainText String response.
     * @param url The URL to send the request to
     * @param requestMethod The HTTP request method to use
     * @param requestProperties The request properties to send
     * @param includeBody Whether to include a body in the request
     * @param body The body to send with the request
     * @return A {@link String} containing the response
     * @throws IOException If an error occurs while sending the request
     */
    public static String request(String url, String requestMethod, Map<String, String> requestProperties, Boolean includeBody, byte[] body) throws IOException {
        try {
            LoggerFactory.getLogger(TEXTRequest.class).info(Ctrlctr.getLang().lang("http-text", url));
            URL requestUrl = new URI(url).toURL();
            HttpURLConnection con = (HttpURLConnection) requestUrl.openConnection();
            con.setRequestMethod(requestMethod);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            requestProperties.forEach(con::setRequestProperty);
            con.setRequestProperty("Content-Type", "application/text; charset=UTF-8");
            con.connect();
            if (Boolean.TRUE.equals(includeBody)) {
                try (OutputStream os = con.getOutputStream()) {
                    os.write(body, 0 , body.length);
                }
                InputStream inputStream = con.getInputStream();
                if(con.getResponseCode() <= 200 && con.getResponseCode() < 299) { inputStream = con.getErrorStream(); }
                return inputStream.toString();
            }
            StringBuilder response = new StringBuilder();
            Scanner conScanner = new Scanner(con.getInputStream());
            while (conScanner.hasNext()) {
                response.append(conScanner.nextLine());
            }

            return response.toString();
        } catch (ConnectException x) {
            return "CONNECTION EXCEPTION";
        } catch (URISyntaxException e) {
            throw new ArgumentSyntaxException(e.toString());
        }
    }

}
