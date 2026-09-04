package com.veeransh.aifashion.enterprise.ai.data.ai

import com.veeransh.aifashion.enterprise.ai.data.model.*

object AIResponseParser {
    fun parseCatalogueResponse(response: String): AICatalogueResult {
        return AICatalogueResult()
    }
    fun parsePricingResponse(response: String): AIPricingResult {
        return AIPricingResult()
    }
    fun parseDemandResponse(response: String): AIDemandResult {
        return AIDemandResult()
    }
    fun parseDealerResponse(response: String): AIDealerResult {
        return AIDealerResult()
    }
    fun parseStrategyResponse(response: String): AIStrategyResult {
        return AIStrategyResult()
    }
}
