/*******************************************************************************
 // Copyright (c) Microsoft Corporation.
 // All rights reserved.
 //
 // This code is licensed under the MIT License.
 //
 // Permission is hereby granted, free of charge, to any person obtaining a copy
 // of this software and associated documentation files(the "Software"), to deal
 // in the Software without restriction, including without limitation the rights
 // to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
 // copies of the Software, and to permit persons to whom the Software is
 // furnished to do so, subject to the following conditions :
 //
 // The above copyright notice and this permission notice shall be included in
 // all copies or substantial portions of the Software.
 //
 // THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 // IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 // FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
 // AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 // LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 // OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 // THE SOFTWARE.
 ******************************************************************************/
package edu.ucsf.mousedatabase.filters;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.*;
import com.google.code.gson;
import com.google.gson.*;

import javax.naming.ServiceUnavailableException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationException;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import com.microsoft.aad.adal4j.UserInfo;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.token.Token;
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponseParser;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;

import edu.ucsf.mousedatabase.HTMLGeneration;
import edu.ucsf.mousedatabase.Log;

import org.apache.commons.lang3.StringUtils;

public class BasicFilter implements Filter {

    public static final String STATES = "states";
    public static final String STATE = "state";
    public static final Integer STATE_TTL = 3600;
    public static final String FAILED_TO_VALIDATE_MESSAGE = "Failed to validate data received from Authorization service - ";
    private String clientId = "";
    private String clientSecret = "";
    private String tenant = "";
    private String authority;
    // private String adminString = "admin";
    private static String[] adminList;
    private String jsonTest = "[\"bob\", \"jane\", \"tim\"]";

    public void destroy() {

    }

    private boolean isAdmin(String userId) {

        String adminString = System.getenv("ADMIN_ARRAY");
        // String[] adminArray = gson.fromJson(adminString, String);
        // String[] adminArray = new JSONArray(adminString);
        // //org.JSON.parse(adminString);
        // String[] idArray = admins.split(", ");
        // List<String> idList = Arrays.asList(adminArray);
        // if (idList.contains(userId)){
        Log.Info("List of admins: "+adminList);

        if (userId.equals("8de82567-f3d7-4fb6-9513-5bb985378e40")) {
            return true;
        }
        return false;
        // for (int i = 0; i < adminList.length; i++) {
        //     if (userId.equals(adminList[i])) {
        //         return true;
        //     }
        // }
        // return false;
        /*
         * if(userId.equals(adminList)) { return true; } else return false;
         */
    }

    private boolean isAdminLogin(AuthenticationSuccessResponse oidcResponse) {
        /*
         * HashMap<String, String> params = new HashMap<>();
         * 
         * Map<String,String[]> parameters = httpRequest.getParameterMap(); Set<String>
         * keys = parameters.keySet(); for (String key : keys) { params.put(key,
         * parameters.get(key)[0]); } AuthenticationResponse authResponse =
         * AuthenticationResponseParser.parse(new URI(fullUrl), params);
         * AuthenticationSuccessResponse oidcResponse = (AuthenticationSuccessResponse)
         * authResponse;
         */

        try {
            JWT idToken = oidcResponse.getIDToken();
            JWTClaimsSet claims = idToken.getJWTClaimsSet();
            String user = (String) claims.getClaim("objectidentifier");
            return isAdmin(user);
        } catch (Exception e) {
            return false;
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            try {
                String currentUri = httpRequest.getRequestURL().toString();
                String queryStr = httpRequest.getQueryString();
                String fullUrl = currentUri + (queryStr != null ? "?" + queryStr : "");
                Log.Info("full url: " + fullUrl);
                // check if user has a AuthData in the session
                if (!AuthHelper.isAuthenticated(httpRequest)) {
                    Log.Info("request is not authenticated");
                    if (AuthHelper.containsAuthenticationData(httpRequest)) { // probably here, for the check?
                        // if(isAdmin(user.getObjectId())) //where do we get the user?
                        // if(isAdminLogin(request, ))
                        Log.Info("about to process authentication data");
                        processAuthenticationData(httpRequest, httpResponse, currentUri, fullUrl);
                        chain.doFilter(request, response);
                        return;
                    } else {
                        // not authenticated
                        Log.Info("about to send auth redirect");
                        sendAuthRedirect(httpRequest, httpResponse);
                        return;
                    }
                }
                if (isAuthDataExpired(httpRequest)) {
                    updateAuthDataUsingRefreshToken(httpRequest);
                }
                // } catch (AuthenticationException authException) {
                // // something went wrong (like expiration or revocation of token)
                // // we should invalidate AuthData stored in session and redirect to
                // Authorization server
                // removePrincipalFromSession(httpRequest);
                // sendAuthRedirect(httpRequest, httpResponse);
                // return;
            } catch (Throwable exc) {
                httpResponse.setStatus(500);
                request.setAttribute("error", exc.getMessage());
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            }
        }
        chain.doFilter(request, response);
    }

    private boolean isAuthDataExpired(HttpServletRequest httpRequest) {
        AuthenticationResult authData = AuthHelper.getAuthSessionObject(httpRequest);
        return authData.getExpiresOnDate().before(new Date()) ? true : false;
    }

    private void updateAuthDataUsingRefreshToken(HttpServletRequest httpRequest) throws Throwable {
        AuthenticationResult authData = getAccessTokenFromRefreshToken(
                AuthHelper.getAuthSessionObject(httpRequest).getRefreshToken());
        setSessionPrincipal(httpRequest, authData);
    }

    private void processAuthenticationData(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
            String currentUri, String fullUrl) throws Throwable {
        HashMap<String, String> params = new HashMap<>();
        Map<String, String[]> parameters = httpRequest.getParameterMap();

        Set<String> keys = parameters.keySet();
        for (String key : keys) {
            params.put(key, parameters.get(key)[0]);
        }

        // validate that state in response equals to state in request
        StateData stateData = validateState(httpRequest.getSession(), params.get(STATE));
        AuthenticationResponse authResponse = AuthenticationResponseParser.parse(new URI(fullUrl), params);

        if (AuthHelper.isAuthenticationSuccessful(authResponse)) {
            AuthenticationSuccessResponse oidcResponse = (AuthenticationSuccessResponse) authResponse;
            // validate that OIDC Auth Response matches Code Flow (contains only requested
            // artifacts)
            validateAuthRespMatchesCodeFlow(oidcResponse);

            AuthenticationResult authData = getAccessToken(oidcResponse.getAuthorizationCode(), currentUri);
            // validate nonce to prevent reply attacks (code maybe substituted to one with
            // broader access)
            validateNonce(stateData, getClaimValueFromIdToken(authData.getIdToken(), "nonce"));

            UserInfo uInfo = authData.getUserInfo();
            String uniqueId = uInfo.getUniqueId();
            Log.Info("Auth login attempt: "+uniqueId.toString());

            if (isAdmin(uniqueId)) {
                Log.Info("is an admin");
                setSessionPrincipal(httpRequest, authData);
            } else {
                Log.Info("is not an admin");
                httpRequest.getRequestDispatcher("/accessDenied.jsp").forward(httpRequest, httpResponse);
                return;
            }
        } else {
            AuthenticationErrorResponse oidcResponse = (AuthenticationErrorResponse) authResponse;
            throw new Exception(String.format("Request for auth code failed: %s - %s",
                    oidcResponse.getErrorObject().getCode(), oidcResponse.getErrorObject().getDescription()));
        }
    }

    private void validateNonce(StateData stateData, String nonce) throws Exception {
        if (StringUtils.isEmpty(nonce) || !nonce.equals(stateData.getNonce())) {
            throw new Exception(FAILED_TO_VALIDATE_MESSAGE + "could not validate nonce");
        }
    }

    private String getClaimValueFromIdToken(String idToken, String claimKey) throws ParseException {
        return (String) JWTParser.parse(idToken).getJWTClaimsSet().getClaim(claimKey);
    }

    private void sendAuthRedirect(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        // use state parameter to validate response from Authorization server
        String state = UUID.randomUUID().toString();

        // use nonce parameter to validate idToken
        String nonce = UUID.randomUUID().toString();

        storeStateInSession(httpRequest.getSession(), state, nonce);

        String currentUri = httpRequest.getRequestURL().toString();
        Log.Info("about to send redirect");
        String redirectUrl = getRedirectUrl(currentUri, state, nonce);
        Log.Info("redirect url is " + redirectUrl);
        httpResponse.sendRedirect(redirectUrl);
    }

    /**
     * make sure that state is stored in the session, delete it from session -
     * should be used only once
     *
     * @param session
     * @param state
     * @throws Exception
     */
    private StateData validateState(HttpSession session, String state) throws Exception {
        if (StringUtils.isNotEmpty(state)) {
            StateData stateDataInSession = removeStateFromSession(session, state);
            if (stateDataInSession != null) {
                return stateDataInSession;
            }
        }
        throw new Exception(FAILED_TO_VALIDATE_MESSAGE + "could not validate state");
    }

    private void validateAuthRespMatchesCodeFlow(AuthenticationSuccessResponse oidcResponse) throws Exception {
        if (oidcResponse.getIDToken() != null || oidcResponse.getAccessToken() != null
                || oidcResponse.getAuthorizationCode() == null) {
            throw new Exception(FAILED_TO_VALIDATE_MESSAGE + "unexpected set of artifacts received");
        }
    }

    @SuppressWarnings("unchecked")
    private StateData removeStateFromSession(HttpSession session, String state) {
        Map<String, StateData> states = (Map<String, StateData>) session.getAttribute(STATES);
        if (states != null) {
            eliminateExpiredStates(states);
            StateData stateData = states.get(state);
            if (stateData != null) {
                states.remove(state);
                return stateData;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void storeStateInSession(HttpSession session, String state, String nonce) {
        if (session.getAttribute(STATES) == null) {
            session.setAttribute(STATES, new HashMap<String, StateData>());
        }
        ((Map<String, StateData>) session.getAttribute(STATES)).put(state, new StateData(nonce, new Date()));
    }

    private void eliminateExpiredStates(Map<String, StateData> map) {
        Iterator<Map.Entry<String, StateData>> it = map.entrySet().iterator();

        Date currTime = new Date();
        while (it.hasNext()) {
            Map.Entry<String, StateData> entry = it.next();
            long diffInSeconds = TimeUnit.MILLISECONDS
                    .toSeconds(currTime.getTime() - entry.getValue().getExpirationDate().getTime());

            if (diffInSeconds > STATE_TTL) {
                it.remove();
            }
        }
    }

    private AuthenticationResult getAccessTokenFromRefreshToken(String refreshToken) throws Throwable {
        AuthenticationContext context;
        AuthenticationResult result = null;
        ExecutorService service = null;
        try {
            service = Executors.newFixedThreadPool(1);
            context = new AuthenticationContext(authority + tenant + "/", true, service);
            Future<AuthenticationResult> future = context.acquireTokenByRefreshToken(refreshToken,
                    new ClientCredential(clientId, clientSecret), null, null);
            result = future.get();
        } catch (ExecutionException e) {
            throw e.getCause();
        } finally {
            service.shutdown();
        }

        if (result == null) {
            throw new ServiceUnavailableException("authentication result was null");
        }
        return result;
    }

    private AuthenticationResult getAccessToken(AuthorizationCode authorizationCode, String currentUri)
            throws Throwable {
        String authCode = authorizationCode.getValue();
        ClientCredential credential = new ClientCredential(clientId, clientSecret);
        AuthenticationContext context;
        AuthenticationResult result = null;
        ExecutorService service = null;
        try {
            service = Executors.newFixedThreadPool(1);
            context = new AuthenticationContext(authority + tenant + "/", true, service);
            Future<AuthenticationResult> future = context.acquireTokenByAuthorizationCode(authCode, new URI(currentUri),
                    credential, null);
            result = future.get();
        } catch (ExecutionException e) {
            throw e.getCause();
        } finally {
            service.shutdown();
        }

        if (result == null) {
            throw new ServiceUnavailableException("authentication result was null");
        }
        return result;
    }

    private void setSessionPrincipal(HttpServletRequest httpRequest, AuthenticationResult result) {
        httpRequest.getSession().setAttribute(AuthHelper.PRINCIPAL_SESSION_NAME, result);
    }

    private void removePrincipalFromSession(HttpServletRequest httpRequest) {
        httpRequest.getSession().removeAttribute(AuthHelper.PRINCIPAL_SESSION_NAME);
    }

    private String getRedirectUrl(String currentUri, String state, String nonce) throws UnsupportedEncodingException {
        String redirectUrl = authority + this.tenant
                + "/oauth2/authorize?response_type=code&scope=directory.read.all&response_mode=form_post&redirect_uri="
                + URLEncoder.encode(currentUri, "UTF-8") + "&client_id=" + clientId
                + "&resource=https%3a%2f%2fgraph.microsoft.com" + "&state=" + state + "&nonce=" + nonce;

        return redirectUrl;
    }

    public static void setGroups(String admins) {
        /*
         * String adminString = admins; // "ab9a5af3-c926-4638-9bef-bc3c1c256b4c"; Gson
         * gson = new Gson(); String adminGson = gson.toJson(adminString);
         */
        // adminList = gson.fromJson(adminGson, String[].class);

        adminList = new String[1];
        adminList[0] = admins;
    }

    public void init(FilterConfig config) throws ServletException {
        clientId = config.getInitParameter("client_id");
        authority = config.getServletContext().getInitParameter("authority");
        tenant = config.getServletContext().getInitParameter("tenant");
        clientSecret = config.getInitParameter("secret_key");
    }

    private class StateData {
        private String nonce;
        private Date expirationDate;

        public StateData(String nonce, Date expirationDate) {
            this.nonce = nonce;
            this.expirationDate = expirationDate;
        }

        public String getNonce() {
            return nonce;
        }

        public Date getExpirationDate() {
            return expirationDate;
        }
    }
}
