package com.racasrpg.client;

import java.util.List;

import com.racasrpg.net.ChooseRacePayload;
import com.racasrpg.net.EvolvePayload;
import com.racasrpg.race.Race;
import com.racasrpg.race.RaceData;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Menu de raça, no estilo do Origins.
 * - Sem raça: lista de raças à esquerda, detalhes da raça selecionada à direita e botão "Escolher".
 * - Com raça: mostra estágio atual, bônus, progresso da missão, caminho de evolução e botão "Evoluir".
 */
public class RaceScreen extends Screen {
    private static final int LEFT_X = 20;
    private static final int LEFT_W = 150;
    private static final int PANEL_X = 190;

    private static final int WHITE = 0xFFFFFFFF;
    private static final int GRAY = 0xFFAAAAAA;
    private static final int GREEN = 0xFF55FF55;
    private static final int RED = 0xFFFF5555;
    private static final int YELLOW = 0xFFFFFF55;
    private static final int GOLD = 0xFFFFAA00;
    private static final int AQUA = 0xFF55FFFF;

    private final RaceData data;
    private Race selected;
    private Button confirmButton;
    private int listSpacing = 22;

    public RaceScreen(RaceData data) {
        super(Component.literal("Raças"));
        this.data = data;
        Race current = data.hasRace() ? Race.byId(data.race()) : null;
        this.selected = current != null ? current : Race.values()[0];
    }

    @Override
    protected void init() {
        if (!data.hasRace()) {
            initSelection();
        } else {
            initStatus();
        }
    }

    private void initSelection() {
        Race[] races = Race.values();
        listSpacing = Math.max(20, Math.min(26, (this.height - 110) / races.length));

        for (int i = 0; i < races.length; i++) {
            final Race race = races[i];
            this.addRenderableWidget(Button.builder(Component.literal(race.displayName()), button -> {
                selected = race;
                updateConfirm();
            }).bounds(LEFT_X + 22, 40 + i * listSpacing, LEFT_W - 22, listSpacing - 4).build());
        }

        confirmButton = this.addRenderableWidget(Button.builder(Component.literal("Escolher"),
                button -> PacketDistributor.sendToServer(new ChooseRacePayload(selected.id())))
                .bounds(LEFT_X, this.height - 34, LEFT_W, 20).build());
        updateConfirm();
    }

    private void updateConfirm() {
        if (confirmButton != null) {
            confirmButton.setMessage(Component.literal("Escolher " + selected.displayName()));
        }
    }

    private void initStatus() {
        Race race = Race.byId(data.race());
        Race.Mission mission = race == null ? null : race.stage(data.stage()).mission();

        Button evolve = this.addRenderableWidget(Button.builder(Component.literal("Evoluir"),
                button -> PacketDistributor.sendToServer(new EvolvePayload()))
                .bounds(LEFT_X, this.height - 60, LEFT_W, 20).build());
        evolve.active = mission != null && data.progress() >= mission.target();

        this.addRenderableWidget(Button.builder(Component.literal("Fechar"), button -> this.onClose())
                .bounds(LEFT_X, this.height - 34, LEFT_W, 20).build());
    }

    // ---------------------------------------------------------------------------------------------
    // Desenho
    // ---------------------------------------------------------------------------------------------

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.fill(0, 0, this.width, this.height, 0xE0101018);
        // painel da esquerda
        g.fill(LEFT_X - 6, 30, LEFT_X + LEFT_W + 6, this.height - 10, 0x90000000);
        // painel da direita
        g.fill(PANEL_X, 30, this.width - 10, this.height - 10, 0x90000000);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);

        String title = data.hasRace() ? "Sua raça" : "Escolha sua raça";
        g.drawCenteredString(this.font, Component.literal(title), this.width / 2, 12, WHITE);

        if (!data.hasRace()) {
            drawRaceList(g);
            drawOverview(g, selected);
        } else {
            drawStatus(g);
        }
    }

    private void drawRaceList(GuiGraphics g) {
        Race[] races = Race.values();
        for (int i = 0; i < races.length; i++) {
            int y = 40 + i * listSpacing;
            int iconY = y + (listSpacing - 4 - 16) / 2;
            g.renderItem(new ItemStack(races[i].icon()), LEFT_X, iconY);
            if (races[i] == selected) {
                g.fill(LEFT_X - 5, y, LEFT_X - 2, y + listSpacing - 4, GOLD);
            }
        }
    }

    /** Detalhes da raça selecionada (tela de escolha). */
    private void drawOverview(GuiGraphics g, Race race) {
        int x = PANEL_X + 12;
        int maxW = this.width - PANEL_X - 34;
        int limit = this.height - 18;
        int y = 40;

        g.renderItem(new ItemStack(race.icon()), x, y - 4);
        g.drawString(this.font, race.displayName(), x + 22, y, GOLD);
        y += 18;

        y = wrapped(g, race.description(), x, y, maxW, WHITE, limit);
        y += 4;

        Race.Stage first = race.stage(0);
        y = line(g, "Você começa com:", x, y, AQUA, limit);
        y = drawBonuses(g, first, x + 6, y, limit);
        y += 4;

        y = line(g, "Evolução:", x, y, AQUA, limit);
        for (int i = 1; i < race.stages().size(); i++) {
            Race.Stage stage = race.stage(i);
            Race.Mission requirement = race.stage(i - 1).mission();
            y = line(g, i + 1 + ". " + stage.name(), x + 6, y, YELLOW, limit);
            if (requirement != null) {
                y = wrapped(g, "Requisito: " + requirement.text(), x + 14, y, maxW - 14, GRAY, limit);
            }
            y = wrapped(g, joinBonuses(stage), x + 14, y, maxW - 14, WHITE, limit);
        }
    }

    /** Estado do jogador (tela de status). */
    private void drawStatus(GuiGraphics g) {
        Race race = Race.byId(data.race());
        if (race == null) {
            return;
        }
        Race.Stage stage = race.stage(data.stage());

        // lado esquerdo: ícone e nome
        g.renderItem(new ItemStack(race.icon()), LEFT_X, 40);
        g.drawString(this.font, race.displayName(), LEFT_X + 24, 38, GOLD);
        g.drawString(this.font, stage.name(), LEFT_X + 24, 50, YELLOW);

        int x = PANEL_X + 12;
        int maxW = this.width - PANEL_X - 34;
        int limit = this.height - 18;
        int y = 40;

        y = line(g, "Bônus atuais:", x, y, AQUA, limit);
        y = drawBonuses(g, stage, x + 6, y, limit);
        y += 6;

        Race.Mission mission = stage.mission();
        if (mission == null) {
            y = line(g, "Você alcançou a forma máxima!", x, y, GOLD, limit);
        } else {
            y = line(g, "Missão:", x, y, AQUA, limit);
            y = wrapped(g, mission.type().description(), x + 6, y, maxW - 6, WHITE, limit);

            int barW = Math.min(maxW, 220);
            int progress = Math.min(data.progress(), mission.target());
            float ratio = mission.target() == 0 ? 1.0F : (float) progress / mission.target();
            g.fill(x + 6, y, x + 6 + barW, y + 10, 0xFF333333);
            g.fill(x + 6, y, x + 6 + (int) (barW * ratio), y + 10, 0xFF55FF55);
            g.drawCenteredString(this.font, Component.literal(progress + " / " + mission.target()),
                    x + 6 + barW / 2, y + 1, WHITE);
            y += 16;

            if (data.progress() >= mission.target()) {
                y = line(g, "Missão completa! Clique em Evoluir.", x + 6, y, GOLD, limit);
            }
        }
        y += 6;

        y = line(g, "Caminho de evolução:", x, y, AQUA, limit);
        for (int i = 0; i < race.stages().size(); i++) {
            String label = (i + 1) + ". " + race.stage(i).name() + (i == data.stage() ? "  (atual)" : "");
            int color = i == data.stage() ? GOLD : (i < data.stage() ? GREEN : GRAY);
            y = line(g, label, x + 6, y, color, limit);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Auxiliares de texto
    // ---------------------------------------------------------------------------------------------

    private int drawBonuses(GuiGraphics g, Race.Stage stage, int x, int y, int limit) {
        if (stage.bonuses().isEmpty()) {
            return line(g, "(sem bônus neste estágio)", x, y, GRAY, limit);
        }
        for (Race.Bonus bonus : stage.bonuses()) {
            y = line(g, bonus.text(), x, y, bonus.amount() >= 0 ? GREEN : RED, limit);
        }
        return y;
    }

    private String joinBonuses(Race.Stage stage) {
        if (stage.bonuses().isEmpty()) {
            return "sem bônus";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stage.bonuses().size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(stage.bonuses().get(i).text());
        }
        return sb.toString();
    }

    private int line(GuiGraphics g, String text, int x, int y, int color, int limit) {
        if (y > limit) {
            return y;
        }
        g.drawString(this.font, text, x, y, color);
        return y + 11;
    }

    private int wrapped(GuiGraphics g, String text, int x, int y, int maxWidth, int color, int limit) {
        List<FormattedCharSequence> lines = this.font.split(Component.literal(text), maxWidth);
        for (FormattedCharSequence sequence : lines) {
            if (y > limit) {
                break;
            }
            g.drawString(this.font, sequence, x, y, color);
            y += 10;
        }
        return y + 2;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
