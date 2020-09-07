package org.mcupdater.fairies.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import org.mcupdater.fairies.entity.FairyEntity;

public class LayerFairyEyes<T extends LivingEntity, M extends EntityModel<T> & IHasHead> extends LayerRenderer<T, M> {
	public LayerFairyEyes(IEntityRenderer<T, M> entityRendererIn) {
		super(entityRendererIn);

		bipedHead = new ModelRenderer(entityRendererIn.getEntityModel(), 40, 0);
		bipedHead.addBox(-3F, -6F, -3F, 6, 6, 6, 0.01F);
		bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		setRotationAngles(netHeadYaw, headPitch);
		bipedHead.render(matrixStackIn, bufferIn.getBuffer(RenderType.getSolid()), packedLightIn, 0, 1.0f, 1.0f, 1.0f, 1.0f);
	}

	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		bipedHead.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	public void setRotationAngles(float netHeadYaw, float headPitch) {
		bipedHead.rotateAngleY = netHeadYaw / (180F / (float)Math.PI);
		bipedHead.rotateAngleX = headPitch / (180F / (float)Math.PI);

		if (flymode)
		{
			bipedHead.rotationPointZ = -3F;
			bipedHead.rotationPointY = 19.75F;
		}
		else
		{
			bipedHead.rotationPointZ = 0.0F;
			bipedHead.rotationPointY = 12F;
		}
	}

	public boolean flymode;
	public ModelRenderer bipedHead;
}
