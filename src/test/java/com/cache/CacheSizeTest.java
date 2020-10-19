package com.cache;

import com.cache.Config.CacheConfiguration1;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(CacheConfiguration1.class)
public class CacheSizeTest {
    @Autowired
    Calculator calculator;

    @Test
    void cacheSizeSequentialTest() throws InterruptedException {
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

    @Test
    void cacheSizeParallelTest() {
        //in average - 3 seconds
        FutureTask<Object> th1 = new FutureTask<>(() -> {
            try {
                return calculator.multiply(5, 6);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        });

        FutureTask<Object> th2 = new FutureTask<>(() -> {
            try {
                return calculator.divide(12, 2);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        });

        FutureTask<Object> th3 = new FutureTask<>(() -> {
            try {
                return calculator.multiply(3, 12);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        });

        FutureTask<Object> th4 = new FutureTask<>(() -> {
            try {
                return calculator.divide(15, 5);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        });

        FutureTask<Object> th5 = new FutureTask<>(() -> {
            try {
                return calculator.multiply(3, 12);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        });

        FutureTask<Object> th6 = new FutureTask<>(() -> {
            try {
                return calculator.divide(15, 5);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        });

        FutureTask<Object> th7 = new FutureTask<>(() -> {
            try {
                return calculator.multiply(5, 6);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        });

        FutureTask<Object> th8 = new FutureTask<>(() -> {
            try {
                return calculator.divide(12, 2);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        });

        FutureTask<Object> th9 = new FutureTask<>(() -> {
            try {
                return calculator.multiply(5, 6);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        });

        FutureTask<Object> th10 = new FutureTask<>(() -> {
            try {
                return calculator.divide(12, 2);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        });

        long start = System.currentTimeMillis()/1000;

        new Thread(th1).start();
        new Thread(th2).start();
        new Thread(th3).start();
        new Thread(th4).start();
        new Thread(th5).start();
        new Thread(th6).start();
        new Thread(th7).start();
        new Thread(th8).start();
        new Thread(th9).start();
        new Thread(th10).start();

        try {
            assertThat(th1.get()).isEqualTo(th7.get()).isEqualTo(th9.get()).isEqualTo(30L);
            assertThat(th2.get()).isEqualTo(th8.get()).isEqualTo(th10.get()).isEqualTo(6.0);
            assertThat(th3.get()).isEqualTo(th5.get()).isEqualTo(36L);
            assertThat(th4.get()).isEqualTo(th6.get()).isEqualTo(3.0);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis()/1000;
        long time = end - start;

        System.out.println(time);
    }
}
