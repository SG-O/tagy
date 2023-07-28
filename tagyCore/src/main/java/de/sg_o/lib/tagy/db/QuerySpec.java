package de.sg_o.lib.tagy.db;

import com.couchbase.lite.From;
import com.couchbase.lite.Query;

public interface QuerySpec {
    Query buildQuery(From str);

}
