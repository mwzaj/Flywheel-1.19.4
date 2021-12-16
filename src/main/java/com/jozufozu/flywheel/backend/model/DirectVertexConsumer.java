package com.jozufozu.flywheel.backend.model;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import com.jozufozu.flywheel.util.RenderMath;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;

public class DirectVertexConsumer implements VertexConsumer {

	public final VertexFormat format;
	private final int stride;
	public final int startPos;

	private int position = -1;
	private int normal = -1;
	private int color = -1;
	private int uv = -1;
	private int uv1 = -1;
	private int uv2 = -1;

	private long vertexBase;
	private int vertexCount;

	public DirectVertexConsumer(ByteBuffer buffer, VertexFormat format) {
		this.format = format;
		startPos = buffer.position();
		stride = format.getVertexSize();

		int offset = 0;

		for (VertexFormatElement element : format.getElements()) {
			switch (element.getUsage()) {
			case POSITION -> this.position = offset;
			case NORMAL -> this.normal = offset;
			case COLOR -> this.color = offset;
			case UV -> {
				switch (element.getIndex()) {
					case 0 -> this.uv = offset;
					case 1 -> this.uv1 = offset;
					case 2 -> this.uv2 = offset;
				}
			}
			}

			offset += element.getByteSize();
		}

		this.vertexBase = MemoryUtil.memAddress(buffer);
	}

	public boolean hasOverlay() {
		return uv1 >= 0;
	}

	@Override
	public VertexConsumer vertex(double x, double y, double z) {
		if (position < 0) return this;
		long base = vertexBase + position;
		MemoryUtil.memPutFloat(base, (float) x);
		MemoryUtil.memPutFloat(base + 4, (float) y);
		MemoryUtil.memPutFloat(base + 8, (float) z);
		return this;
	}

	@Override
	public VertexConsumer color(int r, int g, int b, int a) {
		if (color < 0) return this;
		long base = vertexBase + color;
		MemoryUtil.memPutByte(base, (byte) r);
		MemoryUtil.memPutByte(base + 1, (byte) g);
		MemoryUtil.memPutByte(base + 2, (byte) b);
		MemoryUtil.memPutByte(base + 3, (byte) a);
		return this;
	}

	@Override
	public VertexConsumer uv(float u, float v) {
		if (uv < 0) return this;
		long base = vertexBase + uv;
		MemoryUtil.memPutFloat(base, u);
		MemoryUtil.memPutFloat(base + 4, v);
		return this;
	}

	@Override
	public VertexConsumer overlayCoords(int u, int v) {
		if (uv1 < 0) return this;
		long base = vertexBase + uv1;
		MemoryUtil.memPutShort(base, (short) u);
		MemoryUtil.memPutShort(base + 2, (short) v);
		return this;
	}

	@Override
	public VertexConsumer uv2(int u, int v) {
		if (uv2 < 0) return this;
		long base = vertexBase + uv2;
		MemoryUtil.memPutShort(base, (short) u);
		MemoryUtil.memPutShort(base + 2, (short) v);
		return this;
	}

	@Override
	public VertexConsumer normal(float x, float y, float z) {
		if (normal < 0) return this;
		long base = vertexBase + normal;
		MemoryUtil.memPutByte(base, RenderMath.nb(x));
		MemoryUtil.memPutByte(base + 1, RenderMath.nb(y));
		MemoryUtil.memPutByte(base + 2, RenderMath.nb(z));
		return this;
	}

	@Override
	public void endVertex() {
		vertexBase += stride;
		vertexCount++;
	}

	@Override
	public void defaultColor(int r, int g, int b, int a) {

	}

	@Override
	public void unsetDefaultColor() {

	}

	public int getVertexCount() {
		return vertexCount;
	}
}