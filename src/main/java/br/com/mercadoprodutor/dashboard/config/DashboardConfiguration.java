package br.com.mercadoprodutor.dashboard.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class DashboardConfiguration {

    @Bean
    @Qualifier("dashboardClock")
    public Clock dashboardClock() {
        return Clock.system(ZoneId.of("America/Bahia"));
    }
}
