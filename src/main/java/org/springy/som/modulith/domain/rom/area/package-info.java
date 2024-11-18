@org.springframework.modulith.ApplicationModule(
        type = ApplicationModule.Type.OPEN,
        allowedDependencies = {
                "org.springy.som.modulith.domain.rom.room",
                "org.springy.som.modulith.domain.rom.mobile"
        }
)
package org.springy.som.modulith.domain.rom.area;

import org.springframework.modulith.ApplicationModule;