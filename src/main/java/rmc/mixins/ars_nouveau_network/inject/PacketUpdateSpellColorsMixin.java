package rmc.mixins.ars_nouveau_network.inject;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.hollingsworth.arsnouveau.api.util.StackUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor.IntWrapper;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateSpellColors;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.NetworkEvent.Context;

/**
 * Developed by RMC Team, 2021
 * @author KR33PY
 */
@Mixin(value = PacketUpdateSpellColors.class)
public abstract class PacketUpdateSpellColorsMixin {

    @Shadow int castSlot;
    @Shadow int r;
    @Shadow int g;
    @Shadow int b;

    @Overwrite(remap = false)
    public void handle(Supplier<Context> ctxs) {
        Context ctx = ctxs.get();
        if (this.castSlot >= 1 && this.castSlot <= 10
         && this.r >= 1 && this.r <= 255
         && this.g >= 1 && this.g <= 255
         && this.b >= 1 && this.b <= 255) {
            ctx.enqueueWork(() -> {
                ServerPlayerEntity player = ctx.getSender();
                if (player != null) {
                    ItemStack book = StackUtil.getHeldSpellbook(player);
                    if (!book.isEmpty()
                     && book.getItem() instanceof SpellBook) {
                        CompoundNBT bookTag = book.hasTag() ? book.getTag() : new CompoundNBT();
                        SpellBook.setSpellColor(bookTag, new IntWrapper(this.r, this.g, this.b), this.castSlot);
                        book.setTag(bookTag);
                    }
                }
            });
        }
        ctx.setPacketHandled(true);
    }

}