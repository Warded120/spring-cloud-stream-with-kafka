package com.ihren.processor.annotation;

import com.ihren.processor.config.KafkaAdminClientConfig;
import com.ihren.processor.config.WireMockConfig;
import com.ihren.processor.initializer.KafkaInitializer;
import com.ihren.processor.ProcessorApplication;
import com.ihren.processor.config.KafkaConsumerConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ActiveProfiles("test")
@SpringBootTest(
        classes = ProcessorApplication.class
)
@ContextConfiguration(
        initializers = KafkaInitializer.class
)
@TestExecutionListeners(
        listeners = KafkaInitializer.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@DirtiesContext(
        classMode = DirtiesContext.ClassMode.BEFORE_CLASS
)
@Import({
        KafkaConsumerConfig.class,
        KafkaAdminClientConfig.class,
        WireMockConfig.class
})
@ExtendWith(OutputCaptureExtension.class)
@AutoConfigureWireMock
@AutoConfigureMockMvc
public @interface IntegrationTest {
}
