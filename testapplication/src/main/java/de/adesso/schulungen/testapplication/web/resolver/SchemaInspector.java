package de.adesso.schulungen.testapplication.web.resolver;

import graphql.schema.GraphQLSchema;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SchemaInspector {

/*    @Bean
    public CommandLineRunner inspectSchema(GraphQLSchema schema) {
        return args -> {
            System.out.println("📊 Available Mutations: " + schema.getMutationType().getFieldDefinitions());
        };
    }*/
}