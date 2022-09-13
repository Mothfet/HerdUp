package com.mothfet.herdup.entities.ai;


import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.util.GoalUtils;

//**Handles sheltering after rain is avoided (stay dry!)
public class RestrictRainGoal extends RestrictSunGoal {
    private final PathfinderMob mob;

    public RestrictRainGoal(PathfinderMob mob) {
        super(mob);
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        return this.mob.level.getLevelData().isRaining() && GoalUtils.hasGroundPathNavigation(this.mob);
    }

}
