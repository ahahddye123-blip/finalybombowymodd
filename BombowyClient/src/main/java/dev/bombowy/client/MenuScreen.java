package dev.bombowy.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuScreen extends Screen {
    public static final Identifier SETTINGS_ICON = Identifier.of("bombowyclient", "textures/settings.png");
    public static final Identifier BOMB_ICON_64 = Identifier.of("bombowyclient", "textures/bomb_64.png");

    private static final String[] CATEGORIES = {
        "Wszystkie", "Aktywne", "Walka", "Wygląd", "HUD", "Narzędzia"
    };

    private int scrollOffset = 0;

    public MenuScreen() {
        super(Text.literal("BombowyClient — Konfiguracja"));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    // Ostre, nie-rozmazane tło bez mocnego blura
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0x660B0D12);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        this.renderBackground(ctx, mouseX, mouseY, delta);

        int sidebarW = 148;
        ctx.fill(0, 0, sidebarW, this.height, 0xF2151820);
        ctx.fill(sidebarW, 0, sidebarW + 1, this.height, 0xFF2E3442);

        ctx.drawTexture(net.minecraft.client.render.RenderLayer::getGuiTextured, BOMB_ICON_64, 12, 12, 0, 0, 28, 28, 28, 28);
        ctx.drawTextWithShadow(this.textRenderer, "§6§lBOMBOWYMOD", 46, 14, 0xFFFF912D);
        ctx.drawTextWithShadow(this.textRenderer, "§7Client 1.21.4", 46, 26, 0xFF9AA4B8);

        int catY = 56;
        BombowyConfig cfg = BombowyClient.CONFIG;
        for (String cat : CATEGORIES) {
            boolean active = cat.equals(cfg.activeCategory);
            int bg = active ? 0xFF2A2F3D : 0x00000000;
            ctx.fill(10, catY, sidebarW - 10, catY + 22, bg);
            if (active) {
                ctx.fill(10, catY, 13, catY + 22, 0xFFFF912D);
            }
            ctx.drawTextWithShadow(this.textRenderer, cat, 20, catY + 7, active ? 0xFFFFFFFF : 0xFFB8C0D0);
            catY += 26;
        }

        int testBtnY = this.height - 54;
        boolean hoverTest = mouseX >= 12 && mouseX <= sidebarW - 12 && mouseY >= testBtnY && mouseY <= testBtnY + 20;
        ctx.fill(12, testBtnY, sidebarW - 12, testBtnY + 20, hoverTest ? 0xFFFF912D : 0xFF262B38);
        ctx.drawCenteredTextWithShadow(this.textRenderer, "Testuj efekt (" + KillEffects.EffectType.fromId(cfg.selectedKillEffect).displayName + ")", sidebarW / 2, testBtnY + 6, 0xFFFFFFFF);

        int contentX = sidebarW + 18;
        int contentW = this.width - contentX - 18;
        ctx.fill(contentX - 6, 10, this.width - 12, this.height - 12, 0xD911141C);
        ctx.drawTextWithShadow(this.textRenderer, "§f§lModuły i opcje", contentX, 16, 0xFFFFFFFF);

        int gearX = this.width - 36;
        int gearY = 12;
        ctx.fill(gearX - 2, gearY - 2, gearX + 20, gearY + 20, 0xFF222733);
        ctx.drawTexture(net.minecraft.client.render.RenderLayer::getGuiTextured, SETTINGS_ICON, gearX, gearY, 0, 0, 18, 18, 18, 18);

        List<Map.Entry<String, Boolean>> visible = new ArrayList<>();
        for (Map.Entry<String, Boolean> e : cfg.modules.entrySet()) {
            if ("Aktywne".equals(cfg.activeCategory) && !e.getValue()) continue;
            if (!cfg.searchQuery.isEmpty() && !e.getKey().toLowerCase().contains(cfg.searchQuery.toLowerCase())) continue;
            visible.add(e);
        }

        int cols = Math.max(1, contentW / 155);
        int cardW = (contentW - (cols - 1) * 10) / cols;
        int cardH = 68;
        int startY = 44 - scrollOffset;

        for (int i = 0; i < visible.size(); i++) {
            Map.Entry<String, Boolean> entry = visible.get(i);
            int col = i % cols;
            int row = i / cols;
            int x = contentX + col * (cardW + 10);
            int y = startY + row * (cardH + 10);
            if (y + cardH < 36 || y > this.height) continue;

            boolean enabled = entry.getValue();
            ctx.fill(x, y, x + cardW, y + cardH, 0xF21A1E28);
            int border = enabled ? 0xFFFF912D : 0xFF2E3442;
            ctx.fill(x, y, x + cardW, y + 1, border);
            ctx.fill(x, y + cardH - 1, x + cardW, y + cardH, border);
            ctx.fill(x, y, x + 1, y + cardH, border);
            ctx.fill(x + cardW - 1, y, x + cardW, y + cardH, border);

            ctx.drawTextWithShadow(this.textRenderer, entry.getKey(), x + 8, y + 7, 0xFFFFFFFF);
            ctx.drawTexture(net.minecraft.client.render.RenderLayer::getGuiTextured, SETTINGS_ICON, x + 8, y + 40, 0, 0, 16, 16, 16, 16);

            int btnX = x + 28;
            int btnY = y + 38;
            int btnW = cardW - 36;
            int btnH = 20;
            ctx.fill(btnX, btnY, btnX + btnW, btnY + btnH, enabled ? 0xFFFF7A1A : 0xFF2B303D);
            ctx.drawCenteredTextWithShadow(this.textRenderer, enabled ? "Włączony" : "Wyłączony", btnX + btnW / 2, btnY + 6, 0xFFFFFFFF);
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        BombowyConfig cfg = BombowyClient.CONFIG;
        int sidebarW = 148;

        int catY = 56;
        for (String cat : CATEGORIES) {
            if (mouseX >= 10 && mouseX <= sidebarW - 10 && mouseY >= catY && mouseY <= catY + 22) {
                cfg.activeCategory = cat;
                scrollOffset = 0;
                return true;
            }
            catY += 26;
        }

        int testBtnY = this.height - 54;
        if (mouseX >= 12 && mouseX <= sidebarW - 12 && mouseY >= testBtnY && mouseY <= testBtnY + 20) {
            KillEffects.triggerTest();
            return true;
        }

        int contentX = sidebarW + 18;
        int contentW = this.width - contentX - 18;
        List<Map.Entry<String, Boolean>> visible = new ArrayList<>();
        for (Map.Entry<String, Boolean> e : cfg.modules.entrySet()) {
            if ("Aktywne".equals(cfg.activeCategory) && !e.getValue()) continue;
            if (!cfg.searchQuery.isEmpty() && !e.getKey().toLowerCase().contains(cfg.searchQuery.toLowerCase())) continue;
            visible.add(e);
        }

        int cols = Math.max(1, contentW / 155);
        int cardW = (contentW - (cols - 1) * 10) / cols;
        int cardH = 68;
        int startY = 44 - scrollOffset;

        for (int i = 0; i < visible.size(); i++) {
            Map.Entry<String, Boolean> entry = visible.get(i);
            int col = i % cols;
            int row = i / cols;
            int x = contentX + col * (cardW + 10);
            int y = startY + row * (cardH + 10);
            int btnX = x + 28;
            int btnY = y + 38;
            int btnW = cardW - 36;
            int btnH = 20;
            if (mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
                cfg.toggle(entry.getKey());
                if ("Efekty zabójstwa".equals(entry.getKey())) {
                    cfg.killEffectsEnabled = cfg.isEnabled(entry.getKey());
                } else if ("Licznik COLDOWNER".equals(entry.getKey())) {
                    cfg.coldOwnerTimerEnabled = cfg.isEnabled(entry.getKey());
                } else if ("Przedmioty eventowe TXT".equals(entry.getKey())) {
                    cfg.customTextureEventItems = cfg.isEnabled(entry.getKey());
                }
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollOffset = Math.max(0, scrollOffset - (int) (verticalAmount * 18));
        return true;
    }
}
