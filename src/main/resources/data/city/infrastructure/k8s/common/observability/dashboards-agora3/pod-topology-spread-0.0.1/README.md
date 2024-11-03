### PromQL to monitor Pod Topology Spread
#### output # of pod per zone
```
sum by(label_topology_kubernetes_io_zone) ((count by(node) (kube_pod_info{node!=""})) + on (node) group_left(label_topology_kubernetes_io_zone) (0 * kube_node_labels))
```

#### group zone and ns
```
sum by(label_topology_kubernetes_io_zone,namespace) ((count by(node,namespace) (kube_pod_info{node!=""})) + on (node) group_left(label_topology_kubernetes_io_zone) (0 * kube_node_labels))
```

#### exclude fargate
```
sum by(label_topology_kubernetes_io_zone,label_alpha_eksctl_io_instance_id) ((count by(node) (kube_pod_info{node!=""})) + on (node) group_left(label_topology_kubernetes_io_zone,label_alpha_eksctl_io_instance_id) (0 * kube_node_labels{label_alpha_eksctl_io_instance_id!=""}))
```
