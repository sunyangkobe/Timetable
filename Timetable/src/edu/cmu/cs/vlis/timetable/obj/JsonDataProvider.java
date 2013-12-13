package edu.cmu.cs.vlis.timetable.obj;

import java.io.IOException;
import java.util.Map;

import android.app.Activity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cmu.cs.vlis.timetable.httpclienthelper.HttpClientJsonHelper;
import edu.cmu.cs.vlis.timetable.httpclienthelper.HttpClientJsonHelper.MethodType;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;

public final class JsonDataProvider extends DataProvider {
    int jsonStatusCode;
    HttpClientJsonHelper httpClientJsonHelper;
    String responseBodyContent;
    NetworkStatus networkErrorCode = NetworkStatus.VALID;

    public JsonDataProvider(Activity currentActivity) {
        super(currentActivity);
    }

    @Override
    public <T> T readObject(String apiUrl, Map<String, String> params, Class<T> valueType) {
        getContentFromApiUrl(apiUrl, params);

        ObjectMapper mapper = new ObjectMapper();
        T ret = null;
        try {
            if (responseBodyContent != null) {
                ret = mapper.readValue(responseBodyContent, valueType);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

    @Override
    public NetworkStatus getLastNetworkStatus() {
        return networkErrorCode;
    }

    public JsonNode readJsonNode(String apiUrl, Map<String, String> params) {
        JsonNode responseNode = null;

        getContentFromApiUrl(apiUrl, params);
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (responseBodyContent != null) {
                responseNode = mapper.readTree(responseBodyContent);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return responseNode;
    }

    private void getContentFromApiUrl(String apiUrl, Map<String, String> params) {
        try {
            if (params != null) {
                httpClientJsonHelper = new HttpClientJsonHelper(currentActivity, apiUrl,
                        MethodType.POST);
            }
            else {
                httpClientJsonHelper = new HttpClientJsonHelper(currentActivity, apiUrl,
                        MethodType.GET);
            }
            responseBodyContent = null;
            httpClientJsonHelper.sendRequest(params);
            jsonStatusCode = httpClientJsonHelper.getResponseStatusCode();
            for (NetworkStatus status : NetworkStatus.values()) {
                if (status.getErrorCode() == jsonStatusCode) {
                    networkErrorCode = status;
                    if (networkErrorCode == NetworkStatus.VALID
                            || networkErrorCode == NetworkStatus.CREATED) {
                        responseBodyContent = httpClientJsonHelper.getResponseContent();
                    }
                    return;
                }
            }
            networkErrorCode = NetworkStatus.NETWORK_ERROR;
        }
        catch (IOException e) {
            networkErrorCode = NetworkStatus.NETWORK_ERROR;
            e.printStackTrace();
        }
        finally {
            httpClientJsonHelper.disconnect();
        }
    }
}