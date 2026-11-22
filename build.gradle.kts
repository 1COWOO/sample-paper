import io.papermc.paperweight.tasks.RemapJar

plugins {
    kotlin("jvm") version "2.4.21"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.24"
    id("com.gradleup.shadow") version "9.6.1"
    id("xyz.jpenilla.run-paper") version "3.1.0"
}

repositories {
    mavenCentral()
    maven("https://repo.xenondevs.xyz/releases")

}

dependencies {
    paperweight.paperDevBundle("1.21.8-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
//    implementation("dev.jorel:commandapi-paper-shade:12.1.0")
//    implementation("dev.jorel:commandapi-kotlin-paper:12.1.0") {
//        exclude(group = "dev.jorel", module = "commandapi-paper-core")
//    }
//
//    implementation("xyz.xenondevs.invui:invui:1.49")
//    implementation("xyz.xenondevs.invui:invui-kotlin:1.49")
//    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.23.0")
//    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.23.0")
}

kotlin {
    jvmToolchain(21)
}
paperweight.reobfArtifactConfiguration =
    io.papermc.paperweight.userdev.ReobfArtifactConfiguration.REOBF_PRODUCTION
runPaper {
    disablePluginJarDetection()
}
val reobfJarTask = tasks.named<RemapJar>("reobfJar")

tasks.assemble {
    dependsOn(reobfJarTask)
}

tasks.runServer {
    minecraftVersion("1.21.8")
    javaLauncher.set(javaToolchains.launcherFor {
        vendor.set(JvmVendorSpec.JETBRAINS)
        languageVersion.set(JavaLanguageVersion.of(21))
    })
    jvmArgs(
        "-Xms2G", "-Xmx2G",
        "-Dcom.mojang.eula.agree=true",
        "--add-modules=jdk.incubator.vector",
        "-XX:+UseG1GC", "-XX:+ParallelRefProcEnabled",
        "-XX:MaxGCPauseMillis=200",
        "-XX:+UnlockExperimentalVMOptions",
        "-XX:+DisableExplicitGC",
        "-XX:G1HeapWastePercent=5", "-XX:G1MixedGCCountTarget=4",
        "-XX:InitiatingHeapOccupancyPercent=15",
        "-XX:G1MixedGCLiveThresholdPercent=90",
        "-XX:G1RSetUpdatingPauseTimePercent=5",
        "-XX:SurvivorRatio=32", "-XX:+PerfDisableSharedMem",
        "-XX:MaxTenuringThreshold=1",
        "-Dusing.aikars.flags=https://mcflags.emc.gs",
        "-Daikars.new.flags=true",
        "-XX:G1NewSizePercent=30", "-XX:G1MaxNewSizePercent=40",
        "-XX:G1HeapRegionSize=8M", "-XX:G1ReservePercent=20",
        "-Dfile.encoding=UTF-8",
        "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005",
        "-XX:+AllowEnhancedClassRedefinition"
    )
    pluginJars.from(reobfJarTask.flatMap { it.outputJar })
}
tasks {
    processResources {
        val props = mapOf("version" to version)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    shadowJar {
        val relocateBase = "${project.group}.libs"
        manifest {
            attributes["paperweight-mappings-namespace"] = "spigot"
        }
//        relocate("dev.jorel.commandapi", "$relocateBase.commandapi")
//        relocate("xyz.xenondevs.invui", "$relocateBase.invui")
//        relocate("xyz.xenondevs.inventoryaccess", "$relocateBase.inventoryaccess")
//        relocate("com.github.shynixn.mccoroutine", "$relocateBase.mccoroutine")
    }
}
