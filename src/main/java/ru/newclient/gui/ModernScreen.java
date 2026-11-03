package ru.newclient.gui;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ModernScreen extends Screen {

    // Container dimensions
    private int x, y;
    private final int width = 400;
    private final int height = 250;

    // Color palette (Modern Dark Theme)
    private static final int COLOR_OVERLAY = 0xB4000000;      // Semi-transparent black
    private static final int COLOR_BG = 0xFF18181B;           // Main container bg
    private static final int COLOR_HEADER = 0xFF27272A;       // Header background
    private static final int COLOR_ACCENT = 0xFF8A2BE2;       // Purple accent
    private static final int COLOR_TEXT = 0xFFFFFFFF;         // White text
    private static final int COLOR_TEXT_DESC = 0xFFA1A1AA;    // Gray description text

    // Custom Button storage for tooltips
    private final List<ModernButton> modernButtons = new ArrayList<>();

    public ModernScreen() {
        super(new LiteralText("Modern Screen"));
    }

    @Override
    protected void init() {
        // Calculate centered coordinates
        this.x = (this.width - width) / 2;
        this.y = (this.height - height) / 2;

        this.modernButtons.clear();

        // Add custom buttons
        addModernButton(new ModernButton(
                x + 20, y + 60, 360, 30,
                new LiteralText("Profile Settings"),
                "Change your visual preferences and layout",
                button -> {
                    // Action to execute when clicked
                    System.out.println("Profile Settings Clicked!");
                }
        ));

        addModernButton(new ModernButton(
                x + 20, y + 100, 360, 30,
                new LiteralText("Account Manager"),
                "Manage your alternative accounts",
                button -> {
                    System.out.println("Account Manager Clicked!");
                }
        ));

        addModernButton(new ModernButton(
                x + 20, y + 190, 170, 25,
                new LiteralText("Save Config"),
                "Save current changes to disk",
                button -> {
                    System.out.println("Save Clicked!");
                }
        ));

        addModernButton(new ModernButton(
                x + 210, y + 190, 170, 25,
                new LiteralText("Close"),
                "Return to the game",
                button -> this.client.setScreen(null) // Closes the screen
        ));
    }

    // Helper method to add and register buttons
    private void addModernButton(ModernButton button) {
        this.modernButtons.add(button);
        this.addButton(button);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        // 1. Draw Semi-transparent background overlay
        this.renderBackground(matrices);

        // 2. Draw Main Container Shadow
        DrawableHelper.fill(matrices, x - 2, y - 2, x + width + 2, y + height + 2, 0x50000000);
        
        // 3. Draw Main Container Background
        DrawableHelper.fill(matrices, x, y, x + width, y + height, COLOR_BG);
        
        // 4. Draw Header Area
        DrawableHelper.fill(matrices, x, y, x + width, y + 40, COLOR_HEADER);
        
        // 5. Draw Accent Border (Top and Bottom)
        DrawableHelper.fillGradient(matrices, x, y, x + width, y + 2, COLOR_ACCENT, 0xFF3B82F6);
        DrawableHelper.fill(matrices, x, y + 40, x + width, y + 41, 0xFF3F3F46);

        // 6. Draw Title and Subtitle
        this.textRenderer.drawWithShadow(matrices, "TusiClient", x + 20, y + 12, COLOR_ACCENT);
        this.textRenderer.drawWithShadow(matrices, "v1.0.0", x + 20, y + 24, COLOR_TEXT_DESC);

        // 7. Draw Buttons (super.render calls the render method of all registered buttons)
        super.render(matrices, mouseX, mouseY, delta);

        // 8. Render Tooltips (Must be drawn last so they are on top of everything)
        for (ModernButton btn : modernButtons) {
            if (btn.isHovered() && btn.tooltip != null) {
                // Draw a dark background for the tooltip
                int tooltipWidth = this.textRenderer.getWidth(btn.tooltip) + 6;
                DrawableHelper.fill(matrices, mouseX + 10, mouseY - 12, mouseX + 10 + tooltipWidth, mouseY - 2, 0xF0000000);
                // Draw the tooltip text
                this.textRenderer.drawWithShadow(matrices, btn.tooltip, mouseX + 13, mouseY - 10, 0xFFDDDDDD);
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Close GUI with ESC
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.client.setScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Game keeps running in background
    }

    /**
     * Custom Button Widget with modern styling and hover effects.
     */
    private static class ModernButton extends ButtonWidget {
        private final String tooltip;

        public ModernButton(int x, int y, int width, int height, Text message, String tooltip, PressAction onPress) {
            super(x, y, width, height, message, onPress);
            this.tooltip = tooltip;
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            boolean hovered = this.isHovered();
            
            // Determine colors based on hover state
            int bgColor = hovered ? 0xFF2A2A32 : 0xFF1E1E24;
            int borderColor = hovered ? COLOR_ACCENT : 0xFF3F3F46;
            int textColor = hovered ? COLOR_ACCENT : COLOR_TEXT;

            // Draw Button Background
            DrawableHelper.fill(matrices, this.x, this.y, this.x + this.width, this.y + this.height, bgColor);
            
            // Draw Button Border (1px)
            DrawableHelper.fill(matrices, this.x, this.y, this.x + this.width, this.y + 1, borderColor); // Top
            DrawableHelper.fill(matrices, this.x, this.y + this.height - 1, this.x + this.width, this.y + this.height, borderColor); // Bottom
            DrawableHelper.fill(matrices, this.x, this.y, this.x + 1, this.y + this.height, borderColor); // Left
            DrawableHelper.fill(matrices, this.x + this.width - 1, this.y, this.x + this.width, this.y + this.height, borderColor); // Right

            // Draw Left Accent Line on Hover
            if (hovered) {
                DrawableHelper.fill(matrices, this.x, this.y, this.x + 3, this.y + this.height, COLOR_ACCENT);
            }

            // Draw Text Centered
            String text = this.getMessage().getString();
            int textWidth = MinecraftClient.getInstance().textRenderer.getWidth(text);
            // Vertically center the text (8 is standard font height)
            int textY = this.y + (this.height - 8) / 2;
            
            MinecraftClient.getInstance().textRenderer.drawWithShadow(
                    matrices, 
                    text, 
                    this.x + (this.width / 2f) - (textWidth / 2f), 
                    textY, 
                    textColor
            );
        }
    }
                     }
