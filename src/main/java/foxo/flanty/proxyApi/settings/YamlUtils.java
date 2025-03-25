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
        Config.registerTime = getInt(data, "registerTime", Config.registerTime);
        Config.bossBar = getBoolean(data, "bossBar", Config.bossBar);
        Config.discordLoggingEnabled = getBoolean(data, "discordLoggingEnabled", Config.discordLoggingEnabled);
        Config.loginAttempts = getInt(data, "loginAttempts", Config.loginAttempts);
        Config.loginSessionTime = getInt(data, "loginSessionTime", Config.loginSessionTime);

        return true;
    }

    private static boolean loadEndpoints(Path path) {
        Map<String, Object> data = loadYaml(path);
        if (data == null) return false;

        Endpoints.MojangAPI = getString(data, "MojangAPI", Endpoints.MojangAPI);
        Endpoints.skinUpdate = getString(data, "skinUpdate", Endpoints.skinUpdate);
        Endpoints.playerPasswordsHashes = getString(data, "playerPasswordsHashes", Endpoints.playerPasswordsHashes);
        Endpoints.playerRegister = getString(data, "playerRegister", Endpoints.playerRegister);
        Endpoints.discordLogger = getString(data, "discordLogger", Endpoints.discordLogger);
        Endpoints.playerJoin = getString(data, "playerJoin", Endpoints.playerJoin);

        return true;
    }

    private static boolean loadLanguage(Path path) {
        Map<String, Object> data = loadYaml(path);
        if (data == null) return false;

        Language.bossBarName = getString(data, "bossBarName", Language.bossBarName);
        Language.loginTimeOut = getString(data, "loginTimeOut", Language.loginTimeOut);
        Language.registerMessage = getString(data, "registerMessage", Language.registerMessage);
        Language.urlPlaceholder = getString(data, "urlPlaceholder", Language.urlPlaceholder);
        Language.loginWelcome = getString(data, "loginWelcome", Language.loginWelcome);
        Language.loginMessage = getString(data, "loginMessage", Language.loginMessage);
        Language.wrongCommand = getString(data, "wrongCommand", Language.wrongCommand);
        Language.loginCommandExample = getString(data, "loginCommandExample", Language.loginCommandExample);
        Language.wrongPassword = getString(data, "wrongPassword", Language.wrongPassword);
        Language.loginAttemptsOut = getString(data, "loginAttemptsOut", Language.loginAttemptsOut);
        Language.commandDelay = getString(data, "commandDelay", Language.commandDelay);

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
