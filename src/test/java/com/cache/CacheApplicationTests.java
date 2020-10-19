package com.cache;

import com.cache.Config.CacheConfiguration2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(CacheConfiguration2.class)
class CacheApplicationTests {
	@Autowired
	private Calculator calculator;

	@Test
	void sequentialCacheTest() throws InterruptedException {
		long start = System.currentTimeMillis()/1000;
		//6 seconds
		long res1 = calculator.sum(1, 2);
		long res2 = calculator.subtract(1, 3);

		//fast
		long res3 = calculator.sum(1, 2);
		long res4 = calculator.subtract(1, 3);
		long end = System.currentTimeMillis()/1000;
		long time = end - start;

		assertThat(res1).isEqualTo(res3).isEqualTo(3);
		assertThat(res2).isEqualTo(res4).isEqualTo(-2);
		System.out.println(time);
	}

	@Test
	void parallelCacheTest() {
		//th1 and th2 - 3 seconds
		//th3 and th4 - fast
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

		long start = System.currentTimeMillis()/1000;
		new Thread(th1).start();
		new Thread(th2).start();
		new Thread(th3).start();
		new Thread(th4).start();

		try {
			assertThat(th1.get()).isEqualTo(th3.get()).isEqualTo(30L);
			assertThat(th2.get()).isEqualTo(th4.get()).isEqualTo(6.0);
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis()/1000;
		long time = end - start;

		System.out.println(time);
	}
}
