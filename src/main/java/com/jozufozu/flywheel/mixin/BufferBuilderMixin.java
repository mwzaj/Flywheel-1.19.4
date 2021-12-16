package com.jozufozu.flywheel.mixin;

import java.nio.ByteBuffer;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.jozufozu.flywheel.backend.model.DirectVertexConsumer;
import com.jozufozu.flywheel.backend.model.DirectBufferBuilder;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;

@Mixin(BufferBuilder.class)
public abstract class BufferBuilderMixin implements DirectBufferBuilder {
	@Shadow
	private ByteBuffer buffer;

	@Shadow
	private VertexFormat format;

	@Shadow
	protected abstract void ensureCapacity(int p_85723_);

	@Shadow
	private int vertices;

	@Shadow
	@Nullable
	private VertexFormatElement currentElement;

	@Shadow
	private int elementIndex;

	@Shadow
	private int nextElementByte;

	@Override
	public DirectVertexConsumer intoDirectConsumer(int neededVerts) {
		ensureCapacity(neededVerts * this.format.getVertexSize());
		return new DirectVertexConsumer(this.buffer, this.format);
	}

	@Override
	public void updateAfterWriting(DirectVertexConsumer complete) {
		int vertexCount = complete.getVertexCount();
		int totalWrittenBytes = vertexCount * format.getVertexSize();

		this.vertices += vertexCount;
		this.currentElement = format.getElements()
				.get(0);
		this.elementIndex = 0;
		this.nextElementByte += totalWrittenBytes;
		this.buffer.position(complete.startPos + totalWrittenBytes);
	}
}