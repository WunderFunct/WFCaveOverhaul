package wftech.caveoverhaul;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.AbstractCommentedConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraftforge.common.CreativeModeTabRegistry;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.ObjectHolderRegistry;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryManager;
import wftech.caveoverhaul.biomemodifiers.InitBiomeModifiers;
import wftech.caveoverhaul.carvertypes.InitCarverTypes;
import wftech.caveoverhaul.virtualpack.AddPackFindersEventWatcher;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CaveOverhaul.MOD_ID)
public class CaveOverhaul
{
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "caveoverhaul";
    
    public static AbstractCommentedConfig EARLY_LOAD_CONFIG = null;
    public static boolean ENABLE_MULTILAYER_RIVERS = true;

    public CaveOverhaul()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        InitCarverTypes.registerDeferred(eventBus);
        InitBiomeModifiers.registerDeferred(eventBus);

        eventBus.addListener(AddPackFindersEventWatcher::watch);
        
        MinecraftForge.EVENT_BUS.register(this);
    }
}
