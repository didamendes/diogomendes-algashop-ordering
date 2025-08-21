package com.diogomendes.algashop.ordering.domain.utility;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochRandomGenerator;

import java.util.UUID;

public class IdGenerator {

    private static final TimeBasedEpochRandomGenerator timeBasedRandomGenerator =
            Generators.timeBasedEpochRandomGenerator();

    private IdGenerator() {
    }

    public static UUID generateTimeBaseUUID() {
        return timeBasedRandomGenerator.generate();
    }
}
