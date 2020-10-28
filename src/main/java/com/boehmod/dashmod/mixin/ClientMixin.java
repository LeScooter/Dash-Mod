package com.boehmod.dashmod.mixin;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.MovementType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Calendar;

@Mixin(Keyboard.class)
public class ClientMixin {

    /**
     * The last key pressed
     */
    private static KeyBinding lastKey;
    /**
     * The time since last key press
     */
    private static long keyTimer = Calendar.getInstance().getTimeInMillis();
    /**
     * The last time that a successful dash happened
     */
    private static long lastDash = 0;

    /**
     * Called when the Minecraft client handles key inputs
     *
     * @param info - Given {@link CallbackInfo} instance
     */
    @Inject(at = @At("HEAD"), method = "onKey")
    private void onKey(long window, int key, int keyScanCode, int keyAction, int j, CallbackInfo info) {

        //Fetch the key code of the current input
        InputUtil.Key keyCode = InputUtil.fromKeyCode(key, keyScanCode);

        //Fetch the Minecraft client and game options instances
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        GameOptions gameOptions = minecraftClient.options;

        //Calculate the current time and difference since last dash
        long timeNow = Calendar.getInstance().getTimeInMillis();
        long timeDif = timeNow - lastDash;

        //Check that the time difference is big enough and that key is just pressed
        if (timeDif > 800 && keyAction == 1) {

                if (keyCode == gameOptions.keyLeft.getDefaultKey()) {
                    this.processKey(gameOptions.keyLeft, minecraftClient, timeNow);
                } else if (keyCode == gameOptions.keyRight.getDefaultKey()) {
                    this.processKey(gameOptions.keyRight, minecraftClient, timeNow);
                } else if (keyCode == gameOptions.keyBack.getDefaultKey()) {
                    this.processKey(gameOptions.keyBack, minecraftClient, timeNow);
                }
        }
    }

    /**
     * Called when a given key is pressed
     *
     * @param keyBinding      - Given {@link KeyBinding} that was pressed
     * @param minecraftClient - Given {@link MinecraftClient} instance
     * @param timeNow         - Given current time in milliseconds
     */
    public void processKey(final KeyBinding keyBinding, final MinecraftClient minecraftClient, final long timeNow) {

        //Check if last key is equal to current key pressed
        if (lastKey == keyBinding) {
            //Calculate the time difference between
            long timeDif = timeNow - keyTimer;

            //Check if difference is acceptable
            if (timeDif < 200) {
                //Initiate a dash
                this.onDash(keyBinding, minecraftClient, timeNow);
            }
        }

        //Reset the last key and key timer
        keyTimer = timeNow;
        lastKey = keyBinding;
    }

    /**
     * Called when a given key is pushed twice quickly (dash)
     *
     * @param keyBinding      - Given {@link KeyBinding} that was triggered
     * @param minecraftClient - Given {@link MinecraftClient} instance
     * @param timeNow         - Given current time in milliseconds
     */
    public void onDash(final KeyBinding keyBinding, final MinecraftClient minecraftClient, final long timeNow) {

        //Fetch the Minecraft Client game options instance
        GameOptions gameOptions = minecraftClient.options;

        //Fetch the key code from the given key binding
        InputUtil.Key keyCode = keyBinding.getDefaultKey();
        float dashSpeed = .75F;
        Vec3d moveVector = null;

        if (keyCode == gameOptions.keyLeft.getDefaultKey()) {
            moveVector = new Vec3d(1.0F, 0, 0);
        } else if (keyCode == gameOptions.keyRight.getDefaultKey()) {
            moveVector = new Vec3d(-1.0F, 0, 0);
        } else if (keyCode == gameOptions.keyBack.getDefaultKey()) {
            moveVector = new Vec3d(0, 0, -1.0F);
        }

        //Check that client player exists, is on the ground, and that the move vector exists
        if (minecraftClient.player != null && minecraftClient.player.isOnGround() && moveVector != null) {

            //Play a dash sound effect and spawn a particle
            minecraftClient.player.playSound(SoundEvents.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.PLAYERS, 1.0F, 1.2F);
            minecraftClient.world.addParticle(ParticleTypes.CLOUD, minecraftClient.player.getX(), minecraftClient.player.getY() + .5F, minecraftClient.player.getZ(), 0, 0, 0);

            //Update the players velocity, move them, and make them jump
            minecraftClient.player.updateVelocity(dashSpeed, moveVector);
            minecraftClient.player.move(MovementType.SELF, minecraftClient.player.getVelocity());
            minecraftClient.player.jump();

            //Set the last dash to the current time
            lastDash = timeNow;
        }
    }
}
