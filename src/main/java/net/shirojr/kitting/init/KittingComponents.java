package net.shirojr.kitting.init;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.shirojr.kitting.component.KitComponent;

public class KittingComponents implements EntityComponentInitializer {
    public static final ComponentKey<KitComponent> KIT = ComponentRegistry.getOrCreate(KitComponent.KEY, KitComponent.class);


    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(KIT, KitComponent::new, KitComponent::onRespawn);
    }
}
