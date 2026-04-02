package com.natamus.screenshotcompression.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import com.natamus.screenshotcompression.compression.ImageCompressor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(value = NativeImage.class, priority = 1001)
public class NativeImageMixin {
    @Inject(method = "writeToFile(Ljava/io/File;)V", at = @At("TAIL"))
    private void _onWriteToFile(File file, CallbackInfo ci) {
        if (file.getPath().contains("screenshots")) {
            ImageCompressor.runScreenshotCompressThread(file);
        }
    }
}