package me.lucko.spark.bukkit;

import me.lucko.spark.common.sampler.source.ClassSourceLookup;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;

public class PaperClassSourceLookup extends ClassSourceLookup.ByClassLoader {
    private static final Class<?> PLUGIN_CLASS_LOADER;
    private static final Method GET_PLUGN_METHOD;

    static {
        try {
            PLUGIN_CLASS_LOADER = Class.forName("io.papermc.paper.plugin.entrypoint.classloader.PaperPluginClassLoader");
            GET_PLUGN_METHOD = PLUGIN_CLASS_LOADER.getDeclaredMethod("getPlugin");
            GET_PLUGN_METHOD.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public String identify(ClassLoader loader) throws ReflectiveOperationException {
        if (PLUGIN_CLASS_LOADER.isInstance(loader)) {
            JavaPlugin plugin = (JavaPlugin) GET_PLUGN_METHOD.invoke(loader);
            return plugin.getName();
        }
        return null;
    }
}

