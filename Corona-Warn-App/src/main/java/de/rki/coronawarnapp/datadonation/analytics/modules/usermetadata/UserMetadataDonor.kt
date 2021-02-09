package de.rki.coronawarnapp.datadonation.analytics.modules.usermetadata

import de.rki.coronawarnapp.datadonation.analytics.AnalyticsSettings
import de.rki.coronawarnapp.datadonation.analytics.modules.DonorModule
import de.rki.coronawarnapp.server.protocols.internal.ppdd.PpaData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserMetadataDonor @Inject constructor(
    private val analyticsSettings: AnalyticsSettings
) : DonorModule {

    override suspend fun beginDonation(request: DonorModule.Request): DonorModule.Contribution {
        val userMetadata = PpaData.PPAUserMetadata.newBuilder()
            .setAgeGroup(analyticsSettings.userInfoAgeGroup.value)
            .setFederalState(analyticsSettings.userInfoFederalState.value)
            .setAdministrativeUnit(analyticsSettings.userInfoDistrict.value)
            .build()

        return UserMetadataContribution(
            contributionProto = userMetadata
        )
    }

    data class UserMetadataContribution(
        val contributionProto: PpaData.PPAUserMetadata
    ) : DonorModule.Contribution {
        override suspend fun injectData(protobufContainer: PpaData.PPADataAndroid.Builder) {
            protobufContainer.userMetadata = contributionProto
        }

        override suspend fun finishDonation(successful: Boolean) {
            // No post processing needed for User Metadata
        }
    }
}
