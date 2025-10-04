package com.diogomendes.algashop.ordering.domain.utility;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochRandomGenerator;
import io.hypersistence.tsid.TSID;

import java.util.UUID;

public class IdGenerator {

    private static final TimeBasedEpochRandomGenerator timeBasedRandomGenerator =
            Generators.timeBasedEpochRandomGenerator();

    private static final TSID.Factory tsidFactory = TSID.Factory.INSTANCE;

    private IdGenerator() {
    }

    public static UUID generateTimeBaseUUID() {
        return timeBasedRandomGenerator.generate();
    }

    /**
     * TSID_NODE
     * TSID_NODE_COUNT
     * @return
     */
    public static TSID generateTSID() {
        return tsidFactory.generate();
    }
}
