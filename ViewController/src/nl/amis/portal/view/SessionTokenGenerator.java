package nl.amis.portal.view;

import java.util.Date;

import javax.faces.bean.SessionScoped;
import javax.faces.bean.ManagedBean;

import oracle.adf.share.ADFContext;
import oracle.adf.share.security.SecurityContext;

import oracle.security.restsec.jwt.JwtToken;
import java.util.HashMap;
import java.util.Map;
@ManagedBean
@SessionScoped
public class SessionTokenGenerator {
    
    private String token = ";";
    public SessionTokenGenerator() {
        super();
        System.out.println("Session Token Generator activated");
        ADFContext adfCtx = ADFContext.getCurrent();  
             SecurityContext secCntx = adfCtx.getSecurityContext();  
             String user = secCntx.getUserPrincipal().getName();  
             String _user = secCntx.getUserName();  
             
             System.out.println("User = "+_user);
             
             // https://docs.oracle.com/cloud/latest/fmw121300/SDTRG/jwt.htm#SDTRG1170
             // http://www.redrock-it.nl/soa-suite-12c-generating-json-web-token-jwt-osb/
             // go to JWT.io and check if token verifies and contains stuff

        try {
            String jwt = generateJWT(user, "param ding", _user, "myKey");
            setToken(jwt);
            Map<String,Object> userMap = new HashMap<String,Object>();
            userMap.put("principal", secCntx.getUserPrincipal().getName());
            userMap.put("subject", secCntx.getSubject().toString());
            userMap.put("roles", "king,emperor");
            new TokenCache().storeInCache(jwt, userMap);
        } catch (Exception e) {
        }

    }
    // https://stormpath.com/blog/jwt-java-create-verify
    public String generateJWT(String subject, String extraParam, String extraParam2, String myKey) throws Exception {
           
           String result = null;
           
           JwtToken jwtToken = new JwtToken();
           //Fill in all the parameters- algorithm, issuer, expiry time, other claims etc
           jwtToken.setAlgorithm(JwtToken.SIGN_ALGORITHM.HS512.toString());
           jwtToken.setType(JwtToken.JWT);
           jwtToken.setClaimParameter("ExtraParam", extraParam);
           jwtToken.setClaimParameter("ExtraParam2", extraParam2);
           long nowMillis = System.currentTimeMillis();
              Date now = new Date(nowMillis);
           jwtToken.setIssueTime(now);
           // expiry = 5 minutes
           jwtToken.setExpiryTime(new Date(nowMillis + 5*60*1000));
                                           jwtToken.setSubject(subject);
                                           jwtToken.setIssuer("ADF_JET_REST_APP");
           // Get the private key and sign the token with a secret key or a private key
           result = jwtToken.signAndSerialize(myKey.getBytes());
           return result;
       }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
