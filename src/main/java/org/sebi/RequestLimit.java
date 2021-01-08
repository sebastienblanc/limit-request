package org.sebi;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import java.util.Collections;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirementsBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;

@CommandLine.Command
public class RequestLimit implements Runnable {

    private final KubernetesClient kubernetesClient;

    @Parameters(index = "0", description = "The Deployment", defaultValue = "myDeployment")
    private String deployment;

    @Option(names = "--cpu-limit", paramLabel = "CPULIMIT", description = "CPU Limit")
    String cpuLimit;

    public RequestLimit(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    @Override
    public void run() {
        Collections.singletonMap("cpu", cpuLimit);
        ResourceRequirementsBuilder builder = new ResourceRequirementsBuilder();
        builder.withLimits(Collections.singletonMap("cpu", Quantity.parse(cpuLimit)));

        kubernetesClient.apps().deployments().withName(deployment).edit().editSpec().editTemplate().editSpec()
                .editContainer(0).withNewResourcesLike(builder.build()).endResources().endContainer().endSpec()
                .endTemplate().endSpec().done();
    }

}
