package example;

import com.google.auto.service.AutoService;
import dev.expx.ctrlctr.center.modules.ModuleDependencyLoader;
import dev.expx.ctrlctr.center.util.DirectMavenResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
@AutoService(ModuleDependencyLoader.class)
public class DepMngr extends ModuleDependencyLoader {
    @Override
    public void loadDependencies(DirectMavenResolver mavenResolver) {
        mavenResolver.addRepository(ModuleMain.class, new RemoteRepository.Builder("example", "default", "https://repo.maven.apache.org/maven2/").build());
        mavenResolver.addDependency(ModuleMain.class, new Dependency(new DefaultArtifact("org.apache.commons:commons-lang3:3.12.0"), "compile"));
    }
}
