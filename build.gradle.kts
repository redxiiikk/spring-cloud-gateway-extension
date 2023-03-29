import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java-library")
    id("maven-publish")
    id("org.springframework.boot") version "3.0.5"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.22"
}

group = "com.github.redxiiikk.spring.cloud"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

extra["springCloudVersion"] = "2022.0.1"

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

dependencies {
    compileOnly("org.springframework.cloud:spring-cloud-starter-gateway")
    compileOnly("org.springframework.cloud:spring-cloud-starter-loadbalancer")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

publishing {
    repositories {
        mavenLocal()
    }

    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "$group"
            artifactId = rootProject.name
            version = "${project.version}"

            from(components["kotlin"])
            afterEvaluate {
                artifact(sourcesJar.get())
            }

            pom {
                name.set(rootProject.name)
                description.set("Spring cloud gateway extension")
                url.set("https://github.com/redxiiikk/spring-cloud-gateway-extension")
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://opensource.org/license/mit/")
                    }
                }
                developers {
                    developer {
                        id.set("redxiiikk")
                        name.set("redxiiikk")
                        url.set("https://github.com/redxiiikk")
                    }

                    developer {
                        id.set("redxiiikkTW")
                        name.set("redxiiikkTW")
                        url.set("https://github.com/redxiiikkTW")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/redxiiikk/spring-cloud-gateway-extension.git")
                    developerConnection.set("scm:git:ssh://github.com/redxiiikk/spring-cloud-gateway-extension.git")
                    url.set("https://github.com/redxiiikk/spring-cloud-gateway-extension")
                }
            }
        }
    }
}

tasks.withType<KotlinCompile> {
    dependsOn("processResources")

    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val sourcesJar = tasks.register<Jar>("sourcesJar") {
    from(sourceSets["main"].allJava)
    archiveClassifier.set("sources")
}
