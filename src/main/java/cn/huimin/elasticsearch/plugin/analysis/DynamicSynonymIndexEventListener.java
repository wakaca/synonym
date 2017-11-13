package cn.huimin.elasticsearch.plugin.analysis;

import cn.huimin.elasticsearch.index.analysis.DynamicSynonymTokenFilterFactory;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.index.IndexService;
import org.elasticsearch.index.shard.IndexEventListener;
import org.elasticsearch.indices.cluster.IndicesClusterStateService;

/**
 * 索引事件监听
 */
public class DynamicSynonymIndexEventListener implements IndexEventListener{

    public static Logger logger = ESLoggerFactory.getLogger("dynamic-synonym");

    /** 索引删除事件 */
    @Override
    public void beforeIndexRemoved(IndexService indexService, IndicesClusterStateService.AllocatedIndices.IndexRemovalReason reason) {
        logger.info("beforeIndexRemoved ! indexName:{}",indexService.index().getName());
        DynamicSynonymTokenFilterFactory.closeIndDynamicSynonym(indexService.index().getName());
    }

}
