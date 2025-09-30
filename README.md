

## Setup

1. Setup [MicroK8s](https://microk8s.io/#install-microk8s)
2. Confirm Multipass 
    ```bash
    multipass list
    Name                    State             IPv4             Image
    microk8s-vm             Running           192.168.64.3     Ubuntu 22.04 LTS
    ```
2. Enable Prometheus

   ```bash
   microk8s enable prometheus
   ```
3. Setup [KEDA](https://keda.sh/docs/2.17/deploy/#installing-3)

4. Setup podman to use local registry

```bash
➜  keda-demo git:(main) ✗ podman machine ssh --username root
Connecting to vm podman-machine-default. To close connection, use `~.` or `exit`
Fedora CoreOS 41.20250215.3.0
Tracker: https://github.com/coreos/fedora-coreos-tracker
Discuss: https://discussion.fedoraproject.org/tag/coreos

Last login: Sun Sep 28 12:33:13 2025
root@localhost:~# vi /etc/containers/registries.conf
```

Added the following
```conf
[[registry]]
location = "192.168.64.3:32000"
insecure = true
```

restart podman



View Web-UI. 

```bash
microk8s dashboard-proxy
```

```
microk8s kubectl port-forward -n observability pod/prometheus-kube-prom-stack-kube-prome-prometheus-0 9090:9090
```

http://localhost:9090




## Setup

1. 

```bash
microk8s kubectl apply -f k8s.yaml
```

```bash
microk8s kubectl port-forward service/http-app-service 8080:80
```


```bash
microk8s kubectl port-forward  -n observability service/kube-prom-stack-kube-prome-prometheus 9090:9090
```