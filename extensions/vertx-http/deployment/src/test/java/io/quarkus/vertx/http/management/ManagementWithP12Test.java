package io.quarkus.vertx.http.management;

import java.io.File;
import java.util.function.Consumer;

import javax.inject.Singleton;

import jakarta.enterprise.event.Observes;

import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.builder.BuildChainBuilder;
import io.quarkus.builder.BuildContext;
import io.quarkus.builder.BuildStep;
import io.quarkus.test.QuarkusUnitTest;
import io.quarkus.vertx.http.deployment.NonApplicationRootPathBuildItem;
import io.quarkus.vertx.http.deployment.RouteBuildItem;
import io.restassured.RestAssured;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class ManagementWithP12Test {
    private static final String APP_PROPS = "" +
            "quarkus.management.enabled=true\n" +
            "quarkus.management.root-path=/management\n" +
            "quarkus.management.ssl.certificate.key-store-file=server-keystore.p12\n" +
            "quarkus.management.ssl.certificate.key-store-password=secret\n";

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addAsResource(new StringAsset(APP_PROPS), "application.properties")
                    .addAsResource(new File("src/test/resources/conf/server-keystore.p12"), "server-keystore.p12")
                    .addClasses(MyObserver.class))
            .addBuildChainCustomizer(buildCustomizer());

    static Consumer<BuildChainBuilder> buildCustomizer() {
        return new Consumer<BuildChainBuilder>() {
            @Override
            public void accept(BuildChainBuilder builder) {
                builder.addBuildStep(new BuildStep() {
                    @Override
                    public void execute(BuildContext context) {
                        NonApplicationRootPathBuildItem buildItem = context.consume(NonApplicationRootPathBuildItem.class);
                        context.produce(buildItem.routeBuilder()
                                .management()
                                .route("my-route")
                                .handler(new MyHandler())
                                .blockingRoute()
                                .build());
                    }
                }).produces(RouteBuildItem.class)
                        .consumes(NonApplicationRootPathBuildItem.class)
                        .build();
            }
        };
    }

    public static class MyHandler implements Handler<RoutingContext> {
        @Override
        public void handle(RoutingContext rc) {
            Assertions.assertThat(rc.request().connection().isSsl()).isTrue();
            Assertions.assertThat(rc.request().isSSL()).isTrue();
            Assertions.assertThat(rc.request().connection().sslSession()).isNotNull();
            rc.response().end("ssl");
        }
    }

    @Test
    public void testSslWithP12() {
        RestAssured.given()
                .relaxedHTTPSValidation()
                .get("https://0.0.0.0:9001/management/my-route")
                .then().statusCode(200).body(Matchers.equalTo("ssl"));
    }

    @Singleton
    static class MyObserver {

        void test(@Observes String event) {
            //Do Nothing
        }

    }
}
