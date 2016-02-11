# FeMo.IO HTTP Library

[![Build Status](https://travis-ci.org/femoio/http.svg?branch=master)](https://travis-ci.org/femoio/http)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.femo/http/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.femo/http)

        <dependency>
            <groupId>io.femo</groupId>
            <artifactId>http</artifactId>
            <version>0.0.1</version>
        </dependency>

This library provides a simple API for developers to perform synchronous and asynchronous HTTP Requests.
 
HTTP Versions supported:

* HTTP/1.1

## GET Requests

To perform a simple HTTP GET use the following call.

        HttpResponse response = Http.get("http://example.org/").response();
        
The request will be executed and the result cached in the HttpResponse object. All data is now exposed via simple jQuery
like getters and setters. To check the response status use
 
        if(response.statusCode() == StatusCode.OK) {
            //Response was successfull
        } else {
            //Response was not successfull
        }
        
To retrieve the content of the response use
 
        System.out.println(response.responseString());
        
## POST Request

To perform a simple HTTP POST use the following call.

        Http.post("http://example.org/post").response();
        
To append data use

        Http
            .post("http://example.org/post")
            .data("test", "test")
            .response();
            
The data is automatically UrlFormEncoded and sent to the server.

## Drivers

### Use with Android

        Http.installDriver(new AndroidDriver());
        
### Asynchronous Use
This driver spawns one new Thread to execute each request. Use this only for projects with few requests as it generates heavy load.

        Http.installDriver(new AsynchronousDriver()); 
        
### Asynchronous Batch Use
This driver creates a Thread Executor Service to execute requests in the Background. Use this driver for projects that 
perform a huge amount of requests.

        Http.installDriver(new AsynchronousDriver(5));
        
You have to supply the constructor with the amount of executor threads you want to spawn at the start of the program.
