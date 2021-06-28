package rmc.mixins.ars_nouveau_network.inject;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.util.StackUtil;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateSpellbook;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.NetworkEvent.Context;

/**
 * Developed by RMC Team, 2021
 * @author KR33PY
 */
@Mixin(value = PacketUpdateSpellbook.class)
public abstract class PacketUpdateSpellbookMixin {

    private static Field rmc$maxSlots;

    @Shadow String spellRecipe;
    @Shadow int cast_slot;
    @Shadow String spellName;

    @Overwrite(remap = false)
    public void handle(Supplier<Context> ctxs) {
        Context ctx = ctxs.get();
        if (this.spellRecipe != null
         && this.spellName != null
         && this.cast_slot >= 1 && this.cast_slot <= 10) {
            ctx.enqueueWork(() -> {
                ServerPlayerEntity player = ctx.getSender();
                if (player != null) {
                    ItemStack book = StackUtil.getHeldSpellbook(player);
                    if (!book.isEmpty()
                     && book.getItem() instanceof SpellBook) {
                        boolean valid = true;
                        CompoundNBT bookTag = book.hasTag() ? book.getTag() : new CompoundNBT();
                        List<AbstractSpellPart> current = Spell.deserialize(this.spellRecipe).recipe;
                        int maxSize;
                        try {
                            if (rmc$maxSlots == null) {
                                rmc$maxSlots = SpellBook.class.getDeclaredField("rmc$MAX_SLOTS");
                                rmc$maxSlots.setAccessible(true);
                            }
                            maxSize = rmc$maxSlots.getInt(null);
                        } catch (Exception ex) {
                            maxSize = 10;
                        }
                        if (current.size() <= maxSize) {
                            List<AbstractSpellPart> unlocked = SpellBook.getUnlockedSpells(bookTag);
                            for (AbstractSpellPart part : current) {
                                if (!unlocked.contains(part)) {
                                    valid = false;
                                    break;
                                }
                            }
                        }
                        else {
                            valid = false;
                        }
                        if (valid) {
                            SpellBook.setRecipe(bookTag, this.spellRecipe, this.cast_slot);
                            SpellBook.setSpellName(bookTag, this.spellName, this.cast_slot);
                            SpellBook.setMode(bookTag, this.cast_slot);
                            book.setTag(bookTag);
                        }
                    }
                }
            });
        }
        ctx.setPacketHandled(true);
    }

}