package me.lucko.spark.common.sampler.source;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ClassSourceLookupGrouper extends ClassSourceLookup.ByClassLoader {
    private final List<ClassSourceLookup.ByClassLoader> lookups;

    public ClassSourceLookupGrouper(List<Class<? extends ByClassLoader>> lookups) {
        this.lookups = new ArrayList<>();
        int index = -1;
        for (Class<? extends ByClassLoader> lookup : lookups) {
            index++;
            try {
                this.lookups.add(lookup.getConstructor().newInstance());
            } catch (ExceptionInInitializerError e) {
                if (index == lookups.size() - 1 && this.lookups.isEmpty()) {
                    throw e; // we are the last lookup, and we have no lookups that have been successfully initialised, so throw the error
                }
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                throw new ExceptionInInitializerError(e);
            }
        }
        if (this.lookups.isEmpty()) {
            throw new ExceptionInInitializerError("No lookups were found");
        }
    }

    @Override
    public @Nullable String identify(ClassLoader loader) throws Exception {
        for (ByClassLoader lookup : lookups) {
            String source = lookup.identify(loader);
            if (source != null) {
                return source;
            }
        }
        return null;
    }

    @Override
    public @Nullable String identify(MethodCall methodCall) throws Exception {
        for (ByClassLoader lookup : lookups) {
            String source = lookup.identify(methodCall);
            if (source != null) {
                return source;
            }
        }
        return null;
    }

    @Override
    public @Nullable String identify(MethodCallByLine methodCall) throws Exception {
        for (ByClassLoader lookup : lookups) {
            String source = lookup.identify(methodCall);
            if (source != null) {
                return source;
            }
        }
        return null;
    }
}
