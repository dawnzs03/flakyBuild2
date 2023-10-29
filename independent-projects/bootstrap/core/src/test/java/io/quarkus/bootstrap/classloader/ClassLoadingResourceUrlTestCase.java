package io.quarkus.bootstrap.classloader;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.exporter.ExplodedExporter;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.quarkus.bootstrap.classloading.DirectoryClassPathElement;
import io.quarkus.bootstrap.classloading.JarClassPathElement;
import io.quarkus.bootstrap.classloading.MemoryClassPathElement;
import io.quarkus.bootstrap.classloading.QuarkusClassLoader;
import io.quarkus.bootstrap.util.IoUtils;

//see https://github.com/quarkusio/quarkus/issues/10943
public class ClassLoadingResourceUrlTestCase {

    /**
     * URLClassLoader will return URL's that end with a / if the call to getResource ends with a /
     */
    @ParameterizedTest
    @MethodSource("paths")
    public void testUrlReturnedFromClassLoaderDirectory(String testPath) throws Exception {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class)
                .add(new StringAsset("a"), "a.txt")
                .add(new StringAsset("b"), "b/b.txt");
        Path path = Files.createTempDirectory(testPath);
        try {
            jar.as(ExplodedExporter.class).exportExploded(path.toFile(), "tmp");

            ClassLoader cl = QuarkusClassLoader.builder("test", getClass().getClassLoader(), false)
                    .addElement(new DirectoryClassPathElement(path.resolve("tmp"), true))
                    .build();
            URL res = cl.getResource("a.txt");
            Assertions.assertNotNull(res);
            Assertions.assertNull(res.getQuery());
            res = cl.getResource("a.txt/");
            Assertions.assertNull(res);

            res = cl.getResource("b");
            Assertions.assertNotNull(res);
            Assertions.assertFalse(res.toExternalForm().endsWith("/"));

            res = cl.getResource("b/");
            Assertions.assertNotNull(res);
            Assertions.assertTrue(res.toExternalForm().endsWith("/"));

        } finally {
            IoUtils.recursiveDelete(path);
        }
    }

    /**
     * Test that {@link QuarkusClassLoader#getResourceAsStream(String)} returns a stream for directory
     * resources
     *
     * @throws Exception
     * @see <a href="https://github.com/quarkusio/quarkus/issues/11707"/>
     */
    @ParameterizedTest
    @MethodSource("paths")
    public void testResourceAsStreamForDirectory(String testPath) throws Exception {
        final JavaArchive jar = ShrinkWrap.create(JavaArchive.class)
                .add(new StringAsset("a"), "a.txt")
                .add(new StringAsset("b"), "b/b.txt");
        final Path tmpDir = Files.createTempDirectory(testPath);
        try {
            jar.as(ExplodedExporter.class).exportExploded(tmpDir.toFile(), "tmpcltest");
            final ClassLoader cl = QuarkusClassLoader.builder("test", getClass().getClassLoader(), false)
                    .addElement(new DirectoryClassPathElement(tmpDir.resolve("tmpcltest"), true))
                    .build();

            try (final InputStream is = cl.getResourceAsStream("b/")) {
                Assertions.assertNotNull(is, "InputStream is null for a directory resource");
            }
            try (final InputStream is = cl.getResourceAsStream("b")) {
                Assertions.assertNotNull(is, "InputStream is null for a directory resource");
            }
        } finally {
            IoUtils.recursiveDelete(tmpDir);
        }
    }

    /**
     * URLClassLoader will return URL's that end with a / if the call to getResource ends with a /
     */
    @ParameterizedTest
    @MethodSource("paths")
    public void testUrlReturnedFromClassLoaderJarFile(String testPath) throws Exception {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class)
                .add(new StringAsset("a"), "a.txt")
                .add(new StringAsset("b"), "b/b.txt");
        Path path = Files.createTempFile(testPath, "quarkus-test.jar");
        try {
            jar.as(ZipExporter.class).exportTo(path.toFile(), true);

            ClassLoader cl = QuarkusClassLoader.builder("test", getClass().getClassLoader(), false)
                    .addElement(new JarClassPathElement(path, true))
                    .build();
            URL res = cl.getResource("a.txt");
            Assertions.assertNotNull(res);
            Assertions.assertNull(res.getQuery());
            res = cl.getResource("a.txt/");
            Assertions.assertNull(res);

            res = cl.getResource("b");
            Assertions.assertNotNull(res);
            Assertions.assertFalse(res.toExternalForm().endsWith("/"));

            res = cl.getResource("b/");
            Assertions.assertNotNull(res);
            Assertions.assertTrue(res.toExternalForm().endsWith("/"));

        } finally {
            IoUtils.recursiveDelete(path);
        }
    }

    @Test
    public void testMemoryUrlConnections() throws Exception {
        long start = System.currentTimeMillis();
        Thread.sleep(2);

        ClassLoader cl = QuarkusClassLoader.builder("test", getClass().getClassLoader(), false)
                .addElement(
                        new MemoryClassPathElement(Collections.singletonMap("a.txt", "hello".getBytes(StandardCharsets.UTF_8)),
                                true))
                .build();
        URL res = cl.getResource("a.txt");
        Assertions.assertNotNull(res);
        URLConnection urlConnection = res.openConnection();
        Assertions.assertEquals(5, urlConnection.getContentLength());
        Assertions.assertTrue(urlConnection.getLastModified() > start);
        Assertions.assertEquals("hello", new String(urlConnection.getInputStream().readAllBytes(), StandardCharsets.UTF_8));

    }

    public static Iterable<Object[]> paths() {
        if (OS.WINDOWS.isCurrentOs()) {
            return List.<Object[]> of(new Object[] { "test" });
        }
        return List.<Object[]> of(new Object[] { "test" }, new Object[] { "dir?with?question?mark" });
    }
}
