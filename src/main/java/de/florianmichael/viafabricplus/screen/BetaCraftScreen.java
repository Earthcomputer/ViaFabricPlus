/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.florianmichael.viafabricplus.screen;

import com.github.allinkdev.betacraftserverlistparser.BetacraftServerList;
import com.github.allinkdev.betacraftserverlistparser.Server;
import com.github.allinkdev.betacraftserverlistparser.Version;
import de.florianmichael.viafabricplus.definition.v1_14_4.LegacyServerAddress;
import de.florianmichael.viafabricplus.screen.base.MappedSlotEntry;
import de.florianmichael.viafabricplus.screen.settings.settingrenderer.meta.TitleRenderer;
import de.florianmichael.viafabricplus.util.ScreenUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.List;

public class BetaCraftScreen extends Screen {
    public static BetacraftServerList SERVER_LIST;
    public final static BetaCraftScreen INSTANCE = new BetaCraftScreen();
    public Screen prevScreen;

    protected BetaCraftScreen() {
        super(Text.literal("BetaCraft"));
    }

    public static BetaCraftScreen get(final Screen prevScreen) {
        BetaCraftScreen.INSTANCE.prevScreen = prevScreen;
        return BetaCraftScreen.INSTANCE;
    }

    @Override
    protected void init() {
        super.init();

        this.addDrawableChild(new SlotList(this.client, width, height, 3 + 3 /* start offset */ + (textRenderer.fontHeight + 2) * 3 /* title is 2 */, height + 5, (textRenderer.fontHeight + 2) * 3));
        this.addDrawableChild(ButtonWidget.builder(Text.literal("<-"), button -> this.close()).position(5, 5).size(20, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("words.viafabricplus.reset"), button -> {
            client.setScreen(prevScreen);
            SERVER_LIST = null;
        }).position(width - 98 - 5, 5).size(98, 20).build());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);

        matrices.push();
        matrices.scale(2F, 2F, 2F);
        drawCenteredTextWithShadow(matrices, textRenderer, "ViaFabricPlus", width / 4, 3, Color.ORANGE.getRGB());
        matrices.pop();
        drawCenteredTextWithShadow(matrices, textRenderer, "https://github.com/FlorianMichael/ViaFabricPlus", width / 2, (textRenderer.fontHeight + 2) * 2 + 3, -1);
    }

    @Override
    public void close() {
        client.setScreen(prevScreen);
    }

    public static class SlotList extends AlwaysSelectedEntryListWidget<MappedSlotEntry> {

        public SlotList(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
            super(minecraftClient, width, height, top, bottom, entryHeight);

            for (Version value : Version.values()) {
                final List<Server> servers = SERVER_LIST.getServersOfVersion(value);
                if (servers.isEmpty()) continue;
                addEntry(new TitleRenderer(value.name()));
                for (Server server : servers) {
                    addEntry(new ServerSlot(server));
                }
            }
        }

        @Override
        public int getRowWidth() {
            return super.getRowWidth() + 140;
        }

        @Override
        protected int getScrollbarPositionX() {
            return this.width - 5;
        }
    }

    public static class ServerSlot extends MappedSlotEntry {
        private final Server server;

        public ServerSlot(Server server) {
            this.server = server;
        }

        @Override
        public Text getNarration() {
            return Text.literal(server.getName());
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            final ServerAddress serverAddress = LegacyServerAddress.parse(null, server.getHost() + ":" + server.getPort());
            final ServerInfo entry = new ServerInfo(server.getName(), serverAddress.getAddress(), false);

            ConnectScreen.connect(MinecraftClient.getInstance().currentScreen, MinecraftClient.getInstance(), serverAddress, entry);
            ScreenUtil.playClickSound();
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void mappedRenderer(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            drawCenteredTextWithShadow(matrices, textRenderer, server.getName() + Formatting.DARK_GRAY + " [" + server.getGameVersion() + "]", entryWidth / 2, entryHeight / 2 - textRenderer.fontHeight / 2, -1);

            if (server.isOnlineMode()) {
                drawTextWithShadow(matrices, textRenderer, Text.translatable("words.viafabricplus.online").formatted(Formatting.GREEN), 1, 1, -1);
            }
            final String playerText = server.getPlayerCount() + "/" + server.getPlayerLimit();
            drawTextWithShadow(matrices, textRenderer, playerText, entryWidth - textRenderer.getWidth(playerText) - 4 /* magic value from line 132 */ - 1, 1, -1);
        }
    }
}
