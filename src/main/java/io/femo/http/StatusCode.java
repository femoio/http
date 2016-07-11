package io.femo.http;

import org.jetbrains.annotations.Contract;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by felix on 9/10/15.
 */
public final class StatusCode {

    private int status;
    private String statusMessage;

    private static final Map<Integer, StatusCode> index = new TreeMap<Integer, StatusCode>();

    /**
     * This means that the server has received the request headers, and that the client should proceed to send the request body (in the case of a request for which a body needs to be sent; for example, a POST request). If the request body is large, sending it to a server when a request has already been rejected based upon inappropriate headers is inefficient. To have a server check if the request could be accepted based on the request's headers alone, a client must send Expect: 100-continue as a header in its initial request and check if a 100 Continue status code is received in response before continuing (or receive 417 Expectation Failed and not continue).
     */
    public static final StatusCode CONTINUE = new StatusCode(100, "Continue");

    /**
     * This means the requester has asked the server to switch protocols and the server is acknowledging that it will do so.
     */
    public static final StatusCode SWITCHING_PROTOCOLS = new StatusCode(101, "Switching Protocols");

    /**
     * RFC 2518
     *
     * As a WebDAV request may contain many sub-requests involving file operations, it may take a long time to complete the request. This code indicates that the server has received and is processing the request, but no response is available yet. This prevents the client from timing out and assuming the request was lost.
     */
    public static final StatusCode PROCESSING = new StatusCode(102, "Processing");

    /**
     * Standard response for successful HTTP requests. The actual response will depend on the request method used. In a GET request, the response will contain an entity corresponding to the requested resource. In a POST request, the response will contain an entity describing or containing the result of the action.
     */
    public static final StatusCode OK = new StatusCode(200, "OK");

    /**
     * The request has been fulfilled and resulted in a new resource being created.
     */
    public static final StatusCode CREATED = new StatusCode(201, "Created");

    /**
     * The request has been accepted for processing, but the processing has not been completed. The request might or might not eventually be acted upon, as it might be disallowed when processing actually takes place.
     */
    public static final StatusCode ACCEPTED = new StatusCode(202, "Accepted");

    /**
     * The server successfully processed the request, but is returning information that may be from another source.
     */
    public static final StatusCode NON_AUTHORITIVE_INFORMATION = new StatusCode(203, "Non-Authoritive Information");

    /**
     * The server successfully processed the request, but is not returning any content.
     */
    public static final StatusCode NO_CONTENT = new StatusCode(204, "No Content");

    /**
     * The server successfully processed the request, but is not returning any content. Unlike a 204 response, this response requires that the requester reset the document view.
     */
    public static final StatusCode RESET_CONTENT = new StatusCode(205, "Reset Content");

    /**
     * RFC 7233
     *
     * The server is delivering only part of the resource (byte serving) due to a range header sent by the client. The range header is used by HTTP clients to enable resuming of interrupted downloads, or split a download into multiple simultaneous streams.
     */
    public static final StatusCode PARTIAL_CONTENT = new StatusCode(206, "Partial Content");

    /**
     * RFC 4918
     *
     * The message body that follows is an XML message and can contain a number of separate response codes, depending on how many sub-requests were made.
     */
    public static final StatusCode MULTI_STATUS = new StatusCode(207, "Multi-Status");

    /**
     * RFC 5842
     *
     * The members of a DAV binding have already been enumerated in a previous reply to this request, and are not being included again.
     */
    public static final StatusCode ALREADY_REPORTED = new StatusCode(208, "Already Reported");

    /**
     * RFC 3229
     *
     * The server has fulfilled a request for the resource, and the response is a representation of the result of one or more instance-manipulations applied to the current instance.
     */
    public static final StatusCode IM_USED = new StatusCode(226, "IM Used");

    /**
     * Indicates multiple options for the resource that the client may follow. It, for instance, could be used to present different format options for video, list files with different extensions, or word sense disambiguation.
     */
    public static final StatusCode MULTIPLE_CHOICES = new StatusCode(300, "Multiple Choices");

    /**
     * This and all future requests should be directed to the given URI.
     */
    public static final StatusCode MOVED_PERMANENTLY = new StatusCode(301, "Moved Permanently");

    /**
     * This is an example of industry practice contradicting the standard. The HTTP/1.0 specification (RFC 1945) required the client to perform a temporary redirect (the original describing phrase was "Moved Temporarily"),[6] but popular browsers implemented 302 with the functionality of a 303 See Other. Therefore, HTTP/1.1 added status codes 303 and 307 to distinguish between the two behaviours. However, some Web applications and frameworks use the 302 status code as if it were the 303.
     */
    public static final StatusCode FOUND = new StatusCode(302, "Found");

    /**
     * The response to the request can be found under another URI using a GET method. When received in response to a POST (or PUT/DELETE), it should be assumed that the server has received the data and the redirect should be issued with a separate GET message.
     */
    public static final StatusCode SEE_OTHER = new StatusCode(303, "See Other");

    /**
     * RFC 7232
     *
     * Indicates that the resource has not been modified since the version specified by the request headers If-Modified-Since or If-None-Match. This means that there is no need to retransmit the resource, since the client still has a previously-downloaded copy.
     */
    public static final StatusCode NOT_MODIFIED = new StatusCode(304, "Not Modified");

    /**
     * The requested resource is only available through a proxy, whose address is provided in the response. Many HTTP clients (such as Mozilla and Internet Explorer) do not correctly handle responses with this status code, primarily for security reasons.
     */
    public static final StatusCode USE_PROXY = new StatusCode(305, "Use Proxy");

    /**
     * No longer used. Originally meant "Subsequent requests should use the specified proxy.
     */
    public static final StatusCode SWITCH_PROXY = new StatusCode(306, "Switch Proxy");

    /**
     * In this case, the request should be repeated with another URI; however, future requests should still use the original URI. In contrast to how 302 was historically implemented, the request method is not allowed to be changed when reissuing the original request. For instance, a POST request should be repeated using another POST request.
     */
    public static final StatusCode TEMPORARY_REDIRECT = new StatusCode(307, "Temporary Redirect");

    /**
     * The request, and all future requests should be repeated using another URI. 307 and 308 (as proposed) parallel the behaviours of 302 and 301, but do not allow the HTTP method to change. So, for example, submitting a form to a permanently redirected resource may continue smoothly.
     */
    public static final StatusCode PERMANENT_REDIRECT = new StatusCode(308, "Permanent Redirect");

    /**
     * The server cannot or will not process the request due to something that is perceived to be a client error (e.g., malformed request syntax, invalid request message framing, or deceptive request routing).
     */
    public static final StatusCode BAD_REQUEST = new StatusCode(400, "Bad Request");

    /**
     * Similar to 403 Forbidden, but specifically for use when authentication is required and has failed or has not yet been provided. The response must include a WWW-Authenticate header field containing a challenge applicable to the requested resource. See Basic access authentication and Digest access authentication.
     */
    public static final StatusCode UNAUTHORIZED = new StatusCode(401, "Unauthorized");

    /**
     * Reserved for future use. The original intention was that this code might be used as part of some form of digital cash or micropayment scheme, but that has not happened, and this code is not usually used. YouTube uses this status if a particular IP address has made excessive requests, and requires the person to enter a CAPTCHA.
     */
    public static final StatusCode PAYMENT_REQUIRED = new StatusCode(402, "Payment Required");

    /**
     * The request was a valid request, but the server is refusing to respond to it. Unlike a 401 Unauthorized response, authenticating will make no difference.
     */
    public static final StatusCode FORBIDDEN = new StatusCode(403, "Forbidden");

    /**
     * The requested resource could not be found but may be available again in the future. Subsequent requests by the client are permissible.
     */
    public static final StatusCode NOT_FOUND = new StatusCode(404, "Not Found");

    /**
     * A request was made of a resource using a request method not supported by that resource; for example, using GET on a form which requires data to be presented via POST, or using PUT on a read-only resource.
     */
    public static final StatusCode METHOD_NOT_ALLOWED = new StatusCode(405, "Method Not Allowed");

    /**
     * The requested resource is only capable of generating content not acceptable according to the Accept headers sent in the request.
     */
    public static final StatusCode NOT_ACCEPTABLE = new StatusCode(406, "Not Acceptable");

    /**
     * RFC 7235
     *
     * The client must first authenticate itself with the proxy.
     */
    public static final StatusCode PROXY_AUTHENTICATION_REQUIRED = new StatusCode(407, "Proxy Authentication Required");

    /**
     * The server timed out waiting for the request. According to HTTP specifications: "The client did not produce a request within the time that the server was prepared to wait. The client MAY repeat the request without modifications at any later time."
     */
    public static final StatusCode REQUEST_TIMEOUT = new StatusCode(408, "Request Timeout");

    /**
     * Indicates that the request could not be processed because of conflict in the request, such as an edit conflict in the case of multiple updates.
     */
    public static final StatusCode CONFLICT = new StatusCode(409, "Conflict");

    /**
     * Indicates that the resource requested is no longer available and will not be available again. This should be used when a resource has been intentionally removed and the resource should be purged. Upon receiving a 410 status code, the client should not request the resource again in the future. Clients such as search engines should remove the resource from their indices. Most use cases do not require clients and search engines to purge the resource, and a "404 Not Found" may be used instead.
     */
    public static final StatusCode GONE = new StatusCode(410, "Gone");

    /**
     * The request did not specify the length of its content, which is required by the requested resource.
     */
    public static final StatusCode LENGTH_REQUIRED = new StatusCode(411, "Length Required");

    /**
     * RFC 7232
     *
     * The server does not meet one of the preconditions that the requester put on the request.
     */
    public static final StatusCode PRECONDITION_FAILED = new StatusCode(412, "Precondition Failed");

    /**
     * RFC 7231
     *
     * The request is larger than the server is willing or able to process. Called "Request Entity Too Large " previously.
     */
    public static final StatusCode PAYLOAD_TOO_LARGE = new StatusCode(413, "Payload Too Large");

    /**
     * The URI provided was too long for the server to process. Often the result of too much data being encoded as a query-string of a GET request, in which case it should be converted to a POST request.
     */
    public static final StatusCode REQUREST_URI_TOO_LONG = new StatusCode(414, "Request-URI Too Long");

    /**
     * The request entity has a media type which the server or resource does not support. For example, the client uploads an image as image/svg+xml, but the server requires that images use a different format.
     */
    public static final StatusCode UNSUPPORTED_MEDIA_TYPE = new StatusCode(415, "Unsupported Media Type");

    /**
     * RFC 7233
     *
     * The client has asked for a portion of the file (byte serving), but the server cannot supply that portion. For example, if the client asked for a part of the file that lies beyond the end of the file.
     */
    public static final StatusCode REQUESTED_RANGE_NOT_SATISFIABLE = new StatusCode(416, "Requested Range Not Satisfiable");

    /**
     * The server cannot meet the requirements of the Expect request-header field.
     */
    public static final StatusCode EXPECTATION_FAILED = new StatusCode(417, "Expectation Failed");

    /**
     * RFC 2324
     *
     * This code was defined in 1998 as one of the traditional IETF April Fools' jokes, in RFC 2324, Hyper Text Coffee Pot Control Protocol, and is not expected to be implemented by actual HTTP servers. The RFC specifies this code should be returned by tea pots requested to brew coffee.
     */
    public static final StatusCode IM_A_TEAPOT = new StatusCode(418, "I'm a teapot");

    /**
     * RFC 2616
     *
     * Not a part of the HTTP standard, 419 Authentication Timeout denotes that previously valid authentication has expired. It is used as an alternative to 401 Unauthorized in order to differentiate from otherwise authenticated clients being denied access to specific server resources.
     */
    public static final StatusCode AUTHENTICATION_TIMEOUT = new StatusCode(419, "Authentication Timeout");

    /**
     * Not part of the HTTP standard, but returned by version 1 of the Twitter Search and Trends API when the client is being rate limited. Other services may wish to implement the 429 Too Many Requests response code instead.
     */
    public static final StatusCode ENCHANCE_YOUR_CALM = new StatusCode(420, "Enhance Your Calm");

    /**
     * HTTP/2
     *
     * The request was directed at a server that is not able to produce a response (for example because a connection reuse).
     */
    public static final StatusCode MISDIRECTED_REQUEST = new StatusCode(421, "Misdirected Request");

    /**
     * RFC 4918
     *
     * The request was well-formed but was unable to be followed due to semantic errors.
     */
    public static final StatusCode UNPROCESSABLE_ENTITY = new StatusCode(422, "Unprocessable Entity");

    /**
     * RFC 4918
     *
     * The resource that is being accessed is locked.
     */
    public static final StatusCode LOCKED = new StatusCode(423, "Locked");

    /**
     * The request failed due to failure of a previous request (e.g., a PROPPATCH).
     */
    public static final StatusCode FAILED_DEPENDENCY = new StatusCode(424, "Failed Dependency");

    /**
     * The client should switch to a different protocol such as TLS/1.0, given in the Upgrade header field.
     */
    public static final StatusCode UPGRADE_REQUIRED = new StatusCode(426, "Upgrade Required");

    /**
     * RFC 6585
     *
     * The origin server requires the request to be conditional. Intended to prevent "the 'lost update' problem, where a client GETs a resource's state, modifies it, and PUTs it back to the server, when meanwhile a third party has modified the state on the server, leading to a conflict."
     */
    public static final StatusCode PRECONDITION_REQUIRED = new StatusCode(428, "Precondition Required");

    /**
     * RFC 6585
     *
     * The user has sent too many requests in a given amount of time. Intended for use with rate limiting schemes.
     */
    public static final StatusCode TOO_MANY_REQUESTS = new StatusCode(429, "Too Many Requests");

    /**
     * RFC 6585
     *
     * The server is unwilling to process the request because either an individual header field, or all the header fields collectively, are too large.
     */
    public static final StatusCode REQUEST_HEADER_FIELDS_TOO_LARGE = new StatusCode(431, "Request Header Fields Too Large");

    /**
     * A generic error message, given when an unexpected condition was encountered and no more specific message is suitable.
     */
    public static final StatusCode INTERNAL_SERVER_ERROR = new StatusCode(500, "Internal Server Error");

    /**
     * The server either does not recognize the request method, or it lacks the ability to fulfill the request. Usually this implies future availability (e.g., a new feature of a web-service API).
     */
    public static final StatusCode NOT_IMPLEMENTED = new StatusCode(501, "Not Implemented");

    /**
     * The server was acting as a gateway or proxy and received an invalid response from the upstream server.
     */
    public static final StatusCode BAD_GATEWAY = new StatusCode(502, "Bad Gateway");

    /**
     * The server is currently unavailable (because it is overloaded or down for maintenance). Generally, this is a temporary state.
     */
    public static final StatusCode SERVICE_UNAVAILABLE = new StatusCode(503, "Service Unavailable");

    /**
     * The server was acting as a gateway or proxy and did not receive a timely response from the upstream server.
     */
    public static final StatusCode GATEWAY_TIMEOUT = new StatusCode(504, "Gateway Timeout");

    /**
     * The server does not support the HTTP protocol version used in the request.
     */
    public static final StatusCode HTTP_VERSION_NOT_SUPPORTED = new StatusCode(505, "HTTP Version Not Supported");

    /**
     * RFC 2295
     *
     * Transparent content negotiation for the request results in a circular reference.
     */
    public static final StatusCode VARIANT_ALSO_NEGOTIATES = new StatusCode(506, "Variant Also Negotiates");

    /**
     * RFC 4918
     *
     * The server is unable to store the representation needed to complete the request.
     */
    public static final StatusCode INSUFFICIENT_STORAGE = new StatusCode(507, "Insufficient Storage");

    /**
     * RFC 5842
     *
     * The server detected an infinite loop while processing the request (sent in lieu of 208 Already Reported).
     */
    public static final StatusCode LOOP_DETECTED = new StatusCode(508, "Loop Detected");

    /**
     * RFC 2774
     *
     * Further extensions to the request are required for the server to fulfil it.
     */
    public static final StatusCode NOT_EXTENDED = new StatusCode(510, "Not Extended");

    /**
     * RFC 2774
     *
     * The client needs to authenticate to gain network access. Intended for use by intercepting proxies used to control access to the network (e.g., "captive portals" used to require agreement to Terms of Service before granting full Internet access via a Wi-Fi hotspot).
     */
    public static final StatusCode NETWORK_AUTHENTICATION_REQUIRED = new StatusCode(511, "Network Authentication Required");

    private StatusCode(int status, String statusMessage) {
        this.status = status;
        this.statusMessage = statusMessage;
    }

    @Contract(pure = true)
    public int status() {
        return status;
    }

    public void status(int status) {
        this.status = status;
    }

    @Contract(pure = true)
    public String statusMessage() {
        return statusMessage;
    }

    public void statusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    @Contract(pure = true)
    @Override
    public String toString() {
        return "StatusCode[" + status +
                " - " + statusMessage +
                ']';
    }

    static {
        Field[] fields = StatusCode.class.getDeclaredFields();
        for(Field field : fields) {
            if(field.getType() == StatusCode.class) {
                try {
                    StatusCode statusCode = (StatusCode) field.get(null);
                    index.put(statusCode.status(), statusCode);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static StatusCode find(int status) {
        if(index.containsKey(status)) {
            return index.get(status);
        }
        throw new UnknownStatusCodeException(status);
    }

    public static StatusCode constructFromHttpStatusLine(String statusLine) {
        String statusCode = statusLine.substring(statusLine.indexOf(" ") + 1, statusLine.indexOf(" ", statusLine.indexOf(" ") + 1));
        String statusMessage = statusLine.substring(statusLine.indexOf(" ", statusLine.indexOf(" ") + 1) + 1);
        return new StatusCode(Integer.parseInt(statusCode), statusMessage);
    }

    public static void register(StatusCode statusCode) {
        if(!index.containsKey(statusCode.status())) {
            index.put(statusCode.status(), statusCode);
        }
    }

    public static void register(int code, String statusMessage) {
        register(new StatusCode(code, statusMessage));
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object o) {
        return o instanceof StatusCode && ((StatusCode)o).status == status;
    }
}
