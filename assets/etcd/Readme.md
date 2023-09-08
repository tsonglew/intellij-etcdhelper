# Deploy a single node etcd cluster with SSL/TLS enabled

## Generate SSL/TLS certificates

### Download cfssl

#### Linux

```bash
mkdir ~/bin
curl -s -L -o ~/bin/cfssl https://pkg.cfssl.org/R1.2/cfssl_linux-amd64
curl -s -L -o ~/bin/cfssljson https://pkg.cfssl.org/R1.2/cfssljson_linux-amd64
chmod +x ~/bin/{cfssl,cfssljson}
export PATH=$PATH:~/bin
```

#### Mac

```bash
brew install cfssl
```

### Generate CA

#### Create a directory for storing certificates

```bash
mkdir cfssl
cd cfssl
cfssl print-defaults config > ca-config.json
cfssl print-defaults csr > ca-csr.json
```

#### Update CA config

update ca-config.json using [ca-config.json](cfssl/ca-config.json)

### Update CA CSR config

update ca-csr.json using [ca-csr.json](cfssl/ca-csr.json)

### Self-sign CA

```bash
cfssl gencert -initca ca-csr.json | cfssljson -bare ca -
```

### Generate server certificate

#### Generate server-side CSR config

```bash
cfssl print-defaults csr > server.json
```

#### Update server-side CSR config

update server.json using [server.json](cfssl/server.json)

#### Generate server-side certificate

```bash
cfssl gencert -ca=ca.pem -ca-key=ca-key.pem -config=ca-config.json -profile=server server.json | cfssljson -bare server
```

### Generate P2P certificate

#### Generate P2P CSR config

```bash
cfssl print-defaults csr > node1.json
```

add "node1" to host section in [server.json](cfssl/server.json)

#### Generate P2P certificate

```bash
cfssl gencert -ca=ca.pem -ca-key=ca-key.pem -config=ca-config.json -profile=peer node1.json | cfssljson -bare node1
```

### Generate client certificate

#### Generate client CSR config

```bash
cfssl print-defaults csr > client.json
```

update client.json using [client.json](cfssl/client.json)

#### Generate client certificate

```bash
cfssl gencert -ca=ca.pem -ca-key=ca-key.pem -config=ca-config.json -profile=client client.json | cfssljson -bare client
```

## Deploy using docker

### Create docker-compose.yml

using [docker-compose.yml](docker-compose.yml)

### Start container

```bash
docker-compose up -d
```

test connection

```bash
docker exec -it <container-name> etcdctl --user root --password hillstone --cacert /opt/bitnami/etcd/conf/ca.pem --key /opt/bitnami/etcd/client-key.pem --cert /opt/bitnami/etcd/client.pem member list
```

# Refer

https://blog.try-except.com/technology/docker_etcd_cluster_ssl.html
