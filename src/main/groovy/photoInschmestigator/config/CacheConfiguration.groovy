package photoInschmestigator.config
import com.hazelcast.config.Config
import com.hazelcast.config.MapConfig
import com.hazelcast.config.MaxSizeConfig
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.instance.HazelcastInstanceFactory
import com.hazelcast.spring.cache.HazelcastCacheManager
import org.slf4j.Logger
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject

import static org.slf4j.LoggerFactory.getLogger

@Configuration
@EnableCaching
@AutoConfigureAfter(value = [])
public class CacheConfiguration {

    private final Logger log = getLogger(CacheConfiguration.class)

    private static HazelcastInstance hazelcastInstance

    @Inject
    private Environment env

    private CacheManager cacheManager

    @PreDestroy
    public void destroy() {
        log.info("Closing Cache Manager")
        Hazelcast.shutdownAll()
    }

    @Bean
    public CacheManager cacheManager() {
        log.debug("Starting HazelcastCacheManager");
        new HazelcastCacheManager(hazelcastInstance)
    }

    @PostConstruct
    private HazelcastInstance hazelcastInstance() {
        final Config config = new Config();
        config.setInstanceName("photoInschmestigator");
        config.getNetworkConfig().setPort(5701);
        config.getNetworkConfig().setPortAutoIncrement(true);

        System.setProperty("hazelcast.local.localAddress", "127.0.0.1");

        config.getNetworkConfig().getJoin().getAwsConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(false);

        config.getMapConfigs().put("default", initializeDefaultMapConfig());
        config.getMapConfigs().put("photoInschmestigator.domain.*", initializeDomainMapConfig())
        config.getMapConfigs().put("my-sessions", initializeClusteredSession())

        hazelcastInstance = HazelcastInstanceFactory.newHazelcastInstance(config)
    }

    private MapConfig initializeDefaultMapConfig() {
        MapConfig mapConfig = new MapConfig()
        mapConfig.setBackupCount(0)
        mapConfig.setEvictionPolicy(MapConfig.EvictionPolicy.LRU)
        mapConfig.setMaxSizeConfig(new MaxSizeConfig(0, MaxSizeConfig.MaxSizePolicy.USED_HEAP_SIZE))
        mapConfig.setEvictionPercentage(25)
        mapConfig
    }

    private MapConfig initializeDomainMapConfig() {
        MapConfig mapConfig = new MapConfig()

        mapConfig.setTimeToLiveSeconds(env.getProperty("cache.timeToLiveSeconds", Integer.class, 3600))
        mapConfig
    }


    private MapConfig initializeClusteredSession() {
        MapConfig mapConfig = new MapConfig()

        mapConfig.setBackupCount(env.getProperty("cache.hazelcast.backupCount", Integer.class, 1))
        mapConfig.setTimeToLiveSeconds(env.getProperty("cache.timeToLiveSeconds", Integer.class, 3600))
        mapConfig
    }

    public static HazelcastInstance getHazelcastInstance() {
        hazelcastInstance
    }
}
