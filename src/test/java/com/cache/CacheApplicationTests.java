package com.cache;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CacheApplicationTests {
	@Autowired
	private Calculator calculator;

	@Test
	void sequentialCacheTest() throws InterruptedException {
		long start = System.currentTimeMillis()/1000;
		long res1 = calculator.sum(1, 2);
		long res2 = calculator.subtract(1, 3);
		long res3 = calculator.sum(1, 2);
		long res4 = calculator.subtract(1, 3);
		long end = System.currentTimeMillis()/1000;
		long time = end - start;

		assertThat(res1).isEqualTo(res3).isEqualTo(3);
		assertThat(res2).isEqualTo(res4).isEqualTo(-2);
		assertThat(time).isEqualTo(6);
	}

	@Test
	void parallelCacheTest() {
		Thread th1 = new Thread(() -> {
			try {
				long res = calculator.multiply(5, 6);
				assertThat(res).isEqualTo(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		Thread th2 = new Thread(() -> {
			try {
				double res = calculator.divide(12, 2);
				assertThat(res).isEqualTo(6);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		Thread th3 = new Thread(() -> {
			try {
				long res = calculator.multiply(5, 6);
				assertThat(res).isEqualTo(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		Thread th4 = new Thread(() -> {
			try {
				double res = calculator.divide(12, 2);
				assertThat(res).isEqualTo(6);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		long start = System.currentTimeMillis()/1000;
		th1.start();
		th2.start();
		th3.start();
		th4.start();

		try {
			th1.join();
			th2.join();
			th3.join();
			th4.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis()/1000;
		long time = end - start;

		assertThat(time).isBetween(3L, 4L);
	}
}
