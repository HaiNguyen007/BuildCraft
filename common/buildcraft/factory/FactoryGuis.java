/* Copyright (c) 2016 AlexIIL and the BuildCraft team
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package buildcraft.factory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public enum FactoryGuis {
    AUTO_WORKBENCH_ITEMS,
    AUTO_WORKBENCH_FLUIDS,
    CHUTE;

    public void openGUI(EntityPlayer player) {
        player.openGui(BCFactory.INSTANCE, ordinal(), player.getEntityWorld(), 0, 0, 0);
    }

    public void openGUI(EntityPlayer player, BlockPos pos) {
        player.openGui(BCFactory.INSTANCE, ordinal(), player.getEntityWorld(), pos.getX(), pos.getY(), pos.getZ());
    }
}
