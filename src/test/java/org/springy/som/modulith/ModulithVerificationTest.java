package org.springy.som.modulith;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.core.Violations;

class ModulithVerificationTest {

	@Test
	void verifiesApplicationModules() {
		Violations violations = ApplicationModules.of(SomServerApplication.class).detectViolations();
		violations.throwIfPresent();
	}
}
