import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
// import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

// コンパイルに必要なプラグインを持ってくる
plugins {
    `java-library`
    id("io.papermc.paperweight.userdev").version("2.0.0-beta.18")
    id("xyz.jpenilla.run-paper") version("3.0.0-beta.1") // Adds runServer and runMojangMappedServer tasks for testing
    // id("xyz.jpenilla.resource-factory-bukkit-convention").version("1.3.0") // Generates plugin.yml based on the Gradle config
    id("com.gradleup.shadow").version("9.1.0")
}

// プロジェクトの設定
group = "com.gmail.subnokoii78"
version = "1.0-SNAPSHOT"
description = "Test plugin for net.minecraft package"

// Gradle側が必要な時に自動でJava 21を用意できるようにする
java {
    // Configure the java toolchain. This allows gradle to auto-provision JDK 21 on systems that only have JDK 11 installed for example.
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

// 1)
// For >=1.20.5 when you don't care about supporting spigot
// paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

// 2)
// For 1.20.4 or below, or when you care about supporting Spigot on >=1.20.5
// Configure reobfJar to run when invoking the build task
/*
tasks.assemble {
  dependsOn(tasks.reobfJar)
}
 */

// ほしいPaperAPIとNMSのバージョン指定
dependencies {
    paperweight.paperDevBundle("1.21.8-R0.1-SNAPSHOT")
}

tasks {
    compileJava {
        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release = 21
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    // Only relevant when going with option 2 above
    /*
    reobfJar {
      // This is an example of how you might change the output location for reobfJar. It's recommended not to do this
      // for a variety of reasons, however it's asked frequently enough that an example of how to do it is included here.
      outputJar = layout.buildDirectory.file("libs/PaperweightTestPlugin-${project.version}.jar")
    }
     */

    named<ShadowJar>("shadowJar") {
        archiveClassifier.set("")
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
        options.compilerArgs.add("-Xlint:none");
    }
}

// Configure plugin.yml generation
// - name, version, and description are inherited from the Gradle project.
// plugin.ymlファイルを生成する
/*bukkitPluginYaml {
    main = "com.gmail.subnokoii78.testnmsplugin.TestNMSPlugin"
    load = BukkitPluginYaml.PluginLoadOrder.STARTUP
    authors.add("Takenoko-II")
    apiVersion = "1.21"
    commands {
        register("bar") {

        }
    }
}*/
