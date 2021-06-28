package rmc.mixins.ars_nouveau_network.inject;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.hollingsworth.arsnouveau.api.util.StackUtil;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.network.PacketSetBookMode;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.NetworkEvent.Context;

/**
 * Developed by RMC Team, 2021
 * @author KR33PY
 */
@Mixin(value = PacketSetBookMode.class)
public abstract class PacketSetBookModeMixin {

    @Shadow CompoundNBT tag;

    @Overwrite(remap = false)
    public void handle(Supplier<Context> ctxs) {
        Context ctx = ctxs.get();
        if (this.tag != null) {
            int slot = this.tag.getInt(SpellBook.BOOK_MODE_TAG);
            if (slot >= 1 && slot <= 10) {
                ctx.enqueueWork(() -> {
                    ServerPlayerEntity player = ctx.getSender();
                    if (player != null) {
                        ItemStack book = StackUtil.getHeldSpellbook(player);
                        if (!book.isEmpty()
                         && book.getItem() instanceof SpellBook) {
                            CompoundNBT bookTag = book.hasTag() ? book.getTag() : new CompoundNBT();
                            SpellBook.setMode(bookTag, slot);
                            book.setTag(bookTag);
                        }
                    }
                });
            }
        }
        ctx.setPacketHandled(true);
    }

}