package com.natamus.screenshotcompression.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import com.natamus.screenshotcompression.compression.ImageCompressor;
import net.minecraft.client.Screenshot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.File;
import java.util.function.Consumer;

@Mixin(value = Screenshot.class, priority = 1001, remap = false)
public class ScreenshotMixin {
    @Inject(method = { "method_68157", "lambda$grab$2" }, at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void _grab(File file, String string, Consumer<?> consumer, NativeImage nativeImage, CallbackInfo ci, File file2, File screenshotFile) {
        ImageCompressor.runScreenshotCompressThread(screenshotFile);
    }
}
