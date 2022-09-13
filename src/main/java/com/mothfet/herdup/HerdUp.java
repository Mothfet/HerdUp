package com.mothfet.herdup;

import com.mojang.logging.LogUtils;
import com.mothfet.herdup.entities.ModEntityTypes;
import com.mothfet.herdup.entities.client.HeiferRenderer;
import com.mothfet.herdup.entities.custom.HeiferEntity;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import software.bernie.geckolib3.GeckoLib;

/*
 * All Rights Reserved
 *
 * Copyright (c) 2022 Mothfet
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

@Mod(HerdUp.MOD_ID)
public class HerdUp {
    public static final String MOD_ID = "herdup";
    public static final Logger LOGGER = LogUtils.getLogger();

    public HerdUp() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        //ModItems.register(eventBus);

        ModEntityTypes.register(eventBus);

        GeckoLib.initialize();

        eventBus.addListener(this::setup);
        eventBus.addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void clientSetup(final FMLClientSetupEvent event) {

        EntityRenderers.register(ModEntityTypes.HEIFER.get(), HeiferRenderer::new);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("Beep Boop!");
    }

}
