package com.natamus.screenshotcompression.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.natamus.screenshotcompression.compression.ImageCompressor;
import net.minecraft.client.Screenshot;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.File;
import java.util.function.Consumer;

@Mixin(value = Screenshot.class, priority = 1001)
public class ScreenshotMixin {
    @Inject(method = "_grab(Ljava/io/File;Ljava/lang/String;Lcom/mojang/blaze3d/pipeline/RenderTarget;Ljava/util/function/Consumer;)V", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void _grab(File file, String name, RenderTarget renderTarget, Consumer<Component> componentConsumer, CallbackInfo ci, NativeImage nativeImage, File folder, File screenshotFile) {
        ImageCompressor.runScreenshotCompressThread(screenshotFile);
    }
}
