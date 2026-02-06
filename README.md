# keda-demo

This demo project is meant to showcase using [KEDA](https://keda.sh/) to perform autoscaling of a Spring Boot application using [Micrometer](https://micrometer.io/) exposing HTTP metrics using the [Micrometer Prometheus](https://docs.micrometer.io/micrometer/reference/implementations/prometheus.html) library by using the KEDA [Prometheus Scaler](https://keda.sh/docs/2.17/scalers/prometheus/).

The demo setup below will walk through the steps of how you can run this on your laptop using [kind](https://kind.sigs.k8s.io/), a tool for running local Kubernetes clusters using Docker.


## Setup

Prerequisite: Docker is already installed.

1. Install & Setup [kind](https://kind.sigs.k8s.io/docs/user/quick-start/), `kubectl`, and `helm`
   ```bash
   brew install kind kubectl helm
   ```
2. Setup kind with a local container registry, script in repo
   ```bash
   ./kind-setup-with-registry.sh
   ```
3. Install Prometheus via [community helm charts](https://github.com/prometheus-community/helm-charts?tab=readme-ov-file#prometheus-community-kubernetes-helm-charts)
   ```bash
   helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
   helm repo update
   # Create the namespace
   kubectl create namespace monitoring

   helm install monitoring prometheus-community/kube-prometheus-stack \
   --namespace monitoring \
   --set prometheus.prometheusSpec.podMonitorSelectorNilUsesHelmValues=false \
   --set prometheus.prometheusSpec.serviceMonitorSelectorNilUsesHelmValues=false
   ```
4. Install [KEDA](https://keda.sh/docs/2.18/deploy/#installing) 
   ```bash
   helm repo add kedacore https://kedacore.github.io/charts  
   helm repo update
   helm install keda kedacore/keda --namespace keda --create-namespace
   ```

## Running the Demo

1. Build the application and container image, and push the image to the local registry using the script
```bash
./build.sh
```

2. Deploy the application to the kind cluster
```bash
kubectl apply -f k8s/app.yaml
```

3. To simplify the demo, we won't bother with Ingress and just use port forwarding to the Service, you can test it out at this point.

   a. In one terminal window, use port-forward
    ```bash
   kubectl port-forward service/keda-demo-service 8080:8080
    ```
    
   b. In another terminal window, test it out (or load in browser - [http://localhost:8080/test](http://localhost:8080/test))
    ```bash
    ➜  curl http://localhost:8080/customer/12345/accounts
    {"customerId":12345,"accounts":[{"account":{"accountId":"12345000","accountType":"C"}}]}
    ``` 

4. Next, deploy the [ServiceMonitor](https://doc.crds.dev/github.com/prometheus-operator/kube-prometheus/monitoring.coreos.com/ServiceMonitor/v1@v0.7.0) Custom Resource to the `monitoring` namespace, which will provide details to Prometheus on how to collect metrics from the application.

```bash
kubectl apply -f k8s/monitoring.yaml -n monitoring
```

5. Check in Prometheus for the new metrics, first setup port-fowarding 

```bash
kubectl port-forward  -n monitoring service/monitoring-kube-prometheus-prometheus 9090:9090
```

6. Then load [http://localhost:9090](http://localhost:9090) and browse to the 'Graph' tab and enter the below query. Hit the `/test` endpoint a few more times to make sure it's tracking the metrics
```promql
sum(rate(http_server_requests_seconds_count{service='keda-demo-service', uri='/test'}[30s]))
```

7. Next, apply the KEDA ScaledObject

```bash
kubectl apply -f k8s/scale-config.yaml
```

8. Ensure that the HPA was created by KEDA:

```bash
➜  kubectl get hpa
NAME                              REFERENCE              TARGETS              MINPODS   MAXPODS   REPLICAS   AGE
keda-hpa-keda-demo-scaledobject   Deployment/keda-demo   <unknown>/10 (avg)   1         6         0          6s
```

8. Ensure that [Artillery](https://www.artillery.io/docs/get-started/get-artillery) is installed, and then run the load test

```bash
./load-test.sh
```

9. Monitor the load and HPA/pods scaling up

## Other Tidbits

To setup [K9s](https://k9scli.io/) to view kind cluster

1. `brew install derailed/k9s/k9s`

### Spring Documentation

* [HTTP Server Requests Observation](https://docs.spring.io/spring-framework/reference/integration/observability.html#observability.config.conventions)
