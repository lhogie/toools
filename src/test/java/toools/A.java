package toools;

import org.junit.jupiter.api.Test;

class A {

	@Test
	void test() {
		SimpleCache<Long> cache = new SimpleCache<>(null, () -> System.currentTimeMillis());
		cache.get();
	}
}
