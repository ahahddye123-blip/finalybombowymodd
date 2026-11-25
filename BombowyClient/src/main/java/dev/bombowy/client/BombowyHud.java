package dev.bombowy.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.util.ArrayList;
import java.util.List;

public class BombowyHud {
    public static class TimedItemEntry {
        public final String id;
        public final String label;
        public final ItemStack fallbackStack;
        public long expiresAtMs;
        public final int totalSeconds;

        public TimedItemEntry(String id, String label, ItemStack fallbackStack, int totalSeconds) {
            this.id = id;
            this.label = label;
            this.fallbackStack = fallbackStack;
            this.totalSeconds = Math.max(1, totalSeconds);
            this.expiresAtMs = System.currentTimeMillis() + this.totalSeconds * 1000L;
        }

        public float remainingSeconds() {
            long remMs = Math.max(0L, expiresAtMs - System.currentTimeMillis());
            return remMs / 1000.0f;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() >= expiresAtMs;
        }
    }

    private static final List<TimedItemEntry> TIMERS = new ArrayList<>();

    public static void triggerColdOwner() {
        if (!BombowyClient.CONFIG.coldOwnerTimerEnabled) return;
        int sec = Math.max(1, BombowyClient.CONFIG.coldOwnerDurationSeconds);
        TIMERS.removeIf(e -> e.id.equals("COLDOWNER"));
        TIMERS.add(new TimedItemEntry("COLDOWNER", "COLDOWNER", new ItemStack(Items.ENDER_PEARL), sec));
    }

    public static void triggerEventItem(String name, ItemStack stack, int seconds) {
        if (!BombowyClient.CONFIG.customTextureEventItems) return;
        String cleanName = (name == null || name.isBlank()) ? "Eventówka" : name;
        ItemStack copy = (stack != null && !stack.isEmpty()) ? stack.copy() : new ItemStack(Items.NETHER_STAR);
        TIMERS.removeIf(e -> e.id.equalsIgnoreCase(cleanName));
        TIMERS.add(new TimedItemEntry(cleanName, cleanName, copy, Math.max(1, seconds)));
    }

    public static void tick() {
        TIMERS.removeIf(TimedItemEntry::isExpired);
    }

    public static MutableText formatNicknameWithBomb(Text originalName) {
        MutableText base = originalName == null ? Text.literal("") : originalName.copy();
        if (!BombowyClient.CONFIG.bombBadgeEnabled) {
            return base;
        }
        Style bombStyle = Style.EMPTY.withColor(TextColor.fromRgb(0xFF912D)).withBold(true);
        MutableText badge = Text.literal(" 💣").setStyle(bombStyle);
        return base.append(badge);
    }

    // HUD renderuje wyłącznie liczniki przedmiotów/efektów — nick gracza z ikoną 💣 pojawia się bezpośrednio nad postacią w grze (EntityRendererMixin)
    public static void render(DrawContext ctx, float tickDelta) {
        if (!BombowyClient.CONFIG.hudVisible) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || TIMERS.isEmpty()) return;

        int baseX = 12;
        int y = 26;
        for (TimedItemEntry entry : TIMERS) {
            float rem = entry.remainingSeconds();
            String timeStr = String.format(java.util.Locale.ROOT, "%.1fs", rem);

            ItemStack displayStack = entry.fallbackStack;
            if ("COLDOWNER".equals(entry.id)) {
                for (int i = 0; i < mc.player.getInventory().size(); i++) {
                    ItemStack invStack = mc.player.getInventory().getStack(i);
                    String customName = invStack.getName().getString();
                    if (customName.toUpperCase().contains("COLDOWNER")) {
                        displayStack = invStack;
                        break;
                    }
                }
            }

            ctx.drawItem(displayStack, baseX, y - 4);
            ctx.drawStackOverlay(mc.textRenderer, displayStack, baseX, y - 4);

            int color = rem <= 3.0f ? 0xFFFF5555 : (rem <= 7.0f ? 0xFFFFAA00 : 0xFFFFFFFF);
            ctx.drawTextWithShadow(mc.textRenderer, entry.label + " " + timeStr, baseX + 22, y + 2, color);
            y += 22;
        }
    }
}
