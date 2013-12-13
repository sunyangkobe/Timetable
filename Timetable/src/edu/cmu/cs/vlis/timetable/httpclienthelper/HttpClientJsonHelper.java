package edu.cmu.cs.vlis.timetable.httpclienthelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import android.app.Activity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.cmu.cs.vlis.timetable.persistentdatamanager.SessionManager;

/**
 * This class provides some general method for communicating with server using HTTP protocol.
 * 
 * This class is designed for sending JSON format HTTP request to the server, given specified
 * key-value pairs in String. It supports all kinds of HTTP methods, including GET, POST, DELETE and
 * so on (GET by default), it doesn't support other formats except JSON (eg. XML). This class
 * assumes all key-value pairs you send to the server and receive from server in JSON are String
 * type.
 * 
 * The general usage of this class is: 1. create a instance given the URL, the class will set the
 * method as GET and format as JSON by default (the JSON format cannot be changed) 2. call
 * setRequestHeader() method if you needs to add any thing to the request header 3. call
 * setRequestMethod method if you want to change the method type of this request (if you doesn't
 * call this method, then the HTTP request will be GET by default. 4. call sendRequest method given
 * a Map indicating the key-value pairs you want to send to the server (the server won't receive
 * these key-value pairs if it's a GET method, so just call sendRequest(null) if the method type is
 * GET 5. call getResponseStatusCode() method to get the status code in the response from server 6.
 * call getResponseBody() method, this method will return a Map indicating the key-value pairs in
 * the response body
 * 
 * Note the there are two constructors, the only difference is that HttpClientJsonHelper(Activity,
 * String) constructor will automatically add the sessionId into request header if it exists, while
 * HttpClientJsonHelper(String) will not do it by default.
 */

public class HttpClientJsonHelper {
    public enum MethodType {
        GET, POST, DELETE, PUT
    }

    private URL url;
    private HttpURLConnection httpUrlConnection;

    public HttpClientJsonHelper(Activity currentActivity, String url, MethodType methodType) {
        try {
            // establish the http connection
            this.url = new URL(url);
            this.httpUrlConnection = (HttpURLConnection) this.url.openConnection();
            // set the header of http request indicating the type of body content is JSON
            httpUrlConnection.setRequestMethod(methodType.name());
            httpUrlConnection.setRequestProperty("content-type", "application/json");
            
            SessionManager sessionManager = new SessionManager(currentActivity);
            String sessionId = sessionManager.getSessionId();
            if (sessionId != null) {
                httpUrlConnection.setRequestProperty("Authorization", "Token " + sessionId);
            }
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // this method must be called before "sendRequest" method
    public void setRequestHeader(Map<String, String> requestHeader) {
        for (String key : requestHeader.keySet()) {
            httpUrlConnection.setRequestProperty(key, requestHeader.get(key));
        }
    }
    
    // send request to server, given specified key-value pairs in String type,
    // it will automatically transform the key-value pairs to JSON format and send
    // it to server.
    // The key-value pairs will not be received by the server if the method type of
    // HTTP request is GET. So you can leave the parameter as null if the method type is GET
    public void sendRequest(Map<String, String> requestBody) {
        // If the method type of HTTP request is GET, then return directly since
        // a GET method doesn't contain body content.
        if (httpUrlConnection.getRequestMethod().equals(MethodType.GET.name())) return;

        // use Jackson JSON library to set the JSON node
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();

        // insert the key-value pairs into the JSNO node
        for (String key : requestBody.keySet()) {
            objectNode.put(key, requestBody.get(key));
        }

        // Write JSON object to body of HTTP request
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(httpUrlConnection.getOutputStream());
            writer.write(mapper.writeValueAsString(objectNode));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (writer != null) writer.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // this method must be called after the "sendRequest" method, otherwise IllegalStateException
    // may occur
    public int getResponseStatusCode() throws IOException {
        return httpUrlConnection.getResponseCode();
    }

    public String getResponseContent() {
        String responseContent = null;
        try {
            responseContent = readStream(httpUrlConnection.getInputStream());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return responseContent;
    }

    public void disconnect() {
        httpUrlConnection.disconnect();
    }

    // Read from InputStream to String
    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer result = new StringBuffer();

        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result.toString();
    }
}
