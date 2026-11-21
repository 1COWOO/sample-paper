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
    jvmArgs("-Xms2G", "-Xmx2G", "-Dcom.mojang.eula.agree=true")
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
