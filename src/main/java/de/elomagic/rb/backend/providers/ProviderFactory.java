package de.elomagic.rb.backend.providers;

import de.elomagic.rb.backend.providers.ews.EwsProvider;
import de.elomagic.rb.backend.providers.graph.GraphProvider;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class ProviderFactory {

    private final BeanFactory beanFactory;

    public ProviderFactory(@Autowired BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Bean
    public IProvider getProvider(@Value("${rb.ext.apiType}") String provider) {
        Class<? extends IProvider> clazz = switch (provider) {
            case "graph":
                yield GraphProvider.class;
            case "ews":
                yield EwsProvider.class;

            default:
                throw new IllegalStateException("Unknown provider: " + provider);
        };

        return beanFactory.getBean(clazz);
    }

}
