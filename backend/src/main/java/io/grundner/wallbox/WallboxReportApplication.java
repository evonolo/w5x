package io.grundner.wallbox;

import io.grundner.wallbox.desktop.DesktopLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.util.Arrays;

@SpringBootApplication
@ConfigurationPropertiesScan
public class WallboxReportApplication {

	public static void main(String[] args) {
		// Windows-Desktop-App (ADR 0004) wird mit --desktop gestartet
		if (Arrays.asList(args).contains(DesktopLauncher.ARGUMENT)) {
			DesktopLauncher.main(args);
			return;
		}
		SpringApplication.run(WallboxReportApplication.class, args);
	}

}
