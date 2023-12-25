package latte.lib.tikv.plugin;


import java.util.Objects;

public class JarResourceDir {
    String jarPath;
    String resourceDir;

    public JarResourceDir(String jarPath, String resourceDir) {
        this.jarPath = jarPath;
        this.resourceDir = resourceDir;
    }

    public String getJarPath() {
        return jarPath;
    }

    public String getResourceDir() {
        return resourceDir;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JarResourceDir that = (JarResourceDir) o;
        return Objects.equals(jarPath, that.jarPath) && Objects.equals(resourceDir, that.resourceDir);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jarPath, resourceDir);
    }
}
