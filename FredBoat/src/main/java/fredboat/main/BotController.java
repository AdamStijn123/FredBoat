package fredboat.main;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import fredboat.agent.FredBoatAgent;
import fredboat.audio.player.AudioConnectionFacade;
import fredboat.audio.player.PlayerRegistry;
import fredboat.config.property.*;
import fredboat.db.api.GuildConfigService;
import fredboat.db.api.GuildModulesService;
import fredboat.db.api.GuildPermsService;
import fredboat.db.api.PrefixService;
import fredboat.event.EventListenerBoat;
import fredboat.feature.metrics.BotMetrics;
import fredboat.feature.metrics.Metrics;
import fredboat.jda.JdaEntityProvider;
import fredboat.metrics.OkHttpEventMetrics;
import fredboat.util.ratelimit.Ratelimiter;
import fredboat.util.rest.Http;
import net.dv8tion.jda.bot.sharding.ShardManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutorService;

/**
 * Class responsible for controlling FredBoat at large
 */
@Component
public class BotController {

    public static final Http HTTP = new Http(Http.DEFAULT_BUILDER.newBuilder()
            .eventListener(new OkHttpEventMetrics("default", Metrics.httpEventCounter))
            .build());

    private final ConfigPropertiesProvider configProvider;
    private final AudioConnectionFacade audioConnectionFacade;
    private final ShardManager shardManager;
    //central event listener that all events by all shards pass through
    private final EventListenerBoat mainEventListener;
    private final ShutdownHandler shutdownHandler;
    private final GuildConfigService guildConfigService;
    private final GuildModulesService guildModulesService;
    private final GuildPermsService guildPermsService;
    private final PrefixService prefixService;
    private final PlayerRegistry playerRegistry;
    private final JdaEntityProvider jdaEntityProvider;
    private final BotMetrics botMetrics;
    private final ExecutorService executor;
    private final AudioPlayerManager audioPlayerManager;
    private final Ratelimiter ratelimiter;


    public BotController(ConfigPropertiesProvider configProvider, AudioConnectionFacade audioConnectionFacade, ShardManager shardManager,
                         EventListenerBoat eventListenerBoat, ShutdownHandler shutdownHandler, ExecutorService executor,
                         PlayerRegistry playerRegistry, JdaEntityProvider jdaEntityProvider, BotMetrics botMetrics,
                         @Qualifier("loadAudioPlayerManager") AudioPlayerManager audioPlayerManager,
                         Ratelimiter ratelimiter, GuildConfigService guildConfigService, GuildModulesService guildModulesService,
                         GuildPermsService guildPermsService, PrefixService prefixService) {
        this.configProvider = configProvider;
        this.audioConnectionFacade = audioConnectionFacade;
        this.shardManager = shardManager;
        this.mainEventListener = eventListenerBoat;
        this.shutdownHandler = shutdownHandler;
        this.executor = executor;
        this.playerRegistry = playerRegistry;
        this.jdaEntityProvider = jdaEntityProvider;
        this.botMetrics = botMetrics;
        this.audioPlayerManager = audioPlayerManager;
        this.ratelimiter = ratelimiter;
        this.guildConfigService = guildConfigService;
        this.guildModulesService = guildModulesService;
        this.guildPermsService = guildPermsService;
        this.prefixService = prefixService;

        Runtime.getRuntime().addShutdownHook(new Thread(createShutdownHook(), "FredBoat main shutdownhook"));
    }

    public AppConfig getAppConfig() {
        return configProvider.getAppConfig();
    }

    public AudioSourcesConfig getAudioSourcesConfig() {
        return configProvider.getAudioSourcesConfig();
    }

    public BackendConfig getBackendConfig() {
        return configProvider.getBackendConfig();
    }

    public Credentials getCredentials() {
        return configProvider.getCredentials();
    }

    public AudioConnectionFacade getAudioConnectionFacade() {
        return audioConnectionFacade;
    }

    public ShutdownHandler getShutdownHandler() {
        return shutdownHandler;
    }

    @Nonnull
    public ExecutorService getExecutor() {
        return executor;
    }

    public EventListenerBoat getMainEventListener() {
        return mainEventListener;
    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    public GuildConfigService getGuildConfigService() {
        return guildConfigService;
    }

    public GuildModulesService getGuildModulesService() {
        return guildModulesService;
    }

    public GuildPermsService getGuildPermsService() {
        return guildPermsService;
    }

    public PrefixService getPrefixService() {
        return prefixService;
    }

    public PlayerRegistry getPlayerRegistry() {
        return playerRegistry;
    }

    public JdaEntityProvider getJdaEntityProvider() {
        return jdaEntityProvider;
    }

    public BotMetrics getBotMetrics() {
        return botMetrics;
    }

    public AudioPlayerManager getAudioPlayerManager() {
        return audioPlayerManager;
    }

    public Ratelimiter getRatelimiter() {
        return ratelimiter;
    }

    //Shutdown hook
    private Runnable createShutdownHook() {
        return FredBoatAgent::shutdown;
    }
}
