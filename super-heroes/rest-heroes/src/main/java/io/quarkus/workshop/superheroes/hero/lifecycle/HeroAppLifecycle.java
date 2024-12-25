package io.quarkus.workshop.superheroes.hero.lifecycle;


import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.configuration.ConfigUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class HeroAppLifecycle {

  @Inject
  private Logger logger;

  public void onStart(@Observes StartupEvent ev) {
    logger.info("The application HERO is starting with profile " + ConfigUtils.getProfiles());
  }

  public void onStop(@Observes ShutdownEvent ev) {
    logger.info("The application HERO is stopping...");
  }
}
