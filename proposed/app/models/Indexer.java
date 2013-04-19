//package indexing;
package index;

import com.github.cleverage.elasticsearch.Index;
import com.github.cleverage.elasticsearch.Indexable;
import com.github.cleverage.elasticsearch.IndexResults;
import com.github.cleverage.elasticsearch.annotations.IndexType;

import java.util.HashMap;
import java.util.Map;

/**
 */
@IndexType(name = "indexer")
public class Indexer extends Index {

		public String  type; // R or S (resource or stream)
    public Long    id;
    public String  url;
    public String  label;
    public String  description;

    // Find method static for request
    public static Finder<Indexer> find = new Finder<Indexer>(Indexer.class);

    @Override
    public Map toIndex() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", id.toString());
        map.put("type", type);
        map.put("url", url);
        map.put("label", label);
        map.put("description", description);
        return map;
    }

    @Override
    public Indexable fromIndex(Map map) {
        this.id   = Long.valueOf((String)map.get("id"));
        this.type  = (String) map.get("type");
        this.url  = (String) map.get("url");
        this.label= (String) map.get("label");
        this.description = (String) map.get("description");
        return this;
    }
}

