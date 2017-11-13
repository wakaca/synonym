package cn.huimin.elasticsearch.index.analysis;

import org.apache.lucene.analysis.synonym.SynonymMap;

import java.io.Reader;

/**
 * Created by Administrator on 2017/9/15.
 */
public interface SynonymFile {
    public SynonymMap reloadSynonymMap();

    public boolean isNeedReloadSynonymMap();

    public Reader getReader();

    public  String getLocation();
}
