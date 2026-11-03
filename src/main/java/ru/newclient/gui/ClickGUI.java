package ru.newclient.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;
import ru.newclient.module.Module;
import ru.newclient.module.ModuleManager;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public class ClickGUI extends Screen {
    // Цвета (Премиум тема)
    private final int COLOR_BG = new Color(10, 10, 12, 245).getRGB();
    private final int COLOR_SIDE = new Color(18, 18, 22, 245).getRGB();
    private final int COLOR_ACCENT = new Color(155, 89, 255).getRGB(); // Фиолетовый неон
    private final int COLOR_TEXT = new Color(240, 240, 240).getRGB();
    private final int COLOR_TEXT_DIS = new Color(120, 120, 130).getRGB();
    private final int COLOR_MOD_OFF = new Color(25, 25, 30).getRGB();

    private int x, y;
    private final int width = 460;
    private final int height = 280;
    private final int sidebarWidth = 130;

    private String category = "Combat";
    private final List<String> categories = Arrays.asList("Combat", "Movement", "Render");

    public ClickGUI() {
        super(new LiteralText("ClickGUI"));
    }

    @Override
    protected void init() {
        this.x = (this.width - width) / 2;
        this.y = (this.height - height) / 2;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        // Затемнение фона
        fill(matrices, 0, 0, this.width, this.height, new Color(0, 0, 0, 180).getRGB());
        
        // Основное окно и тень
        fill(matrices, x + 2, y + 2, x + width + 2, y + height + 2, new Color(0, 0, 0, 100).getRGB());
        fill(matrices, x, y, x + width, y + height, COLOR_BG);
        
        // Сайдбар
        fill(matrices, x, y, x + sidebarWidth, y + height, COLOR_SIDE);
        fill(matrices, x + sidebarWidth, y, x + sidebarWidth + 2, y + height, COLOR_ACCENT);

        // Логотип
        fill(matrices, x + 15, y + 20, x + 35, y + 40, COLOR_ACCENT);
        textRenderer.drawWithShadow(matrices, "Tusi", x + 45, y + 26, COLOR_TEXT);
        textRenderer.drawWithShadow(matrices, "Client", x + 45, y + 36, COLOR_ACCENT);
        
        // Разделитель
        fill(matrices, x + 15, y + 50, x + sidebarWidth - 15, y + 51, new Color(40, 40, 50).getRGB());

        // Категории
        int catY = y + 65;
        for (String cat : categories) {
            boolean active = category.equals(cat);
            
            if (active) {
                // Плавная подсветка активной категории
                fill(matrices, x + 10, catY - 5, x + sidebarWidth - 10, catY + 15, new Color(155, 89, 255, 60).getRGB());
                fill(matrices, x + 10, catY - 5, x + 12, catY + 15, COLOR_ACCENT);
            }
            
            textRenderer.drawWithShadow(matrices, cat, x + 20, catY, active ? COLOR_TEXT : COLOR_TEXT_DIS);
            catY += 28;
        }

        // Модули
        int modX = x + sidebarWidth + 20;
        int modY = y + 20;
        int col = 0;

        for (Module mod : ModuleManager.modules) {
            if (!mod.category.equals(category)) continue;

            int mX = modX + (col * 150);
            int mY = modY;

            // Фон кнопки
            fill(matrices, mX, mY, mX + 140, mY + 40, mod.enabled ? new Color(40, 20, 60).getRGB() : COLOR_MOD_OFF);
            
            // Обводка
            drawBorder(matrices, mX, mY, 140, 40, mod.enabled ? COLOR_ACCENT : new Color(45, 45, 55).getRGB());

            // Название
            textRenderer.drawWithShadow(matrices, mod.name, mX + 10, mY + 10, mod.enabled ? COLOR_TEXT : COLOR_TEXT_DIS);

            // Кнопка вкл/выкл (Toggle)
            int togX = mX + 110;
            int togY = mY + 15;
            fill(matrices, togX, togY, togX + 20, togY + 10, mod.enabled ? COLOR_ACCENT : new Color(60, 60, 70).getRGB());
            fill(matrices, togX + (mod.enabled ? 10 : 1), togY - 2, togX + (mod.enabled ? 19 : 10), togY + 12, -1);

            col++;
            if (col >= 2) { col = 0; modY += 50; }
        }
        
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int catY = y + 65;
        for (String cat : categories) {
            if (mouseX >= x + 10 && mouseX <= x + sidebarWidth - 10 && mouseY >= catY - 5 && mouseY <= catY + 15) {
                category = cat;
                return true;
            }
            catY += 28;
        }

        int modX = x + sidebarWidth + 20;
        int modY = y + 20;
        int col = 0;

        for (Module mod : ModuleManager.modules) {
            if (!mod.category.equals(category)) continue;
            int mX = modX + (col * 150);
            int mY = modY;
            
            if (mouseX >= mX && mouseX <= mX + 140 && mouseY >= mY && mouseY <= mY + 40) {
                mod.toggle();
                return true;
            }
            col++;
            if (col >= 2) { col = 0; modY += 50; }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.client.openScreen(null);
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
