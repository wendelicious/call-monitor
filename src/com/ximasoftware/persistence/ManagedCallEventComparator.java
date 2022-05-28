package com.ximasoftware.persistence;

import com.ximasoftware.EventType;
import com.ximasoftware.model.ManagedCallEvent;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Comparator;

public class ManagedCallEventComparator implements Comparator<ManagedCallEvent> {
    @Override
    public int compare(ManagedCallEvent o1, ManagedCallEvent o2) {
        int one = compareCallId(o1, o2);
        int two = compareEventType(o1, o2);
        int three = compareWhen(o1, o2);

        return firstNonZero(one, two, three);
    }

    private int compareCallId(ManagedCallEvent o1, ManagedCallEvent o2) {
        final String t1 = o1.getEvent().getCallId();
        final String t2 = o2.getEvent().getCallId();

        return t1.compareTo(t2);
    }

    private int compareEventType(ManagedCallEvent o1, ManagedCallEvent o2) {
        final EventType t1 = o1.getEvent().getEventType();
        final EventType t2 = o2.getEvent().getEventType();

        return t1.compareTo(t2);
    }

    private int compareWhen(ManagedCallEvent o1, ManagedCallEvent o2) {
        final OffsetDateTime t1 = o1.getEvent().getWhen();
        final OffsetDateTime t2 = o2.getEvent().getWhen();

        return t1.compareTo(t2);
    }

    private int firstNonZero(int... candidates) {
        return Arrays.stream(candidates)
                .filter(c -> c != 0)
                .findFirst()
                .orElse(0);
    }
}
