package foxo.flanty.proxyApi.settings;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;


public class YamlUtils {
    public static boolean saveConfigs(Path path) throws IOException {
        Files.createDirectories(path);
        return copyResourceIfNotExists("config.yml", path) &&
                copyResourceIfNotExists("endpoints.yml", path) &&
                copyResourceIfNotExists("language.yml", path);
    }

    public static boolean copyResourceIfNotExists(String resourceName, Path targetDir) {
        Path targetPath = targetDir.resolve(resourceName);

        if (Files.exists(targetPath)) return true;

        try (InputStream resourceStream = YamlUtils.class.getClassLoader().getResourceAsStream(resourceName)) {

            Files.copy(resourceStream, targetPath);
        } catch (IOException e) {
            System.err.println("Ошибка при копировании: " + e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean loadConfigs(Path configDir) {
        return loadConfig(configDir.resolve("config.yml")) &&
                loadEndpoints(configDir.resolve("endpoints.yml")) &&
                loadLanguage(configDir.resolve("language.yml"));

    }

    private static boolean loadConfig(Path path) {
        Map<String, Object> data = loadYaml(path);
        if (data == null) return false;

        Config.authTime = getInt(data, "authTime", Config.authTime);
        Config.bossBar = getBoolean(data, "bossBar", Config.bossBar);
        Config.httpPort = getInt(data, "httpPort", Config.httpPort);
        Config.uuidGenerateKey = getString(data, "uuidGenerateKey", Config.uuidGenerateKey);
        return true;
    }

    private static boolean loadEndpoints(Path path) {
        Map<String, Object> data = loadYaml(path);
        if (data == null) return false;

        Endpoints.loginEndpoint = getString(data, "loginEndpoint", Endpoints.loginEndpoint);
        Endpoints.logoutEndpoint = getString(data, "logoutEndpoint", Endpoints.logoutEndpoint);
        Endpoints.playerLoginRequest = getString(data, "playerLoginRequest", Endpoints.playerLoginRequest);

        return true;
    }

    private static boolean loadLanguage(Path path) {
        Map<String, Object> data = loadYaml(path);
        if (data == null) return false;

        Language.bossBarName = getString(data, "bossBarName", Language.bossBarName);
        Language.bossBarColor = getString(data, "bossBarColor", Language.bossBarColor);
        Language.loginTimeOut = getString(data, "loginTimeOut", Language.loginTimeOut);
        Language.logoutReason = getString(data, "logoutReason", Language.logoutReason);
        Language.urlPlaceholder = getString(data, "urlPlaceholder", Language.urlPlaceholder);
        Language.urlHoverText = getString(data, "urlHoverText", Language.urlHoverText);
        Language.loginWelcome = getString(data, "loginWelcome", Language.loginWelcome);
        Language.loginMessage = getString(data, "loginMessage", Language.loginMessage);
        Language.notInWhitelist = getString(data, "notInWhitelist", Language.notInWhitelist);
        Language.reloadMessage = getString(data, "reloadMessage", Language.reloadMessage);
        return true;
    }

    public static Map<String, Object> loadYaml(Path filePath) {


        try (InputStream inputStream = Files.newInputStream(filePath)) {
            Yaml yaml = new Yaml();
            return yaml.load(inputStream);
        } catch (YAMLException e) {
            System.err.println("Ошибка синтаксиса в файле " + filePath + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ошибка загрузки " + filePath + ": " + e.getMessage());
        }

        return null;
    }

    private static String getString(Map<String, Object> data, String key, String defaultValue) {
        return data.getOrDefault(key, defaultValue).toString();
    }

    private static int getInt(Map<String, Object> data, String key, int defaultValue) {
        Object value = data.get(key);
        return (value instanceof Number) ? ((Number) value).intValue() : defaultValue;
    }

    private static boolean getBoolean(Map<String, Object> data, String key, boolean defaultValue) {
        Object value = data.get(key);
        return (value instanceof Boolean) ? (Boolean) value : defaultValue;
    }

}
