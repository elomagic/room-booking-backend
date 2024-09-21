package de.elomagic.rb.backend;

import de.elomagic.rb.backend.providers.IProvider;
import de.elomagic.rb.backend.providers.ProviderMock;
import de.elomagic.rb.backend.providers.ews.EwsProvider;
import de.elomagic.rb.backend.providers.graph.GraphProvider;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TestProviderFactory {

    private final BeanFactory beanFactory;

    public TestProviderFactory(@Autowired BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Bean
    @Primary
    public IProvider getTestProvider(@Value("${rb.ext.apiType}") String provider) {
        Class<? extends IProvider> clazz = switch (provider) {
            case "graph":
                yield GraphProvider.class;
            case "ews":
                yield EwsProvider.class;

            default:
                yield ProviderMock.class;
        };

        return beanFactory.getBean(clazz);
    }

}
