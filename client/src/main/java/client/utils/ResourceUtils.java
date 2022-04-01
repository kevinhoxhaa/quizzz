package client.utils;

import java.io.File;

public class ResourceUtils {
    /**
     * Returns the resource with the specified name
     * @param resourceName the resource name
     * @return the resource file
     */
    public static File getResource(String resourceName) {
        String path = String.valueOf(ResourceUtils.class.getClassLoader().getResource(""));
        path = path.substring(
                0, path.length() - "classes/java/main/".length())
                + "resources/main/client/images/" + resourceName;
        return new File(path);
    }
}
