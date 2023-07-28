package de.sg_o.lib.tagy.db;

import com.couchbase.lite.Collection;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Scope;
import org.jetbrains.annotations.NotNull;

public class DbScope {
    @NotNull
    private final Scope scope;
    @NotNull
    private final Database db;

    public DbScope(@NotNull Scope scope, @NotNull Database db) {
        this.scope = scope;
        this.db = db;
    }

    @SuppressWarnings("unused")
    public @NotNull Scope getScope() {
        return scope;
    }

    @SuppressWarnings("unused")
    public @NotNull Database getDb() {
        return db;
    }

    public Collection getCollection(@NotNull String name) {
        try {
            Collection collection = scope.getCollection(name);
            if (collection == null) {
                collection = db.createCollection(name, scope.getName());
            }
            return collection;
        } catch (CouchbaseLiteException e) {
            return null;
        }
    }
}
