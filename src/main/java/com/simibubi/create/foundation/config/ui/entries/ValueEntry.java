package com.simibubi.create.foundation.config.ui.entries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.config.ui.ConfigAnnotations;
import com.simibubi.create.foundation.config.ui.ConfigHelper;
import com.simibubi.create.foundation.config.ui.ConfigScreen;
import com.simibubi.create.foundation.config.ui.ConfigScreenList;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.element.DelegatedStencilElement;
import com.simibubi.create.foundation.gui.widget.BoxWidget;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Pair;
import io.github.fabricators_of_create.porting_lib.mixin.client.accessor.AbstractSelectionList$EntryAccessor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

public class ValueEntry<T> extends ConfigScreenList.LabeledEntry {

	protected static final int resetWidth = 28;//including 6px offset on either side

	protected ForgeConfigSpec.ConfigValue<T> value;
	protected ForgeConfigSpec.ValueSpec spec;
	protected BoxWidget resetButton;
	protected boolean editable = true;

	public ValueEntry(String label, ForgeConfigSpec.ConfigValue<T> value, ForgeConfigSpec.ValueSpec spec) {
		super(label);
		this.value = value;
		this.spec = spec;
		this.path = String.join(".", value.getPath());

		resetButton = new BoxWidget(0, 0, resetWidth - 12, 16)
				.showingElement(AllIcons.I_CONFIG_RESET.asStencil())
				.withCallback(() -> {
					setValue((T) spec.getDefault());
					this.onReset();
				});
		resetButton.modifyElement(e -> ((DelegatedStencilElement) e).withElementRenderer(BoxWidget.gradientFactory.apply(resetButton)));

		listeners.add(resetButton);

		List<String> path = value.getPath();
		labelTooltip.add(Components.literal(label).withStyle(ChatFormatting.WHITE));
		String comment = spec.getComment();
		if (comment == null || comment.isEmpty())
			return;

		List<String> commentLines = new ArrayList<>(Arrays.asList(comment.split("\n")));


		Pair<String, Map<String, String>> metadata = ConfigHelper.readMetadataFromComment(commentLines);
		if (metadata.getFirst() != null) {
			unit = metadata.getFirst();
		}
		if (metadata.getSecond() != null && !metadata.getSecond().isEmpty()) {
			annotations.putAll(metadata.getSecond());
		}
		// add comment to tooltip
		labelTooltip.addAll(commentLines.stream()
				.filter(Predicates.not(s -> s.startsWith("Range")))
				.map(Components::literal)
				.flatMap(stc -> TooltipHelper.cutTextComponent(stc, ChatFormatting.GRAY, ChatFormatting.GRAY)
						.stream())
				.collect(Collectors.toList()));

		if (annotations.containsKey(ConfigAnnotations.RequiresRelog.TRUE.getName()))
			labelTooltip.addAll(TooltipHelper.cutTextComponent(Components.literal("Changing this value will require a _relog_ to take full effect"), ChatFormatting.GRAY, ChatFormatting.GOLD));

		if (annotations.containsKey(ConfigAnnotations.RequiresRestart.CLIENT.getName()))
			labelTooltip.addAll(TooltipHelper.cutTextComponent(Components.literal("Changing this value will require a _restart_ to take full effect"), ChatFormatting.GRAY, ChatFormatting.RED));

		labelTooltip.add(Components.literal(ConfigScreen.modID + ":" + path.get(path.size() - 1)).withStyle(ChatFormatting.DARK_GRAY));
	}

	public ValueEntry(String label) {
		super(label);
	}

	@Override
	public void setEditable(boolean b) {
		editable = b;
		resetButton.active = editable && !isCurrentValueDefault();
		resetButton.animateGradientFromState();
	}

	@Override
	public void tick() {
		super.tick();
		resetButton.tick();
	}

	@Override
	public void render(PoseStack ms, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean p_230432_9_, float partialTicks) {
		super.render(ms, index, y, x, width, height, mouseX, mouseY, p_230432_9_, partialTicks);

		resetButton.x = x + width - resetWidth + 6;
		resetButton.y = y + 10;
		resetButton.render(ms, mouseX, mouseY, partialTicks);
	}

	@Override
	protected int getLabelWidth(int totalWidth) {
		return (int) (totalWidth * labelWidthMult) + 30;
	}

	public void setValue(@Nonnull T value) {
		ConfigHelper.setValue(path, this.value, value, annotations);
		onValueChange(value);
	}

	@Nonnull
	public T getValue() {
		return ConfigHelper.getValue(path, this.value);
	}

	protected boolean isCurrentValueDefault() {
		return spec.getDefault().equals(getValue());
	}

	public void onReset() {
		onValueChange(getValue());
	}

	public void onValueChange() {
		onValueChange(getValue());
	}
	public void onValueChange(T newValue) {
		resetButton.active = editable && !isCurrentValueDefault();
		resetButton.animateGradientFromState();
	}

	protected void bumpCog() {bumpCog(10f);}
	protected void bumpCog(float force) {
		AbstractSelectionList<?> list = ((AbstractSelectionList$EntryAccessor<?>) this).port_lib$getList();
		if (list != null && list instanceof ConfigScreenList)
			((ConfigScreenList) list).bumpCog(force);
	}
}
