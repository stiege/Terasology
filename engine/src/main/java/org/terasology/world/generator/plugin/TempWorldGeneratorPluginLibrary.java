/*
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.world.generator.plugin;

import com.google.common.collect.Sets;
import org.terasology.assets.module.ModuleAwareAssetTypeManager;
import org.terasology.config.Config;
import org.terasology.engine.module.ModuleManager;
import org.terasology.module.DependencyInfo;
import org.terasology.module.Module;
import org.terasology.module.ModuleEnvironment;
import org.terasology.naming.Name;
import org.terasology.reflection.copy.CopyStrategyLibrary;
import org.terasology.reflection.reflect.ReflectFactory;
import org.terasology.registry.CoreRegistry;

import java.util.Set;

/**
 * A fake environment so that plugins can be loaded for configuration.
 *
 * @author Immortius
 */
public class TempWorldGeneratorPluginLibrary extends DefaultWorldGeneratorPluginLibrary {

    public TempWorldGeneratorPluginLibrary() {
        super(getEnv(), CoreRegistry.get(ReflectFactory.class), CoreRegistry.get(CopyStrategyLibrary.class));
    }

    private static ModuleEnvironment getEnv() {
        ModuleManager moduleManager = CoreRegistry.get(ModuleManager.class);
        Config config = CoreRegistry.get(Config.class);

        Set<Module> selectedModules = Sets.newHashSet();
        for (Name moduleName : config.getDefaultModSelection().listModules()) {
            Module module = moduleManager.getRegistry().getLatestModuleVersion(moduleName);
            if (module != null) {
                selectedModules.add(module);
                for (DependencyInfo dependencyInfo : module.getMetadata().getDependencies()) {
                    selectedModules.add(moduleManager.getRegistry().getLatestModuleVersion(dependencyInfo.getId()));
                }
            }
        }
        ModuleEnvironment environment = moduleManager.loadEnvironment(selectedModules, false);
        CoreRegistry.get(ModuleAwareAssetTypeManager.class).switchEnvironment(environment);
        return environment;
    }
}
