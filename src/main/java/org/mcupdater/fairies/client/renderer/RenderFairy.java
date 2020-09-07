package org.mcupdater.fairies.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.mcupdater.fairies.FairyFactions;
import org.mcupdater.fairies.client.model.ModelFairy;
import org.mcupdater.fairies.client.model.ModelFairyEyes;
import org.mcupdater.fairies.client.model.ModelFairyProps;
import org.mcupdater.fairies.client.model.ModelFairyProps2;
import org.mcupdater.fairies.entity.FairyEntity;

public class RenderFairy extends MobRenderer<FairyEntity, ModelFairy> {

	protected ModelFairy fairyModel, fairyModel4;   //Body and withered overlay
	protected ModelFairyProps fairyModel2;          //Clothes and stuff
	protected ModelFairyEyes fairyModel3;           //Eyes
	protected ModelFairyProps2 fairyModel5;         //Rogue Clothes

	public RenderFairy(EntityRendererManager renderManagerIn, ModelFairy entityModelIn, float shadowSizeIn) {
		super(renderManagerIn, entityModelIn, shadowSizeIn);
		this.addLayer(new HeldItemLayer<>(this));

		this.fairyModel = entityModelIn;
		this.fairyModel2 = new ModelFairyProps();
		this.fairyModel3 = new ModelFairyEyes();
		this.fairyModel4 = new ModelFairy(0.015625F);
		this.fairyModel5 = new ModelFairyProps2();
	}

	public RenderFairy(EntityRendererManager renderManagerIn) {
		this(renderManagerIn, new ModelFairy(), 0.5f);
	}

	@Override
	public ResourceLocation getEntityTexture(FairyEntity entity) {
		final String texturePath;
		/*
		if (entity.getCustomName().equals("Steve")) {
			texturePath = "textures/entities/notFairy.png";
		} else {
		 */
			texturePath = "textures/entities/fairy"
					+ ( entity.isQueen() ? "q" : "" )
					+ ( entity.skinVariant()+1 )
					+ ".png";
		// }

		return new ResourceLocation(FairyFactions.MOD_ID, texturePath);
	}

	@Override
	protected void preRenderCallback(FairyEntity fairy, MatrixStack matrixStackIn, float partialTickTime) {
		super.preRenderCallback(fairy, matrixStackIn, partialTickTime);

		float f1 = 0.875F;
		if (fairyModel != null) {
			//fairyModel.sinage = fairy.sinage;
			//fairyModel.flymode = fairy.flymode();
			//fairyModel.showCrown = fairy.tamed() || fairy.queen();
			fairyModel.showCrown = fairy.isQueen();
			fairyModel.isSneak = fairy.isSneaking();
			//fairyModel.scoutWings = fairy.scout();
			//fairyModel.rogueParts = fairy.rogue();
			//fairyModel.hairType = fairy.hairType();
		}
		GL11.glScalef(f1, f1, f1);

		if (fairy.isSneaking()) {
			GL11.glTranslatef(0F, (5F / 16F), 0F);
		}
	}
}
