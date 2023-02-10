/*
 * Copyright @ 2018 - present 8x8, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jitsi.videobridge.health

import org.ice4j.ice.harvest.MappingCandidateHarvesters
import org.jitsi.health.HealthCheckService
import org.jitsi.health.HealthChecker
import org.jitsi.health.Result
import org.jitsi.videobridge.health.config.HealthConfig.Companion.config
import org.jitsi.videobridge.ice.Harvesters

class JvbHealthChecker : HealthCheckService {
    private val healthChecker = HealthChecker(
        config.interval,
        config.timeout,
        config.maxCheckDuration,
        config.stickyFailures,
        healthCheckFunc = ::check
    )

    fun start() = healthChecker.start()
    fun stop() = healthChecker.stop()

    private fun check(): Result {
        if (MappingCandidateHarvesters.stunDiscoveryFailed) {
            return Result(success = false, message = "Address discovery through STUN failed")
        }
        if (!Harvesters.isHealthy()) {
            return Result(success = false, message = "Failed to bind single-port")
        }

        // TODO: check if XmppConnection is configured and connected.

        return Result(success = true)
    }

    override val result: Result
        get() = healthChecker.result
}
