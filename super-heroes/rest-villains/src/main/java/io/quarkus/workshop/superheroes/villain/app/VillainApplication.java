package io.quarkus.workshop.superheroes.villain.app;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

@ApplicationPath("/")
@OpenAPIDefinition(
  info = @Info(
    title = "Villain API",
    description = "This API allows CRUD operations on a villain",
    version = "1.0"
  ),
  servers = { @Server(url = "http://localhost:8084") }
)
public class VillainApplication extends Application {
  // Empty body
}