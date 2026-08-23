package com.example.demo.Random;

import java.util.Random;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.log4j.Log4j2;

@Log4j2
@SpringBootTest
public class OptimizedRandom {
    private static final char[] ALPHA_NUMERIC = 
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghij_-klmnopqrstuvwxyz0123456789".toCharArray();

    
    public static String generate(int length) {
        char[] result = new char[length];
        Random random = new Random();
        
        // 힙 메모리 재할당을 피하기 위해 배열 크기를 로컬 변수에 캐싱
        int charsLength = ALPHA_NUMERIC.length; 

        for (int i = 0; i < length; i++) {
            result[i] = ALPHA_NUMERIC[random.nextInt(charsLength)];
        }
        
        
        return new String(result);
    }
    
    @Test
    public void test() {
    	log.info(generate(128));
    }
    
}

