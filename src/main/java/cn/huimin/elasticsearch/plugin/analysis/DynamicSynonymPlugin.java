package cn.huimin.elasticsearch.plugin.analysis;

import cn.huimin.elasticsearch.index.analysis.DynamicSynonymTokenFilterFactory;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.component.LifecycleComponent;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexModule;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AnalysisRegistry;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.indices.analysis.AnalysisModule;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.watcher.ResourceWatcherService;

import java.io.IOException;
import java.util.*;

import static java.util.Collections.singletonList;


public class DynamicSynonymPlugin extends Plugin implements AnalysisPlugin {

    public static Logger logger = ESLoggerFactory.getLogger("dynamic-synonym");

    private DynamicSynonymComponent pluginComponent = new DynamicSynonymComponent();

    // 1.创建组件
    @Override
    public Collection<Object> createComponents(Client client,
                                               ClusterService clusterService,
                                               ThreadPool threadPool,
                                               ResourceWatcherService resourceWatcherService,
                                               ScriptService scriptService,
                                               NamedXContentRegistry xContentRegistry) {
        Collection<Object> components = new ArrayList<>();
        components.add(pluginComponent);
        return components;
    }

    // 2.注入1创建的组件给目标类,在目标类中初始化
    @Override
    public Collection<Class<? extends LifecycleComponent>> getGuiceServiceClasses() {
        return singletonList(DynamicSynonymGuiceService.class);
    }

    // 3.添加listener
    public void onIndexModule(IndexModule indexModule) {
        indexModule.addIndexEventListener(new DynamicSynonymIndexEventListener());
    }

    // 4.释放资源
    public void close() {
        logger.info("DynamicSynonymPlugin close...");
        DynamicSynonymTokenFilterFactory.closeDynamicSynonym();
    }

    // 5.加载插件
    @Override
    public Map<String, AnalysisModule.AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
        Map<String, AnalysisModule.AnalysisProvider<TokenFilterFactory>> tokenFilters =
                new HashMap<>();

        tokenFilters.put("dynamic_synonym", new AnalysisModule.AnalysisProvider<TokenFilterFactory>() {
            @Override
            public TokenFilterFactory get(IndexSettings indexSettings, Environment environment, String name, Settings settings) throws IOException {
                return new DynamicSynonymTokenFilterFactory(indexSettings, environment, pluginComponent.getAnalysisRegistry(), name, settings);
            }

            @Override
            public boolean requiresAnalysisSettings() { return  true; }
        });

        return tokenFilters;
    }

    /** 插件生命周期内的组件 */
    class DynamicSynonymComponent {
        private AnalysisRegistry analysisRegistry;
        public AnalysisRegistry getAnalysisRegistry() {
            return analysisRegistry;
        }

        /**
         * 该组件被传递给生命周期内的bean 初始化时调用，保存同义词初始化需要用到的analysisRegistry */
        public void setAnalysisRegistry(AnalysisRegistry analysisRegistry) {
            this.analysisRegistry = analysisRegistry;
        }

    }

}
