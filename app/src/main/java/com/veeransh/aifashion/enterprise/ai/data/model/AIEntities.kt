package com.veeransh.aifashion.enterprise.ai.data.model

import com.veeransh.aifashion.enterprise.data.local.entity.ProductEntity

// --- ENUMS ---
enum class DemandTab { DASHBOARD, FORECAST, MODELS, HISTORY, FORECAST_STUDIO, INVENTORY_PLANNER, RISK_RADAR, HISTORY_LOGS }
enum class ForecastHorizon(val label: String = "", val days: Int = 0) {
    SHORT_TERM("7 Days", 7),
    MEDIUM_TERM("30 Days", 30),
    LONG_TERM("90 Days", 90),
    STRATEGIC("1 Year", 365),
    SEVEN_DAYS("7 Days", 7),
    THIRTY_DAYS("30 Days", 30),
    NINETY_DAYS("90 Days", 90),
    ONE_YEAR("1 Year", 365)
}
enum class ExportType(val displayName: String = "", val width: Int = 1024, val height: Int = 1024, val subfolder: String = "general") {
    WHATSAPP("WhatsApp"),
    INSTAGRAM("Instagram"),
    CATALOGUE("Catalogue"),
    PDF("PDF"),
    EXCEL("Excel"),
    IMAGE("Image"),
    ZIP("ZIP"),
    WHATSAPP_CARD("WhatsApp Card", 1200, 630, "whatsapp"),
    INSTAGRAM_POST("Instagram Post", 1080, 1080, "instagram"),
    FACEBOOK_ADS("Facebook Ads", 1200, 628, "facebook"),
    CATALOGUE_PDF("Catalogue PDF"),
    PURCHASE_ORDER("Purchase Order")
}
enum class FitMode { CONTAIN, COVER, FILL, CENTER_CROP, FIT_CENTER_PADDING, SQUARE, CROP }
enum class MediaSortOption(val displayName: String = "") { NEWEST("Newest"), OLDEST("Oldest"), NAME("Name"), SIZE("Size"), MOST_VIEWED("Most Viewed"), HIGHEST_RATED("Highest Rated") }
enum class AICatalogueExecutionState { IDLE, RUNNING, COMPLETED, ERROR }
enum class AIPricingExecutionState { IDLE, RUNNING, COMPLETED, ERROR }
enum class FolderGrouping { NONE, DATE, SOURCE, PROVIDER }
enum class AiDrapingStyle { TRADITIONAL, BOUTIQUE, HIGH_FASHION, BRIDAL }

enum class ResizePreset(val title: String, val width: Int, val height: Int, val ratioLabel: String) {
    SQUARE("Square Post", 1080, 1080, "1:1"),
    PORTRAIT("Portrait", 1080, 1350, "4:5"),
    STORY("Story / Reel", 1080, 1920, "9:16"),
    LANDSCAPE("Landscape", 1200, 628, "1.91:1"),
    HD("HD Display", 1280, 720, "16:9"),
    FULL_HD("Full HD", 1920, 1080, "16:9"),
    WHATSAPP_STATUS("WA Status", 720, 1280, "9:16");

    val nameVal: String get() = name
}

// --- PRESETS ---
data class DealerPreset(val name: String = "", val tag: String = "")
val PRESET_DEALERS = listOf(DealerPreset("North Region", "High Priority"))

data class DemandProductPreset(val name: String = "", val sku: String = "")
data class InventoryPreset(val name: String = "", val sku: String = "")

// --- DATA CLASSES ---

// Extensions for compatibility with ProductEntity from enterprise package
val ProductEntity.color: String get() = colour
val ProductEntity.colourValue: String get() = colour
val ProductEntity.displayName: String get() = name
val ProductEntity.imageUri: String get() = image
val ProductEntity.priceValue: Double get() = retailPrice
val ProductEntity.mrpValue: Double get() = mrp
val ProductEntity.purchasePriceValue: Double get() = purchasePrice

data class AIPromptEntity(
    val id: Long = 0,
    val promptName: String = "",
    val promptContent: String = "",
    val category: String = "",
    val featureType: String = "",
    val latencyMs: Long = 0,
    val status: String = "",
    val inputPayload: String = ""
)

data class AICatalogueResult(
    val productTitle: String = "",
    val productDescription: String = "",
    val fabricDetails: String = "",
    val occasionSuitability: String = "",
    val suggestedTags: List<String> = emptyList(),
    val pricingInsights: String = "",
    val marketingCopy: String = "",
    val technicalSpecs: String = "",
    val instagramCaption: String = "",
    val whatsappCaption: String = "",
    val seoKeywords: List<String> = emptyList(),
    val shortDescription: String = "",
    val longDescription: String = "",
    val seoDescription: String = "",
    val premiumCatalogueContent: String = "",
    val fabric: String = "",
    val color: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val occasion: String = "",
    val resultId: String = "",
    val isFavorite: Boolean = false,
    val headerTagline: String = "",
    val sampleFabric: String = "",
    val sampleColor: String = "",
    val sampleDesignDetails: String = "",
    val facebookCaption: String = "",
    val whatsappPromotionText: String = "",
    val dealerMarketingText: String = ""
)

data class AIPricingResult(
    val retailPrice: Double = 0.0,
    val wholesalePrice: Double = 0.0,
    val dealerPrice: Double = 0.0,
    val suggestedMarginPct: Double = 0.0,
    val pricingRationale: String = "",
    val costPrice: Double = 0.0,
    val fabricType: String = "",
    val basePrice: Double = 0.0,
    val distributorPrice: Double = 0.0,
    val premiumPrice: Double = 0.0,
    val recommendedMargin: Double = 0.0,
    val profitPercentage: Double = 0.0,
    val discountLimit: Double = 0.0,
    val marketCompetitivenessScore: Double = 0.0,
    val aiRationale: String = "",
    val isFallback: Boolean = false,
    val isFavorite: Boolean = false,
    val priceConfidenceScore: Double = 0.0,
    val channelAdvice: String = "",
    val competitorDifference: Double = 0.0,
    val priceStrength: String = "",
    val marketRank: String = "",
    val volumeBreakEvenUnits: Int = 0
)

data class AIDemandResult(
    val demandPrediction: String = "",
    val growthTrend: String = "",
    val predictedSalesUnits: Int = 0,
    val reorderQuantity: Int = 0,
    val aiRationale: String = "",
    val fastMovingPrediction: String = "",
    val slowMovingPrediction: String = "",
    val forecast7dUnits: Int = 0,
    val forecast30dUnits: Int = 0,
    val forecast90dUnits: Int = 0,
    val forecast1yUnits: Int = 0,
    val forecast7dRevenue: Double = 0.0,
    val forecast30dRevenue: Double = 0.0,
    val forecast90dRevenue: Double = 0.0,
    val forecast1yRevenue: Double = 0.0,
    val seasonalPeakTiming: String = "",
    val growthOpportunityScore: Double = 0.0,
    val isFastMoving: Boolean = false,
    val deadStockRiskScore: Double = 0.0,
    val deadStockRisk: String = "",
    val stockOutRiskProbability: Double = 0.0
)

data class AIDealerResult(
    val topDealers: List<AIDealerResultEntity> = emptyList(),
    val expansionDealers: List<AIDealerResultEntity> = emptyList(),
    val recoveryDealers: List<AIDealerResultEntity> = emptyList(),
    val strategicActionPlan: String = ""
)

data class AIDealerResultEntity(
    val dealerName: String = "",
    val annualTurnoverCr: Double = 0.0,
    val growthRatePct: Double = 0.0,
    val region: String = "",
    val keyStrength: String = "",
    val recommendedIncentive: String = "",
    val targetRevenueInrCr: Double = 0.0,
    val creditLimitCr: Double = 0.0,
    val expansionRationale: String = "",
    val daysOverdue: Int = 0,
    val issueIdentified: String = "",
    val turnaroundPlan: String = ""
)

data class AIStrategyResult(
    val executiveSummary: String = "",
    val growthVectors: List<GrowthVectorEntity> = emptyList(),
    val riskMitigations: List<RiskMitigationEntity> = emptyList(),
    val capitalAllocationPlan: String = ""
)

data class GrowthVectorEntity(val title: String = "", val projectedRoi: String = "", val timeframe: String = "", val description: String = "")
data class RiskMitigationEntity(val risk: String = "", val severity: String = "", val solution: String = "")

// --- PLATFORM ENTITIES ---

data class AbsoluteCoreEntity(val civilizationsGovernedCount: Int = 0, val absoluteStatus: String = "", val universalControllerTelemetry: String = "")
data class EconomicOSEntity(val id: String = "", val operatingSystemName: String = "", val governanceLaw: String = "", val activeUnifiedNodesCount: Long = 0, val executionState: String = "", val subsystemDomain: String = "", val kernelStabilityPct: Double = 0.0)
data class WealthMatrixEntity(val id: String = "", val wealthPillar: String = "", val volumeTrillionUsd: Double = 0.0, val streamIdentifier: String = "", val compoundGrowthRatePct: Double = 0.0, val capitalAllocationStatus: String = "")
data class OpportunityGridEntity(val id: String = "", val discoveryHorizon: String = "", val timeToGenesisDays: Int = 0, val opportunityConcept: String = "", val projectedValueTrillionUsd: Double = 0.0, val realizationProbabilityPct: Double = 0.0)
data class DemandMatrixEntity(val id: String = "", val temporalSpan: String = "", val predictedDemandMillionUnits: Double = 0.0, val marketCluster: String = "", val fulfillmentPrecisionPct: Double = 0.0, val predictiveLatencyMs: Long = 0, val autoBalancingAction: String = "")
data class CapitalSupremacyEntity(val id: String = "", val capitalSector: String = "", val managedVolumeBillionUsd: Double = 0.0, val fundOrPoolName: String = "", val annualizedYieldPct: Double = 0.0, val reserveSolvencyRatioPct: Double = 0.0, val deploymentMode: String = "")
data class TradeNetworkEntity(val id: String = "", val optimizationDomain: String = "", val throughputBillionUsdPerMonth: Double = 0.0, val routeMeshName: String = "", val routingLatencyMs: Long = 0, val seamlessClearanceRatePct: Double = 0.0, val routeProtectionStatus: String = "")
data class RealityMatrixEntity(val id: String = "", val realityLayer: String = "", val simulationFidelityPct: Double = 0.0, val matrixDesignation: String = "", val computeOpsPerSecMillion: Double = 0.0, val quantumCoherenceRatePct: Double = 0.0, val synthesisAction: String = "")
data class DecisionEngineEntity(val id: String = "", val decisionType: String = "", val economicImpactTrillionUsd: Double = 0.0, val policyTitle: String = "", val decisionAccuracyIndex: Double = 0.0, val executionLatencyMicrosec: Long = 0, val autonomousDirective: String = "")
data class KnowledgeMatrixEntity(val id: String = "", val temporalSphere: String = "", val synthesizedDataVolumeYb: Double = 0.0, val corpusDomain: String = "", val executiveWisdomSynthesis: String = "")
data class InnovationEngineEntity(val id: String = "", val innovationCategory: String = "", val commercialVelocityMultiplier: Double = 0.0, val breakthroughTitle: String = "", val registryIdentifier: String = "", val deploymentStatus: String = "")
data class ProtectionSystemEntity(val id: String = "", val protectedFrontier: String = "", val barrierIntegrityPct: Double = 0.0, val threatVectorNullified: String = "", val defenseProtocol: String = "", val mitigationLatencyNanosec: Long = 0, val fortressStatus: String = "")
data class AbsoluteHealthEngineEntity(val id: String = "", val diagnosticDomain: String = "", val vitalityScore: Double = 0.0, val diagnosticSynthesis: String = "", val systemicEquilibriumState: String = "")
data class AbsoluteCommandTowerEntity(val id: String = "", val governanceSector: String = "", val throughputQPS: Double = 0.0, val commandTowerId: String = "", val activeChannelsCount: Int = 0, val telemetryScore: Double = 0.0, val universalState: String = "")
data class UnityEngineEntity(val id: String = "", val unificationTarget: String = "", val universalUnityIndex: Double = 0.0, val convergenceVector: String = "", val unificationBlueprint: String = "", val organismCohesionFactor: Double = 0.0, val state: String = "")

data class ExportQueueEntity(val id: String = "", val productId: String = "", val exportType: String = "", val status: String = "", val outputImageUri: String = "")
data class MediaLibraryEntity(val id: Long = 0, val productId: String? = null, val imageUri: String = "", val versionNumber: String = "1", val mediaType: String = "IMAGE", val imageSource: String = "", val imageType: String = "", val isPrimary: Boolean = false, val createdDate: String = "", val qrNumber: String = "", val sku: String = "", val width: Int = 1024, val height: Int = 1024)
data class AIInventoryRecommendationEntity(val id: Long = 0, val productName: String = "", val sku: String = "", val priority: String = "", val recommendedAction: String = "", val expectedImpact: String = "", val estimatedCostSavingsInr: Double = 0.0, val isApplied: Boolean = false, val aiOptimizationRationale: String = "", val recommendationId: Long = 0)

data class EconomicCivilizationEntity(val id: String = "", val civilizationName: String = "", val economicZone: String = "", val managedCompaniesCount: Long = 0, val managedIndustriesCount: Int = 0, val totalTradeVolumeBillionUsd: Double = 0.0, val growthRatePct: Double = 0.0, val autonomyLevelPct: Double = 0.0)
data class ResourceIntelligenceEntity(val id: String = "", val resourceName: String = "", val resourceCategory: String = "", val optimizationGainPct: Double = 0.0, val allocatedCapacityUsdMillion: Double = 0.0, val utilizationRatePct: Double = 0.0, val bottleneckRiskLevel: String = "", val recommendedActionPlan: String = "")
data class TradeUniverseEntity(val id: String = "", val originRegion: String = "", val destinationMarket: String = "", val connectedIndustries: String = "", val tradeEfficiencyScore: Double = 0.0, val tradeThroughputUsdMillion: Double = 0.0, val activeBusinessesCount: Int = 0, val tariffOptimizationPct: Double = 0.0, val routeHealthStatus: String = "")
data class ProsperityEngineEntity(val id: String = "", val economicDomain: String = "", val prosperityIndex: Double = 0.0, val cumulativeWealthUsdMillion: Double = 0.0, val annualGrowthRatePct: Double = 0.0, val allocatedCapitalUsdMillion: Double = 0.0, val generatedEconomicValueUsdMillion: Double = 0.0)
data class InnovationUniverseEntity(val id: String = "", val innovationTitle: String = "", val patentIdentifier: String = "", val innovationIndex: Double = 0.0, val economicPotentialUsdMillion: Double = 0.0, val readinessStage: String = "", val disruptionFactorPct: Double = 0.0)
data class DecisionUniverseEntity(val id: String = "", val decisionTitle: String = "", val decisionCategory: String = "", val decisionAccuracyScore: Double = 0.0, val proposedAction: String = "", val expectedEconomicImpactUsdMillion: Double = 0.0, val confidenceIntervalPct: Double = 0.0, val executionState: String = "")
data class AscensionHealthEntity(val id: String = "", val dimensionName: String = "", val score: Double = 0.0, val diagnosticSummary: String = "")
data class AscensionCoreEntity(val id: String = "")

data class TranscendenceCoreEntity(val id: String = "", val realitiesGovernedCount: Int = 0, val universalCoordinationRatePct: Double = 0.0, val realitySyncScore: Double = 0.0, val infiniteGovernancePct: Double = 0.0, val crossSystemEvolutionMultiplier: Double = 0.0, val transcendenceStatus: String = "", val controllerTelemetry: String = "")
data class RealityCommerceEntity(val id: String = "", val marketRealm: String = "", val realityCommerceIndex: Double = 0.0, val tradeVolumeBillionUsd: Double = 0.0, val connectedNodesCount: Int = 0, val crossRealityFrictionLatencyMs: Int = 0, val interoperabilityScore: Double = 0.0, val realmStatus: String = "")
data class EnterpriseCreatorEntity(val id: String = "", val entityName: String = "", val createdEntityType: String = "", val marketModel: String = "", val autonomousRevenueProjectionMillionUsd: Double = 0.0, val enterpriseCreationScore: Double = 0.0, val lifecycleStage: String = "", val autonomousCeoAgent: String = "")
data class TranscendenceOpportunityEntity(val id: String = "", val opportunityTitle: String = "", val spaceCategory: String = "", val strategicRoadmap: String = "", val addressableCosmicValueMillionUsd: Double = 0.0, val captureProbabilityPct: Double = 0.0, val expansionHorizonMonths: Int = 0, val executionStage: String = "")
data class DemandNetworkEntity(val id: String = "", val productOrSector: String = "", val demandTier: String = "", val demandCatalystSummary: String = "", val forecastUnitsDemand: Long = 0, val projectedGrossRevenueMillionUsd: Double = 0.0, val demandResonanceMultiplier: Double = 0.0, val predictiveConfidencePct: Double = 0.0)
data class CapitalCivilizationEntity(val id: String = "", val fundCategory: String = "", val annualizedGrowthYieldPct: Double = 0.0, val autonomousGovernancePolicy: String = "", val totalCapitalManagedMillionUsd: Double = 0.0, val allocatedCapitalMillionUsd: Double = 0.0, val capitalCivilizationIndex: Double = 0.0, val liquidityReserveStatus: String = "")
data class DecisionCosmosEntity(val id: String = "", val title: String = "", val decisionType: String = "", val impactScope: String = "", val telemetryOutcome: String = "", val autonomousExecutionConfidencePct: Double = 0.0, val executionStatus: String = "")
data class KnowledgeOceanEntity(val id: String = "", val knowledgeCategory: String = "", val knowledgeOceanIndex: Double = 0.0, val knowledgeTopic: String = "", val deepInsightSummary: String = "", val synthesizedExabytes: Double = 0.0, val truthConfidencePct: Double = 0.0)
data class TranscendenceEvolutionEntity(val id: String = "", val targetDimension: String = "", val evolutionaryStatus: String = "", val entityEvolving: String = "", val emergentParadigm: String = "", val adaptationVelocityPct: Double = 0.0, val evolutionIntelligenceIndex: Double = 0.0)
data class TranscendenceRealityTwinEntity(val id: String = "", val twinName: String = "", val twinType: String = "", val fidelityLevelPct: Double = 0.0, val simulationTicksPerSec: Long = 0, val simulationHypothesisResult: String = "")
data class TranscendenceInnovationEntity(val id: String = "", val title: String = "", val innovationCategory: String = "", val patentOrCodeReference: String = "", val innovationMatrixScore: Double = 0.0, val commercialYieldPotentialMillionUsd: Double = 0.0)
data class TranscendenceRiskEntity(val id: String = "", val protectionDomain: String = "", val riskIntelligenceIndex: Double = 0.0, val threatVector: String = "", val mitigationProtocol: String = "", val containmentEfficiencyPct: Double = 0.0)
data class TranscendenceHealthEntity(val id: String = "", val dimensionName: String = "", val status: String = "", val diagnosticAnalysis: String = "", val healthScore: Double = 0.0, val transcendenceHealthIndex: Double = 0.0)
data class TranscendenceExpansionEntity(val id: String = "", val expansionDomain: String = "", val expansionState: String = "", val targetTerritoryOrVector: String = "", val synergyMultiplier: Double = 0.0, val universalExpansionScore: Double = 0.0, val expansionVelocityPct: Double = 0.0)

data class PackingSlipEntity(
    val id: Long = 0,
    val orderId: Long = 0,
    val packingNumber: String = "",
    val totalBoxes: Int = 0,
    val totalItems: Int = 0,
    val packedBy: String = "",
    val packedDate: String = "",
    val remarks: String = ""
)

data class DispatchEntity(
    val id: Long = 0,
    val orderId: Long = 0,
    val dispatchNumber: String = "",
    val transportName: String = "",
    val lrNumber: String = "",
    val vehicleNumber: String = "",
    val expectedDeliveryDate: String = "",
    val dispatchedDate: String = ""
)

data class DeliveryEntity(
    val id: Long = 0,
    val orderId: Long = 0,
    val receivedBy: String = "",
    val mobile: String = "",
    val deliveredDate: String = "",
    val proofUri: String = "",
    val remarks: String = ""
)

data class OrderTrackingEntity(
    val id: Long = 0,
    val orderId: Long = 0,
    val status: String = "",
    val message: String = "",
    val createdDate: String = ""
)

data class OpportunityQuantumEntity(val id: String = "", val detectionType: String = "", val title: String = "", val estimatedEconomicValueBillionUsd: Double = 0.0, val timeToManifestHorizonMonths: Int = 0, val strategicReadinessPct: Double = 0.0, val actionDirective: String = "")
data class MarketQuantumEntity(val id: String = "", val sectorOrRegion: String = "", val marketDimension: String = "", val marketPredictionIndexPct: Double = 0.0, val intentVelocityScore: Double = 0.0, val forecastedDemandSurgeMultiplier: Double = 0.0, val predictiveSignalInsight: String = "", val autoAllocationRule: String = "")
data class DecisionMatrixEntity(val id: String = "", val decisionTopic: String = "", val riskScore: Double = 0.0, val rewardScore: Double = 0.0, val capitalRequiredMillionUsd: Double = 0.0, val compositeEfficiencyScore: Double = 0.0, val bestDecisionRecommendation: String = "")
data class RiskQuantumEntity(val id: String = "", val riskCategory: String = "", val status: String = "", val riskName: String = "", val probabilityPct: Double = 0.0, val severityScorePct: Double = 0.0, val potentialFinancialImpactMillionUsd: Double = 0.0, val earlyWarningDetectionTrigger: String = "", val quantumAutomatedCountermeasure: String = "")
data class QuantumHealthEntity(val id: String = "", val systemStatusSummary: String = "", val quantumHealthIndex: Double = 0.0, val businessHealthScore: Double = 0.0, val marketHealthScore: Double = 0.0, val aiHealthScore: Double = 0.0, val economicHealthScore: Double = 0.0, val growthHealthScore: Double = 0.0)

// --- MORE MODELS FOR OMNIVERSE ---
data class OpportunityEntity(val id: String = "", val title: String = "", val category: String = "", val probability: Double = 0.0, val value: Double = 0.0, val months: Int = 0)
data class TradeNodeEntity(val id: String = "", val title: String = "", val tier: String = "", val efficiency: Double = 0.0, val throughput: Double = 0.0)
data class ScenarioEntity(val id: String = "", val title: String = "", val driver: String = "", val value: Double = 0.0, val confidence: Double = 0.0)
data class InsightEntity(val id: String = "", val topic: String = "", val content: String = "", val impact: Double = 0.0)
data class RealityEngineEntity(val id: String = "", val simulationMatrixName: String = "", val realityLayer: String = "", val predictiveSynthesisDirective: String = "", val simulationResolutionPct: Double = 0.0, val operationsPerMicrosecondMillion: Double = 0.0, val quantumCoherencePct: Double = 0.0)
data class KnowledgePrimeEntity(val id: String = "", val knowledgeUniverseTopic: String = "", val temporalHorizon: String = "", val executiveInsightSynthesis: String = "", val synthesizedYottabytes: Double = 0.0, val comprehensionFidelityPct: Double = 0.0)
data class HealthPrimeEntityPrime(val id: String = "", val dimension: String = "", val score: Double = 0.0)
data class TradeSupremacyEntity(val id: String = "", val tradeMeshIdentifier: String = "", val optimizationVector: String = "", val throughputBillionUsdPerMonth: Double = 0.0, val latencyMilliseconds: Long = 0, val tradeSupremacyScore: Double = 0.0, val channelSecurityRating: String = "")

data class UniversalHarmonyEngineEntity(val id: String = "", val synchronizationTarget: String = "", val universalHarmonyIndex: Double = 0.0, val convergenceVector: String = "", val harmonyBlueprint: String = "")
data class UltimaTowerEntity(val id: String = "", val monitoredSector: String = "", val towerDesignation: String = "", val activeChannelsCount: Int = 0, val throughputQPS: Double = 0.0)
data class HealthCivilizationEntity(val id: String = "")
data class UltimaDecisionAuthorityEntity(val id: String = "")
data class TradeCivilizationEntity(val id: String = "")
data class UltimaCapitalAuthorityEntity(val id: String = "")
data class UltimaDemandUniverseEntity(val id: String = "")
data class FutureOpportunityEntity(val id: String = "")
data class UltimaWealthUniverseEntity(val id: String = "")
data class CommerceCivilizationEntity(val id: String = "")
data class UltimaCoreEntity(val id: String = "", val ultimaStatus: String = "", val universalControllerTelemetry: String = "", val civilizationsGovernedCount: Int = 0, val universalCommandRatePct: Double = 0.0, val infiniteCoordinationScore: Double = 0.0, val civilizationSyncRatePct: Double = 0.0)

data class AIDemandInputState(
    val productName: String = "",
    val sku: String = "",
    val category: String = "",
    val region: String = "",
    val dealerNetwork: String = "",
    val season: String = "",
    val festivalCalendar: String = "",
    val marketingCampaignData: String = "",
    val currentInventory: Int = 0,
    val unitPrice: Double = 0.0,
    val leadTimeDays: Int = 0,
    val salesHistory30d: Int = 0,
    val salesHistory90d: Int = 0,
    val salesHistory1y: Int = 0
)

data class DealerInputState(
    val dealerName: String = "",
    val dealerCategory: String = "",
    val location: String = "",
    val productPreferences: String = "",
    val salesHistoryAnnual: Double = 0.0,
    val salesHistoryQuarterly: Double = 0.0,
    val orderFrequencyPerMonth: Double = 0.0,
    val growthTrendPercent: Double = 0.0,
    val dealerRating: Double = 0.0,
    val paymentPerformance: String = "",
    val customerReachCount: Int = 0,
    val creditLimit: Double = 0.0,
    val creditUsed: Double = 0.0
)

data class InventoryInputState(
    val productName: String = "",
    val sku: String = "",
    val category: String = "",
    val warehouseLocation: String = "",
    val currentStock: Int = 0,
    val salesHistory30d: Int = 0,
    val forecastDemand30d: Int = 0,
    val dealerPendingOrders: Int = 0,
    val unitCostPrice: Double = 0.0,
    val leadTimeDays: Int = 0
)

data class AIInventoryInputState(
    val name: String = "",
    val sku: String = "",
    val productName: String = "",
    val category: String = "",
    val warehouseLocation: String = "",
    val currentStock: Int = 0,
    val salesHistory30d: Int = 0,
    val forecastDemand30d: Int = 0,
    val dealerPendingOrders: Int = 0,
    val unitCostPrice: Double = 0.0,
    val leadTimeDays: Int = 0
)

data class MediaCommandCenterEntity(
    val id: Long = 0,
    val mediaId: String = "",
    val productName: String = "",
    val sku: String = "",
    val qrNumber: String = "",
    val mediaType: String = "",
    val versionNumber: String = "",
    val viewCount: Int = 0,
    val shareCount: Int = 0,
    val downloadCount: Int = 0,
    val imageUri: String = "",
    val productId: Long = 0,
    val isArchived: Boolean = false,
    val isDeleted: Boolean = false,
    val mediaSource: String = "",
    val width: Int = 0,
    val height: Int = 0,
    val createdDate: Long = 0
)

data class IndustryMasterEntity(val id: String = "", val industryName: String = "", val sector: String = "", val marketCapTrillionUsd: Double = 0.0, val globalGrowthRatePct: Double = 0.0, val automationIndex: Double = 0.0, val riskFactor: String = "", val status: String = "")
data class CountryMasterEntity(val id: String = "", val countryName: String = "", val isoCode: String = "", val easeOfBusinessRating: String = "", val gdpBillionUsd: Double = 0.0, val corporateTaxPct: Double = 0.0, val exportTariffPct: Double = 0.0, val primaryTradeOpportunities: String = "")
data class GlobalEconomyEntity(val id: String = "", val indicatorName: String = "", val valueStr: String = "", val inflationRatePct: Double = 0.0, val interestRatePct: Double = 0.0, val currencyPairVolatility: String = "", val globalTradeTrend: String = "", val aiEconomicForecast: String = "")
data class ResearchReportEntity(val id: String = "", val topicTitle: String = "", val domain: String = "", val aiConfidenceScore: Double = 0.0, val executiveSummary: String = "", val disruptiveTechnologies: String = "")
data class MarketOpportunityEntity(val id: String = "", val title: String = "", val aiRating: String = "", val targetIndustry: String = "", val targetRegion: String = "", val estimatedMarketCapInr: Double = 0.0, val expectedRoiMultiplier: Double = 0.0, val entryBarrier: String = "", val strategicActionPlan: String = "")
data class ExpansionBlueprintEntity(val id: String = "", val expansionName: String = "", val status: String = "", val targetLevel: String = "", val geographicalTarget: String = "", val capitalRequiredInr: Double = 0.0, val projectedRevenueInr: Double = 0.0, val operationalMilestones: String = "")
data class UniversalMarketplaceEntity(val id: String = "", val itemName: String = "", val productType: String = "", val sellerName: String = "", val industry: String = "", val basePriceInr: Double = 0.0, val targetAudience: String = "", val stockOrCapacity: String = "", val aiDemandRating: String = "", val crossBorderEligible: Boolean = false)

data class AICatalogueResultEntity(
    val id: String = "",
    val resultId: String = "",
    val productName: String = "",
    val productTitle: String = "",
    val fabric: String = "",
    val color: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val occasion: String = "",
    val shortDescription: String = "",
    val longDescription: String = "",
    val seoDescription: String = "",
    val seoKeywords: String = "",
    val premiumCatalogueContent: String = "",
    val instagramCaption: String = "",
    val facebookCaption: String = "",
    val whatsappPromotionText: String = "",
    val dealerMarketingText: String = "",
    val headerTagline: String = "",
    val sampleFabric: String = "",
    val sampleColor: String = "",
    val sampleDesignDetails: String = "",
    val isFavorite: Boolean = false
)

data class AICatalogueTemplateEntity(
    val id: String = "",
    val templateId: String = "",
    val templateName: String = "",
    val category: String = "",
    val headerTagline: String = "",
    val sampleFabric: String = "",
    val sampleColor: String = "",
    val sampleDesignDetails: String = ""
)

data class AIPricingHistoryEntity(
    val id: Long = 0,
    val historyId: Long = 0,
    val productName: String = "",
    val price: Double = 0.0,
    val timestamp: Long = 0,
    val recommendedMargin: Double = 0.0,
    val profitPercentage: Double = 0.0,
    val dealerPrice: Double = 0.0,
    val category: String = "",
    val costPrice: Double = 0.0,
    val retailPrice: Double = 0.0
)

data class AIPricingRuleEntity(val id: String = "", val ruleName: String = "", val ruleContent: String = "")

data class AIPricingResultEntity(
    val id: String = "",
    val productName: String = "",
    val resultJson: String = "",
    val timestamp: Long = 0,
    val fabricType: String = "",
    val costPrice: Double = 0.0,
    val retailPrice: Double = 0.0,
    val wholesalePrice: Double = 0.0,
    val distributorPrice: Double = 0.0,
    val dealerPrice: Double = 0.0,
    val premiumPrice: Double = 0.0,
    val recommendedMargin: Double = 0.0,
    val profitPercentage: Double = 0.0,
    val discountLimit: Double = 0.0,
    val marketCompetitivenessScore: Int = 0,
    val aiRationale: String = "",
    val isFallback: Boolean = false,
    val isFavorite: Boolean = false,
    val priceConfidenceScore: Int = 0,
    val channelAdvice: String = "",
    val competitorDifference: Double = 0.0,
    val priceStrength: String = "",
    val marketRank: String = "",
    val volumeBreakEvenUnits: Int = 0,
    val category: String = ""
)

data class AIDemandForecastEntity(
    val id: String = "",
    val productName: String = "",
    val forecastJson: String = "",
    val timestamp: Long = 0,
    val forecast30dUnits: Int = 0,
    val forecast30dRevenue: Double = 0.0,
    val reorderQuantity: Int = 0,
    val safetyStockRecommendation: Int = 0,
    val deadStockRisk: String = "",
    val aiRationale: String = "",
    val seasonalPeakTiming: String = "",
    val category: String = "",
    val growthOpportunityScore: Float = 0.0f,
    val isFastMoving: Boolean = false,
    val deadStockRiskScore: Float = 0.0f,
    val stockOutRiskProbability: Float = 0.0f,
    val isApplied: Boolean = false,
    val recommendedAction: String = "",
    val description: String = "",
    val seasonalityMultiplier: Double = 0.0,
    val festivalSpikeMultiplier: Double = 0.0,
    val leadTimeBufferDays: Int = 0,
    val fastMovingPrediction: String = "",
    val slowMovingPrediction: String = "",
    val sku: String = "",
    val actionTaken: String = "",
    val forecast7dUnits: Int = 0,
    val forecast90dUnits: Int = 0,
    val forecast1yUnits: Int = 0,
    val forecast7dRevenue: Double = 0.0,
    val forecast90dRevenue: Double = 0.0,
    val forecast1yRevenue: Double = 0.0,
    val unitPrice: Double = 0.0,
    val currentInventory: Int = 0
)

data class AIDemandHistoryEntity(
    val id: Long = 0,
    val historyId: Long = 0,
    val productName: String = "",
    val actualSales: Int = 0,
    val predictedSales: Int = 0,
    val timestamp: Long = 0,
    val sku: String = "",
    val category: String = "",
    val forecast30dUnits: Int = 0,
    val forecast90dUnits: Int = 0,
    val reorderQuantity: Int = 0,
    val actionTaken: String = ""
)

data class AIDemandModelEntity(
    val id: String = "",
    val modelName: String = "",
    val modelType: String = "",
    val accuracy: Double = 0.0,
    val category: String = "",
    val description: String = "",
    val seasonalityMultiplier: Double = 0.0,
    val festivalSpikeMultiplier: Double = 0.0,
    val leadTimeBufferDays: Int = 0
)

data class AIInventoryForecastEntity(
    val id: String = "",
    val sku: String = "",
    val productName: String = "",
    val predictedDemand: Int = 0,
    val velocity: String = "",
    val warehouseLocation: String = "",
    val velocityClassification: String = "",
    val reorderQuantity: Int = 0,
    val reorderDate: String = "",
    val daysOfSupply: Int = 0,
    val safetyStockUnits: Int = 0,
    val stockOutRiskProbability: Double = 0.0,
    val forecast30d: Int = 0,
    val category: String = "",
    val name: String = "",
    val estimatedReorderCost: Double = 0.0,
    val projectedHoldingCostMonthly: Double = 0.0,
    val stockoutRiskDays: Int = 0,
    val seasonalMultiplier: Double = 0.0,
    val currentStock: Int = 0,
    val fastMovingScore: Int = 0,
    val deadStockRiskScore: Int = 0,
    val growthOpportunityScore: Int = 0,
    val aiOptimizationRationale: String = ""
)

data class AIInventoryAlertEntity(
    val id: Long = 0,
    val alertType: String = "",
    val productName: String = "",
    val sku: String = "",
    val severity: String = "",
    val message: String = "",
    val timestamp: Long = 0,
    val alertId: Long = 0,
    val actionRequired: String = "",
    val isResolved: Boolean = false
)

data class AIInventoryHealthEntity(
    val id: String = "",
    val overallHealth: Double = 0.0,
    val overallHealthScore: Double = 0.0,
    val fastMovingPercentage: Double = 0.0,
    val warehouseUtilizationScore: Double = 0.0,
    val stockTurnoverRatio: Double = 0.0,
    val totalStockValueInr: Double = 0.0,
    val deadStockValueInr: Double = 0.0,
    val fastMovingScore: Int = 0,
    val deadStockRiskScore: Int = 0,
    val growthOpportunityScore: Int = 0,
    val aiOptimizationRationale: String = ""
)

data class AIDealerRecommendationEntity(
    val id: String = "",
    val recommendationId: String = "",
    val dealerName: String = "",
    val recommendationJson: String = "",
    val location: String = "",
    val classification: String = "",
    val dealerPotentialScore: Int = 0,
    val dealerLoyaltyScore: Int = 0,
    val revenueContributionScore: Int = 0,
    val riskScore: Int = 0,
    val futureGrowthForecastPercent: Double = 0.0,
    val recommendedActions: String = "",
    val creditRecommendation: String = "",
    val exclusiveCatalogAccess: String = "",
    val isApplied: Boolean = false,
    val rationale: String = "",
    val isFavorite: Boolean = false,
    val overallScore: Double = 0.0,
    val dealerCategory: String = "",
    val salesScore: Double = 0.0,
    val growthScore: Double = 0.0,
    val paymentScore: Double = 0.0,
    val loyaltyScore: Double = 0.0,
    val tierBadge: String = "",
    val annualProjectedRevenue: Double = 0.0,
    val projectedQ1Revenue: Double = 0.0,
    val projectedQ2Revenue: Double = 0.0,
    val projectedQ3Revenue: Double = 0.0,
    val projectedQ4Revenue: Double = 0.0,
    val recommendedProductMix: String = "",
    val targetIncentiveBudget: Double = 0.0,
    val isRiskAlert: Boolean = false,
    val isRecoveryTarget: Boolean = false
)

data class AIDealerScoreEntity(
    val id: String = "",
    val dealerName: String = "",
    val score: Double = 0.0,
    val overallScore: Double = 0.0,
    val dealerCategory: String = "",
    val location: String = "",
    val salesScore: Double = 0.0,
    val growthScore: Double = 0.0,
    val paymentScore: Double = 0.0,
    val loyaltyScore: Double = 0.0,
    val tierBadge: String = "",
    val riskScore: Double = 0.0
)

data class AIDealerGrowthForecastEntity(
    val id: String = "",
    val dealerName: String = "",
    val forecast: Double = 0.0,
    val annualProjectedRevenue: Double = 0.0,
    val projectedQ1Revenue: Double = 0.0,
    val projectedQ2Revenue: Double = 0.0,
    val projectedQ3Revenue: Double = 0.0,
    val projectedQ4Revenue: Double = 0.0,
    val recommendedProductMix: String = "",
    val targetIncentiveBudget: Double = 0.0
)

data class SingularityPrimeCoreEntity(val id: String = "", val civilizationsGovernedCount: Int = 0, val primeIntelligenceIndex: Double = 0.0, val infiniteCoordinationRatePct: Double = 0.0, val economicSovereigntyScore: Double = 0.0, val selfEvolutionVelocityIndex: Double = 0.0, val primeStatus: String = "", val primeControllerTelemetry: String = "", val executiveInsightSynthesis: String = "")
data class HealthPrimeEntity(val id: String = "", val healthDimension: String = "", val diagnosticSummary: String = "", val healthScore: Double = 0.0, val operationalVitality: String = "")
data class PrimeCommandTowerEntity(val id: String = "", val controlSector: String = "", val sentinelBeaconId: String = "", val activeChannelsCount: Int = 0, val throughputQPS: Double = 0.0, val primeTelemetryScore: Double = 0.0)
data class EvolutionAuthorityEntity(val id: String = "", val evolutionTarget: String = "", val selfEvolutionFactor: Double = 0.0, val transformationVector: String = "", val evolutionBlueprintSummary: String = "", val targetEvolutionScore: Double = 0.0, val state: String = "")
data class RiskShieldPrimeEntity(val id: String = "", val threatVectorMitigated: String = "", val protectedBastion: String = "", val neutralizationMechanism: String = "", val neutralizationSpeedNanosec: Long = 0, val fortressIntegrityPct: Double = 0.0)
data class CivilizationEngineEntity(val id: String = "", val entityName: String = "", val domainDomain: String = "", val autonomousGovernanceLaw: String = "", val activeNodesCount: Int = 0, val civilizationControlIndex: Double = 0.0)
data class WealthGeneratorEntity(val id: String = "", val wealthStreamName: String = "", val wealthPillar: String = "", val currentVolumeTrillionUsd: Double = 0.0, val compoundGrowthRatePct: Double = 0.0, val distributionEfficiencyPct: Double = 0.0)
data class OpportunityCreatorEntity(val id: String = "", val conceptTitle: String = "", val creationHorizon: String = "", val autonomousSeedingStrategy: String = "", val projectedValueTrillionUsd: Double = 0.0, val timeToGenesisDays: Int = 0, val probabilityOfSuccessPct: Double = 0.0)
data class DemandCosmosEntity(val id: String = "", val marketCluster: String = "", val scopeLevel: String = "", val dynamicBalancingAction: String = "", val predictedDemandUnitsMillion: Double = 0.0, val fulfillmentVelocityMs: Long = 0, val predictiveAccuracyPct: Double = 0.0)
data class CapitalAuthorityEntity(val id: String = "", val fundName: String = "", val allocationPillar: String = "", val totalUnderManagementBillionUsd: Double = 0.0, val targetYieldRatePct: Double = 0.0, val deploymentStatus: String = "")
data class DecisionPrimeEntity(val id: String = "", val decisionDirectiveTitle: String = "", val executionDomain: String = "", val algorithmicAction: String = "", val economicMagnitudeTrillionUsd: Double = 0.0, val executionLatencyMicrosec: Long = 0, val confidenceRatePct: Double = 0.0)
data class InnovationFactoryEntity(val id: String = "", val innovationTitle: String = "", val creationCategory: String = "", val globalIdentifier: String = "", val commercializationPaceScore: Double = 0.0, val civilizationImpactMultiplier: Double = 0.0)

data class EternityCoreEntity(val id: String = "", val perpetualEconomiesCount: Int = 0, val perpetualStatus: String = "", val infiniteIntelligenceScore: Double = 0.0, val continuousLearningRatePct: Double = 0.0, val eternalGrowthMultiplier: Double = 0.0, val universalOptimizationPct: Double = 0.0, val perpetualContinuityScore: Double = 0.0, val controllerTelemetry: String = "")
data class EternityHealthEntity(val id: String = "", val dimensionName: String = "", val score: Double = 0.0, val diagnosticSummary: String = "")
data class RiskShieldEntity(val id: String = "", val protectedVector: String = "", val shieldStatus: String = "", val threatDescription: String = "", val automatedShieldProtocol: String = "")
data class KnowledgeEternityEntity(val id: String = "", val temporalHorizon: String = "", val synthesizedDataPointsTrillion: Double = 0.0, val knowledgeDomain: String = "", val actionableWisdomSummary: String = "", val synthesizedYottabytes: Double = 0.0, val comprehensionFidelityPct: Double = 0.0)
data class EternityInnovationEntity(val id: String = "", val innovationCategory: String = "", val perpetualPatentCode: String = "", val innovationName: String = "", val projectedYieldBillionUsd: Double = 0.0, val deploymentVelocity: Double = 0.0, val innovationGrowthIndex: Double = 0.0)

data class WealthUniverseEntity(val id: String = "", val wealthDomain: String = "", val totalAssetsBillionUsd: Double = 0.0, val cumulativeRevenueBillionUsd: Double = 0.0, val netProfitBillionUsd: Double = 0.0, val capitalGrowthYoYPct: Double = 0.0, val capitalEfficiencyPct: Double = 0.0)
data class DemandUniverseEntity(val id: String = "", val forecastHorizon: String = "", val demandConfidencePct: Double = 0.0, val productSector: String = "", val demandDriverSummary: String = "", val projectedUnitsDemand: Long = 0, val projectedRevenueMillionUsd: Double = 0.0, val seasonalGrowthSpikePct: Double = 0.0)
data class CapitalUniverseEntity(val id: String = "", val capitalCategory: String = "", val liquidityHealthStatus: String = "", val automatedReinvestmentPlan: String = "", val allocatedCapacityMillionUsd: Double = 0.0, val deployedAmountMillionUsd: Double = 0.0, val annualizedRoiPct: Double = 0.0)
data class TradeInfinityEntity(val id: String = "", val tradeCorridorTitle: String = "", val connectedSovereignZones: String = "", val volumeCapacityBillionUsd: Double = 0.0, val transactionLagMicroseconds: Long = 0, val tariffOptimizationPct: Double = 0.0)

data class GlobalTradeDataPrimeEntity(val id: String = "", val targetCountry: String = "", val tradeRoute: String = "", val optimalCategory: String = "", val projectedVolumePcs: Int = 0, val demandScore: Double = 0.0)
data class CompetitorIntelligencePrimeEntity(val id: String = "", val competitorName: String = "", val primaryRegion: String = "", val marketSharePct: Double = 0.0, val competitiveGapOpportunity: String = "")
data class SupplyChainAiPrimeEntity(val id: String = "", val logisticsNode: String = "", val efficiencyScorePct: Double = 0.0, val costReductionPct: Double = 0.0, val bottleneckAlert: String = "")
data class CapitalManagementPrimeEntity(val id: String = "", val allocationCategory: String = "", val allocatedBudgetInr: Double = 0.0, val projectedRoiPct: Double = 0.0, val riskLevel: String = "")
data class RevenueEnginePrimeEntity(val id: String = "", val streamName: String = "", val currentRevenueInr: Double = 0.0, val profitMarginPct: Double = 0.0, val growthRatePct: Double = 0.0, val optimizationDirective: String = "")

data class OmniverseInnovationPrimeEntity(val id: String = "", val innovationTitle: String = "", val patentIdentifier: String = "", val innovationClass: String = "", val deploymentStatus: String = "", val potentialYieldMillionUsd: Double = 0.0, val expansionIndex: Double = 0.0)
data class OmniverseRiskPrimeEntity(val id: String = "", val riskTitle: String = "", val riskDomain: String = "", val severityLevel: String = "", val exposureValueMillionUsd: Double = 0.0, val riskResilienceScore: Double = 0.0, val automatedMitigationStrategy: String = "")

data class UltimaRealityGridEntity(val id: String = "")
data class UltimaTowerPrimeEntity(val id: String = "", val monitoredSector: String = "", val towerDesignation: String = "", val activeChannelsCount: Int = 0, val throughputQPS: Double = 0.0)
data class KnowledgeCivilizationPrimeEntity(val id: String = "")
data class InnovationCivilizationPrimeEntity(val id: String = "")
data class ProtectionGridPrimeEntity(val id: String = "")

data class KnowledgeCivilizationEntity(val id: String = "")
data class InnovationCivilizationEntity(val id: String = "")
data class ProtectionGridEntity(val id: String = "")
data class FutureEngineEntity(val id: String = "", val futurePathName: String = "", val isBestPath: Boolean = false, val trajectoryDescription: String = "", val probabilityScorePct: Double = 0.0, val growthForecastMultiplier: Double = 0.0, val revenueProjectionBillionUsd: Double = 0.0, val riskFactorScore: Double = 0.0, val strategicRecommendation: String = "")
data class SimulationNetworkEntity(val id: String = "", val simulationType: String = "", val iterationsRun: Long = 0, val simulationTitle: String = "", val successProbabilityPct: Double = 0.0, val projectedGrowthPct: Double = 0.0, val vulnerabilityDetected: String = "", val automatedMitigation: String = "")
data class EvolutionEngineEntity(val id: String = "", val agentOrSubsystem: String = "", val learningIterationsCompleted: Long = 0, val evolutionaryCapability: String = "", val emergentBehaviorDiscovered: String = "", val autonomousSelfUpgradeAction: String = "")
data class EconomyNetworkEntity(val id: String = "", val economyName: String = "", val economyScope: String = "", val currencyRegime: String = "", val autonomyLevelPct: Double = 0.0, val activeEntitiesCount: Long = 0, val totalGdpBillionUsd: Double = 0.0, val growthRateYoYPct: Double = 0.0, val networkInterconnectednessScore: Double = 0.0)
data class MarketMatrixEntity(val id: String = "", val marketName: String = "", val geographicRegion: String = "", val marketEfficiencyPct: Double = 0.0, val aggregateDemandIndex: Double = 0.0, val supplyCapacityPct: Double = 0.0, val consumerSentimentScore: Double = 0.0, val emergingOpportunitiesCount: Int = 0, val marketSignalSummary: String = "")
data class TradeGridEntity(val id: String = "", val tradeNodeTitle: String = "", val nodeTier: String = "", val gridHealthStatus: String = "", val tradeEfficiencyScore: Double = 0.0, val connectedEndpointsCount: Int = 0, val volumeThroughputMillionUsd: Double = 0.0, val frictionLagMs: Long = 0, val tariffOptimizationPct: Double = 0.0)
data class OpportunityUniverseEntity(val id: String = "", val opportunityTitle: String = "", val opportunityCategory: String = "", val executionStage: String = "", val captureProbabilityPct: Double = 0.0, val addressableValueMillionUsd: Double = 0.0, val timeToMaturityMonths: Int = 0, val universeOpportunityScore: Double = 0.0, val strategicActionPlan: String = "")
data class OmniverseInnovationEntity(val id: String = "", val innovationTitle: String = "", val patentIdentifier: String = "", val innovationClass: String = "", val deploymentStatus: String = "", val potentialYieldMillionUsd: Double = 0.0, val expansionIndex: Double = 0.0)
data class OmniverseRiskEntity(val id: String = "", val riskTitle: String = "", val riskDomain: String = "", val severityLevel: String = "", val exposureValueMillionUsd: Double = 0.0, val riskResilienceScore: Double = 0.0, val automatedMitigationStrategy: String = "")
data class OmniverseHealthEntity(val id: String = "", val dimensionName: String = "", val score: Double = 0.0, val status: String = "", val diagnosticSummary: String = "")
data class OmniverseCoreEntity(val id: String = "", val realitySyncIndex: Double = 0.0, val wealthIndex: Double = 0.0, val evolutionIndex: Double = 0.0, val decisionPrecisionScore: Double = 0.0)
data class KnowledgeFabricEntity(val id: String = "", val knowledgeTopic: String = "", val domainCategory: String = "", val reasoningConfidencePct: Double = 0.0, val indexedNodesCount: Int = 0, val synthesisDepthLevel: Int = 0, val predictiveAccuracyPct: Double = 0.0, val actionableInsightsSummary: String = "")
data class IndustryMatrixEntity(val id: String = "", val industrySector: String = "", val aiIntegrationLevelPct: Double = 0.0, val activeClustersCount: Int = 0, val sectoralMarketCapBillionUsd: Double = 0.0, val transformationVelocityPct: Double = 0.0, val crossIndustrySynergyScore: Double = 0.0, val keyDisruptionVector: String = "")
data class PlanetarySimulationEntity(val id: String = "", val scenarioName: String = "", val primaryDriver: String = "", val projectedValueCreationTrillionUsd: Double = 0.0, val confidenceIntervalPct: Double = 0.0)
data class AiGenerationRequest(val id: String = "", val productId: String = "", val modelId: String = "", val status: String = "", val progress: Float = 0.0f, val resultImageUri: String = "", val errorMessage: String = "")
data class NexusCoreEntity(val id: String = "", val systemName: String = "", val connectivityStatus: String = "", val synchronizationMode: String = "", val enterpriseCoordination: String = "", val networkGovernance: String = "", val activeEnterprisesCount: Int = 0, val networkLatencyMs: Long = 0, val throughputTps: Double = 0.0)
data class EnterpriseNetworkEntity(val id: String = "", val enterpriseName: String = "", val entityType: String = "", val regionOrCountry: String = "", val status: String = "", val ecosystemHealthScore: Double = 0.0, val connectedBranchesCount: Int = 0, val connectedFactoriesCount: Int = 0, val connectedWarehousesCount: Int = 0, val connectedDealersCount: Int = 0, val connectedPartnersCount: Int = 0)
data class KnowledgeWebEntity(val id: String = "", val relationCategory: String = "", val relationType: String = "", val sourceEntity: String = "", val targetEntity: String = "", val strengthScorePct: Double = 0.0, val aiReasoningInsight: String = "", val predictiveTrend: String = "", val optimizationRecommendation: String = "")
data class PartnershipNetworkEntity(val id: String = "", val partnerName: String = "", val partnerType: String = "", val domainOrSector: String = "", val status: String = "", val partnershipScore: Double = 0.0, val strategicValueProposition: String = "", val synergyValueMillionUsd: Double = 0.0, val reliabilityPct: Double = 0.0)
data class OpportunityExchangeEntity(val id: String = "", val title: String = "", val opportunityCategory: String = "", val description: String = "", val opportunityRank: Int = 0, val potentialValueBillionUsd: Double = 0.0, val confidenceScorePct: Double = 0.0, val executionReadinessScore: Double = 0.0)
data class DecisionExchangeEntity(val id: String = "", val topicTitle: String = "", val decisionType: String = "", val originatorAiRole: String = "", val executiveSummary: String = "", val recommendationAction: String = "", val expectedRoiPct: Double = 0.0)
data class NexusHealthEntity(val id: String = "", val healthGrade: String = "", val nexusHealthIndex: Double = 0.0, val networkHealthScore: Double = 0.0, val enterpriseHealthScore: Double = 0.0, val industryHealthScore: Double = 0.0, val economicHealthScore: Double = 0.0)
data class CosmicMarketIndexEntity(val id: String = "")

// --- MISSING ENTITIES STUBS ---
data class SelfEvolvingModelEntity(val id: String = "", val modelName: String = "", val evolutionaryGeneration: Int = 0, val coreDomain: String = "", val inferenceAccuracyPct: Double = 0.0, val autonomousOptimizationsPerHour: Int = 0)
data class AutonomousGovernanceLogEntity(val id: String = "", val proposalTitle: String = "", val approvalRatingPct: Double = 0.0, val aiDecisionSummary: String = "")
data class SovereignReserveEntity(val id: String = "", val reserveName: String = "", val totalReserveValueUsd: Double = 0.0, val assetClass: String = "", val allocationPercentage: Double = 0.0, val riskRating: String = "")
data class PlanetaryTradeRouteEntity(val id: String = "")
data class CosmosNodeEntity(val id: String = "", val nodeName: String = "", val computePowerPFLOPS: Double = 0.0, val region: String = "", val latencyMs: Long = 0)
data class CosmosHealthEntity(val id: String = "", val cosmosHealthIndex: Double = 0.0, val businessHealthScore: Double = 0.0, val marketHealthScore: Double = 0.0, val industryHealthScore: Double = 0.0, val tradeHealthScore: Double = 0.0, val economicHealthScore: Double = 0.0, val healthGrade: String = "")
data class TradeNetworksEntity(val id: String = "", val networkName: String = "", val efficiencyPct: Double = 0.0, val tradeCorridor: String = "", val tradeDependencies: String = "", val bestOpportunity: String = "", val bestTradeRoute: String = "", val bestTradePartner: String = "")
data class GlobalRiskEntity(val id: String = "", val regionOrDomain: String = "", val globalRiskIndex: Double = 0.0, val mitigationAction: String = "", val economicRiskScore: Int = 0, val politicalRiskScore: Int = 0, val supplyRiskScore: Int = 0, val currencyRiskScore: Int = 0)
data class EconomicTwinsEntity(val id: String = "", val entityName: String = "", val twinType: String = "", val futureSimulationSummary: String = "", val forecastedGrowthRatePct: Double = 0.0, val economicForecastTrillionUsd: Double = 0.0, val accuracyConfidencePct: Double = 0.0)
data class MarketCosmosEntity(val id: String = "", val marketName: String = "", val opportunityScore: Int = 0, val consumerTrends: String = "", val demandPattern: String = "", val marketPotentialBillionUsd: Double = 0.0, val expansionPriority: String = "")
data class SupplyGridEntity(val id: String = "", val hubName: String = "", val connectedManufacturersCount: Int = 0, val connectedSuppliersCount: Int = 0, val connectedWarehousesCount: Int = 0, val connectedTransportersCount: Int = 0, val connectedDealersCount: Int = 0, val frictionScorePct: Double = 0.0)
data class CosmosCoreEntity(val id: String = "", val systemName: String = "", val synchronizationStatus: String = "", val coordinationScope: String = "", val aiSupervisionLevel: String = "", val networkGovernanceMode: String = "", val activeNodesCount: Int = 0, val latencyMs: Long = 0, val throughputTps: Double = 0.0)
data class OmegaCoreEntity(val id: String = "", val systemStatus: String = "", val activeSubsystemsCount: Int = 0, val omegaIndex: Double = 0.0, val globalStrategyDirective: String = "")
data class GlobalTradeDataEntity(val id: String = "", val targetCountry: String = "", val tradeRoute: String = "", val optimalCategory: String = "", val projectedVolumePcs: Int = 0, val demandScore: Int = 0)
data class CompetitorIntelligenceEntity(val id: String = "", val competitorName: String = "", val primaryRegion: String = "", val marketSharePct: Double = 0.0, val competitiveGapOpportunity: String = "")
data class SupplyChainAiEntity(val id: String = "", val logisticsNode: String = "", val efficiencyScorePct: Double = 0.0, val costReductionPct: Double = 0.0, val bottleneckAlert: String = "")
data class CapitalManagementEntity(val id: String = "", val allocationCategory: String = "", val allocatedBudgetInr: Double = 0.0, val projectedRoiPct: Double = 0.0, val riskLevel: String = "")
data class RevenueEngineEntity(val id: String = "", val streamName: String = "", val currentRevenueInr: Double = 0.0, val profitMarginPct: Double = 0.0, val growthRatePct: Double = 0.0, val optimizationDirective: String = "")
data class OmegaHealthEntity(val id: String = "", val healthDomain: String = "", val score: Double = 0.0, val statusGrade: String = "", val riskFactor: String = "")
data class OmegaTwinEntity(val id: String = "", val replicaType: String = "", val fidelityScorePct: Double = 0.0, val forecastedGrowthMultiplier: Double = 0.0, val strategicInsight: String = "")
data class SocialAnalyticsEntity(
    val id: String = "",
    val eventType: String = ""
)

data class DealerCatalogueEntity(
    val id: String = "",
    val title: String = "",
    val catalogueType: String = "",
    val downloadCount: Int = 0
)

data class DealerOrderEntity(
    val id: String = "",
    val orderId: String = "",
    val status: String = "",
    val productName: String = "",
    val qty: Int = 0,
    val rate: Double = 0.0,
    val amount: Double = 0.0,
    val notes: String = ""
)

data class WhatsAppCampaignEntity(
    val id: String = "",
    val targetDealerType: String = "",
    val targetDealerCount: Int = 0,
    val sentCount: Int = 0
)

data class DealerEntity(
    val id: Long = 0,
    val dealerName: String = "",
    val dealerId: String = "",
    val firmName: String = "",
    val city: String = "",
    val state: String = "",
    val mobile: String = "",
    val whatsapp: String = "",
    val dealerType: String = "",
    val creditLimit: Double = 0.0,
    val gstNumber: String = ""
)
data class SalesDashboardMetrics(
    val conversionRate: Double = 0.0,
    val totalQuotationValue: Double = 0.0,
    val newLeadsCount: Int = 0,
    val activeLeadsCount: Int = 0,
    val quotationsCount: Int = 0,
    val confirmedOrdersCount: Int = 0,
    val lostLeadsCount: Int = 0,
    val totalLeadsCount: Int = 0
)

data class WhatsappTemplateEntity(
    val title: String = "",
    val templateType: String = "",
    val content: String = ""
)

data class BroadcastCampaignEntity(
    val campaignId: String = "",
    val campaignName: String = "",
    val status: String = "",
    val targetSegment: String = "",
    val targetCount: Int = 0,
    val templateUsed: String = "",
    val sentCount: Int = 0,
    val deliveredCount: Int = 0,
    val createdDate: String = ""
)

data class FollowupEntity(
    val followupId: String = "",
    val leadId: Long = 0,
    val customerName: String = "",
    val mobile: String = "",
    val status: String = "",
    val reminderType: String = "",
    val dueDate: String = "",
    val notes: String = ""
)

data class QuotationEntity(
    val quotationId: String = "",
    val leadId: Long = 0,
    val quotationNo: String = "",
    val customerName: String = "",
    val mobile: String = "",
    val validityDate: String = "",
    val totalQty: Int = 0,
    val totalAmount: Double = 0.0,
    val gstAmount: Double = 0.0,
    val netAmount: Double = 0.0
)

data class CustomerLeadEntity(
    val leadId: Long = 0,
    val customerName: String = "",
    val mobile: String = "",
    val city: String = "",
    val state: String = "",
    val status: String = "",
    val interestedProduct: String = "",
    val source: String = "",
    val createdDate: String = ""
)
data class AiImageArchiveEntity(
    val id: Long = 0,
    val productId: Long = 0,
    val imageUri: String = "",
    val versionNumber: String = "1",
    val mediaType: String = "IMAGE",
    val imageSource: String = "",
    val imageType: String = "",
    val isPrimary: Boolean = false,
    val createdDate: String = "",
    val qrNumber: String = "",
    val sku: String = "",
    val productName: String = "",
    val archiveId: String = "",
    val providerName: String = "",
    val modelName: String = "",
    val width: Int = 1024,
    val height: Int = 1024,
    val prompt: String = "",
    val usageCount: Int = 0,
    val shareCount: Int = 0,
    val downloadCount: Int = 0,
    val coverAppliedCount: Int = 0,
    val isDeleted: Boolean = false
)

data class DashboardStats(val totalProducts: Int = 0, val activeBatches: Int = 0, val totalStock: Int = 0, val totalInventoryValue: Double = 0.0, val lowStockCount: Int = 0)

data class ProductBatchEntity(
    val id: String = "",
    val batchNumber: String = "",
    val batchName: String = "",
    val category: String = "",
    val brand: String = "",
    val status: String = "DRAFT",
    val description: String = "",
    val productIdsJson: String = "[]",
    val totalProducts: Int = 0,
    val completedProducts: Int = 0,
    val pendingProducts: Int = 0,
    val failedProducts: Int = 0,
    val createdAt: String = "",
    val updatedAt: String = ""
)

data class ProductImageEntity(
    val id: Long = 0,
    val productId: String = "",
    val uri: String = "",
    val imageType: String = "",
    val isPrimary: Boolean = false
)

data class AICatalogueInputState(
    val productName: String = "",
    val productCategory: String = "",
    val fabric: String = "",
    val color: String = "",
    val price: String = "",
    val designDetails: String = "",
    val occasion: String = "",
    val productImageUrl: String = ""
)

data class MarginCalculatorState(
    val customCost: String = "",
    val customSellingPrice: String = "",
    val customDealerDiscountPct: String = "",
    val calculatedMarginPct: Double = 0.0,
    val calculatedProfitPct: Double = 0.0,
    val calculatedDealerNet: Double = 0.0,
    val calculatedDealerMargin: Double = 0.0
)

data class AIPricingInputState(
    val productName: String = "",
    val costPrice: String = "",
    val productCategory: String = "",
    val brand: String = "",
    val fabricType: String = "",
    val dealerCategory: String = "",
    val existingSellingPrice: String = "",
    val competitorPrice: String = "",
    val targetMargin: String = "",
    val region: String = "",
    val marketType: String = ""
)
