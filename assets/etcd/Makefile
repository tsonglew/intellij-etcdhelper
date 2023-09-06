.PHONY: run members

run:
	docker compose up -d
members:
	etcdctl --endpoints=https://127.0.0.1:42379 --user root --password hillstone --cacert assets/etcd/cfssl/ca.pem --key assets/etcd/cfssl/client-key.pem --cert assets/etcd/cfssl/client.pem member list
