plugins {
    kotlin("jvm")
    java
}

group = "me.akhsaul"
version = "1.0"
val korioVersion = "2.2.0"

dependencies {
    implementation("com.github.oshi:oshi-core-java11:6.1.0")
    implementation("net.java.dev.jna:jna-jpms:5.10.0")
    implementation("net.java.dev.jna:jna-platform-jpms:5.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${property("coroutine.version")}")
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    implementation("com.squareup.okhttp3:okhttp-urlconnection")
    implementation("com.squareup.okhttp3:okhttp-dnsoverhttps")
    //implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
    implementation("org.apache.tika:tika-core:2.2.1")
    implementation("org.jsoup:jsoup:1.14.3")
    implementation("commons-codec:commons-codec:1.15")
    implementation("com.soywiz.korlibs.korio:korio:$korioVersion")
    /*
    annotationProcessor("com.google.auto.service:auto-service:1.0.1")
    implementation("com.google.auto.service:auto-service-annotations:1.0.1")
    sudah di implement
    implementation("com.google.code.gson:gson:2.8.7")
    implementation("org.passay:passay:1.6.1")
    implementation("org.apache.commons:commons-text:1.9")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("com.transloadit.sdk:transloadit:0.3.0")

    // it will be implemented
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.tukaani:xz:1.9")
    implementation("org.lz4:lz4-java:1.8.0")
    // implement difference string
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.apache.commons:commons-compress:1.21")
    implementation("org.apache.commons:commons-exec:1.3")

    implementation("com.github.atomashpolskiy:bt-core:1.10")
    implementation("com.github.atomashpolskiy:bt-http-tracker-client:1.10")
    implementation("com.github.atomashpolskiy:bt-dht:1.10")
    implementation("com.github.atomashpolskiy:bt-upnp:1.10")
    implementation("io.socket:socket.io-client:2.0.1")
    implementation("io.sentry:sentry:5.5.0")
    implementation("com.newrelic.agent.android:agent-gradle-plugin:6.3.0")
    implementation("net.sourceforge.htmlunit:htmlunit:2.56.0")
    implementation("io.tus.java.client:tus-java-client:0.4.5")

    // include for JVM target
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:$kotlinxHtmlVersion")
    // include for JS target
    implementation("org.jetbrains.kotlinx:kotlinx-html-js:$kotlinxHtmlVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-html:$kotlinxHtmlVersion")
     */
}
