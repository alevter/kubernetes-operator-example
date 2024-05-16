### Create namespace and labels

```bash
kubectl create namespace local-ns
kubectl label ns local-ns operator.example.ru/name=gateway-k8s-operator
```

### Create ConfigMap for gateway (will be updated by operators)

```bash
kubectl create -f ./first-install/gateway/config.yaml -n local-ns
```

### Create Cluster resources and cluster roles

```bash
helm upgrade -i --namespace local-ns \
    --values ./cluster/values.yaml \
    helm-cluster-objects \
    ./cluster
```

### Install gateway

```bash
helm upgrade -i --namespace local-ns \
    --atomic \
    --values ./gateway/values.yaml \
    --set "image.build=latest" \
    --set fullnameOverride=gateway \
    gateway \
    ./gateway
```

### Install gateway-k8s-operator

```bash
helm upgrade -i --namespace local-ns \
    --atomic \
    --values ./gateway-operator/values.yaml \
    --set "image.build=latest" \
    --set fullnameOverride=gateway-k8s-operator \
    gateway-k8s-operator \
    ./gateway-operator
```

### Example create CR:

```bash
kubectl apply -f ./cr/1/gateway-route.yml
kubectl apply -f ./cr/1/gateway-microfrontend.yml
```

kubectl -n local-ns port-forward gateway-6967d67f67-5pdmc 8080:8080
http://localhost:8080/api/configuration
http://localhost:8080/actuator/gateway/routes
