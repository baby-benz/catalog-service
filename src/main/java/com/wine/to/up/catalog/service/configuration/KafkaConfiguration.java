/*
package com.wine.to.up.catalog.service.configuration;

import com.wine.to.up.catalog.service.api.CatalogServiceApiProperties;
import com.wine.to.up.catalog.service.api.message.NewWineSavedMessageSentEventOuterClass;
import com.wine.to.up.catalog.service.api.message.UpdatePriceMessageSentEventOuterClass;
import com.wine.to.up.catalog.service.components.CatalogServiceMetricsCollector;
import com.wine.to.up.catalog.service.messaging.ParserTopicKafkaMessageHandler;
import com.wine.to.up.catalog.service.messaging.serialization.NewWineSavedEventSerializer;
import com.wine.to.up.catalog.service.messaging.serialization.UpdateWineEventSerializer;
import com.wine.to.up.catalog.service.messaging.serialization.WineParsedEventDeserializer;
import com.wine.to.up.commonlib.messaging.BaseKafkaHandler;
import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.parser.common.api.ParserCommonApiProperties;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Properties;

@Configuration
@Slf4j
public class KafkaConfiguration {
    */
/**
     * List of kafka servers
     *//*

    @Value("${spring.kafka.bootstrap-server}")
    private String brokers;

    */
/**
     * Application consumer group id
     *//*

    @Value("${spring.kafka.consumer.group-id}")
    private String applicationConsumerGroupId;

    */
/**
     * Creating general producer properties. Common for all the producers
     *//*

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Properties producerProperties() {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return properties;
    }

    */
/**
     * Creating general consumer properties. Common for all the consumers
     *//*

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    Properties consumerProperties() {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, applicationConsumerGroupId);
        //in case of consumer crashing, new consumer will read all messages from committed offset
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OffsetResetStrategy.EARLIEST.name().toLowerCase());
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return properties;
    }


    @Bean
    BaseKafkaHandler<ParserApi.WineParsedEvent> wineParsedEventBaseKafkaHandler(
            Properties consumerProperties,
            ParserCommonApiProperties parserCommonApiProperties,
            ParserTopicKafkaMessageHandler parserTopicKafkaMessageHandler
    ) {
        consumerProperties.setProperty(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                WineParsedEventDeserializer.class.getName()
        );
        log.info("PARSER TOPIC " + parserCommonApiProperties.getWineParsedEventsTopicName());
        log.info("KAFKA_BOOTSTRAP_SERVER "+ brokers);
        return new BaseKafkaHandler<>(
                parserCommonApiProperties.getWineParsedEventsTopicName(),
                new KafkaConsumer<>(consumerProperties),
                parserTopicKafkaMessageHandler
        );
    }

    @Bean
    KafkaMessageSender<UpdatePriceMessageSentEventOuterClass.UpdatePriceMessageSentEvent> updateWineEventKafkaMessageSender(
            Properties producerProperties,
            CatalogServiceApiProperties serviceApiProperties,
            CatalogServiceMetricsCollector metricsCollector) {

        producerProperties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, UpdateWineEventSerializer.class.getName());

        return new KafkaMessageSender<>(new KafkaProducer<>(producerProperties),  serviceApiProperties.getEventTopic(), metricsCollector);
    }

    @Bean
    KafkaMessageSender<NewWineSavedMessageSentEventOuterClass.NewWineSavedMessageSentEvent> saveNewWineEventKafkaMessageSender(
            Properties producerProperties,
            CatalogServiceApiProperties serviceApiProperties,
            CatalogServiceMetricsCollector metricsCollector) {

        producerProperties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, NewWineSavedEventSerializer.class.getName());

        return new KafkaMessageSender<>(new KafkaProducer<>(producerProperties),  serviceApiProperties.getEventTopic(), metricsCollector);
    }
}
*/
