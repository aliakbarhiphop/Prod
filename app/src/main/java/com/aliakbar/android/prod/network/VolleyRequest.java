package com.aliakbar.android.prod.network;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

public class VolleyRequest extends StringRequest {
    private static final String LOGIN_URL = "http://localhost:8080//prod/ali.php";
    private Map<String, String> parameters;

    public VolleyRequest(Map<String, String> passedParameters, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, LOGIN_URL, listener, errorListener);
        parameters = passedParameters;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }
}
