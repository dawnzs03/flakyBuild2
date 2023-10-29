package io.quarkus.hibernate.reactive.panache.test.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.hibernate.reactive.mutiny.Mutiny;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.arc.Arc;
import io.quarkus.hibernate.reactive.panache.test.MyEntity;
import io.quarkus.test.QuarkusUnitTest;

public class ConfigEnabledFalseTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar.addClass(MyEntity.class))
            .withConfigurationResource("application.properties")
            // We shouldn't get any build error caused by Panache consuming build items that are not produced
            // See https://github.com/quarkusio/quarkus/issues/28842
            .overrideConfigKey("quarkus.hibernate-orm.enabled", "false");

    @Test
    public void startsWithoutError() {
        // Quarkus started without problem, even though the Panache extension is present.
        // Just check that Hibernate ORM is disabled.
        assertThat(Arc.container().instance(Mutiny.SessionFactory.class).get())
                .isNull();
    }
}
