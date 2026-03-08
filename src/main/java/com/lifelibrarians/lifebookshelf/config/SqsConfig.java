package com.lifelibrarians.lifebookshelf.config;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

@Configuration
public class SqsConfig {

    /**
     * QueueMessagingTemplate에 Jackson 컨버터를 명시적으로 설정한다.
     *
     * 설정하지 않으면 기본 SimpleMessageConverter가 사용되어
     * DTO를 .toString()으로 직렬화하므로 수신 측에서 파싱 불가.
     * setStrictContentTypeMatch(false)는 SQS 메시지에 content-type 헤더가
     * 없어도 JSON으로 처리하기 위해 필요하다.
     */
    @Bean
    public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSQSAsync, ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        converter.setStrictContentTypeMatch(false);

        QueueMessagingTemplate template = new QueueMessagingTemplate(amazonSQSAsync);
        template.setMessageConverter(converter);
        return template;
    }
}
