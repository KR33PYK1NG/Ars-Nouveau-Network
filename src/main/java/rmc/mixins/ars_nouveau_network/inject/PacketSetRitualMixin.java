package rmc.mixins.ars_nouveau_network.inject;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.hollingsworth.arsnouveau.common.items.RitualBook;
import com.hollingsworth.arsnouveau.common.network.PacketSetRitual;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.NetworkEvent.Context;

/**
 * Developed by RMC Team, 2021
 * @author KR33PY
 */
@Mixin(value = PacketSetRitual.class)
public abstract class PacketSetRitualMixin {

    @Shadow String ritualID;
    @Shadow boolean isMainhand;

    @Overwrite(remap = false)
    public void handle(Supplier<Context> ctxs) {
        Context ctx = ctxs.get();
        if (this.ritualID != null) {
            ctx.enqueueWork(() -> {
                ServerPlayerEntity player = ctx.getSender();
                if (player != null) {
                    ItemStack book = this.isMainhand ? player.getMainHandItem() : player.getOffhandItem();
                    if (!book.isEmpty()
                     && book.getItem() instanceof RitualBook
                     && RitualBook.getRitualCaster(book).getUnlockedRitualIDs().contains(this.ritualID)) {
                        RitualBook.setRitualID(book, this.ritualID);
                    }
                }
            });
        }
        ctx.setPacketHandled(true);
    }

}