# Grafana Dashboards for FLuxCD

## Source
  - [Github](https://github.com/fluxcd/flux2/tree/f58b82fb4ed53939dc1c9d7e370deab885527368/manifests/monitoring/monitoring-config/dashboards)

## Manual changes

### logs.json

  - manual modifications to dashboard "expr" fields
  - manual modifications to dashboard variables: use a `Text box` instead of Loki queries of label values
```diff
diff -rup ./logs-original.json ./logs.json
--- ./logs-original.json       2023-07-12 11:57:41.810014205 +0900
+++ ./logs.json    2023-07-12 11:50:44.605080338 +0900
@@ -128,7 +128,7 @@
       "targets": [
         {
           "datasource": "${DS_LOKI}",
-          "expr": "sum(count_over_time({namespace=~\"$namespace\", stream=~\"$stream\", app =~\"$controller\"} | json | __error__!=\"JSONParserErr\" | level=~\"$level\" |= \"$query\" [$__interval]))",
+          "expr": "sum(count_over_time({namespace=\"flux-system\"} | json | __error__ != `JSONParserErr` | level =~ `$level` | name =~ `$name` | controller =~ `$controller` |= `$query` [$__interval]))",
           "instant": false,
           "legendFormat": "Log count",
           "range": true,
@@ -160,7 +160,7 @@
       "targets": [
         {
           "datasource": "${DS_LOKI}",
-          "expr": "{namespace=~\"$namespace\", stream=~\"$stream\", app =~\"$controller\"} | json | __error__!=\"JSONParserErr\" | level=~\"$level\" |= \"$query\"",
+          "expr": "{namespace=\"flux-system\"} | json | __error__ != `JSONParserErr` | level =~ `$level` | name =~ `$name` | controller =~ `$controller` |= `$query`",
           "refId": "A"
         }
       ],
@@ -230,79 +230,27 @@
         "type": "custom"
       },
       {
-        "allValue": ".+",
         "current": {
-          "selected": true,
-          "text": [
-            "All"
-          ],
-          "value": [
-            "$__all"
-          ]
+          "selected": false,
+          "text": ".*",
+          "value": ".*"
         },
-        "datasource": "${DS_LOKI}",
-        "definition": "label_values(app)",
         "hide": 0,
-        "includeAll": true,
-        "multi": true,
         "name": "controller",
-        "options": [],
-        "query": "label_values(app)",
-        "refresh": 1,
-        "regex": "",
+        "options": [
+          {
+            "selected": true,
+            "text": ".*",
+            "value": ".*"
+          }
+        ],
+        "query": ".*",
         "skipUrlSync": false,
-        "sort": 0,
-        "type": "query"
+        "type": "textbox"
       },
       {
-        "allValue": ".+",
         "current": {
           "selected": true,
-          "text": [
-            "flux-system"
-          ],
-          "value": [
-            "flux-system"
-          ]
-        },
-        "datasource": "${DS_LOKI}",
-        "definition": "label_values(namespace)",
-        "hide": 0,
-        "includeAll": true,
-        "multi": true,
-        "name": "namespace",
-        "options": [],
-        "query": "label_values(namespace)",
-        "refresh": 1,
-        "regex": "",
-        "skipUrlSync": false,
-        "sort": 0,
-        "type": "query"
-      },
-      {
-        "allValue": ".+",
-        "current": {
-          "selected": false,
-          "text": "All",
-          "value": "$__all"
-        },
-        "datasource": "${DS_LOKI}",
-        "definition": "label_values(stream)",
-        "hide": 0,
-        "includeAll": true,
-        "multi": true,
-        "name": "stream",
-        "options": [],
-        "query": "label_values(stream)",
-        "refresh": 1,
-        "regex": "",
-        "skipUrlSync": false,
-        "sort": 0,
-        "type": "query"
-      },
-      {
-        "current": {
-          "selected": false,
           "text": "Loki",
           "value": "Loki"
         },
@@ -317,6 +265,25 @@
         "regex": "",
         "skipUrlSync": false,
         "type": "datasource"
+      },
+      {
+        "current": {
+          "selected": false,
+          "text": ".*",
+          "value": ".*"
+        },
+        "hide": 0,
+        "name": "name",
+        "options": [
+          {
+            "selected": true,
+            "text": ".*",
+            "value": ".*"
+          }
+        ],
+        "query": ".*",
+        "skipUrlSync": false,
+        "type": "textbox"
       }
     ]
   },
```

## Version History
  - agora1: Import dashboards from source
