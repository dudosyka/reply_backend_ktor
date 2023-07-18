plugins {
    id("com.reply.kotlin-application-conventions")
}

dependencies {
    implementation(project(":libs"))
    implementation(project(mapOf("path" to ":gateway-service")))
}

application {
    // Define the main class for the application.
    mainClass.set("com.reply.block.ApplicationKt")
}
