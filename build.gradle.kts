plugins {
    `java-library`
    id("io.papermc.paperweight.userdev").version("2.0.0-beta.18")
    id("xyz.jpenilla.run-paper") version("3.0.0-beta.1")
    // id("xyz.jpenilla.resource-factory-bukkit-convention").version("1.3.0")
}

group = "com.gmail.subnokoii78"
version = "1.0-SNAPSHOT"
description = "Test plugin for net.minecraft package"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

dependencies {
    paperweight.paperDevBundle("1.21.8-R0.1-SNAPSHOT")
}

tasks {
    compileJava {
        options.release = 21
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    withType<JavaCompile> {
        // ソースコードの文字列エンコード形式をUTF-8にする(これやらないとコンパイル時に日本語が文字化けする)
        options.encoding = Charsets.UTF_8.name()

        // 各種警告を無視(これがないと永遠にビルドできない)
        options.compilerArgs.add("-Xlint:none")
    }
}
