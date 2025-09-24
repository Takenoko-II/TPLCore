import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    id("io.papermc.paperweight.userdev").version("2.0.0-beta.18")
    // id("xyz.jpenilla.run-paper") version("3.0.0-beta.1")
    // id("xyz.jpenilla.resource-factory-bukkit-convention").version("1.3.0")
    id("com.gradleup.shadow").version("9.1.0")
}

group = "com.gmail.subnokoii78"
version = "1.0-SNAPSHOT"
description = ""

repositories {
    mavenCentral()
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

dependencies {
    paperweight.paperDevBundle("1.21.8-R0.1-SNAPSHOT")
    implementation(files(
        "../JSON/target/JSON-1.0-SNAPSHOT.jar",
        "../ConsoleColorizer/target/ConsoleColorizer-1.0-SNAPSHOT.jar"
    ))
}

tasks {
    compileJava {
        options.release = 21
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    named<ShadowJar>("shadowJar") {
        archiveClassifier.set("")
    }

    shadowJar {
        mergeServiceFiles()
    }

    // jar fileの生成位置をコンソールに出力する(これはなくてもok)
    withType<Jar>().configureEach {
        doLast {
            println("Jar file was generated at: ${archiveFile.get().asFile.absolutePath}")
        }
    }

    withType<JavaCompile> {
        // ソースコードの文字列エンコード形式をUTF-8にする(これやらないとコンパイル時に日本語が文字化けする)
        options.encoding = Charsets.UTF_8.name()

        // 各種警告を無視(これがないと永遠にビルドできない)
        options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:-unchecked"))
    }

    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
}
