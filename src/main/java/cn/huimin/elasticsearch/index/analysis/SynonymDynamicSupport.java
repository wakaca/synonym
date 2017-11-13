package cn.huimin.elasticsearch.index.analysis;

import org.apache.lucene.analysis.synonym.SynonymMap;

/**
 * 同义词更新接口
 */
public interface SynonymDynamicSupport {
    public void update(SynonymMap synonymMap);
}
