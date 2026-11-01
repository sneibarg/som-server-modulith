package org.springy.som.modulith;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModulithVerificationTest {

	@Test
	void verifiesApplicationModules() {
		ApplicationModules.of(SomServerApplication.class).verify();
	}
}
