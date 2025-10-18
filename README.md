# keda-demo

This demo project is meant to showcase using [KEDA](https://keda.sh/) to perform autoscaling of a Spring Boot application using [Micrometer](https://micrometer.io/) exposing HTTP metrics using the [Micrometer Prometheus](https://docs.micrometer.io/micrometer/reference/implementations/prometheus.html) library by using the KEDA [Prometheus Scaler](https://keda.sh/docs/2.17/scalers/prometheus/).

The demo setup below will walk through the steps of how you can run this on your laptop using [MicroK8s](https://microk8s.io/), a lightweight Kubernetes distribution. I used [Podman](https://podman.io/) for building a container image, but you could also use Docker.


## Setup

1. Install & Setup [MicroK8s](https://microk8s.io/#install-microk8s)
2. Enable observability if not already enabled
   ```bash
   microk8s enable observability
   ```
3. Install [KEDA](https://keda.sh/docs/2.17/deploy/#installing-3) on the MicroK8s cluster
4. MicroK8s has a local container registry that can be enabled and you can push to it from a local container build.  

   a. First ensure the registry is enabled:

    ```bash
    microk8s enable registry
    ```
   b. Next determine the IP address of the node, by running the following and grabbing the Internal IP

    ```bash
    microk8s kubectl get nodes -o wide

    NAME          STATUS   ROLES    AGE   VERSION    INTERNAL-IP    EXTERNAL-IP   OS-IMAGE             KERNEL-VERSION       CONTAINER-RUNTIME
    microk8s-vm   Ready    <none>   8d    v1.28.15   192.168.64.3   <none>        Ubuntu 22.04.5 LTS   5.15.0-156-generic   containerd://1.6.28
    ```

   c. Now, setup podman to enable pushing to the registry running at the IP address from the prior step at port 32000

    ```bash
    ➜  podman machine ssh --username root
    Connecting to vm podman-machine-default. To close connection, use `~.` or `exit`

    root@localhost:~# vi /etc/containers/registries.conf
    ```

    Add the following entry (replace IP address appropriately)

    ```conf
    [[registry]]
    location = "192.168.64.3:32000"
    insecure = true
    ```

   d. Then stop / start the podman machine


## Running the Demo

1. Build the application and container image, and push the image to the local registry using the script
```bash
./build.sh
```

2. Deploy the application to the MicroK8s cluster
```bash
microk8s kubectl apply -f k8s/app.yaml
```

3. To simplify the demo, we won't bother with Ingress and just use port forwarding to the Service, you can test it out at this point.

   a. In one terminal window, use port-forward
    ```bash
    microk8s kubectl port-forward service/keda-demo-service 8080:8080
    ```
    
   b. In another terminal window, test it out (or load in browser - [http://localhost:8080/test](http://localhost:8080/test))
    ```bash
    ➜  curl http://localhost:8080/test
    🚀
    ``` 

4. Next, deploy the [ServiceMonitor](https://doc.crds.dev/github.com/prometheus-operator/kube-prometheus/monitoring.coreos.com/ServiceMonitor/v1@v0.7.0) Custom Resource to the `observability` namespace, which will provide details to Prometheus on how to collect metrics from the application.

```bash
microk8s kubectl apply -f k8s/monitoring.yaml -n observability
```

5. Check in Prometheus for the new metrics, first setup port-fowarding 

```bash
microk8s kubectl port-forward  -n observability service/kube-prom-stack-kube-prome-prometheus 9090:9090
```

6. Then load [http://localhost:9090](http://localhost:9090)


## Other Tidbits

To View the MicroK8s Web-UI, use the following commands

```bash
microk8s dashboard-proxy
```
