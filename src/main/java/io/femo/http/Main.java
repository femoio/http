package io.femo.http;

import java.io.IOException;
import java.net.URL;

/**
 * Created by felix on 1/18/16.
 */
public class Main {

    public static void main(String[] args) {
        try {
            HttpResponse response = Http.get(new URL("http://httpbin.org/get")).execute().response();
            System.out.println(response.responseString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
