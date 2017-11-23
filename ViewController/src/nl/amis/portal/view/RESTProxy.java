package nl.amis.portal.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import javax.ws.rs.core.HttpHeaders;

import oracle.adf.share.ADFContext;
import oracle.adf.share.security.SecurityContext;

import java.util.Date;

import java.util.Map;

import oracle.security.restsec.jwt.JwtException;
import oracle.security.restsec.jwt.JwtToken;
import oracle.security.restsec.jwt.VerifyException;


@WebServlet(name = "RESTProxy", urlPatterns = { "/restproxy/*" })
public class RESTProxy extends HttpServlet {
    private static final String CONTENT_TYPE = "application/json; charset=UTF-8";

            

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    private TokenDetails validateToken(HttpServletRequest request) {
        TokenDetails td = new TokenDetails();
        try{
        boolean tokenAccepted = false;
        boolean tokenValid = false;
        // 1. check if session context established (based on JSESSIONID and in same session
        System.out.println("session identifier " + request.getSession().getId());
        SecurityContext secCntx = ADFContext.getCurrent().getSecurityContext();
        String user = secCntx.getUserPrincipal().getName();
        String _user = secCntx.getUserName();

        System.out.println("Security Context already set - session established User = " + _user);
        td.setIsJSessionEstablished(true);
        // 2. check if request contains token

        // Get the HTTP Authorization header from the request
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Extract the token from the HTTP Authorization header
        String tokenString = authorizationHeader.substring("Bearer".length()).trim();

        String jwtToken = "";
        String issuer = "";
td.setIsTokenPresent(true);

        try {
            JwtToken token = new JwtToken(tokenString);
            // verify whether token was signed with my key
            // key = myKey
            String yourKey = "yourKey";
            boolean result = token.verify(yourKey.getBytes());
            if (!result) {
                System.out.println("Token was not signed with your key");
            }
            String myKey = "myKey";
            result = token.verify(myKey.getBytes());
            if (!result) {
                System.out.println("Token was not signed with MY KEY");
                td.addMotivation("Token was not signed with correct key" );
            } else {
                System.out.println("Token was correctly signed with MY KEY");
        td.setIsTokenVerified(true);
                td.setJwtTokenString(tokenString);
                tokenAccepted = false;
            }

            // Validate the issued and expiry time stamp.
            if (token.getExpiryTime().after(new Date())) {
                System.out.println("Token is still valid");
                jwtToken = tokenString;
                tokenValid = true;
                td.setIsTokenFresh(true);
            } else {
                System.out.println("Token is NOT valid ANYMORE");
                td.addMotivation("Token has expired" );
            }

            // Get the issuer from the token
            issuer = token.getIssuer();
            System.out.println("Token issuer = " + issuer);
            
            // check on issuer
            System.out.println("Servlet inspected token " + token);
            td.setIsTokenAccepted( td.isIsTokenPresent() && td.isIsTokenFresh()&& td.isIsTokenVerified());

            return td;

        } catch (JwtException e) {
            System.out.println("Servlet failed to inspect token " + e.getMessage());
            td.addMotivation("No valid token was found in request" );

        } catch (VerifyException e) {
            System.out.println("Servlet failed to verify token " + e.getMessage());
            td.addMotivation("Token was not verified (not signed using correct key" );

        }
        } catch (Exception e) {
            System.out.println("Exception while validating token "+e.getMessage());
            td.addMotivation("No valid token was found in request" );
 }
        return td;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        TokenDetails td = validateToken(request);
        if (!td.isIsTokenAccepted()) {
            response.setContentType(CONTENT_TYPE);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.addHeader("Refusal-Motivation", td.getMotivation());
            response.getOutputStream().close();
        } else {

            // parse token, get details for user; get stuff from cache
            Map userMap = (Map)new TokenCache().retrieveFromCache(td.getJwtTokenString());

System.out.println("Retrieved User Map - ");
            System.out.println("Principal "+ userMap.get("principal"));
            System.out.println("Roles "+ userMap.get("roles"));


            // 3. get URL path for REST call
            String uriPath = request.getRequestURI();
            System.out.println("URL Path is " + uriPath);
            String pathInfo = request.getPathInfo();
            System.out.println("Path is " + pathInfo);

            // redirect the API call/ call API and return result

            URL url = new URL("http://127.0.0.1:7101/RESTBackend/resources" + pathInfo);
            System.out.println("Target URL = " + url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));


            response.setContentType(CONTENT_TYPE);
            response.setStatus(conn.getResponseCode());
            RESTProxy.copyStream(conn.getInputStream(), response.getOutputStream());
            response.getOutputStream().close();
        } // token valid so continue

    }


    public static void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024]; // Adjust if you want
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(CONTENT_TYPE);
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head><title>RESTProxy</title></head>");
        out.println("<body>");
        out.println("<p>The servlet has received a POST. This is the reply.</p>");
        out.println("</body></html>");
        out.close();
    }

    private class TokenDetails {
        private String jwtTokenString;
        private String motivation;
        
        private boolean isJSessionEstablished; // Http Session could be reestablished
        private boolean isTokenVerified; // signed with correct key
        private boolean isTokenFresh; // not expired yet
        private boolean isTokenPresent; // is there a token at all
        private boolean isTokenValid; // can it be parsed
        private boolean isTokenIssued; // issued by a trusted token issuer
        
        private boolean isTokenAccepted = false; // overall conclusion


        private void setJwtTokenString(String jwtTokenString) {
            this.jwtTokenString = jwtTokenString;
        }

        private String getJwtTokenString() {
            return jwtTokenString;
        }

        private void setIsTokenVerified(boolean isTokenVerified) {
            this.isTokenVerified = isTokenVerified;
        }

        private boolean isIsTokenVerified() {
            return isTokenVerified;
        }

        private void setIsTokenFresh(boolean isTokenFresh) {
            this.isTokenFresh = isTokenFresh;
        }

        private boolean isIsTokenFresh() {
            return isTokenFresh;
        }

        private void setIsTokenPresent(boolean isTokenPresent) {
            this.isTokenPresent = isTokenPresent;
        }

        private boolean isIsTokenPresent() {
            return isTokenPresent;
        }

        private void setIsTokenValid(boolean isTokenValid) {
            this.isTokenValid = isTokenValid;
        }

        private boolean isIsTokenValid() {
            return isTokenValid;
        }

        private void setIsTokenIssued(boolean isTokenIssued) {
            this.isTokenIssued = isTokenIssued;
        }

        private boolean isIsTokenIssued() {
            return isTokenIssued;
        }

        private void setIsTokenAccepted(boolean isTokenAccepted) {
            this.isTokenAccepted = isTokenAccepted;
        }

        private boolean isIsTokenAccepted() {
            return isTokenAccepted;
        }

        private void setIsJSessionEstablished(boolean isJSessionEstablished) {
            this.isJSessionEstablished = isJSessionEstablished;
        }

        private boolean isIsJSessionEstablished() {
            return isJSessionEstablished;
        }

        private void addMotivation(String motivation) {
            this.motivation = this.motivation+";"+motivation;
        }
        private void setMotivation(String motivation) {
            this.motivation = motivation;
        }

        private String getMotivation() {
            return motivation;
        }
    }
    
}
