package net.shirojr.kitting.mixin.compat;

import net.minecraft.util.Identifier;
import net.shirojr.kitting.compat.PehkuiCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import virtuoel.pehkui.api.ScaleType;
import virtuoel.pehkui.api.ScaleTypes;

@Mixin(ScaleTypes.class)
public abstract class PehkuiCompatMixin {

    @Inject(method = "register(Lnet/minecraft/util/Identifier;Lvirtuoel/pehkui/api/ScaleType$Builder;)Lvirtuoel/pehkui/api/ScaleType;", at = @At("RETURN"))
    private static void registerTest(Identifier id, ScaleType.Builder builder, CallbackInfoReturnable<ScaleType> cir) {
        ScaleType returnValue = cir.getReturnValue();
        PehkuiCompat.ALL_SCALE_TYPES.put(id, returnValue);
    }
}
