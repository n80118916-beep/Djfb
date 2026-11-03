package ru.forgeclient.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;
import ru.forgeclient.module.Module;
import ru.forgeclient.module.ModuleManager;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public class MenuScreen extends Screen {
    // Цвета
    private final int BG = new Color(15, 15, 18, 245).getRGB();
    private final int SIDE = new Color(22, 22, 28, 245).getRGB();
    private final int ACCENT = new Color(155, 89, 255).getRGB();
    private final int TEXT = new Color(240, 240, 240).getRGB();
    private final int TEXT_DIS = new Color(120, 120, 130).getRGB();
    private final int MOD_OFF = new Color(30, 30, 35).getRGB();

    private int x, y;
    private final int width = 500, height = 300, sidebarWidth = 140;
    
    private String category = "Combat";
    private final List<String> categories = Arrays.asList("Combat", "Movement", "Render");

    public MenuScreen() {
        super(new StringTextComponent("Menu"));
    }

    @Override
    protected void init() {
        this.x = (this.width - width) / 2;
        this.y = (this.height - height) / 2;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);

        // Тень
        fill(matrixStack, x + 2, y + 2, x + width + 2, y + height + 2, new Color(0, 0, 0, 100).getRGB());
        // Фон
        fill(matrixStack, x, y, x + width, y + height, BG);
        // Сайдбар
        fill(matrixStack, x, y, x + sidebarWidth, y + height, SIDE);
        fill(matrixStack, x + sidebarWidth, y, x + sidebarWidth + 2, y + height, ACCENT);

        // Лого (ИСПРАВЛЕНО: убран matrixStack из drawStringWithShadow)
        fill(matrixStack, x + 15, y + 15, x + 35, y + 35, ACCENT);
        font.drawStringWithShadow("ForgeClient", x + 45, y + 23, TEXT);

        // Категории
        int catY = y + 60;
        for (String cat : categories) {
            boolean active = category.equals(cat);
            if (active) fill(matrixStack, x + 10, catY - 4, x + sidebarWidth - 10, catY + 15, new Color(155, 89, 255, 60).getRGB());
            font.drawStringWithShadow(cat, x + 20, catY, active ? TEXT : TEXT_DIS);
            catY += 25;
        }

        // Модули
        int modX = x + sidebarWidth + 20;
        int modY = y + 20;
        int col = 0;

        for (Module mod : ModuleManager.modules) {
            if (!mod.category.equals(category)) continue;

            int mX = modX + (col * 160);
            int mY = modY;

            fill(matrixStack, mX, mY, mX + 150, mY + 35, mod.enabled ? new Color(40, 20, 60).getRGB() : MOD_OFF);
            drawBorder(matrixStack, mX, mY, 150, 35, mod.enabled ? ACCENT : new Color(50, 50, 60).getRGB());
            
            // ИСПРАВЛЕНО: убран matrixStack
            font.drawStringWithShadow(mod.name, mX + 10, mY + 14, mod.enabled ? TEXT : TEXT_DIS);

            // Кружок (Тумблер)
            fill(matrixStack, mX + 125, mY + 15, mX + 140, mY + 20, mod.enabled ? ACCENT : new Color(60, 60, 70).getRGB());

            col++;
            if (col >= 2) { col = 0; modY += 45; }
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int catY = y + 60;
        for (String cat : categories) {
            if (mouseX >= x + 10 && mouseX <= x + sidebarWidth - 10 && mouseY >= catY - 4 && mouseY <= catY + 15) {
                category = cat;
                return true;
            }
            catY += 25;
        }

        int modX = x + sidebarWidth + 20;
        int modY = y + 20;
        int col = 0;

        for (Module mod : ModuleManager.modules) {
            if (!mod.category.equals(category)) continue;
            int mX = modX + (col * 160);
            int mY = modY;
            
            if (mouseX >= mX && mouseX <= mX + 150 && mouseY >= mY && mouseY <= mY + 35) {
                mod.toggle();
                return true;
            }
            col++;
            if (col >= 2) { col = 0; modY += 45; }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.minecraft.setScreen(null); // ИСПРАВЛЕНО: setScreen вместо displayGuiScreen
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void drawBorder(MatrixStack m, int x, int y, int w, int h, int color) {
        fill(m, x, y, x + w, y + 1, color);
        fill(m, x, y + h - 1, x + w, y + h, color);
        fill(m, x, y, x + 1, y + h, color);
        fill(m, x + w - 1, y, x + w, y + h, color);
    }
}
