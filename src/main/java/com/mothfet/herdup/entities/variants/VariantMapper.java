package com.mothfet.herdup.entities.variants;

import com.mothfet.herdup.HerdUp;

import java.util.*;

public class VariantMapper {
    private static HashMap<HeiferVariants, HeiferVariants> variantMap =
            new HashMap() {
                {
                    put(HeiferVariants.HEIFER, HeiferVariants.CALF);
                    put(HeiferVariants.BULL, HeiferVariants.CALF);
                }
            };

    protected static HeiferVariants parentToCalfType(HeiferVariants parent) {
        HeiferVariants type = variantMap.get(parent);
        if (type == null) {
            HerdUp.LOGGER.debug("Oops! Non-mapped variant used for breeding. Using default instead.");
            type = variantMap.get(HeiferVariants.HEIFER);
        }
        return type;
    }

    protected static HeiferVariants calfToParentType(HeiferVariants calf) {
        List<HeiferVariants> applicableParentCoats = new ArrayList<>();
        for (Map.Entry<HeiferVariants, HeiferVariants> entry : variantMap.entrySet()) {
            if (entry.getValue() == calf) {
                applicableParentCoats.add(entry.getKey());
            }
        }
        Collections.shuffle(applicableParentCoats);
        if (applicableParentCoats.isEmpty()) {
            HerdUp.LOGGER.error(
                    "Something went wrong mapping foal coat: "
                            + calf
                            + " to any parent coat! Returning Parent White Coat.");
            return HeiferVariants.HEIFER;
        }
        return applicableParentCoats.get(0);
    }
}
