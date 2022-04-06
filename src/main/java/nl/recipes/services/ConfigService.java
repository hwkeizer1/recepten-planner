package nl.recipes.services;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import static nl.recipes.views.ViewConstants.*;

@Slf4j
@Component
public class ConfigService {

	Path rootPath;
	Path appConfigPath;
	Properties appConfig;
	
	public ConfigService() {
		rootPath = Path.of("").toAbsolutePath();
		appConfigPath = Path.of(rootPath.toString(), "recepten-planner.config");
		appConfig = new Properties();
		readConfigProperties();
		setDefaultConfiguration();
	}
	
	public String getConfigProperty(String key) {
		return appConfig.getProperty(key);
	}
	
	public void setConfigProperty(String key, String value) {
		appConfig.setProperty(key, value);
		writeConfigProperties();
	}
	
	public void readConfigProperties() {
		try (FileInputStream input = new FileInputStream(appConfigPath.toString())) {
			appConfig.load(input);
		} catch (IOException e) {
			log.error("Could not load configuration: {}", e.getMessage());
		}
	}
	
	public void writeConfigProperties() {
		try (FileWriter writer = new FileWriter(appConfigPath.toString())) {
			appConfig.store(writer, null);
		} catch (IOException e) {
			log.error("Could not write configuration: {}", e.getMessage());
		}
	}
	
	private void setDefaultConfiguration() {
		if (getConfigProperty(BACKUPS_TO_KEEP) == null || getConfigProperty(BACKUPS_TO_KEEP).isBlank()) {
			setConfigProperty(BACKUPS_TO_KEEP, "5");
		}
	}
}
