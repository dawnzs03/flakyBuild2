package io.quarkus.kubernetes.deployment;

import java.util.Optional;

import io.dekorate.kubernetes.annotation.JobCompletionMode;
import io.dekorate.kubernetes.annotation.JobRestartPolicy;
import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class JobConfig {

    /**
     * Specifies the maximum desired number of pods the job should run at any given time.
     *
     * @return The desired number of pods.
     */
    @ConfigItem
    Optional<Integer> parallelism;

    /**
     * Specifies the desired number of successfully finished pods the job should be run with.
     *
     * @return The desired number of successfully finished pods.
     */
    @ConfigItem
    Optional<Integer> completions;

    /**
     * CompletionMode specifies how Pod completions are tracked.
     *
     * @return the completion mode.
     */
    @ConfigItem(defaultValue = "NonIndexed")
    JobCompletionMode completionMode;

    /**
     * Specifies the number of retries before marking this job failed.
     *
     * @return The back-off limit.
     */
    @ConfigItem
    Optional<Integer> backoffLimit;

    /**
     * Specifies the duration in seconds relative to the startTime that the job may be continuously active before the system
     * tries to terminate it; value must be positive integer.
     *
     * @return the active deadline seconds.
     */
    @ConfigItem
    Optional<Long> activeDeadlineSeconds;

    /**
     * Limits the lifetime of a Job that has finished execution (either Complete or Failed). If this
     * field is set, ttlSecondsAfterFinished after the Job finishes, it is eligible to be automatically deleted.
     *
     * @return the time to live seconds after finished.
     */
    @ConfigItem
    Optional<Integer> ttlSecondsAfterFinished;

    /**
     * Suspend specifies whether the Job controller should create Pods or not.
     *
     * @return the suspend job attribute.
     */
    @ConfigItem(defaultValue = "false")
    boolean suspend;

    /**
     * Restart policy when the job container fails.
     *
     * @return the restart policy.
     */
    @ConfigItem(defaultValue = "OnFailure")
    JobRestartPolicy restartPolicy;

}
