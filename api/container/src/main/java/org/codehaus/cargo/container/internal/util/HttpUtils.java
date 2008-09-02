/* 
 * ========================================================================
 * 
 * Copyright 2003-2004 The Apache Software Foundation. Code from this file 
 * was originally imported from the Jakarta Cactus project.
 * 
 * Copyright 2004-2006 Vincent Massol.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ========================================================================
 */
package org.codehaus.cargo.container.internal.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.KeyManagementException;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.codehaus.cargo.util.log.LoggedObject;

/**
 * Set of common HTTP(S) utility methods.
 *
 * @version $Id$
 */
public class HttpUtils extends LoggedObject
{
    /**
     * Storage class for the HTTP ping result.
     */
    public static class HttpResult
    {
        /**
         * The HTTP connection response code (eg 200).
         */
        public int responseCode;

        /**
         * The HTTP connection response message (eg "Ok").
         */
        public String responseMessage;

        /**
         * The HTTP connection response body.
         */
        public String responseBody;
    }

    /**
     * @param pingURL the URL to ping
     * @return true if the URL can be ping or false otherwise
     */
    public final boolean ping(URL pingURL)
    {
        return isAvailable(testConnectivity(pingURL, null));
    }

    /**
     * Ping a URL and store the detailed result in a {@link HttpResult} object.
     *
     * @param pingURL the URL to ping
     * @param result the detailed ping result
     * @return true if the URL can be ping or false otherwise
     */
    public final boolean ping(URL pingURL, HttpResult result)
    {
        return ping(pingURL, null, result);
    }

    /**
     * Ping a URL and store the detailed result in a {@link HttpResult} object.
     *
     * @param pingURL the URL to ping
     * @param requestProperties optional request properties to add to the connection (can be null)
     * @param result the detailed ping result
     * @return true if the URL can be ping or false otherwise
     */
    public final boolean ping(URL pingURL, Map requestProperties, HttpResult result)
    {
        HttpResult responseResult = testConnectivity(pingURL, requestProperties);
        result.responseBody = responseResult.responseBody;
        result.responseCode = responseResult.responseCode;
        result.responseMessage = responseResult.responseMessage;

        return isAvailable(responseResult);
    }

    /**
     * Tests whether we are able to connect to the HTTP(S) server identified by the specified URL.
     *
     * @param url the URL to check
     * @param requestProperties optional request properties to add to the connection (can be null)
     * @return the HTTP(S) result containing -1 as response code if no connection could be
     *         established
     */
    private HttpResult testConnectivity(URL url, Map requestProperties)
    {
        HttpResult result = new HttpResult();
        try
        {
            HttpURLConnection connection;
            if (url.getProtocol().equalsIgnoreCase("https"))
            {
                TrustManager[] trustAll = {new PermissiveTrustManager()};
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAll, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

                connection = (HttpsURLConnection) url.openConnection();

                HostnameVerifier verifyAll = new PermissiveHostnameVerifier();
                ((HttpsURLConnection) connection).setHostnameVerifier(verifyAll);
            }
            else
            {
                connection = (HttpURLConnection) url.openConnection();
            }

            connection.setRequestProperty("Connection", "close");

            // Add optional request properties specified by the caller
            if (requestProperties != null)
            {
                Iterator keys = requestProperties.keySet().iterator();
                while (keys.hasNext())
                {
                    String key = (String) keys.next();
                    connection.setRequestProperty(key, (String) requestProperties.get(key));

                    getLogger().debug("Added property [" + key + "] = ["
                        + requestProperties.get(key) + "]", this.getClass().getName());
                }
            }

            connection.connect();
            result.responseBody = readFully(connection);
            connection.disconnect();
            result.responseCode = connection.getResponseCode();
            result.responseMessage = connection.getResponseMessage();
        }
        catch (IOException e)
        {
            result.responseCode = -1;
        }
        catch (NoSuchAlgorithmException e)
        {
            result.responseCode = -1;
        }
        catch (KeyManagementException e)
        {
            result.responseCode = -1;
        }

        getLogger().debug("Pinged [" + url + "], result = [" + result.responseCode + "]",
            this.getClass().getName());

        return result;
    }

    /**
     * Tests whether an HTTP(S) return code corresponds to a valid connection to the test URL or
     * not. Success is 200 up to but excluding 300.
     *
     * @param responseResult the detailed HTTP ping result
     * @return <code>true</code> if the test URL could be called without error, <code>false</code>
     *         otherwise
     */
    private boolean isAvailable(HttpResult responseResult)
    {
        boolean result;
        if ((responseResult.responseCode != -1) && (responseResult.responseCode < 300))
        {
            result = true;
        }
        else
        {
            result = false;
        }
        return result;
    }

    /**
     * Fully reads the input stream from the passed HTTP URL connection to prevent (harmless)
     * server-side exception.
     *
     * @param connection the HTTP URL connection to read from
     * @exception IOException if an error happens during the read
     * @return the HTTP connection response body
     */
    private String readFully(HttpURLConnection connection) throws IOException
    {
        String responseBody = "";

        // Only read if there is data to read ... The problem is that not all servers return a
        // content-length header. If there is no header getContentLength() returns -1. It seems to
        // work and it seems that all servers that return no content-length header also do not
        // block on read() operations!
        if (connection.getContentLength() != 0)
        {
            // try getting data from the input stream first an if it fails from the error stream
            try
            {
                InputStream in = connection.getInputStream();
                if (in != null)
                {
                    responseBody = readStreamData(in);
                }
            }
            catch (IOException e)
            {
                InputStream in = connection.getErrorStream();
                if (in != null)
                {
                    responseBody = readStreamData(in);
                }
                else
                {
                    throw e;
                }
            }
        }

        return responseBody;
    }

    /**
     * @param stream the stream from which to read data from
     * @return the stream data
     * @throws IOException in case of error
     */
    private String readStreamData(InputStream stream) throws IOException
    {
        StringBuffer body = new StringBuffer();
        byte[] buf = new byte[256];

        // Make sure we read all the data in the stream
        int n;
        while ((n = stream.read(buf)) != -1)
        {
            body.append(new String(buf, 0, n));
        }

        return body.toString();
    }

    /**
     * A TrustManager that does not validate certificate chains.
     */
    private class PermissiveTrustManager implements X509TrustManager
    {
        /**
         * {@inheritDoc}
         * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
         */
        public java.security.cert.X509Certificate[] getAcceptedIssuers()
        {
            return null;
        }

        /**
         * {@inheritDoc}
         * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], String)
         */
        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
        {
        }

        /**
         * {@inheritDoc}
         * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], String)
         */
        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
        {
        }
    }

    /**
     * A HostnameVerifier that does not care whether the name on the certificate matches the
     * hostname.
     */
    private class PermissiveHostnameVerifier implements HostnameVerifier
    {
        /**
         * {@inheritDoc}
         * @see HostnameVerifier#verify
         */
        public boolean verify(String hostname, SSLSession session)
        {
            return true;
        }
    }
}
