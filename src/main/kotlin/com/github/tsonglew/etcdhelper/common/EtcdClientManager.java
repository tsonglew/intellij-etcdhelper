package com.github.tsonglew.etcdhelper.common;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tsonglew
 */
public class EtcdClientManager {

    private static final ConcurrentHashMap<String, EtcdClient> CONN_MAP = new ConcurrentHashMap<>();

    public static EtcdClient addConn(String endpoints, String user, String password) {
        System.out.println("add etcd connection: ");
        System.out.println("endpoints: " + endpoints);
        System.out.println("user: "+ user);
        System.out.println("password: "+ password);
        CONN_MAP.computeIfAbsent(endpoints, k -> {
            var c = new EtcdClient();
            c.init(k.split(","), null, null);
            return c;
        });
        return CONN_MAP.get(endpoints);
    }

    public static EtcdClient getConn() {
        return CONN_MAP.entrySet().stream().findAny().get().getValue();
    }
}
