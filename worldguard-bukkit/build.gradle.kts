import io.papermc.paperweight.userdev.ReobfArtifactConfiguration

plugins {
    `java-library`
    id("buildlogic.platform")
    id("io.papermc.paperweight.userdev")
}
paperweight.reobfArtifactConfiguration = ReobfArtifactConfiguration.MOJANG_PRODUCTION
dependencies {
    "api"(project(":worldguard-core"))
    "api"(libs.worldedit.bukkit) { isTransitive = false }
    "compileOnly"(libs.commandbook) { isTransitive = false }

    "compileOnly"(libs.jetbrains.annotations) {
        because("Resolving Spigot annotations")
    }
    "testCompileOnly"(libs.jetbrains.annotations) {
        because("Resolving Spigot annotations")
    }
    paperweight.devBundle("com.starsrealm.nylon", "1.21.7-R0.1-20250705.122835-1") {
        exclude("org.slf4j", "slf4j-api")
        exclude("junit", "junit")
    }

    "implementation"(libs.paperLib)
    "implementation"(libs.bstats.bukkit)
}

tasks.named<Copy>("processResources") {
    val internalVersion = project.ext["internalVersion"]
    inputs.property("internalVersion", internalVersion)
    filesMatching("plugin.yml") {
        expand("internalVersion" to internalVersion)
    }
}

tasks.shadowJar {
    dependencies {
        include(dependency(":worldguard-core"))
        include(dependency("org.bstats:"))
        include(dependency("io.papermc:paperlib"))

        relocate("org.bstats", "com.sk89q.worldguard.bukkit.bstats")
        relocate("io.papermc.lib", "com.sk89q.worldguard.bukkit.paperlib")
    }
}

tasks.named("assemble").configure {
    dependsOn("shadowJar")
}

configure<PublishingExtension> {
    publications.named<MavenPublication>("maven") {
        from(components["java"])
    }
}
