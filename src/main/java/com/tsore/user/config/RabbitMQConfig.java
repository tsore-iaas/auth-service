package com.tsore.user.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  public static final String USER_CREATE_QUEUE_NAME = "user-create-queue";
  public static final String EXCHANGE_NAME = "user-exchange";

  @Bean
  public Queue userCreateQueue() {
    return new Queue(USER_CREATE_QUEUE_NAME, false);
  }

  @Bean
  public TopicExchange userExchange() {
    return new TopicExchange(EXCHANGE_NAME);
  }

  @Bean
  public Binding userCreateBinding(Queue userCreateQueue, TopicExchange userExchange) {
    return BindingBuilder.bind(userCreateQueue).to(userExchange).with("user.create.key.#");
  }

  @Bean
  public Jackson2JsonMessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }
}
