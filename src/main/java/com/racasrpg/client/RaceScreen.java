package com.racasrpg.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.racasrpg.ability.Abilities;
import com.racasrpg.classes.ClassDef;
import com.racasrpg.net.ChooseClassPayload;
import com.racasrpg.net.ChooseRacePayload;
import com.racasrpg.net.EvolvePayload;
import com.racasrpg.race.Race;
import com.racasrpg.race.Ranks;
import com.racasrpg.race.RaceData;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Menu de raça com visual de RPG.
 * - Sem raça: lista de raças à esquerda e, à direita, abas com História, Ficha, Evolução e Habilidades.
 * - Com raça: retrato, missão e botão Evoluir à esquerda, e as mesmas abas à direita.
 */
public class RaceScreen extends Screen {
    private enum Tab {
        LORE("Lenda"), STATS("Ficha"), PATH("Evol."), SKILLS("Poder"), CLASS("Classe"), RANK("Posto");

        private final String label;

        Tab(String label) {
            this.label = label;
        }
    }

    private static final int LEFT_X = 24;
    private static final int LEFT_W = 146;
    private static final int PANEL_X = 190;

    private static final int WHITE = 0xFFFFFFFF;
    private static final int GRAY = 0xFFAAAAAA;
    private static final int DARK_GRAY = 0xFF666666;
    private static final int GREEN = 0xFF55FF55;
    private static final int RED = 0xFFFF5555;
    private static final int YELLOW = 0xFFFFFF55;
    private static final int GOLD = 0xFFFFAA00;
    private static final int AQUA = 0xFF55FFFF;
    private static final int PARCHMENT = 0xFFE8D9B0;
    private static final int BORDER = 0xFFB8860B;

    private final RaceData data;
    private final int honor;
    private Race selected;
    private Tab tab;
    private Button confirmButton;
    private Button classButton;
    private ClassDef selectedClass = ClassDef.values()[0];
    private int listSpacing = 20;

    public RaceScreen(RaceData data, int honor) {
        super(Component.literal("Raças"));
        this.data = data;
        this.honor = honor;
        Race current = data.hasRace() ? Race.byId(data.race()) : null;
        this.selected = current != null ? current : Race.values()[0];
        ClassDef current2 = ClassDef.byId(data.clazz());
        if (current2 != null) {
            this.selectedClass = current2;
        }
        this.tab = !data.hasRace() ? Tab.LORE : (data.hasClass() ? Tab.STATS : Tab.CLASS);
    }

    /** Estágio que está sendo mostrado: o do jogador, ou o inicial na tela de escolha. */
    private int shownStage() {
        return data.hasRace() ? data.stage() : 0;
    }

    // ---------------------------------------------------------------------------------------------
    // Widgets
    // ---------------------------------------------------------------------------------------------

    @Override
    protected void init() {
        if (!data.hasRace()) {
            initSelection();
        } else {
            initStatus();
        }
        initTabs();
    }

    private void initTabs() {
        int gap = 4;
        int total = this.width - PANEL_X - 20;
        int w = (total - gap * (Tab.values().length - 1)) / Tab.values().length;
        int x = PANEL_X + 8;
        for (Tab t : Tab.values()) {
            final Tab target = t;
            Button button = Button.builder(Component.literal(t.label), b -> {
                tab = target;
                this.rebuildWidgets();
            }).bounds(x, 36, w, 18).build();
            button.active = tab != t;
            this.addRenderableWidget(button);
            x += w + gap;
        }
        if (tab == Tab.CLASS && data.hasRace() && !data.hasClass()) {
            initClassList();
        }
    }

    private void initClassList() {
        ClassDef[] classes = ClassDef.values();
        int listW = 84;
        int x = PANEL_X + 14;
        for (int i = 0; i < classes.length; i++) {
            final ClassDef clazz = classes[i];
            this.addRenderableWidget(Button.builder(Component.literal(clazz.displayName()), b -> {
                selectedClass = clazz;
                this.rebuildWidgets();
            }).bounds(x, 62 + i * 19, listW, 17).build());
        }
        classButton = this.addRenderableWidget(Button.builder(Component.literal("Escolher " + selectedClass.displayName()),
                b -> PacketDistributor.sendToServer(new ChooseClassPayload(selectedClass.id())))
                .bounds(x, this.height - 36, this.width - PANEL_X - 40, 20).build());
    }

    private void initSelection() {
        Race[] races = Race.values();
        int rows = (races.length + 1) / 2;
        listSpacing = Math.max(18, Math.min(24, (this.height - 110) / rows));
        int colW = (LEFT_W - 4) / 2;

        for (int i = 0; i < races.length; i++) {
            final Race race = races[i];
            int col = i % 2;
            int row = i / 2;
            this.addRenderableWidget(Button.builder(Component.literal(race.displayName()), button -> {
                selected = race;
                updateConfirm();
            }).bounds(LEFT_X + col * (colW + 4), 40 + row * listSpacing, colW, listSpacing - 3).build());
        }

        confirmButton = this.addRenderableWidget(Button.builder(Component.literal("Escolher"),
                button -> PacketDistributor.sendToServer(new ChooseRacePayload(selected.id())))
                .bounds(LEFT_X, this.height - 36, LEFT_W, 20).build());
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
                .bounds(LEFT_X, this.height - 36, LEFT_W, 20).build());
    }

    // ---------------------------------------------------------------------------------------------
    // Desenho
    // ---------------------------------------------------------------------------------------------

    private void frame(GuiGraphics g, int x, int y, int w, int h) {
        g.fill(x, y, x + w, y + h, 0xCC0E0E16);
        g.renderOutline(x, y, w, h, BORDER);
        g.renderOutline(x + 2, y + 2, w - 4, h - 4, 0xFF4A3410);
    }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.fillGradient(0, 0, this.width, this.height, 0xF0141020, 0xF0050508);
        frame(g, LEFT_X - 8, 28, LEFT_W + 16, this.height - 38);
        frame(g, PANEL_X, 28, this.width - PANEL_X - 12, this.height - 38);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);

        String title = data.hasRace() ? "Ficha do Personagem" : "Escolha sua Raça";
        g.drawCenteredString(this.font, Component.literal(title), this.width / 2, 12, GOLD);

        if (!data.hasRace()) {
            drawRaceList(g);
        } else {
            drawPortrait(g);
        }

        int x = PANEL_X + 14;
        int y = 62;
        int maxW = this.width - PANEL_X - 40;
        int limit = this.height - 22;

        drawActiveTabMark(g);

        switch (tab) {
            case LORE -> drawLore(g, x, y, maxW, limit);
            case STATS -> drawStats(g, x, y, maxW, limit);
            case PATH -> drawPath(g, x, y, maxW, limit);
            case SKILLS -> drawSkills(g, x, y, maxW, limit);
            case CLASS -> drawClass(g, x, y, maxW, limit);
            case RANK -> drawRank(g, x, y, maxW, limit);
        }
    }

    private void drawActiveTabMark(GuiGraphics g) {
        int gap = 4;
        int total = this.width - PANEL_X - 20;
        int w = (total - gap * (Tab.values().length - 1)) / Tab.values().length;
        int x = PANEL_X + 8 + tab.ordinal() * (w + gap);
        g.fill(x, 55, x + w, 57, GOLD);
    }

    private void drawRaceList(GuiGraphics g) {
        Race[] races = Race.values();
        int colW = (LEFT_W - 4) / 2;
        for (int i = 0; i < races.length; i++) {
            if (races[i] != selected) {
                continue;
            }
            int col = i % 2;
            int row = i / 2;
            int x = LEFT_X + col * (colW + 4);
            int y = 40 + row * listSpacing;
            g.renderOutline(x - 1, y - 1, colW + 2, listSpacing - 1, GOLD);
        }
        g.drawCenteredString(this.font, Component.literal(selected.displayName()), LEFT_X + LEFT_W / 2,
                40 + ((races.length + 1) / 2) * listSpacing + 4, GOLD);
    }

    /** Coluna da esquerda na tela de status: retrato, nome, estágio e missão. */
    private void drawPortrait(GuiGraphics g) {
        Race race = Race.byId(data.race());
        if (race == null) {
            return;
        }
        Race.Stage stage = race.stage(data.stage());

        int boxX = LEFT_X + (LEFT_W - 64) / 2;
        g.fill(boxX - 2, 38, boxX + 66, 106, 0xFF1A1A28);
        g.renderOutline(boxX - 2, 38, 68, 68, BORDER);
        g.pose().pushPose();
        g.pose().translate(boxX + 8, 46, 0);
        g.pose().scale(3.0F, 3.0F, 1.0F);
        g.renderItem(new ItemStack(race.icon()), 0, 0);
        g.pose().popPose();

        g.drawCenteredString(this.font, Component.literal(race.displayName()), LEFT_X + LEFT_W / 2, 114, GOLD);
        g.drawCenteredString(this.font, Component.literal(stage.name()), LEFT_X + LEFT_W / 2, 126, YELLOW);
        g.drawCenteredString(this.font, Component.literal("Estágio " + (data.stage() + 1) + "/" + race.stages().size()),
                LEFT_X + LEFT_W / 2, 138, GRAY);

        Race.Mission mission = stage.mission();
        int y = 152;
        if (mission == null) {
            g.drawCenteredString(this.font, Component.literal("Forma máxima!"), LEFT_X + LEFT_W / 2, y, GOLD);
            return;
        }
        g.drawString(this.font, "Missão:", LEFT_X, y, AQUA);
        y += 11;
        for (FormattedCharSequence seq : this.font.split(Component.literal(race.describe(mission)), LEFT_W)) {
            if (y > this.height - 90) {
                break;
            }
            g.drawString(this.font, seq, LEFT_X, y, WHITE);
            y += 10;
        }
        y += 2;
        int progress = Math.min(data.progress(), mission.target());
        float ratio = mission.target() == 0 ? 1.0F : (float) progress / mission.target();
        if (y < this.height - 78) {
            g.fill(LEFT_X, y, LEFT_X + LEFT_W, y + 10, 0xFF333333);
            g.fill(LEFT_X, y, LEFT_X + (int) (LEFT_W * ratio), y + 10, 0xFF55FF55);
            g.renderOutline(LEFT_X, y, LEFT_W, 10, BORDER);
            g.drawCenteredString(this.font, Component.literal(progress + " / " + mission.target()),
                    LEFT_X + LEFT_W / 2, y + 1, WHITE);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Abas
    // ---------------------------------------------------------------------------------------------

    private void drawLore(GuiGraphics g, int x, int y, int maxW, int limit) {
        Race race = selected;
        g.fill(x, y, x + 36, y + 36, 0xFF1A1A28);
        g.renderOutline(x, y, 36, 36, BORDER);
        g.pose().pushPose();
        g.pose().translate(x + 2, y + 2, 0);
        g.pose().scale(2.0F, 2.0F, 1.0F);
        g.renderItem(new ItemStack(race.icon()), 0, 0);
        g.pose().popPose();

        g.drawString(this.font, race.displayName(), x + 44, y + 4, GOLD);
        List<FormattedCharSequence> desc = this.font.split(Component.literal(race.description()), maxW - 44);
        int dy = y + 16;
        for (FormattedCharSequence seq : desc) {
            if (dy > y + 40) {
                break;
            }
            g.drawString(this.font, seq, x + 44, dy, YELLOW);
            dy += 10;
        }

        int ly = y + 46;
        g.drawString(this.font, "A história", x, ly, AQUA);
        ly += 12;
        wrapped(g, race.lore(), x, ly, maxW, PARCHMENT, limit);
    }

    private void drawStats(GuiGraphics g, int x, int y, int maxW, int limit) {
        Race race = selected;
        Race.Stage stage = race.stage(shownStage());
        Map<String, Double> sum = new HashMap<>();
        for (Race.Bonus b : stage.bonuses()) {
            sum.merge(b.key(), b.amount(), Double::sum);
        }

        g.drawString(this.font, race.displayName() + " - " + stage.name(), x, y, GOLD);
        y += 12;
        if (data.hasRace()) {
            int rank = Ranks.rankIndex(honor);
            g.drawString(this.font, "Patente: " + Ranks.title(race, rank) + " (honra " + honor + ")", x, y, YELLOW);
        }
        y += 12;

        double hp = 20.0 + sum.getOrDefault("max_health", 0.0);
        double atk = 1.0 + sum.getOrDefault("attack_damage", 0.0);
        double armor = sum.getOrDefault("armor", 0.0);
        double speed = 100.0 + 100.0 * sum.getOrDefault("speed", 0.0);
        double luck = sum.getOrDefault("luck", 0.0);

        y = bar(g, "Vida", hp, 40.0, (int) hp + " (" + trim(hp / 2.0) + " corações)", 0xFFE04040, x, y, maxW);
        y = bar(g, "Ataque", atk, 10.0, trim(atk), 0xFFE0A030, x, y, maxW);
        y = bar(g, "Armadura", armor, 20.0, trim(armor), 0xFF5080E0, x, y, maxW);
        y = bar(g, "Velocidade", speed, 150.0, (int) speed + "%", 0xFF40D0A0, x, y, maxW);
        y = bar(g, "Sorte", luck, 5.0, trim(luck), 0xFF90E050, x, y, maxW);
        y += 4;

        g.drawString(this.font, "Efeitos especiais", x, y, AQUA);
        y += 12;
        boolean any = false;
        for (Race.Bonus b : stage.bonuses()) {
            String key = b.key();
            if (key.equals("max_health") || key.equals("attack_damage") || key.equals("armor")
                    || key.equals("speed") || key.equals("luck")) {
                continue;
            }
            any = true;
            if (y > limit) {
                break;
            }
            g.drawString(this.font, b.text(), x + 4, y, b.amount() >= 0 ? GREEN : RED);
            y += 10;
        }
        if (!any) {
            g.drawString(this.font, "Nenhum neste estágio.", x + 4, y, GRAY);
        }
    }

    private int bar(GuiGraphics g, String label, double value, double max, String text, int color,
                    int x, int y, int maxW) {
        int labelW = 70;
        int barX = x + labelW;
        int barW = Math.max(40, maxW - labelW - 4);
        g.drawString(this.font, label, x, y + 1, WHITE);
        g.fill(barX, y, barX + barW, y + 10, 0xFF222230);
        double ratio = Math.max(0.0, Math.min(1.0, value / max));
        g.fill(barX, y, barX + (int) (barW * ratio), y + 10, color);
        g.renderOutline(barX, y, barW, 10, DARK_GRAY);
        g.drawCenteredString(this.font, Component.literal(text), barX + barW / 2, y + 1, WHITE);
        return y + 15;
    }

    private void drawPath(GuiGraphics g, int x, int y, int maxW, int limit) {
        Race race = selected;
        int current = shownStage();
        for (int i = 0; i < race.stages().size(); i++) {
            Race.Stage stage = race.stage(i);
            boolean isCurrent = data.hasRace() && i == current;
            boolean done = data.hasRace() && i < current;
            int color = isCurrent ? GOLD : (done ? GREEN : WHITE);

            int top = y;
            g.drawString(this.font, (i + 1) + ". " + stage.name() + (isCurrent ? "  (atual)" : ""), x, y, color);
            y += 12;
            if (i > 0) {
                Race.Mission req = race.stage(i - 1).mission();
                if (req != null) {
                    y = wrapped(g, "Requisito: " + race.describeFull(req), x + 8, y, maxW - 8, GRAY, limit);
                }
            }
            StringBuilder sb = new StringBuilder();
            for (int b = 0; b < stage.bonuses().size(); b++) {
                if (b > 0) sb.append(", ");
                sb.append(stage.bonuses().get(b).text());
            }
            y = wrapped(g, sb.length() == 0 ? "Sem bônus." : sb.toString(), x + 8, y, maxW - 8, 0xFFCCCCCC, limit);
            g.fill(x, y, x + maxW, y + 1, 0xFF3A2A10);
            y += 5;
            if (y > limit) {
                break;
            }
            if (isCurrent) {
                g.renderOutline(x - 4, top - 3, maxW + 8, y - top - 1, GOLD);
            }
        }
    }

    private void drawSkills(GuiGraphics g, int x, int y, int maxW, int limit) {
        Race race = selected;
        int current = shownStage();
        String[] keys = {"R", "G", "V"};
        g.drawString(this.font, "Movimento especial", x, y, AQUA);
        y += 11;
        y = wrapped(g, race.movement(), x + 4, y, maxW - 4, YELLOW, limit);
        y += 2;
        for (int slot = 0; slot < 3; slot++) {
            Abilities.Ability ability = Abilities.get(race, slot);
            boolean unlocked = slot <= current;
            int cardH = 40;
            g.fill(x, y, x + maxW, y + cardH, unlocked ? 0xFF1A1A28 : 0xFF14141A);
            g.renderOutline(x, y, maxW, cardH, unlocked ? BORDER : DARK_GRAY);

            g.fill(x + 6, y + 6, x + 30, y + 30, 0xFF2A2A3A);
            g.renderOutline(x + 6, y + 6, 24, 24, unlocked ? GOLD : DARK_GRAY);
            g.drawCenteredString(this.font, Component.literal(keys[slot]), x + 18, y + 14, unlocked ? WHITE : DARK_GRAY);

            g.drawString(this.font, ability.name(), x + 38, y + 5, unlocked ? GOLD : DARK_GRAY);
            int dy = y + 17;
            for (FormattedCharSequence seq : this.font.split(Component.literal(ability.description()), maxW - 46)) {
                if (dy > y + cardH - 12) {
                    break;
                }
                g.drawString(this.font, seq, x + 38, dy, unlocked ? WHITE : DARK_GRAY);
                dy += 10;
            }
            String foot = unlocked ? "Recarga: " + ability.cooldownSeconds() + "s"
                    : "Libera no estágio " + (slot + 1);
            g.drawString(this.font, foot, x + 38, y + cardH - 11, unlocked ? AQUA : RED);

            y += cardH + 4;
            if (y > limit) {
                break;
            }
        }
    }


    private void drawRank(GuiGraphics g, int x, int y, int maxW, int limit) {
        Race race = selected;
        int current = data.hasRace() ? Ranks.rankIndex(honor) : -1;

        g.drawString(this.font, "Hierarquia dos " + race.displayName(), x, y, GOLD);
        y += 14;
        if (data.hasRace()) {
            int next = Ranks.nextThreshold(current);
            double ratio = next < 0 ? 1.0 : (honor - Ranks.THRESHOLDS[current])
                    / (double) (next - Ranks.THRESHOLDS[current]);
            String text = next < 0 ? honor + " de honra (máximo)" : honor + " / " + next + " de honra";
            y = bar(g, "Honra", ratio, 1.0, text, 0xFFD0A020, x, y, maxW);
            y += 2;
        } else {
            y = wrapped(g, "Ganhe honra lutando, derrotando chefes e cumprindo missões para subir de patente.",
                    x, y, maxW, GRAY, limit);
        }

        for (int i = 0; i < Ranks.THRESHOLDS.length; i++) {
            if (y > limit - 12) {
                break;
            }
            boolean isCurrent = i == current;
            boolean done = i < current;
            int color = isCurrent ? GOLD : (done ? GREEN : WHITE);
            g.drawString(this.font, (i + 1) + ". " + Ranks.title(race, i) + (isCurrent ? "  (atual)" : ""), x, y, color);
            y += 10;
            String perk = i == 0 ? "Honra 0 - sem bônus"
                    : "Honra " + Ranks.THRESHOLDS[i] + " - +" + (2 * i) + " de vida, +" + trim(0.5 * i) + " de dano";
            g.drawString(this.font, perk, x + 10, y, done || isCurrent ? 0xFFCCCCCC : DARK_GRAY);
            y += 13;
        }
    }

    private void drawClass(GuiGraphics g, int x, int y, int maxW, int limit) {
        ClassDef clazz = data.hasClass() ? ClassDef.byId(data.clazz()) : selectedClass;
        if (clazz == null) {
            clazz = selectedClass;
        }
        int listW = 84;
        if (!data.hasRace()) {
            wrapped(g, "Escolha uma raça primeiro. A classe é escolhida depois, na ficha do personagem.",
                    x, y, maxW, GRAY, limit);
            return;
        }
        int dx = data.hasClass() ? x : x + listW + 8;
        int dw = data.hasClass() ? maxW : maxW - listW - 8;

        g.fill(dx, y, dx + 28, y + 28, 0xFF1A1A28);
        g.renderOutline(dx, y, 28, 28, BORDER);
        g.pose().pushPose();
        g.pose().translate(dx + 6, y + 6, 0);
        g.renderItem(new ItemStack(clazz.icon()), 0, 0);
        g.pose().popPose();
        g.drawString(this.font, clazz.displayName(), dx + 34, y + 4, GOLD);
        if (data.hasClass()) {
            g.drawString(this.font, "Sua classe", dx + 34, y + 16, GRAY);
        }
        int dy = y + 32;
        dy = wrapped(g, clazz.description(), dx, dy, dw, YELLOW, limit);
        for (Race.Bonus b : clazz.bonuses()) {
            if (dy > limit) {
                break;
            }
            g.drawString(this.font, b.text(), dx + 2, dy, b.amount() >= 0 ? GREEN : RED);
            dy += 10;
        }
        dy += 4;
        String[] keys = {"Z", "X", "C"};
        int current = shownStage();
        for (int slot = 0; slot < 3; slot++) {
            if (dy > limit - 12) {
                break;
            }
            Abilities.Ability ability = clazz.ability(slot);
            boolean unlocked = data.hasClass() && slot <= current;
            g.drawString(this.font, "[" + keys[slot] + "] " + ability.name(), dx, dy,
                    unlocked ? GOLD : (data.hasClass() ? DARK_GRAY : WHITE));
            dy += 10;
            dy = wrapped(g, ability.description() + (data.hasClass() && !unlocked ? " (estágio " + (slot + 1) + ")" : ""),
                    dx + 6, dy, dw - 6, unlocked || !data.hasClass() ? GRAY : DARK_GRAY, limit);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Auxiliares
    // ---------------------------------------------------------------------------------------------

    private String trim(double v) {
        return v == Math.floor(v) ? String.valueOf((long) v) : String.format(java.util.Locale.ROOT, "%.1f", v);
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
