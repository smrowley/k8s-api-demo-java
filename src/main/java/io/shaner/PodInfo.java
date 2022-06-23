package io.shaner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Config;
import io.quarkus.logging.Log;
import io.shaner.dto.Pod;

@Path("/podinfo")
public class PodInfo {
    
    @ConfigProperty(name = "NAMESPACE")
    private String namespace;

    private CoreV1Api api;

    @PostConstruct
    public void setup() throws IOException {
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

        api = new CoreV1Api();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Pod> getPodInfo(@QueryParam("labels") @DefaultValue("") String labels) {
        
        if (labels.isEmpty()) {
            labels = null;
        }

        try {
            Log.info("Performing pod lookup");
            V1PodList podList = api.listNamespacedPod(namespace, "true", false, null, null, labels, null, null, null, 10, null);
            Log.debug(podList.toString());
            Log.info("Pods looked up");
            Log.info("Num pods: " + podList.getItems().size());
            List<Pod> list = podList.getItems().stream()
                .peek(v1Pod -> Log.info(v1Pod.getMetadata().getName()))
                .map(v1Pod -> new Pod(v1Pod.getMetadata().getName(), v1Pod.getStatus().getPodIP()))
                .collect(Collectors.toList());
            Log.info("Pods mapped");

            return list;
        }
        catch (ApiException e) {
            return new ArrayList<>();
        }
    }

}
