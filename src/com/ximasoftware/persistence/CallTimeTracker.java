package com.ximasoftware.persistence;

import com.ximasoftware.model.ManagedCall;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;

class CallTimeTracker {
    private final Set<ManagedCall> activeCalls = new ConcurrentSkipListSet<>(Comparator.comparing(ManagedCall::getCallId));
    private final IndexedSet<String, ManagedCall> activeCallsByParty = new IndexedSet<>();
    private final Map<String, AtomicLong> completedCalltimeByParty = new ConcurrentHashMap<>();

    public void logCallStart(ManagedCall call) {
        activeCalls.add(call);
        activeCallsByParty.add(call.getReceivingParty(), call);
        activeCallsByParty.add(call.getCallingParty(), call);

        final AtomicLong al = completedCalltimeByParty.getOrDefault(call.getReceivingParty(), new AtomicLong());
        completedCalltimeByParty.put(call.getReceivingParty(), al);
        final AtomicLong betty = completedCalltimeByParty.getOrDefault(call.getCallingParty(), new AtomicLong());
        completedCalltimeByParty.put(call.getCallingParty(), betty);
    }

    public void logCallEnd(ManagedCall call) {
        if (activeCalls.remove(call)) {
            completedCalltimeByParty.get(call.getCallingParty()).addAndGet(call.getDuration().toMillis());
            completedCalltimeByParty.get(call.getReceivingParty()).addAndGet(call.getDuration().toMillis());
        }
    }

    public int getActiveCallCount() {
        return activeCalls.size();
    }

    public long countCalltimeForParty(String party) {
        final long active = activeCallsByParty.get(party).stream()
                .filter(ManagedCall::isActive)
                .map(ManagedCall::getDuration)
                .mapToLong(d -> d.toMillis())
                .sum();
        final long completed = completedCalltimeByParty.getOrDefault(party, new AtomicLong(0)).get();

        return active + completed;
    }
}
