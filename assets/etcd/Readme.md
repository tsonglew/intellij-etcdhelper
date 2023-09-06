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

```bash
mkdir cfssl
cd cfssl
cfssl print-defaults config > ca-config.json
cfssl print-defaults csr > ca-csr.json
```

###

# Refer

https://blog.try-except.com/technology/docker_etcd_cluster_ssl.html
