package org.hosoku.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.function.Function;

public class JwtTokenProvider<T> {

    private Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private String secret;

    public JwtTokenProvider(String secret){
        this.secret = secret;
    }



    public String generateTokenForModel(T model, long expiryTime)  {

        Date now = new Date();

        Date expiryDate = new Date(now.getTime() + expiryTime);

        try {
            String token = JWT.create().withSubject(model.toString())
                    .withIssuedAt(new Date())
                    .withExpiresAt(expiryDate)
                    .sign(Algorithm.HMAC512(secret));
            return token;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "NO_TOKEN";

    }

    public T getModelFromJWT(String token, Function<String, T> convertToModel) {
        String subject = null;
        try {
            subject = JWT.require(Algorithm.HMAC512(secret))
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return convertToModel.apply(subject);
    }

    public boolean validateToken(String authToken){

        try{
            JWT.require(Algorithm.HMAC512(secret))
                    .build()
                    .verify(authToken);
            return true;
        }catch(AlgorithmMismatchException a){
            a.printStackTrace();
        }catch(InvalidClaimException i){
            i.printStackTrace();
        }catch(JWTDecodeException decoder){
            decoder.printStackTrace();
        }catch(TokenExpiredException t) {
            t.printStackTrace();
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }
}
