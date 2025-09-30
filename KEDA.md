
Installing KEDA

.Kubernetes Event-driven Autoscaling (KEDA) - Application autoscaling made simple.

Get started by deploying Scaled Objects to your cluster:
    - Information about Scaled Objects : https://keda.sh/docs/latest/concepts/
    - Samples: https://github.com/kedacore/samples

Get information about the deployed ScaledObjects:
  kubectl get scaledobject [--namespace <namespace>]

Get details about a deployed ScaledObject:
  kubectl describe scaledobject <scaled-object-name> [--namespace <namespace>]

Get information about the deployed ScaledObjects:
  kubectl get triggerauthentication [--namespace <namespace>]

Get details about a deployed ScaledObject:
  kubectl describe triggerauthentication <trigger-authentication-name> [--namespace <namespace>]

Get an overview of the Horizontal Pod Autoscalers (HPA) that KEDA is using behind the scenes:
  kubectl get hpa [--all-namespaces] [--namespace <namespace>]

Learn more about KEDA:
- Documentation: https://keda.sh/
- Support: https://keda.sh/support/
- File an issue: https://github.com/kedacore/keda/issues/new/choose



ServiceMonitor/PodMonitor (Prometheus Operator): If you are using the Prometheus Operator (often included with kube-prometheus-stack), you can use custom resources like ServiceMonitor and PodMonitor to define scrape configurations in a more Kubernetes-native way. These resources automatically generate the corresponding scrape_configs for Prometheus.
Code

```yaml
    apiVersion: monitoring.coreos.com/v1
    kind: ServiceMonitor
    metadata:
      name: my-app-servicemonitor
      labels:
        release: prometheus-stack # Match the release label of your Prometheus deployment
    spec:
      selector:
        matchLabels:
          app: my-app # Select services with this label
      endpoints:
        - port: http-metrics # Name of the port in the Service definition
          path: /metrics
```
