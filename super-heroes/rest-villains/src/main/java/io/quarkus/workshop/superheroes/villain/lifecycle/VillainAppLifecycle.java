package io.quarkus.workshop.superheroes.villain.lifecycle;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.configuration.ConfigUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class VillainAppLifecycle {

  @Inject
  private Logger logger;

  public void onStart(@Observes StartupEvent ev) {
    logger.info("__      ___  _ _      _             -   _____  _____    ");
    logger.info("\\ \\    / (_) | |     (_)           /\\   |  __ \\_   _|");
    logger.info(" \\ \\  / / _| | | __ _ _ _ __      /  \\  | |__) || |  ");
    logger.info("  \\ \\/ / | | | |/ _` | | '_ \\    / /\\ \\ |  ___/ | |  ");
    logger.info("   \\  /  | | | | (_| | | | | |  / ____ \\| |    _| |_ ");
    logger.info("    \\/   |_|_|_|\\__,_|_|_| |_| /_/    \\_\\_|   |_____|");
    logger.info("The application VILLAIN is starting with profile " + ConfigUtils.getProfiles());
  }

  public void onStop(@Observes ShutdownEvent ev) {
      logger.info("The application VILLAIN is stopping...");
  }
}
