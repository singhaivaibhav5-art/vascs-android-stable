package com.veeransh.aifashion.enterprise.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veeransh.aifashion.enterprise.data.datastore.AdminPreferences
import com.veeransh.aifashion.enterprise.data.local.dao.AdminConfigDao
import com.veeransh.aifashion.enterprise.data.local.entity.AdminConfigEntity
import com.veeransh.aifashion.enterprise.data.local.entity.ProductEntity
import com.veeransh.aifashion.enterprise.data.repository.ProductRepository
import com.veeransh.aifashion.enterprise.types.PlacementTemplate
import com.veeransh.aifashion.enterprise.util.PlacementTemplateHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SuperAdminViewModel @Inject constructor(
    private val adminPreferences: AdminPreferences,
    private val productRepository: ProductRepository,
    private val adminConfigDao: AdminConfigDao
) : ViewModel() {

    val enableCod = adminPreferences.enableCod.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val minCod = adminPreferences.minCod.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val maxCod = adminPreferences.maxCod.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 5000)
    val codCharge = adminPreferences.codCharge.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 50)
    val adminPin = adminPreferences.adminPin.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "2026")
    val bananaAiEnabled = adminPreferences.bananaAiEnabled.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val adminConfig = adminConfigDao.getConfig().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val products: StateFlow<List<ProductEntity>> = productRepository.allProducts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateCodSettings(enabled: Boolean, min: Int, max: Int, charge: Int) {
        viewModelScope.launch {
            adminPreferences.updateCodSettings(enabled, min, max, charge)
        }
    }

    fun updateStock(product: ProductEntity, delta: Int) {
        viewModelScope.launch {
            val newStock = (product.stock + delta).coerceAtLeast(0)
            productRepository.updateProduct(product.copy(stock = newStock))
        }
    }

    fun updateAdminPin(pin: String) {
        viewModelScope.launch {
            adminPreferences.updateAdminPin(pin)
        }
    }

    fun updateBananaAiEnabled(enabled: Boolean) {
        viewModelScope.launch {
            adminPreferences.updateBananaAiEnabled(enabled)
        }
    }

    fun updatePlacementTemplate(updatedTemplate: PlacementTemplate) {
        viewModelScope.launch {
            val currentConfig = adminConfig.value ?: AdminConfigEntity()
            val templates = PlacementTemplateHelper.parseTemplates(currentConfig.templatesJson).toMutableList()
            
            val index = templates.indexOfFirst { it.placementType == updatedTemplate.placementType }
            if (index != -1) {
                templates[index] = updatedTemplate
            } else {
                templates.add(updatedTemplate)
            }
            
            val newJson = PlacementTemplateHelper.serializeTemplates(templates)
            adminConfigDao.update(currentConfig.copy(templatesJson = newJson))
        }
    }

    fun resetAllTemplates(defaultJson: String) {
        viewModelScope.launch {
            val currentConfig = adminConfig.value ?: AdminConfigEntity()
            adminConfigDao.update(currentConfig.copy(templatesJson = defaultJson))
        }
    }
}
