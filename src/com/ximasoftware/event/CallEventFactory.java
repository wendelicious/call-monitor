package com.ximasoftware.event;

import com.ximasoftware.Column;

import static com.ximasoftware.util.Assertion.isEmpty;

public class CallEventFactory {

    public CallEvent createEventFromRawFeed(String raw) throws InvalidCallEventException {
        if (isEmpty(raw)) {
            throw new InvalidCallEventException("Call event is empty");
        }

        final String[] columns = raw.split(",");

        if (columns.length != 4) {
            throw new InvalidCallEventException("Call event did not have expected data: " + columns.length + " fields");
        }

        return CallEvent.newBuilder()
                .withCallId(columns[Column.CALL_ID.getIndex()])
                .withEventType(columns[Column.EVENT_TYPE.getIndex()])
                .withCallingParty(columns[Column.CALLING_PARTY.getIndex()])
                .withReceivingParty(columns[Column.RECEIVING_PARTY.getIndex()])
                .build();
    }

}
