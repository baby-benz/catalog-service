/*
package com.wine.to.up.catalog.service.messaging.serialization;

import com.google.protobuf.InvalidProtocolBufferException;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

@Slf4j
public class WineParsedEventDeserializer implements Deserializer<ParserApi.WineParsedEvent> {

    @Override
    public ParserApi.WineParsedEvent deserialize(String topic, byte[] data) {
        try {
            return ParserApi.WineParsedEvent.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            log.error("Failed to deserilize message from topic: {}. {}", topic, e);
            return null;
        }
    }
}
*/
