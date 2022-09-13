package com.mothfet.herdup.entities.variants;

import com.mothfet.herdup.HerdUp;

import java.util.*;
import java.util.stream.Collectors;

//** VARIANT SYSTEM BORROWED FROM SWEM, USED FOR FUTURE COMPATIBILITY!!

public enum HeiferVariants {
    HEIFER(1, true),
    BULL(2, true),
    CALF(80001, false);

    private static final HeiferVariants[] VALUES = Arrays.stream(values())
            .sorted(Comparator.comparingInt(HeiferVariants::getId))
            .toArray(HeiferVariants[]::new);
    private final int id;
    private final boolean naturallyOccurring;

    HeiferVariants(int id, boolean naturallyOccurring) {
        this.id = id;
        this.naturallyOccurring = naturallyOccurring;
    }

    public static int getIndexFromId(int id) {
        for (int i = 0; i < VALUES.length; i++) {
            if (id == VALUES[i].getId()) {
                return i;
            }
        }
        return 1;
    }

    public static HeiferVariants getById(int id) {
        for (HeiferVariants cowType : VALUES) {
            if (cowType.getId() == id) {
                return cowType;
            }
        }
        HerdUp.LOGGER.error("Couldn't find coat with id: " + id + " - returning default variant.");
        return HeiferVariants.HEIFER;
    }

    private static HeiferVariants getNextType(int prevId) {
        return VALUES[(prevId + 1) % VALUES.length];
    }

    private static HeiferVariants getPreviousType(int prevId) {
        int index = prevId - 1;
        if (index < 0) {
            index += VALUES.length;
        }
        return VALUES[index % VALUES.length];
    }

    public static HeiferVariants getNextNaturalType(int prevId) {
        HeiferVariants color = getNextType(getIndexFromId(prevId));
        while (!color.isNaturallyOccurring()) {
            color = getNextType(getIndexFromId(color.getId()));
        }
        return color;
    }


    public static HeiferVariants getPreviousNaturalType(int prevId) {
        HeiferVariants color = getPreviousType(getIndexFromId(prevId));
        while (!color.isNaturallyOccurring()) {
            color = getPreviousType(getIndexFromId(color.getId()));
        }

        return color;
    }

    private static HeiferVariants getRandomCowType() {
        Random random = new Random();
        return VALUES[random.nextInt(VALUES.length)];
    }

    public static HeiferVariants getRandomNaturalCowType() {
        HeiferVariants color = getRandomCowType();
        while (!color.isNaturallyOccurring()) {
            color = getNextNaturalType(getIndexFromId(color.getId()));
        }

        return color;
    }


    public static HeiferVariants getRandomCalfCoat() {
        List<HeiferVariants> calfCoats =
                Arrays.stream(VALUES).filter((coat) -> coat.getId() >= 80000).collect(Collectors.toList());
        Collections.shuffle(calfCoats);

        return calfCoats.get(0);
    }

    public static HeiferVariants parentToCalfType(HeiferVariants parent) {
        return VariantMapper.parentToCalfType(parent);
    }

    public static HeiferVariants calfToParentType(HeiferVariants calf) {
        return VariantMapper.calfToParentType(calf);
    }

    public int getId() {
        return this.id;
    }

    public boolean isNaturallyOccurring() {
        return naturallyOccurring;
    }

}


