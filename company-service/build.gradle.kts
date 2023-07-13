plugins {
    id("com.reply.kotlin-application-conventions")
}

dependencies {
    implementation(project(":libs"))
}

application {
    // Define the main class for the application.
    mainClass.set("com.reply.company.ApplicationKt")
}
