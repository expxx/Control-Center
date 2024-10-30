package dev.expx.ctrlctr.center.servertype.velocity;

import dev.expx.ctrlctr.center.lang.Lang;
import dev.expx.ctrlctr.center.lang.LangLoader;
import dev.expx.ctrlctr.center.modules.ModuleManager;
import dev.expx.ctrlctr.center.util.ServerIF;
import dev.expx.ctrlctr.center.util.dependencies.resolver.DirectMavenResolver;
import dev.expx.ctrlctr.center.util.dependencies.resolver.impl.SimpleLibraryStore;
import dev.expx.ctrlctr.center.util.dependencies.resolver.lib.URLClassLoaderHelper;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Map;
import java.util.ResourceBundle;

import static dev.expx.ctrlctr.center.Statics.lang;

public class DepMngr {

    public DepMngr(DirectMavenResolver resolver, Path dataDir, ServerIF iface) {
        ResourceBundle bundle = new LangLoader(getClass(), "lang", "en", "US", dataDir).getBundle();
        lang = new Lang(bundle);


        Class<?> clazz = Velocity.class;
        resolver.addRepository(clazz, new RemoteRepository.Builder("central", "default", "https://repo.maven.apache.org/maven2/").build());
        resolver.addRepository(clazz, new RemoteRepository.Builder("jitpack", "default", "https://jitpack.io").build());
        resolver.addRepository(clazz, new RemoteRepository.Builder("papermc", "default", "https://papermc.io/repo/repository/maven-public/").build());
        resolver.addRepository(clazz, new RemoteRepository.Builder("sonatype", "default", "https://oss.sonatype.org/content/groups/public/").build());
        resolver.addRepository(clazz, new RemoteRepository.Builder("the-cavern-tp", "default", "https://repo.expx.dev/repository/third-party/").build());

        resolver.addDependency(clazz, new Dependency(new DefaultArtifact("dev.dejvokep:boosted-yaml:1.3.6"), "compile"));

        resolver.addDependency(clazz, new Dependency(new DefaultArtifact("redis.clients:jedis:5.2.0"), "compile"));
        resolver.addDependency(clazz, new Dependency(new DefaultArtifact("com.rabbitmq:amqp-client:5.21.0"), "compile"));
        resolver.addDependency(clazz, new Dependency(new DefaultArtifact("org.mongodb:mongodb-driver-sync:5.2.0"), "compile"));
        resolver.addDependency(clazz, new Dependency(new DefaultArtifact("org.mongodb:mongodb-driver-core:5.2.0"), "compile"));
        resolver.addDependency(clazz, new Dependency(new DefaultArtifact("org.mongodb:bson:5.2.0"), "compile"));

        resolver.addDependency(clazz, new Dependency(new DefaultArtifact("io.socket:socket.io-client:1.0.3-SNAPSHOT-PATCH3"), "compile"));
        resolver.addDependency(clazz, new Dependency(new DefaultArtifact("io.socket:engine.io-client:1.0.3-SNAPSHOT-PATCH1"), "compile"));

        resolver.addDependency(clazz, new Dependency(new DefaultArtifact("com.squareup.okhttp3:okhttp:4.12.0"), "compile"));
        resolver.addDependency(clazz, new Dependency(new DefaultArtifact("com.squareup.okio:okio:3.9.1"), "compile"));
        resolver.addDependency(clazz, new Dependency(new DefaultArtifact("com.squareup.okio:okio-jvm:3.9.1"), "compile"));
        resolver.addDependency(clazz, new Dependency(new DefaultArtifact("org.jetbrains.kotlin:kotlin-stdlib:2.0.21"), "compile"));

        resolver.addDependency(clazz, new Dependency(new DefaultArtifact("com.google.code.gson:gson:2.11.0"), "compile"));
        resolver.addDependency(clazz, new Dependency(new DefaultArtifact("com.github.oshi:oshi-core:6.6.5"), "compile"));
        resolver.addDependency(clazz, new Dependency(new DefaultArtifact("org.eclipse.collections:eclipse-collections:12.0.0.M3"), "compile"));
        resolver.addDependency(clazz, new Dependency(new DefaultArtifact("org.eclipse.collections:eclipse-collections-api:12.0.0.M3"), "compile"));
        resolver.addDependency(clazz, new Dependency(new DefaultArtifact("com.google.auto.service:auto-service:1.1.1"), "compile"));

        resolver.addDependency(clazz, new Dependency(new DefaultArtifact("commons-io:commons-io:2.16.1"), "compile"));
        resolver.addDependency(clazz, new Dependency(new DefaultArtifact("org.projectlombok:lombok:1.18.34"), "compile"));

        resolver.addDependency(clazz, new Dependency(new DefaultArtifact("org.json:json:20240303"), "compile"));

        ModuleManager.dependencyModules(resolver, dataDir);

        Logger log = LoggerFactory.getLogger(DepMngr.class);

        log.info(lang.lang("dependency-header"));
        log.info("");
        log.info(lang.lang("dependency-dep-amount", resolver.getDependencies().size()));
        for (Map.Entry<Dependency, Class<?>> dependency : resolver.getDependencies().entrySet()) {
            Artifact artifact = dependency.getKey().getArtifact();
            log.info(lang.lang("dependency-dependency", dependency.getValue().getName(), artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(),
                    (StringUtils.isBlank(artifact.getClassifier()) ? "" : ":" + artifact.getClassifier())));
        }
        log.info("");
        log.info(lang.lang("dependency-repo-amount", resolver.getRepositories().size()));
        for (Map.Entry<RemoteRepository, Class<?>> repository : resolver.getRepositories().entrySet()) {
            log.info(lang.lang("dependency-repository", repository.getValue().getName(), repository.getKey().getId(), repository.getKey().getUrl()));
        }
        log.info("");
        log.info(lang.lang("dependency-footer"));

        URLClassLoaderHelper helper = new URLClassLoaderHelper((URLClassLoader) iface.proxyInterface().getClass().getClassLoader());
        SimpleLibraryStore sls = new SimpleLibraryStore();
        resolver.register(sls);
        for(Path path : sls.getPaths()) {
            helper.addToClasspath(path);
        }

    }

}
