package com.cache;

import com.cache.Config.CacheConfiguration3;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(CacheConfiguration3.class)
public class CacheTimeTest {
    @Autowired
    Calculator calculator;

    @Test
    public void cachingByTimeSequentiallyTest() throws InterruptedException {
        long start = System.currentTimeMillis()/1000;
        //6 seconds
        long res1 = calculator.sum(1, 2);
        long res2 = calculator.subtract(1, 3);

        //6 seconds
        TimeUnit.SECONDS.sleep(6);

        //6 seconds
        long res3 = calculator.sum(1, 2);
        long res4 = calculator.subtract(1, 3);

        //fast
        long res5 = calculator.sum(1, 2);
        long res6 = calculator.subtract(1, 3);

        //in average - 18 seconds
        long end = System.currentTimeMillis()/1000;
        long time = end - start;


        assertThat(res1).isEqualTo(3);
        assertThat(res2).isEqualTo(-2);
        System.out.println(time);
    }

    @Test
    public void cachingByTimeParallelTest() throws InterruptedException {
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
                return calculator.multiply(5, 6);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        });

        FutureTask<Object> th4 = new FutureTask<>(() -> {
            try {
                return calculator.divide(12, 2);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        });

        FutureTask<Object> th5 = new FutureTask<>(() -> {
            try {
                return calculator.multiply(5, 6);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        });

        FutureTask<Object> th6 = new FutureTask<>(() -> {
            try {
                return calculator.divide(12, 2);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        });

        long start = System.currentTimeMillis()/1000;

        //10 seconds with sleep
        new Thread(th1).start();
        new Thread(th2).start();

        TimeUnit.SECONDS.sleep(10);

        //3 seconds
        new Thread(th3).start();
        new Thread(th4).start();
        new Thread(th5).start();
        new Thread(th6).start();

        try {
            assertThat(th1.get()).isEqualTo(th3.get()).isEqualTo(th5.get()).isEqualTo(30L);
            assertThat(th2.get()).isEqualTo(th4.get()).isEqualTo(th6.get()).isEqualTo(6.0);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        //in average - 13 seconds
        long end = System.currentTimeMillis()/1000;
        long time = end - start;

        System.out.println(time);
    }
}
