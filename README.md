## Пример Kubernetes оператора

Оператор gateway-k8s-operator обновляет ConfigMap сервиса gateway, при добавлении новых CR GatewayRoute, Microfrontend.

После обновления ConfigMap (и обновления config.yml в поде gateway), оператор отправляет запрос в gateway по эндпойнту /actuator/refresh, 
для того чтобы gateway перечитал обновлённую конфигурацию из config.yml.

Для того чтобы локально в Kubernetes развернуть сервисы необходимо:
- установить Docker, Helm, kubectl, Maven, JDK 17
- развернуть локально кластер Kubernetes (например, с помощью утилиты [kind](https://kind.sigs.k8s.io/docs/user/quick-start/))
- дополнительно можно установить [Kubernetes Dashboard](https://kubernetes.io/docs/tasks/access-application-cluster/web-ui-dashboard/) (UI для взаимодействия с k8s)
- собрать с помощью mvn clean install сервисы gateway и gateway-operator
- создать docker образы командами:
    ```
    docker build -t gateway:latest --build-arg "JAR_FILE=target/gateway-1.0.0-SNAPSHOT-exec.jar" .
    docker build -t gateway-k8s-operator:latest --build-arg "JAR_FILE=target/gateway-k8s-operator-1.0.0-SNAPSHOT.jar" .
    ```
- загрузить образы в кластер kind:
    ```
    kind load docker-image gateway:latest --name kind
    kind load docker-image gateway-k8s-operator:latest --name kind
    ```
- далее выполнить шаги описанные в ./charts/FIRST_INSTALL.md

Запросы к gateway после прокидования портов можно выполнить по http://localhost:8080
/api/configuration - получение списка настроек микрофронтенда
/actuator/gateway/routes - получение списка роутов