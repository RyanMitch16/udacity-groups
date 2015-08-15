package com.codeu.teamjacob.groups.sync.request;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *  Sends http GET and POST requests to URLs
 */
public final class HttpRequest {

    private HttpRequest() {}  //Hide the constructor

    private static final String urlCharset = "UTF-8";

    /**
     * Sends a http get request and returns the response
     * @param url       the url to connect send the request to
     * @return          the response connection to the url
     * @throws IOException
     */
    public static HttpURLConnection get(Uri url) throws IOException{

        //Create the connection to the url
        HttpURLConnection con = (HttpURLConnection) (new URL(url.toString())).openConnection();

        // Add the http header to the request
        con.setRequestProperty("User-Agent", urlCharset);

        return con;
    }

    /**
     * Sends a http post request and returns the response
     * @param url       the url to connect send the request to
     * @return          the response connection to the url
     * @throws IOException
     */
    public static HttpURLConnection post (Uri url) throws IOException{

        //Create the connection to the url
        HttpURLConnection con = (HttpURLConnection) (new URL(url.getScheme()+"://"+url.getAuthority()+url.getPath())).openConnection();

        // Add the http header to the request
        con.setDoOutput(true); // Triggers POST.
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", urlCharset);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + urlCharset);

        // Write the parameters to the query string and send the post request to the url
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(url.getQuery());
        Log.d("XCX", url.getQuery().toString());
        wr.flush();
        wr.close();

        return con;
    }

    /**
     * Retrieves the string output from the url connection
     * @param con   the connection to the http url
     * @return      the content in a string format
     * @throws IOException
     */
    public static String getContentString(HttpURLConnection con) throws IOException{

        //Read the http response from the url
        int responseCode = con.getResponseCode();
        BufferedReader in;
        if (responseCode >= 200 && responseCode <= 300) {
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));}
        else {
            in = new BufferedReader(new InputStreamReader(con.getErrorStream()));}

        //
        String response = "", inputLine;
        while ((inputLine = in.readLine()) != null) {
            response += inputLine;
        }

        return response;
    }

    /**
     * Retrieves the json object represented by the content from the url connection
     * @param con   the connection to the http url
     * @return      the content in a json format
     * @throws IOException
     * @throws JSONException
     */
    public static JSONObject getContentJson(HttpURLConnection con) throws IOException, JSONException{
        return new JSONObject(getContentString(con));
    }

}
