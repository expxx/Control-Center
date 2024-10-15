
# Control Center

Control Center is a hobby project of mine, and just to play around with various things. I use this for any plugin I'd create, just because I have all my utilities easily accessible, and I can easily add more. It's a work in progress, and I'm always adding more to it.

## Authors
- [@expxx](https://www.github.com/expxx)

## Example Usage

### Dependencies
```xml
<repositories>
    <repository>
        <id>the-cavern</id>
        <url>https://repo.expx.dev/repository/public-releases/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>dev.expx.ctrlctr</groupId>
        <artifactId>ctrlctr</artifactId>
        <version>1.0.6</version>
    </dependency>
</dependencies>
```
Or, if you want to live on the edge, take a look at the snapshot repository found [here](https://repo.expx.dev/repository/public-snapshots/).

### Main Class
You'll notice it's a lot like the Bukkit or PaperMC API, and that's because it's based off them in a sense. It's a lot more abstracted, however, and is designed to be more user-friendly. However, it's still in development, so it's not perfect.

Along with that, you still _can_ access the Bukkit API directly, and for some things, you may need to. You can do so via the `getPlugin()` method, which returns the `JavaPlugin` instance. From there, you can `getServer()` to access the `Server` instance, and from there, you can access the Bukkit API directly.

If there's something there you find yourself accessing often, please open an issue or submit a PR adding it to the Control Center API. The goal is to be as user-friendly as possible, and if you find yourself repeatedly using the Bukkit API, that's not user-friendly.

```java
package example;

import com.google.auto.service.AutoService;
import dev.expx.ctrlctr.center.modules.Module;
import lombok.Getter;
import org.bukkit.command.CommandSender;

@AutoService(Module.class)
public class ModuleMain extends Module {

    @Getter
    private static ModuleMain instance;

    @Override
    public void create() {
        instance = this;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void reload(CommandSender executor) {

    }
}
```

## module.toml
Modules are loaded via AutoService from Google, then loaded via the `ModuleLoader` class, which askes for a `module.toml` from each module located in the resources. If it's not there, it won't load the module. This is to prevent any issues with loading modules that aren't meant to be loaded, and supplying information about the module.

```toml
id = "example"
name = "Example"
description = "Example Module for Control Center"
version = "${project.version}"
dependencies = []
```

## Dependency Loader
In an effort to keep jar sizes down, and to keep the project as modular as possible, I've supplied the PaperMC method of downloading dependencies at runtime. This is done via the `DependencyLoader` class, which downloads the dependencies from the Maven repository, and loads them into the classpath. In order to use this, you'll need to add a new class to your project. You can find an example below.

This one I'm not entirely sure about, and I'm still working on it. It's a bit of a pain to get working, but it's a lot better than having a 50MB jar file. In the future, I may refactor this to be a bit more user-friendly, but for now, it's a bit of a pain, sorry :( 

```java
package example;

import com.google.auto.service.AutoService;
import dev.expx.ctrlctr.center.modules.ModuleDependencyLoader;
import dev.expx.ctrlctr.center.util.DirectMavenResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

@AutoService(ModuleDependencyLoader.class)
public class DepMngr extends ModuleDependencyLoader {
    @Override
    public void loadDependencies(DirectMavenResolver mavenResolver) {
        mavenResolver.addRepository(ModuleMain.class, new RemoteRepository.Builder("example", "default", "https://repo.maven.apache.org/maven2/").build());
        mavenResolver.addDependency(ModuleMain.class, new Dependency(new DefaultArtifact("org.apache.commons:commons-lang3:3.12.0"), "compile"));
    }
}
```

## Registering & Creating Commands
Once again, with this one, I'm not entirely sure about the syntax. I'm still working on it, and I'm not entirely sure how I want to do it. I'm thinking of using annotations, but I'm not entirely sure. For now, you can use the `registerCommand` method provided to all Module classes to register commands. You can find an example below.

This might get depricated and replaced with Annotations once I figure those out, but for now, this is how you do it. If you have any suggestions, please open an issue or submit a PR.

```java
// Command.java
package example;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;

@SuppressWarnings("UnstableApiUsage")
public class Command implements BasicCommand {
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        source.sendMessage("Hello, world!");
    }
}
```
```java
// ModuleMain.java
package example;

import com.google.auto.service.AutoService;
import dev.expx.ctrlctr.center.modules.Module;
import lombok.Getter;
import org.bukkit.command.CommandSender;

@AutoService(Module.class)
public class ModuleMain extends Module {

    @Getter
    private static ModuleMain instance;

    @Override
    public void create() {
        instance = this;
        registerCommand("helloworld", "Sends a simple Hello World", new Command());
    }

    @Override
    public void destroy() {

    }

    @Override
    public void reload(CommandSender executor) {

    }
}
```

## Registering Events
For now, unfortunately, I haven't implemented a way to register events. I'm still working on it, and I'm not entirely sure how I want to do it. It'll probably be similar to the new way of registering commands, however for now you have to use the Bukkit API directly.

## Conclusion
All in all, this is just a hobby project. I'm not entirely sure where I want to go with it, but I'm having fun with it. If you have any suggestions, please open an issue or submit a PR. I'm always looking for new ideas, and I'm always looking to improve. 

If you think I'm missing any documentation, or things are unclear, please let me know. I just covered the basics here, and I'm sure there's a lot more to cover.

If you want to have a chat with me, you can join my discord [here](https://discord.gg/Pnq3BCBBax) and select the Control Center role. I'm always looking to chat with new people!