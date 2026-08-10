package net.shirojr.kitting.compat;

import com.glisco.numismaticoverhaul.ModComponents;
import com.glisco.numismaticoverhaul.currency.CurrencyComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.shirojr.kitting.compat.util.CompatEntry;
import net.shirojr.kitting.compat.util.Mod;
import net.shirojr.kitting.util.KittingNbtKeys;

public class NumismaticCompat implements CompatEntry<NumismaticCompat> {
    private long storedCurrency;

    public NumismaticCompat(long storedCurrency) {
        this.loadCheck();
        this.storedCurrency = storedCurrency;
    }

    public NumismaticCompat() {
        this(0);
    }

    @Override
    public Mod getCompatMod() {
        return Mod.NUMISMATIC_OVERHAUL;
    }

    public long getStoredCurrency() {
        return storedCurrency;
    }

    public void setStoredCurrency(long storedCurrency) {
        this.storedCurrency = Math.max(0, storedCurrency);
    }

    private static long getLiveCurrency(PlayerEntity player) {
        return ModComponents.CURRENCY.get(player).getValue();
    }

    private static void setLiveCurrency(PlayerEntity player, long value) {
        CurrencyComponent component = ModComponents.CURRENCY.get(player);
        component.pushTransaction(-component.getValue());
        component.pushTransaction(value);
        component.commitTransactions();
    }

    @Override
    public NumismaticCompat copy() {
        return new NumismaticCompat(this.storedCurrency);
    }

    @Override
    public void update(PlayerEntity player) {
        this.setStoredCurrency(getLiveCurrency(player));
    }

    @Override
    public void apply(PlayerEntity player) {
        setLiveCurrency(player, this.getStoredCurrency());
    }

    @Override
    public void clearLiveData(PlayerEntity player) {
        setLiveCurrency(player, 0);
    }

    @Override
    public void fromNbt(NbtCompound nbt) {
        if (nbt.contains(KittingNbtKeys.NUMISMATIC_CURRENCY)) {
            this.storedCurrency = nbt.getLong(KittingNbtKeys.NUMISMATIC_CURRENCY);
        }
    }

    @Override
    public void toNbt(NbtCompound nbt) {
        nbt.putLong(KittingNbtKeys.NUMISMATIC_CURRENCY, this.storedCurrency);
    }
}
