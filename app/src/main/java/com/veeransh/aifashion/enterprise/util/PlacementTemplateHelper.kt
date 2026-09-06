package com.veeransh.aifashion.enterprise.util

import com.veeransh.aifashion.enterprise.types.DefaultTemplates
import com.veeransh.aifashion.enterprise.types.PlacementTemplate

object PlacementTemplateHelper {

    fun getTemplate(type: String, json: String): PlacementTemplate {
        val templates = parseTemplates(json)
        return templates.find { it.placementType == type } 
            ?: DefaultTemplates.DEFAULTS.find { it.placementType == type }
            ?: DefaultTemplates.DEFAULTS.first()
    }

    // Manual JSON parsing for PlacementTemplate (no GSON/Jackson)
    fun parseTemplates(json: String): List<PlacementTemplate> {
        if (json == "[]" || json.isBlank()) return emptyList()
        val results = mutableListOf<PlacementTemplate>()
        try {
            // Very simple parser for: [{"placementType":"...","aspectRatio":"...","targetWidthPx":1920,"targetHeightPx":1080,"cropMode":"..."}]
            val items = json.removePrefix("[").removeSuffix("]").split("},{")
            items.forEach { item ->
                val clean = item.replace("{", "").replace("}", "").replace("\"", "")
                val fields = clean.split(",")
                var pType = ""
                var ratio = ""
                var width = 0
                var height = 0
                var crop = ""
                
                fields.forEach { f ->
                    val pair = f.split(":")
                    if (pair.size == 2) {
                        when (pair[0].trim()) {
                            "placementType" -> pType = pair[1].trim()
                            "aspectRatio" -> ratio = pair[1].trim()
                            "targetWidthPx" -> width = pair[1].trim().toIntOrNull() ?: 0
                            "targetHeightPx" -> height = pair[1].trim().toIntOrNull() ?: 0
                            "cropMode" -> crop = pair[1].trim()
                        }
                    }
                }
                if (pType.isNotEmpty()) {
                    results.add(PlacementTemplate(pType, ratio, width, height, crop))
                }
            }
        } catch (e: Exception) {
            // Fallback to empty if parse fails
        }
        return results
    }

    fun serializeTemplates(templates: List<PlacementTemplate>): String {
        if (templates.isEmpty()) return "[]"
        val sb = StringBuilder("[")
        templates.forEachIndexed { index, t ->
            sb.append("{")
            sb.append("\"placementType\":\"${t.placementType}\",")
            sb.append("\"aspectRatio\":\"${t.aspectRatio}\",")
            sb.append("\"targetWidthPx\":${t.targetWidthPx},")
            sb.append("\"targetHeightPx\":${t.targetHeightPx},")
            sb.append("\"cropMode\":\"${t.cropMode}\"")
            sb.append("}")
            if (index < templates.size - 1) sb.append(",")
        }
        sb.append("]")
        return sb.toString()
    }
}
