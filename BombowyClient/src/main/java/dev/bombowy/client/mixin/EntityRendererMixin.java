package dev.bombowy.client.mixin;

import dev.bombowy.client.BombowyHud;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    private void bombowyclient$appendBombAboveHead(
        S state,
        Text text,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
        CallbackInfo ci
    ) {
        if (state instanceof EntityRenderState) {
            String raw = text != null ? text.getString() : "";
            if (!raw.isEmpty() && !raw.contains("💣")) {
                Text updated = BombowyHud.formatNicknameWithBomb(text);
                // Delegujemy przez standardowe renderowanie z dopisaną ikoną bomby
                @SuppressWarnings("unchecked")
                EntityRenderer<T, S> self = (EntityRenderer<T, S>) (Object) this;
                ci.cancel();
                self.renderLabelIfPresent(state, updated, matrices, vertexConsumers, light);
            }
        }
    }
}
