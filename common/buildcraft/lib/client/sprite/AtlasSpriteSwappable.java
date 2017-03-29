package buildcraft.lib.client.sprite;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.PngSizeInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.client.FMLClientHandler;

/** Provides the basic implementation for */
public abstract class AtlasSpriteSwappable extends TextureAtlasSprite {
    private TextureAtlasSprite current;
    private boolean needsSwapping = true;

    public AtlasSpriteSwappable(String baseName) {
        super(baseName);
    }

    @Override
    public boolean hasAnimationMetadata() {
        return true;
    }

    @Override
    public void updateAnimation() {
        if (current == null) {
            return;
        }
        if (needsSwapping) {
            current.copyFrom(this);
        }
        if (current.hasAnimationMetadata()) {
            current.updateAnimation();
        } else if (needsSwapping) {
            TextureUtil.uploadTextureMipmap(current.getFrameTextureData(0), current.getIconWidth(), current.getIconHeight(), current.getOriginX(), current.getOriginY(), false, false);
        }
        needsSwapping = false;
    }

    public boolean swapWith(TextureAtlasSprite other) {
        if (current != other && (current == null || other != null)) {
            current = other;
            if (other != null && width == 0) {
                this.width = other.getIconWidth();
                this.height = other.getIconHeight();
            }
            generateMipmaps(Minecraft.getMinecraft().gameSettings.mipmapLevels);
            needsSwapping = true;
            return true;
        }
        return false;
    }

    public void reload(ResourceLocation from) {
        load(Minecraft.getMinecraft().getResourceManager(), from);
    }

    /** Actually loads the given location. Note that subclasses should override this, and possibly call
     * {@link #loadSprite(IResourceManager, String, ResourceLocation, boolean)} to load all of the possible variants. */
    @Override
    public boolean load(IResourceManager manager, ResourceLocation location) {
        TextureAtlasSprite sprite = loadSprite(manager, super.getIconName(), location, true);
        if (sprite != null) {
            swapWith(sprite);
        }
        return false;
    }

    public static final TextureAtlasSprite loadSprite(String name, ResourceLocation location, boolean careIfMissing) {
        return loadSprite(Minecraft.getMinecraft().getResourceManager(), name, location, careIfMissing);
    }

    public static final TextureAtlasSprite loadSprite(IResourceManager manager, String name, ResourceLocation location, boolean careIfMissing) {
        // Load the initial variant
        TextureAtlasSprite sprite = new AtlasSpriteDirect(name);
        try {
            // Copied almost directly from TextureMap.
            PngSizeInfo pngsizeinfo = PngSizeInfo.makeFromResource(manager.getResource(location));
            try (IResource iresource = manager.getResource(location)) {
                boolean flag = iresource.getMetadata("animation") != null;
                sprite.loadSprite(pngsizeinfo, flag);
                sprite.loadSpriteFrames(iresource, Minecraft.getMinecraft().gameSettings.mipmapLevels + 1);
            }
        } catch (IOException e) {
            if (careIfMissing) {
                // Do the same as forge - track the missing texture for later rather than printing out the error.
                FMLClientHandler.instance().trackMissingTexture(location);
            }
            return null;
        }
        return sprite;
    }

    @Override
    public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
        return true;
    }

    @Override
    public void generateMipmaps(int level) {
        if (current != null) {
            current.generateMipmaps(level);
        }
    }

    // Overrides

    @Override
    public int getFrameCount() {
        return current.getFrameCount();
    }

    @Override
    public void copyFrom(TextureAtlasSprite from) {
        super.copyFrom(from);
        if (current == null) {
            current = from;
        } else {
            current.copyFrom(from);
        }
    }

    @Override
    public int[][] getFrameTextureData(int index) {
        return current.getFrameTextureData(index);
    }

    @Override
    public void setFramesTextureData(List<int[][]> newFramesTextureData) {
        // NO-OP
    }
}
