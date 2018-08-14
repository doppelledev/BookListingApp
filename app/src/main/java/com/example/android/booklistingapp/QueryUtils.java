package com.example.android.booklistingapp;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class QueryUtils {

    private final static String API_KEY = "AIzaSyDo6J7OLiKwl72LUTJtvB1KHD8RT-q0QeE";
    private final static String URL_PREFIX  = "https://www.googleapis.com/books/v1/volumes?prettyPrint=false&maxResults=20&";

    private QueryUtils(){
    }

    public static ArrayList<Book> searchBooks(String search_query) {
        // split the search query into keywords, and form a request url
        String request_url  = formRequestUrl(search_query.split(" "));
        // create a URL object using the request url
        URL url = createURL(request_url);
        // make an http request to the url, and retrieve the input stream
        InputStream inputStream = makeHttpRequest(url);
        // read the JSON response from the input stream
        String JSONResponse = readFromStream(inputStream);
        // try to close the input stream
        try {
            if (inputStream != null)
                inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // parse the JSON response into book objects
        return parseJSON(JSONResponse);
    }

    // forms and then returns a String representing the request url
    private static String formRequestUrl(String [] keywords) {
        // first the prefix
        StringBuilder request_url = new StringBuilder(URL_PREFIX );
        // append the api key
        request_url.append("Key=").append(API_KEY).append("&");
        // append the search query
        request_url.append("q=");
        for (int i = 0; i < keywords.length; i++) {
            if (i != 0)
                request_url.append("+");
            request_url.append(keywords[i]);
        }
        // return the url string
        return request_url.toString();
    }

    // creates and returns a URL object using the request url string
    private static URL createURL(String request_url){
        try {
            return new URL(request_url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // make an http request and return the input stream
    private static InputStream makeHttpRequest(URL url){
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(3000);
            urlConnection.setConnectTimeout(3000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200)
                return urlConnection.getInputStream();
            else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // reads the JSON response from the input stream and returns it
    private static String readFromStream(InputStream is){
        if (is == null)
            return null;
        InputStreamReader is_reader = new InputStreamReader(is, Charset.forName("UTF-8"));
        BufferedReader reader = new BufferedReader(is_reader);
        StringBuilder JSONResponse = new StringBuilder();
        try {
            String line = reader.readLine();
            while (line != null) {
                JSONResponse.append(line);
                line = reader.readLine();
            }
            return JSONResponse.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ArrayList<Book> parseJSON(String JSONString){
        try {
            JSONObject root = new JSONObject(JSONString);
            JSONArray items = root.getJSONArray("items");
            ArrayList<Book> books = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                String id = items.getJSONObject(i).getString("id");
                JSONObject item = items.getJSONObject(i).getJSONObject("volumeInfo");
                String title = item.getString("title");
                JSONArray authors_array = item.optJSONArray("authors");
                String authors = authors_array != null ? authors_array.join(", ").replace("\"", "") : "Author not found";
                String publisher = item.optString("publisher");
                String publishedDate = item.optString("publishedDate");
                String description = item.optString("description");
                String link = item.optString("previewLink");
                JSONObject imageLinks = item.optJSONObject("imageLinks");
                String thumbUrl = null;
                if (imageLinks != null)
                    thumbUrl = imageLinks.optString("thumbnail");
                books.add(new Book(id, title, authors, publisher, publishedDate, description, thumbUrl, link));
            }
            return books;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
