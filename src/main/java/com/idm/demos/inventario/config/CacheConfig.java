package com.idm.demos.inventario.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
	
	@Bean
    public CacheManager cacheManager() {
        // Inicializa el gestor bloqueando la creación dinámica y registrando solo los cachés declarados
        ConcurrentMapCacheManager cacheManager = 
        		new ConcurrentMapCacheManager("estadosCache", "categoriasCache", "sesionesCache");
        cacheManager.setAllowNullValues(false); // Evita guardar respuestas nulas en caché
        return cacheManager;
    }

}
