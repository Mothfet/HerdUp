package com.mothfet.herdup.entities.ai;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

//** Handles the actual running from rain
public class FleeRainGoal extends FleeSunGoal {
    protected final PathfinderMob mob;
    private final Level level;

    public FleeRainGoal(PathfinderMob mob, double speed) {
        super(mob, speed);
        this.mob = mob;
        this.level = mob.level;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!this.level.getLevelData().isRaining()) {
            return false;
        } else {
            if (!this.level.canSeeSky(this.mob.blockPosition())) {
                return false;
            } else {
                return this.setWantedPos();
            }
        }
    }
}



