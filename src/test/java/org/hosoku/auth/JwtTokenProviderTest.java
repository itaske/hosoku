package org.hosoku.auth;


import org.assertj.core.api.Assertions;
import org.hosoku.auth.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Function;


public class JwtTokenProviderTest {

    private JwtTokenProvider<Long> jwtTokenProvider;
    private String secret= "Victoria";

    @BeforeEach
    public void init(){

        jwtTokenProvider = new JwtTokenProvider(secret);
    }

    @Test
    public void shouldGiveSameModel_WhenTokenIsConvertedBack(){
        //Arrange
        Long id = Long.valueOf(3);

        //Act
        String token = jwtTokenProvider.generateTokenForModel(id, 1000);
        Function<String, Long> convertStringToLong = (t)->Long.valueOf(t);
        Long generateId = jwtTokenProvider.getModelFromJWT(token, convertStringToLong);
        //Assert
        Assertions.assertThat(id).isEqualTo(generateId);
    }

    @Test
    public void shouldGiveTrue_WhenTokenIsValid(){
        //Arrange
        Long id = Long .valueOf(3);

        //Act
        String token = jwtTokenProvider.generateTokenForModel(id, 34000);
        boolean isValid = jwtTokenProvider.validateToken(token);

        //Assert
        Assertions.assertThat(isValid).isTrue();
    }

    @Test
    public void shouldGiveFalse_WhenTokenIsExpired(){
        //Arrange
        Long id = Long.valueOf(3);

        //Act
        String token = jwtTokenProvider.generateTokenForModel(id,-1000);
        boolean isValid = jwtTokenProvider.validateToken(token);

        //Assert
        Assertions.assertThat(isValid).isFalse();
    }

    @Test
    public void shouldGiveFalse_WhenTokenIsWrong(){

        //Act
        boolean isValid = jwtTokenProvider.validateToken("wrong token");

        //Assert
        Assertions.assertThat(isValid).isFalse();
    }
}
