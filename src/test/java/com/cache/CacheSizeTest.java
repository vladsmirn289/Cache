package com.cache;

import com.cache.Config.CacheConfiguration1;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(CacheConfiguration1.class)
public class CacheSizeTest {
    @Autowired
    Calculator calculator;

    @Test
    void cacheSizeTest() throws InterruptedException {
        long start = System.currentTimeMillis()/1000;
        //6 seconds
        long res1 = calculator.sum(1, 2);
        long res2 = calculator.subtract(1, 3);

        //6 second
        long res3 = calculator.sum(1, 5);
        long res4 = calculator.subtract(1, 9);

        //fast
        long res5 = calculator.sum(1, 5);
        long res6 = calculator.subtract(1, 9);

        //6 seconds
        long res7 = calculator.sum(1, 2);
        long res8 = calculator.subtract(1, 3);

        //fast
        long res9 = calculator.sum(1, 2);
        long res10 = calculator.subtract(1, 3);
        long end = System.currentTimeMillis()/1000;
        long time = end - start;

        assertThat(res1).isEqualTo(res7).isEqualTo(res9).isEqualTo(3);
        assertThat(res2).isEqualTo(res8).isEqualTo(res10).isEqualTo(-2);
        assertThat(res3).isEqualTo(res5).isEqualTo(6);
        assertThat(res4).isEqualTo(res6).isEqualTo(-8);
        System.out.println(time);
    }
}
